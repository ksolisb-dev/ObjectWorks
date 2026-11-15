package objectworks.modelo.dao.mysql;

import objectworks.modelo.Habitacion;
import objectworks.modelo.HabitacionEstandar;
import objectworks.modelo.HabitacionSuperior;
import objectworks.modelo.Suite;
import objectworks.modelo.Lista;
import objectworks.modelo.dao.HabitacionDAO;
import objectworks.modelo.enums.CategoriaHabitacion;
import objectworks.modelo.exceptions.DAOException;
import objectworks.modelo.util.ConexionBD;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;

/**
 * Implementacion MySQL del DAO de Habitaciones.
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
            "SELECT h.id_habitacion, h.id_hotel, h.numero, h.planta, h.capacidad, h.categoria, h.precio_base_noche" +
                    " FROM habitacion h";

    // Secuencias base para la gestión de una habitación
    private static final String SP_INSERTAR = "{CALL sp_insertar_habitacion(?, ?, ?, ?, ?, ?, ?)}";
    private static final String SP_ACTUALIZAR = "{CALL sp_modificar_habitacion(?, ?, ?, ?, ?, ?, ?)}";
    private static final String SP_ELIMINAR = "{CALL sp_eliminar_habitacion(?)}";

    // Secuencias de las funciones requeridas para las habitaciones

    /** Devuelve habitacion por id*/
    private static final String SQL_POR_ID= SELECT_BASE + " WHERE h.id_habitacion=?";

    /** Lista todas las habitaciones*/
    private static final String SQL_TODAS= SELECT_BASE + " ORDER BY h.id_habitacion";

    /** Devuelve las habitaciones de ese hotel o lista vacía si no tiene. */
    private static final String SQL_POR_HOTEL= SELECT_BASE + " WHERE h.id_hotel=? ORDER BY h.numero";

    /** Busca habitaciones por categoria. */
    private static final String SQL_HAB_CATEGORIA = SELECT_BASE + " WHERE h.categoria=? ORDER BY h.id_habitacion";

    /** Busca habitaciones por hotel y por categoria. */
    private static final String SQL_HAB_HOT_CATEGORIA = SELECT_BASE + " WHERE h.id_hotel=? AND h.categoria=? ORDER BY h.id_habitacion";

    /** Busca habitaciones por número de personas. */
    private static final String SQL_HAB_NPERS = SELECT_BASE + " WHERE h.capacidad>=? ORDER BY h.id_habitacion";

    /** Busca habitaciones por hotel y por número de personas. */
    private static final String SQL_HAB_HOT_NPERS = SELECT_BASE + " WHERE h.id_hotel=? AND h.capacidad>=? ORDER BY h.numero";

    /** Comprueba si existe solapamiento
     * este definidio:
     * solapamiento: entradaSolicitada < salidaExistente y salidaSolicitada > entradaExistente.
     * */
    private static final String SIN_SOLAPAMIENTO =
            " NOT EXISTS (SELECT 1 FROM reserva_habitacion rh" +
                    " JOIN reserva r ON r.id_reserva=rh.id_reserva" +
                    " WHERE rh.id_habitacion=h.id_habitacion" +
                    " AND r.estado<>'CANCELADA'" +
                    " AND r.fecha_entrada<? AND r.fecha_salida>?" +
                    " AND (? IS NULL OR r.id_reserva<>?))";

    /** Busca habitaciones disponibles entre 2 fechas. */
    private static final String SQL_HAB_DISPO = SELECT_BASE + " WHERE" + SIN_SOLAPAMIENTO + " ORDER BY h.id_habitacion";

    /** Busca habitaciones disponibles por hotel entre 2 fechas. */
    private static final String SQL_HAB_HOT_DISPO = SELECT_BASE + " WHERE h.id_hotel=? AND" + SIN_SOLAPAMIENTO + " ORDER BY h.numero"; // Vamos a tener que añadir o buscar si existe algun métod0 para verificar si esta disponible o no la habitación


    // SECUENCIAS BASE (Insert, Update, Delete)
    @Override
    public Integer insertar(Habitacion habitacion) throws DAOException {
        validarHabitacion(habitacion);
        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);
            try {
                int id;
                try (CallableStatement cs = con.prepareCall(SP_INSERTAR)) {
                    cs.setInt(1, habitacion.getIdHotel());
                    asignarDatos(cs, 2, habitacion);
                    cs.registerOutParameter(7, Types.INTEGER);
                    cs.execute();
                    id = cs.getInt(7);
                    if (cs.wasNull() || id <= 0) {
                        throw new SQLException("No se recibió un identificador válido de habitación");
                    }
                }
                con.commit();
                // Se modifica el objeto únicamente después de confirmar la inserción.
                habitacion.setId(id);
                return id;
            } catch (SQLException | RuntimeException e) {
                deshacer(con, e);
                throw error("Error al insertar la habitación", e);
            }
        } catch (SQLException e) {
            throw error("Error de conexión al insertar la habitación", e);
        }
    }

    @Override
    public void actualizar(Habitacion habitacion) throws DAOException {
        validarHabitacion(habitacion);
        validarId(habitacion.getId(), "habitación");
        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);
            try {
                try (CallableStatement cs = con.prepareCall(SP_ACTUALIZAR)) {
                    cs.setInt(1, habitacion.getId());
                    cs.setInt(2, habitacion.getIdHotel());
                    asignarDatos(cs, 3, habitacion);
                    cs.execute();
                }
                con.commit();
            } catch (SQLException | RuntimeException e) {
                deshacer(con, e);
                throw error("Error al actualizar la habitación", e);
            }
        } catch (SQLException e) {
            throw error("Error de conexión al actualizar la habitación", e);
        }
    }

    @Override
    public void eliminar(Integer id) throws DAOException {
        validarId(id, "habitación");
        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);
            try {
                try (CallableStatement cs = con.prepareCall(SP_ELIMINAR)) {
                    cs.setInt(1, id);
                    cs.execute();
                }
                con.commit();
            } catch (SQLException | RuntimeException e) {
                deshacer(con, e);
                throw error("Error al eliminar la habitación", e);
            }
        } catch (SQLException e) {
            throw error("Error de conexión al eliminar la habitación", e);
        }
    }

    @Override
    public Habitacion buscarPorId(Integer id) throws DAOException {
        validarId(id, "habitación");
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_POR_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw error("Error al buscar la habitación", e);
        }
    }

    @Override
    public Lista<Habitacion> listarTodos() throws DAOException {
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_TODAS)) {
            return leerHab(ps);
        } catch (SQLException e) {
            throw error("Error al listar las habitaciones", e);
        }
    }

    @Override
    public Lista<Habitacion> buscarPorHotel(int idHotel) throws DAOException {
        validarId(idHotel, "hotel");
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_POR_HOTEL)) {
            ps.setInt(1, idHotel);
            return leerHab(ps);
        } catch (SQLException e) {
            throw error("Error al buscar habitaciones por hotel", e);
        }
    }

    @Override
    public Lista<Habitacion> buscarPorCategoria(CategoriaHabitacion categoria) throws DAOException {
        validarCategoria(categoria);
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_HAB_CATEGORIA)) {
            ps.setString(1, categoria.name());
            return leerHab(ps);
        } catch (SQLException e) {
            throw error("Error al buscar habitaciones por categoría", e);
        }
    }

    @Override
    public Lista<Habitacion> buscarPorHotelCategoria(int idHotel, CategoriaHabitacion categoria)
            throws DAOException {
        validarId(idHotel, "hotel");
        validarCategoria(categoria);
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_HAB_HOT_CATEGORIA)) {
            ps.setInt(1, idHotel);
            ps.setString(2, categoria.name());
            return leerHab(ps);
        } catch (SQLException e) {
            throw error("Error al buscar habitaciones por hotel y categoría", e);
        }
    }

    @Override
    public Lista<Habitacion> buscarPorNumPersonas(int personas) throws DAOException {
        validarPersonas(personas);
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_HAB_NPERS)) {
            ps.setInt(1, personas);
            return leerHab(ps);
        } catch (SQLException e) {
            throw error("Error al buscar habitaciones por capacidad", e);
        }
    }

    @Override
    public Lista<Habitacion> buscarPorHotelNumPersonas(int idHotel, int personas) throws DAOException {
        validarId(idHotel, "hotel");
        validarPersonas(personas);
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_HAB_HOT_NPERS)) {
            ps.setInt(1, idHotel);
            ps.setInt(2, personas);
            return leerHab(ps);
        } catch (SQLException e) {
            throw error("Error al buscar habitaciones por hotel y capacidad", e);
        }
    }

    @Override
    public Lista<Habitacion> buscarDisponibles(LocalDate entrada, LocalDate salida)
            throws DAOException {
        return buscarDisponibles(entrada, salida, null);
    }

    @Override
    public Lista<Habitacion> buscarDisponibles(LocalDate entrada, LocalDate salida,
                                               Integer idReservaIgnorada) throws DAOException {
        return consultarDisponibles(null, entrada, salida, idReservaIgnorada);
    }

    @Override
    public Lista<Habitacion> buscarDisponiblesPorHotel(int idHotel, LocalDate entrada, LocalDate salida)
            throws DAOException {
        return buscarDisponiblesPorHotel(idHotel, entrada, salida, null);
    }

    @Override
    public Lista<Habitacion> buscarDisponiblesPorHotel(int idHotel, LocalDate entrada, LocalDate salida,
                                                       Integer idReservaIgnorada) throws DAOException {
        validarId(idHotel, "hotel");
        return consultarDisponibles(idHotel, entrada, salida, idReservaIgnorada);
    }

    private Lista<Habitacion> consultarDisponibles(Integer idHotel, LocalDate entrada, LocalDate salida,
                                                   Integer idReservaIgnorada) throws DAOException {
        if (entrada == null || salida == null || !salida.isAfter(entrada)) {
            throw new DAOException("La salida debe ser posterior a la entrada y ambas fechas son obligatorias");
        }
        if (idReservaIgnorada != null) validarId(idReservaIgnorada, "reserva");

        // Solo se elige entre dos SQL fijos; los valores siempre van como parámetros.
        String sql = idHotel == null ? SQL_HAB_DISPO : SQL_HAB_HOT_DISPO;
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int indice = 1;
            if (idHotel != null) ps.setInt(indice++, idHotel);
            // El primer ? de fechas se compara con fecha_entrada: recibe la salida solicitada.
            ps.setDate(indice++, Date.valueOf(salida));
            ps.setDate(indice++, Date.valueOf(entrada));
            if (idReservaIgnorada == null) {
                ps.setNull(indice++, Types.INTEGER);
                ps.setNull(indice, Types.INTEGER);
            } else {
                ps.setInt(indice++, idReservaIgnorada);
                ps.setInt(indice, idReservaIgnorada);
            }
            return leerHab(ps);
        } catch (SQLException e) {
            throw error("Error al consultar disponibilidad de habitaciones", e);
        }
    }

    /** Ejecuta una sentencia cuyos parámetros ya han sido asignados. */
    private Lista<Habitacion> leerHab(PreparedStatement ps) throws SQLException {
        Lista<Habitacion> lista = new Lista<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Habitacion mapear(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_habitacion");
        int idHotel = rs.getInt("id_hotel");
        String numero = rs.getString("numero");
        String planta = rs.getString("planta");
        int capacidad = rs.getInt("capacidad");
        String textoCategoria = rs.getString("categoria");
        BigDecimal precioBase = rs.getBigDecimal("precio_base_noche");
        if (textoCategoria == null || precioBase == null) {
            throw new SQLException("La habitación " + id + " tiene categoría o precio base nulos");
        }

        CategoriaHabitacion categoria;
        try {
            categoria = CategoriaHabitacion.valueOf(textoCategoria);
        } catch (IllegalArgumentException e) {
            throw new SQLException("Categoría desconocida en la habitación " + id + ": " + textoCategoria, e);
        }

        // Los constructores reales reciben cinco argumentos
        double precio = precioBase.doubleValue();
        Habitacion habitacion = switch (categoria) {
            case ESTANDAR -> new HabitacionEstandar(id, numero, planta, capacidad, precio);
            case SUPERIOR -> new HabitacionSuperior(id, numero, planta, capacidad, precio);
            case SUITE -> new Suite(id, numero, planta, capacidad, precio);
        };
        habitacion.setIdHotel(idHotel);
        return habitacion;
    }

    /** Asigna numero, planta, capacidad, categoria y precio, a partir del índice indicado. */
    private void asignarDatos(CallableStatement cs, int indice, Habitacion h) throws SQLException {
        cs.setString(indice++, h.getNumero());
        cs.setString(indice++, h.getPlanta());
        cs.setInt(indice++, h.getCapacidad());
        cs.setString(indice++, h.getCategoria().name());
        cs.setBigDecimal(indice, BigDecimal.valueOf(h.getPrecioBaseNoche()));
    }

    private void validarHabitacion(Habitacion h) throws DAOException {
        if (h == null) throw new DAOException("La habitación es obligatoria");
        validarId(h.getIdHotel(), "hotel");
        validarCategoria(h.getCategoria());
        if (h.getNumero() == null || h.getNumero().isBlank() || h.getNumero().length() > 10
                || h.getPlanta() == null || h.getPlanta().isBlank() || h.getPlanta().length() > 10) {
            throw new DAOException("Número y planta son obligatorios y admiten hasta 10 caracteres");
        }
        if (h.getCapacidad() <= 0 || h.getCapacidad() > 255) {
            throw new DAOException("La capacidad debe estar entre 1 y 255");
        }
        if (!Double.isFinite(h.getPrecioBaseNoche()) || h.getPrecioBaseNoche() < 0
                || h.getPrecioBaseNoche() > 999999.99) {
            throw new DAOException("El precio base debe estar entre 0 y 999999.99");
        }
    }

    private void validarId(Integer id, String entidad) throws DAOException {
        if (id == null || id <= 0) throw new DAOException("El identificador de " + entidad + " debe ser positivo");
    }

    private void validarPersonas(int personas) throws DAOException {
        if (personas <= 0) throw new DAOException("El número de personas debe ser positivo");
    }

    private void validarCategoria(CategoriaHabitacion categoria) throws DAOException {
        if (categoria == null) throw new DAOException("La categoría es obligatoria");
    }

    private void deshacer(Connection con, Throwable causa) {
        try {
            con.rollback();
        } catch (SQLException errorRollback) {
            causa.addSuppressed(errorRollback);
        }
    }

    private DAOException error(String contexto, Throwable causa) {
        if (causa instanceof SQLException sql && "45000".equals(sql.getSQLState())) {
            return new DAOException(sql.getMessage(), sql);
        }
        return new DAOException(contexto, causa);
    }
}
