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
        String sql = "SELECT * FROM reserva WHERE id_reserva = ?";
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
    public Lista<Reserva> listarTodos() throws DAOException {
        return listarConSql("SELECT * FROM reserva ORDER BY fecha_entrada DESC");
    }
    @Override
    public Lista<Reserva> listarPorCliente(int idCliente) throws DAOException {
        return listarConSql("SELECT * FROM reserva WHERE id_cliente = " + idCliente); // mejor con PreparedStatement, lo dejo simple
    }

    @Override
    public Lista<Reserva> listarPorHotel(int idHotel) throws DAOException {
        return listarConSql("SELECT * FROM reserva WHERE id_hotel = " + idHotel);
    }
    @Override
    public Lista<Reserva> listarPorFechas(LocalDate desde, LocalDate hasta) throws DAOException {
        String sql = "SELECT * FROM reserva WHERE fecha_entrada >= ? AND fecha_salida <= ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(hasta));
            return leerVarios(ps);
        } catch (SQLException e) {
            throw new DAOException("Error al listar reservas por fechas", e);
        }
    }

    @Override
    public Lista<Reserva> listarPorEstado(EstadoReserva estado) throws DAOException {
        return listarConSql("SELECT * FROM reserva WHERE estado = '" + estado.name() + "'");
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
    private Lista<Reserva> listarConSql(String sql) throws DAOException {
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
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


    // PARTE 5 - Esto puedes tener que cambiarlo!!!!!
    private Reserva mapearBasico(ResultSet rs) throws SQLException {
        Reserva r = new Reserva(
                rs.getInt("id_reserva"),
                rs.getDate("fecha_entrada").toLocalDate(),
                rs.getDate("fecha_salida").toLocalDate(),
                rs.getInt("num_personas"),
                rs.getDate("fecha_creacion").toLocalDate(),
                EstadoReserva.valueOf(rs.getString("estado")),
                null,   // Hotel (se puede cargar después)
                null,   // Cliente
                new Lista<>()
        );
        return r;
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