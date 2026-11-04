package objectworks.modelo.dao.mysql;

import objectworks.modelo.Cliente;
import objectworks.modelo.Lista;
import objectworks.modelo.dao.ClienteDAO;
import objectworks.modelo.exceptions.DAOException;
import objectworks.modelo.util.ConexionBD;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Implementacion MySQL del DAO de clientes.
 *
 * - Consultas (SELECT): PreparedStatement con parametros "?".
 * - Operaciones DML (INSERT, UPDATE, DELETE): procedimientos almacenados
 *   llamados con CallableStatement, dentro de una transaccion
 *   (setAutoCommit(false) + commit, y rollback si algo falla).
 *
 * Nunca se concatenan datos del usuario en el SQL, asi se evita SQL Injection.
 */
public class MySQLClienteDAO implements ClienteDAO {

    private static final String SELECT_BASE =
            "SELECT id_cliente, dni, nombre, apellidos, email, telefono FROM cliente";

    private static final String SQL_LISTAR = SELECT_BASE + " ORDER BY apellidos, nombre";
    private static final String SQL_POR_ID = SELECT_BASE + " WHERE id_cliente = ?";
    private static final String SQL_POR_DNI = SELECT_BASE + " WHERE dni = ?";
    private static final String SQL_POR_NOMBRE = SELECT_BASE
            + " WHERE nombre LIKE ? OR apellidos LIKE ? ORDER BY apellidos, nombre";
    private static final String SQL_TIENE_RESERVAS =
            "SELECT EXISTS (SELECT 1 FROM reserva WHERE id_cliente = ?)";

    private static final String SP_INSERTAR = "{CALL sp_insertar_cliente(?, ?, ?, ?, ?, ?)}";
    private static final String SP_MODIFICAR = "{CALL sp_modificar_cliente(?, ?, ?, ?, ?)}";
    private static final String SP_ELIMINAR = "{CALL sp_eliminar_cliente(?)}";

    // Codigo SQLSTATE que lanzan nuestros procedimientos con SIGNAL
    private static final String SQLSTATE_PROCEDIMIENTO = "45000";
    // Codigo de error de MySQL para clave duplicada
    private static final int ERROR_DUPLICADO = 1062;

    // ===================== DML (transacciones + procedimientos) =====================

    @Override
    public Integer insertar(Cliente cliente) throws DAOException {
        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);
            try (CallableStatement cs = con.prepareCall(SP_INSERTAR)) {
                cs.setString(1, cliente.getDni());
                cs.setString(2, cliente.getNombre());
                cs.setString(3, cliente.getApellidos());
                cs.setString(4, cliente.getEmail());
                setTextoONulo(cs, 5, cliente.getTelefono());
                cs.registerOutParameter(6, Types.INTEGER);
                cs.execute();

                int idGenerado = cs.getInt(6);
                con.commit();
                cliente.setId(idGenerado);
                return idGenerado;
            } catch (SQLException e) {
                ConexionBD.rollback(con);
                throw traducir("No se pudo anadir el cliente", e);
            }
        } catch (SQLException e) {
            throw errorConexion(e);
        }
    }

    @Override
    public void actualizar(Cliente cliente) throws DAOException {
        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);
            try (CallableStatement cs = con.prepareCall(SP_MODIFICAR)) {
                cs.setInt(1, cliente.getId());
                cs.setString(2, cliente.getNombre());
                cs.setString(3, cliente.getApellidos());
                cs.setString(4, cliente.getEmail());
                setTextoONulo(cs, 5, cliente.getTelefono());
                cs.execute();
                con.commit();
            } catch (SQLException e) {
                ConexionBD.rollback(con);
                throw traducir("No se pudo modificar el cliente", e);
            }
        } catch (SQLException e) {
            throw errorConexion(e);
        }
    }

    @Override
    public void eliminar(Integer idCliente) throws DAOException {
        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);
            try (CallableStatement cs = con.prepareCall(SP_ELIMINAR)) {
                cs.setInt(1, idCliente);
                cs.execute();
                con.commit();
            } catch (SQLException e) {
                ConexionBD.rollback(con);
                throw traducir("No se pudo eliminar el cliente", e);
            }
        } catch (SQLException e) {
            throw errorConexion(e);
        }
    }

    // ===================== CONSULTAS (PreparedStatement) =====================

    @Override
    public Cliente buscarPorId(Integer idCliente) throws DAOException {
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_POR_ID)) {
            ps.setInt(1, idCliente);
            return leerUno(ps);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar el cliente", e);
        }
    }

    @Override
    public Cliente buscarPorDni(String dni) throws DAOException {
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_POR_DNI)) {
            ps.setString(1, dni);
            return leerUno(ps);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar el cliente por DNI", e);
        }
    }

    @Override
    public Lista<Cliente> listarTodos() throws DAOException {
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR)) {
            return leerVarios(ps);
        } catch (SQLException e) {
            throw new DAOException("Error al listar los clientes", e);
        }
    }

    @Override
    public Lista<Cliente> buscarPorNombreOApellido(String texto) throws DAOException {
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_POR_NOMBRE)) {
            // El comodin % va en el parametro, no en el SQL: sigue siendo seguro
            String patron = "%" + escaparLike(texto) + "%";
            ps.setString(1, patron);
            ps.setString(2, patron);
            return leerVarios(ps);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar clientes", e);
        }
    }

    @Override
    public boolean tieneReservas(int idCliente) throws DAOException {
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_TIENE_RESERVAS)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al comprobar las reservas del cliente", e);
        }
    }

    // ===================== AUXILIARES =====================

    private Cliente leerUno(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next() ? mapear(rs) : null;
        }
    }

    private Lista<Cliente> leerVarios(PreparedStatement ps) throws SQLException {
        Lista<Cliente> resultado = new Lista<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
        }
        return resultado;
    }

    // Convierte una fila de la tabla en un objeto Cliente
    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id_cliente"),
                rs.getString("dni"),
                rs.getString("nombre"),
                rs.getString("apellidos"),
                rs.getString("email"),
                rs.getString("telefono"));
    }

    private void setTextoONulo(PreparedStatement ps, int posicion, String valor) throws SQLException {
        if (valor == null || valor.isBlank()) {
            ps.setNull(posicion, Types.VARCHAR);
        } else {
            ps.setString(posicion, valor);
        }
    }

    // Para que un "%" o "_" escrito por el usuario se busque como texto normal
    private String escaparLike(String texto) {
        return texto.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    // Convierte el error de MySQL en un mensaje comprensible para el usuario
    private DAOException traducir(String contexto, SQLException e) {
        if (SQLSTATE_PROCEDIMIENTO.equals(e.getSQLState())) {
            // Mensaje que hemos escrito nosotros en el SIGNAL del procedimiento
            return new DAOException(e.getMessage(), e);
        }
        if (e.getErrorCode() == ERROR_DUPLICADO) {
            return new DAOException("Ya existe un cliente con ese DNI", e);
        }
        return new DAOException(contexto + ": " + e.getMessage(), e);
    }

    private DAOException errorConexion(SQLException e) {
        return new DAOException("Error de conexion con la base de datos: " + e.getMessage(), e);
    }
}
