package objectworks.modelo.dao.mysql;

import objectworks.modelo.Habitacion;
import objectworks.modelo.Lista;
import objectworks.modelo.dao.HabitacionDAO;
import objectworks.modelo.enums.CategoriaHabitacion;
import objectworks.modelo.exceptions.DAOException;
import objectworks.modelo.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

public class MySQLHabitacionDAO implements HabitacionDAO {

    // Secuencia de selección básica de una habitación
    private static final String SELECT_BASE =
            "SELECT id_habitacion, numero, planta, capacidad, categoria, precio_base_noche" +
                    "FROM habitacion";

    // Secuencias base para la gestión de una habitación
    private static final String SQL_INSERT =
            "INSERT INTO habitacion (numero, planta, capacidad, categoria, precio_base_noche) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_ACTUALIZAR =
            "UPDATE hotel SET numero=?, planta=?, capacidad=?, categoria=?, precio_base_noche=?";
    private static final String SQL_ELIMINAR =
            "DELETE FROM habitacion WHERE id_habitacion=?";

    // Secuencias de las funciones requeridas para las habitaciones

    /** Devuelve las habitaciones de ese hotel o lista vacía si no tiene. */
    private static final String SQL_LISTAR= SELECT_BASE + "WHERE id_hotel=?" + "ORDER BY numero";

    /** Busca habitaciones por categoria. */
    private static final String SQL_HAB_CATEGORIA = SELECT_BASE + "WHERE categoria=?" + "ORDER BY id_habitacion";

    /** Busca habitaciones por hotel y por categoria. */
    private static final String SQL_HAB_HOT_CATEGORIA = SELECT_BASE + "WHERE categoria=?, id_hotel=?" + "ORDER BY id_habitacion";

    /** Busca habitaciones por número de personas. */
    private static final String SQL_HAB_NPERS = SELECT_BASE + "WHERE capacidad=?" + "ORDER BY id_habitacion";

    /** Busca habitaciones por hotel y por número de personas. */
    private static final String SQL_HAB_HOT_NPERS = SELECT_BASE + "WHERE capacidad=?, id_hotel=?" + "ORDER BY numero";

    /** Busca habitaciones disponibles entre 2 fechas. */
    private static final String SQL_HAB_DISPO = SELECT_BASE + "WHERE disponibilidad=?" + "ORDER BY id_habitacion"; // Vamos a tener que añadir o buscar si existe algun métod0 para verificar si esta disponible o no la habitación

    /** Busca habitaciones disponibles por hotel entre 2 fechas. */
    private static final String SQL_HAB_HOT_DISPO = SELECT_BASE + "WHERE disponibilidad=?, id_hotel=?" + "ORDER BY id_habitacion"; // Vamos a tener que añadir o buscar si existe algun métod0 para verificar si esta disponible o no la habitación


    // SECUENCIAS BASE (Insert, Update, Delete)
    @Override
    public Integer insertar(Habitacion habitacion) throws DAOException {
        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, habitacion.getNumero());
                ps.setString(2, habitacion.getPlanta());
                ps.setInt(3, habitacion.getCapacidad());
                ps.setObject(4, habitacion.getCategoria());
                ps.setDouble(5, habitacion.getPrecioBaseNoche());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        con.commit();
                        habitacion.setId(id);
                        return id;
                    }
                }

                con.commit();
                return null;
            } catch (SQLException e) {
                ConexionBD.rollback(con);
                throw new DAOException("Error al insertar la habitacion", e);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al insertar la habitacion", e);
        }
    }

    @Override
    public void actualizar(Habitacion habitacion) throws DAOException {
        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR)) {
                ps.setString(1, habitacion.getNumero());
                ps.setString(2, habitacion.getPlanta());
                ps.setInt(3, habitacion.getCapacidad());
                ps.setObject(4, habitacion.getCategoria());
                ps.setDouble(5, habitacion.getPrecioBaseNoche());
                ps.executeUpdate();
                con.commit();
            } catch (SQLException e) {
                ConexionBD.rollback(con);
                throw new DAOException("Error al actualizar la habitacion", e);
            }
        } catch (SQLException e) {
            throw new DAOException("Error de conexión al actualizar la habitacion", e);
        }
    }

    @Override
    public void eliminar(Integer id) throws DAOException {
        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(SQL_ELIMINAR)) {
                ps.setInt(1, id);
                ps.executeUpdate();
                con.commit();
            } catch (SQLException e) {
                ConexionBD.rollback(con);
                throw new DAOException("Error al eliminar la habitacion", e);
            }
        } catch (SQLException e) {
            throw new DAOException("Error de conexión al eliminar la habitacion", e);
        }
    }

    // METOD0S PARA LA GESTIÓN DE LAS HABITACIONES

    /** Devuelve las habitaciones de ese hotel o lista vacía si no tiene. */
    public Lista<Habitacion> obtenerHabitaciones(Integer id) throws DAOException {
        try (Connection con = ConexionBD.getConexion();
            PreparedStatement ps = con.prepareStatement(SQL_LISTAR);
            ResultSet rs = ps.executeQuery()) {
            return
        }
    }


    // FUNCIONES AUXILIARES

    /** Retorna una lista con todas las habitaciones */
    private Lista<Habitacion> leerHab(ResultSet rs) throws SQLException {
        Lista<Habitacion> lista = new Lista<>();
        while (rs.next()) {
            lista.add(mapear(rs));
        }
        return lista;
    }

    /** Mapea una habitación */
    private Habitacion mapear(ResultSet rs) throws SQLException {
        return new Habitacion(
                rs.getInt("id_habitacion"),
                rs.getString("numero"),
                rs.getString("planta"),
                rs.getInt("capacidad"),
                rs.getObject("categoria"), // No se que debo callear en categoria si getObject, getClass o que.
                rs.getDouble("precio_base_noche")
        ) {
            @Override
            public CategoriaHabitacion getCategoria() {
                return null;
            }
        };
    }
}
