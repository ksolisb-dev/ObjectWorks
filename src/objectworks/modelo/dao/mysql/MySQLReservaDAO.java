package objectworks.modelo.dao.mysql;

import objectworks.modelo.Reserva;
import objectworks.modelo.Lista;
import objectworks.modelo.Hotel;
import objectworks.modelo.Cliente;
import objectworks.modelo.Habitacion;
import objectworks.modelo.enums.EstadoReserva;
import objectworks.modelo.dao.ReservaDAO;
import objectworks.modelo.exceptions.DAOException;
import objectworks.modelo.util.ConexionBD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
// Se me bugearon los imports (?)

public class MySQLReservaDAO implements ReservaDAO {

    private static final String SP_CREAR     = "{CALL sp_crear_reserva(?, ?, ?, ?, ?, ?, ?)}";
    private static final String SP_MODIFICAR = "{CALL sp_modificar_reserva(?, ?, ?, ?, ?)}";
    private static final String SP_CANCELAR  = "{CALL sp_cancelar_reserva(?)}";

    private static final String SELECT_BASE =
            "SELECT r.*, c.dni FROM reserva r INNER JOIN cliente c ON r.id_cliente = c.id_cliente ";
    private static final String ORDEN = " ORDER BY r.fecha_entrada DESC, r.id_reserva";



    // Para los bugs
    private static final String SQLSTATE_PROCEDIMIENTO = "45000";


    @Override
    public Integer insertar(Reserva reserva) throws DAOException {
        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);
            try (CallableStatement cs = con.prepareCall(SP_CREAR)) {
                cs.setInt(1, reserva.getCliente().getId());
                cs.setInt(2, reserva.getHotel().getId());
                cs.setDate(3, Date.valueOf(reserva.getFechaEntrada()));
                cs.setDate(4, Date.valueOf(reserva.getFechaSalida()));
                cs.setInt(5, reserva.getNumPersonas());
                cs.setDouble(6, reserva.getImporte());
                cs.registerOutParameter(7, Types.INTEGER);
                cs.execute();

                int idGenerado = cs.getInt(7);
                reserva.setId(idGenerado);
                insertarHabitaciones(con, idGenerado, reserva.getHabitaciones());

                con.commit();
                return idGenerado;
            } catch (SQLException e) {
                ConexionBD.rollback(con);
                throw traducir("No se pudo crear la reserva", e);
            }
        } catch (SQLException e) {
            throw errorConexion(e);
        }
    }

    @Override
    public void actualizar(Reserva reserva) throws DAOException {
        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);
            try (CallableStatement cs = con.prepareCall(SP_MODIFICAR)) {
                cs.setInt(1, reserva.getId());
                cs.setDate(2, Date.valueOf(reserva.getFechaEntrada()));
                cs.setDate(3, Date.valueOf(reserva.getFechaSalida()));
                cs.setInt(4, reserva.getNumPersonas());
                cs.setDouble(5, reserva.getImporte());
                cs.execute();



                eliminarHabitaciones(con, reserva.getId());
                insertarHabitaciones(con, reserva.getId(), reserva.getHabitaciones());

                con.commit();
            } catch (SQLException e) {
                ConexionBD.rollback(con);
                throw traducir("No se pudo modificar la reserva", e);
            }
        } catch (SQLException e) {
            throw errorConexion(e);
        }
    }

    @Override
    // En vez de borrar la reserva simplemente la cancelo
    // /(Me suena de la ultima llamada cuando los esquemas)
    public void eliminar(Integer idReserva) throws DAOException {
        cancelar(idReserva);
    }

    @Override
    public void cancelar(int idReserva) throws DAOException {
        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);
            try (CallableStatement cs = con.prepareCall(SP_CANCELAR)) {
                cs.setInt(1, idReserva);
                cs.execute();
                con.commit();
            } catch (SQLException e) {
                ConexionBD.rollback(con);
                throw traducir("No se pudo cancelar la reserva", e);
            }
        } catch (SQLException e) {
            throw errorConexion(e);
        }
    }

    @Override
    public Reserva buscarPorId(Integer id) throws DAOException {
        String sql = SELECT_BASE + "WHERE r.id_reserva = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearBasico(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar la reserva", e);
        }
    }

    @Override
    public Lista<Reserva> buscarPorDni(String dni) throws DAOException {
        return listarConParametros(SELECT_BASE + "WHERE c.dni = ?" + ORDEN, dni);
    }

    @Override
    public Lista<Reserva> listarTodos() throws DAOException {
        return listarConParametros(SELECT_BASE + ORDEN);
    }

    @Override
    public Lista<Reserva> listarPorHotel(int idHotel) throws DAOException {
        return listarConParametros(SELECT_BASE + "WHERE r.id_hotel = ?" + ORDEN, idHotel);
    }

    @Override
    public Lista<Reserva> listarPorFechaEntrada(LocalDate fecha) throws DAOException {
        return listarConParametros(SELECT_BASE + "WHERE r.fecha_entrada = ?" + ORDEN, Date.valueOf(fecha));
    }

    @Override
    public Lista<Reserva> listarPorFechaSalida(LocalDate fecha) throws DAOException {
        return listarConParametros(SELECT_BASE + "WHERE r.fecha_salida = ?" + ORDEN, Date.valueOf(fecha));
    }

    @Override
    public Lista<Reserva> listarPorEstado(EstadoReserva estado) throws DAOException {
        return listarConParametros(SELECT_BASE + "WHERE r.estado = ?" + ORDEN, estado.name());
    }

    private void insertarHabitaciones(Connection con, int idReserva, Lista<Habitacion> habitaciones) throws SQLException {
        String sql = "INSERT INTO reserva_habitacion (id_reserva, id_habitacion) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < habitaciones.size(); i++) {
                ps.setInt(1, idReserva);
                ps.setInt(2, habitaciones.getAt(i).getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
    private void eliminarHabitaciones(Connection con, int idReserva) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM reserva_habitacion WHERE id_reserva = ?")) {
            ps.setInt(1, idReserva);
            ps.executeUpdate();
        }
    }

    private Lista<Reserva> listarConParametros(String sql, Object... parametros) throws DAOException {
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            return leerVarios(ps);
        } catch (SQLException e) {
            throw new DAOException("Error al listar reservas", e);
        }
    }





    private Lista<Reserva> leerVarios(PreparedStatement ps) throws SQLException {
        Lista<Reserva> resultado = new Lista<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(mapearBasico(rs));
            }
        }
        return resultado;
    }


    private Reserva mapearBasico(ResultSet rs) throws SQLException {
        Reserva r = new Reserva(
                rs.getInt("id_reserva"),
                rs.getDate("fecha_entrada").toLocalDate(),
                rs.getDate("fecha_salida").toLocalDate(),
                rs.getInt("num_personas"),
                rs.getDate("fecha_creacion").toLocalDate(),
                EstadoReserva.valueOf(rs.getString("estado")),
                hotelSoloId(rs.getInt("id_hotel")),
                clienteSoloIdYDni(rs.getInt("id_cliente"), rs.getString("dni")),
                new Lista<>()
        );
        return r;
    }

    // Hotel "ligero": solo con el id, suficiente para mostrar y consultar la reserva
    private Hotel hotelSoloId(int idHotel) {
        return new Hotel(idHotel, "", "", "", 0, "");
    }

    // Cliente "ligero": id y DNI, que es lo que se muestra en las consultas
    private Cliente clienteSoloIdYDni(int idCliente, String dni) {
        Cliente c = new Cliente();
        c.setId(idCliente);
        c.setDni(dni);
        return c;
    }

    private DAOException traducir(String contexto, SQLException e) {
        if (SQLSTATE_PROCEDIMIENTO.equals(e.getSQLState())) {
            return new DAOException(e.getMessage(), e);
        }
        return new DAOException(contexto + ": " + e.getMessage(), e);
    }










    private DAOException errorConexion(SQLException e) {
        return new DAOException("Error de conexión con la base de datos: " + e.getMessage(), e);
    }
}