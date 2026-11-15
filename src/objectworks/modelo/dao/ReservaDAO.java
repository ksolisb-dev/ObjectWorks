package objectworks.modelo.dao;

import objectworks.modelo.Reserva;
import objectworks.modelo.Lista;
import objectworks.modelo.enums.EstadoReserva;
import objectworks.modelo.exceptions.DAOException;

import java.time.LocalDate;

// Dejo tambien hechas las de lectura que pedirá mas alante
// No me costaba nada y asi creo que le robo trabajillo a la Parte 5

public interface ReservaDAO extends DAO<Reserva, Integer> {
    // Creo que asi basta
    void cancelar(int idReserva) throws DAOException;




    // Al compi que haga la P5; si estas te conflictean quitalas sin mas
    Lista<Reserva> listarPorCliente(int idCliente) throws DAOException;
    Lista<Reserva> listarPorHotel(int idHotel) throws DAOException;
    Lista<Reserva> listarPorFechas(LocalDate desde, LocalDate hasta) throws DAOException;
    Lista<Reserva> listarPorEstado(EstadoReserva estado) throws DAOException;
}

