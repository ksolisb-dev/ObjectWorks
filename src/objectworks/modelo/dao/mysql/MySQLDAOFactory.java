package objectworks.modelo.dao.mysql;

import objectworks.modelo.dao.ClienteDAO;
import objectworks.modelo.dao.DAOFactory;
import objectworks.modelo.util.ConexionBD;

/** Fabrica concreta que crea los DAO para MySQL. */
public class MySQLDAOFactory extends DAOFactory {

    @Override
    public ClienteDAO getClienteDAO() {
        return new MySQLClienteDAO();
    }

    @Override
    public boolean probarConexion() {
        return ConexionBD.probarConexion();
    }
}
