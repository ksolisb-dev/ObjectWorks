package objectworks.modelo.dao.jpa;

import objectworks.modelo.dao.ClienteDAO;
import objectworks.modelo.dao.DAOFactory;
import objectworks.modelo.dao.HabitacionDAO;
import objectworks.modelo.dao.HotelDAO;
import objectworks.modelo.dao.ReservaDAO;
import objectworks.modelo.dao.mysql.MySQLClienteDAO;
import objectworks.modelo.dao.mysql.MySQLHabitacionDAO;
import objectworks.modelo.dao.mysql.MySQLReservaDAO;
import objectworks.modelo.util.JPAUtil;

/**
 * Fabrica concreta que crea los DAO con JPA (Producto 4).
 *
 * Mientras el equipo migra, las partes que aun no tienen su DAO JPA
 * devuelven el DAO JDBC del Producto 3. Cada parte cambia SOLO su metodo
 * cuando tenga su JPAXxxDAO (p. ej. getClienteDAO -> new JPAClienteDAO()).
 * Controladores y Vistas no cambian: siguen pidiendo los DAO a la fabrica.
 */
public class JPADAOFactory extends DAOFactory {

    @Override
    public ClienteDAO getClienteDAO() {
        return new MySQLClienteDAO();        // Parte 1: cambiar por new JPAClienteDAO()
    }

    @Override
    public HotelDAO getHotelDAO() {
        return new JPAHotelDAO();            // Parte 2 - Hecha con JPA
    }

    @Override
    public HabitacionDAO getHabitacionDAO() {
        return new MySQLHabitacionDAO();     // Parte 3: cambiar por new JPAHabitacionDAO()
    }

    @Override
    public ReservaDAO getReservaDAO() {
        return new MySQLReservaDAO();        // Parte 4: cambiar por new JPAReservaDAO()
    }

    @Override
    public boolean probarConexion() {
        return JPAUtil.probarConexion();
    }

    @Override
    public void cerrar() {
        JPAUtil.close();
    }
}