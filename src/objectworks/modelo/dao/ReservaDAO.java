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
    Lista<Reserva> buscarPorDni(String dni) throws DAOException;
    Lista<Reserva> listarPorHotel(int idHotel) throws DAOException;
    Lista<Reserva> listarPorEstado(EstadoReserva estado) throws DAOException;
    Lista<Reserva> listarPorFechaEntrada(LocalDate fecha) throws DAOException;
    Lista<Reserva> listarPorFechaSalida(LocalDate fecha) throws DAOException;
}

