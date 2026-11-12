package objectworks.modelo.dao;

import objectworks.modelo.dao.mysql.MySQLDAOFactory;

/**
 * Patron Factory: el resto de la aplicacion pide los DAO a esta clase
 * y nunca hace "new MySQLClienteDAO()". Si un dia cambiamos de motor
 * de base de datos, solo hay que crear otra fabrica (p. ej. PostgreSQLDAOFactory)
 * y anadirla al switch; Controladores y Vistas no cambian.
 *
 * Cada parte del equipo anade aqui el metodo abstracto de su DAO
 * (getHotelDAO, getHabitacionDAO, getReservaDAO) y lo implementa
 * en MySQLDAOFactory.
 */
public abstract class DAOFactory {

    public static final int MYSQL = 1;

    public abstract ClienteDAO getClienteDAO();
    // public abstract HotelDAO getHotelDAO();            // Parte 2
    // public abstract HabitacionDAO getHabitacionDAO();  // Parte 3
    public abstract ReservaDAO getReservaDAO();        // Parte 4 - Hecha

    /** Comprueba que el almacen de datos esta disponible. */
    public abstract boolean probarConexion();

    /** Devuelve la fabrica concreta para el tipo de base de datos indicado. */
    public static DAOFactory getDAOFactory(int tipo) {
        switch (tipo) {
            case MYSQL:
                return new MySQLDAOFactory();
            default:
                throw new IllegalArgumentException("Tipo de base de datos no soportado: " + tipo);
        }
    }
}
