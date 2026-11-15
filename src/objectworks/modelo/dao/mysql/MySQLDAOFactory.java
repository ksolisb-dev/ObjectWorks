package objectworks.modelo.dao.mysql;

import objectworks.modelo.dao.ClienteDAO;
import objectworks.modelo.dao.ReservaDAO;
import objectworks.modelo.dao.DAOFactory;
import objectworks.modelo.dao.HabitacionDAO;
import objectworks.modelo.dao.HotelDAO;
import objectworks.modelo.util.ConexionBD;

/** Fabrica concreta que crea los DAO para MySQL. */
public class MySQLDAOFactory extends DAOFactory {

    @Override
    public ClienteDAO getClienteDAO() {
        return new MySQLClienteDAO();
    }

    @Override
    public ReservaDAO getReservaDAO() {
        return new MySQLReservaDAO();
    }

    @Override
    public boolean probarConexion() {
        return ConexionBD.probarConexion();
    }

    @Override
    public HotelDAO getHotelDAO() {
        return new MySQLHotelDAO();
    }

    @Override
    public HabitacionDAO getHabitacionDAO() { return new MySQLHabitacionDAO(); }
}
