package objectworks.modelo.dao.mysql;

import objectworks.modelo.Hotel;
import objectworks.modelo.Lista;
import objectworks.modelo.dao.HotelDAO;
import objectworks.modelo.exceptions.DAOException;
import objectworks.modelo.exceptions.DatoObligatorioException;
import objectworks.modelo.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLHotelDAO implements HotelDAO {

    private static final String SELECT_BASE =
            "SELECT id_hotel, nombre, ciudad, direccion, estrellas, telefono FROM hotel";
    private static final String SQL_LISTAR = SELECT_BASE + " ORDER BY nombre";
    private static final String SQL_POR_ID = SELECT_BASE + " WHERE id_hotel = ?";
    private static final String SQL_POR_CIUDAD = SELECT_BASE + " WHERE ciudad LIKE ? ORDER BY nombre";

    private static final String SQL_INSERT =
            "INSERT INTO hotel (nombre, ciudad, direccion, estrellas, telefono) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_ACTUALIZAR =
            "UPDATE hotel SET nombre=?, ciudad=?, direccion=?, estrellas=?, telefono=? WHERE id_hotel=?";
    private static final String SQL_ELIMINAR =
            "DELETE FROM hotel WHERE id_hotel=?";

    @Override
    public Integer insertar(Hotel hotel) throws DAOException {
        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, hotel.getNombre());
                ps.setString(2, hotel.getCiudad());
                ps.setString(3, hotel.getDireccion());
                ps.setInt(4, hotel.getEstrellas());
                ps.setString(5, hotel.getTelefono());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        con.commit();
                        hotel.setId(id);
                        return id;
                    }
                }

                con.commit();
                return null;
            } catch (SQLException e) {
                ConexionBD.rollback(con);
                throw new DAOException("Error al insertar el hotel", e);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al insertar el hotel", e);
        }
    }

    @Override
    public void actualizar(Hotel hotel) throws DAOException {
        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR)) {
                ps.setString(1, hotel.getNombre());
                ps.setString(2, hotel.getCiudad());
                ps.setString(3, hotel.getDireccion());
                ps.setInt(4, hotel.getEstrellas());
                ps.setString(5, hotel.getTelefono());
                ps.setInt(6, hotel.getId());
                ps.executeUpdate();
                con.commit();
            } catch (SQLException e) {
                ConexionBD.rollback(con);
                throw new DAOException("Error al actualizar el hotel", e);
            }
        } catch (SQLException e) {
            throw new DAOException("Error de conexión al actualizar el hotel", e);
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
                throw new DAOException("Error al eliminar el hotel", e);
            }
        } catch (SQLException e) {
            throw new DAOException("Error de conexión al eliminar el hotel", e);
        }
    }

    @Override
    public Hotel buscarPorId(Integer id) throws DAOException {
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_POR_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return  rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar el hotel", e);
        }
    }

    @Override
    public Lista<Hotel> listarTodos() throws DAOException {
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            return leerVarios(rs);
        } catch (SQLException e) {
            throw new DAOException("Error al listar el hotel", e);
        }
    }

    @Override
    public Lista<Hotel> buscarPorCiudad(String ciudad) throws DAOException, DatoObligatorioException {
        if (ciudad == null || ciudad.trim().isEmpty()) {
            throw new DatoObligatorioException("La ciudad es obligatoria");
        }
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_POR_CIUDAD)) {
            ps.setString(1, "%" + ciudad.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                return leerVarios(rs);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar el hotel por ciudad", e);
        }
    }

    //-- FUNCIONES AUXILIARES

    private Lista<Hotel> leerVarios(ResultSet rs) throws SQLException {
        Lista<Hotel> lista = new Lista<>();
        while (rs.next()) {
            lista.add(mapear(rs));
        }
        return lista;
    }

    private Hotel mapear(ResultSet rs) throws SQLException {
        return new Hotel(
                rs.getInt("id_hotel"),
                rs.getString("nombre"),
                rs.getString("ciudad"),
                rs.getString("direccion"),
                rs.getInt("estrellas"),
                rs.getString("telefono")
        );
    }
}
