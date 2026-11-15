package objectworks.modelo.dao;

import  objectworks.modelo.Hotel;
import objectworks.modelo.Lista;
import objectworks.modelo.exceptions.DAOException;
import objectworks.modelo.exceptions.DatoObligatorioException;

public interface HotelDAO extends DAO<Hotel,Integer> {

    /** Busca hoteles cuya ciudad contenga el texto. */
    Lista<Hotel> buscarPorCiudad(String ciudad) throws DAOException, DatoObligatorioException;
}
