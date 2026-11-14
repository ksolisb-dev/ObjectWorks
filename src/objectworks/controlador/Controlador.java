package objectworks.controlador;

import objectworks.modelo.*; // Truco, con esto importa todas las clases que haya en modelo
import objectworks.modelo.enums.CategoriaHabitacion;
import objectworks.modelo.enums.EstadoReserva;
import objectworks.modelo.exceptions.*;
import javax.print.DocFlavor;
import objectworks.modelo.exceptions.*;
import java.time.LocalDate;

public class Controlador {
    private Datos datos;

    public Controlador() {
        this.datos = new Datos(); // ahora datos() ya carga las cosas asi que te borro lo de cargar lo de prueba
    }

    // CLIENTES: movido a ClienteController (Producto 3, Parte 1)

    // ===================== HOTELES =====================
    // No las he testeado mucho....
    public Lista<Hotel> listarHoteles() {
        return datos.listarHoteles();
    }

    public Lista<Hotel> buscarHotelesPorCiudad(String ciudad) throws DatoObligatorioException {
        return datos.buscarHotelesPorCiudad(ciudad);
    }

    public Lista<Habitacion> habitacionesDisponibles(int hotelId, LocalDate entrada, LocalDate salida)
            throws HotelNoEncontradoException, FechasInvalidasException {
        return datos.getHabitacionesDisponibles(hotelId, entrada, salida);
    }

    public Lista<Habitacion> habitacionesPorCategoria(int hotelId, CategoriaHabitacion categoria)
            throws HotelNoEncontradoException {
        return datos.getHabitacionesPorCategoria(hotelId, categoria);
    }

    public int numHabitacionesDisponibles(int hotelId, LocalDate entrada, LocalDate salida)
            throws HotelNoEncontradoException, FechasInvalidasException {
        return datos.getNumHabitacionesDisponibles(hotelId, entrada, salida);
    }

    // ===================== HABITACIONES =====================

    public Lista<Habitacion> listarHabitacionesHotel(int hotelId)
            throws HotelNoEncontradoException {
        return datos.getHabitacionesHotel(hotelId);
    }

    public Lista<Habitacion> getHabitacionesDisponibles(
            int hotelId, LocalDate entrada, LocalDate salida)
            throws HotelNoEncontradoException, FechasInvalidasException {
        return datos.getHabitacionesDisponibles(hotelId, entrada, salida);
    }

    public Lista<Habitacion> filtrarHabitacionesPorPersonas(
            int hotelId, int numPersonas)
            throws HotelNoEncontradoException {
        return datos.filtrarHabitacionesPorPersonas(hotelId, numPersonas);
    }

        
    // ===================== RESERVAS =====================

    public Lista<Reserva> listarReservas() {
        return datos.listarReservas();
    }

    public Reserva buscarReservaPorId(int idReserva)
            throws ReservaNoEncontradaException, DatoInvalidoException {

        return datos.buscarReservaPorId(idReserva);
    }

    // Lista todas las reservas de un cliente
    public Lista<Reserva> listarReservasCliente(String dniCliente)
            throws ClienteNoEncontradoException {

        return datos.listarReservasCliente(dniCliente);
    }


    public Reserva crearReserva(String dniCliente, int hotelId, LocalDate entrada, LocalDate salida, int numPersonas, Lista<Habitacion> habitacionesSeleccionadas)
                throws ClienteNoEncontradoException, HotelNoEncontradoException, FechasInvalidasException, DatoObligatorioException, DatoInvalidoException, HabitacionOcupadaException {

        return datos.crearReserva(dniCliente, hotelId, entrada, salida, numPersonas, habitacionesSeleccionadas);
    }

    public Reserva modificarReserva(int idReserva, LocalDate nuevaEntrada, LocalDate nuevaSalida, int nuevoNumPersonas, Lista<Habitacion> nuevasHabitaciones)
                throws ReservaNoEncontradaException, FechasInvalidasException, DatoObligatorioException, HabitacionOcupadaException {

        return datos.modificarReserva(idReserva, nuevaEntrada, nuevaSalida, nuevoNumPersonas, nuevasHabitaciones);
    }

    public void cancelarReserva(int idReserva)
                throws ReservaNoEncontradaException, DatoInvalidoException {

        datos.cancelarReserva(idReserva);
    }

    // Lista todas las reservas que pueden ser modificadas al usuario
    public Lista<Habitacion> habitacionesDisponiblesParaModificar(int idReserva, LocalDate entrada, LocalDate salida)
                throws ReservaNoEncontradaException, DatoInvalidoException, FechasInvalidasException {

        // Validamos las fechas de entrada y salida
        Validador.validarFechas(entrada, salida);

        // Guardamos las reservas y creamos la nueva lista con las reservas disponibles
        Reserva reserva = datos.buscarReservaPorId(idReserva);
        Lista<Habitacion> disponibles = new Lista<>();

        // Llenamos la lista con las reservas modificables
        for (Habitacion habitacion
                : reserva.getHotel().getHabitaciones().getArrayList()) {

            if (datos.estaDisponible(
                    habitacion, entrada, salida, reserva)) {
                disponibles.add(habitacion);
            }
        }

        return disponibles;
    }

    //-------------------------PARTE 5: CONSULTA DE RESERVAS----------------------------

    public Lista<Reserva> listarTodasLasReservas() {
        return datos.listarTodasLasReservas();
    }

    public Lista<Reserva> buscarReservasPorCliente(String dni) {
        return datos.buscarReservasPorClientes(dni);
    }

    public Lista<Reserva> buscarReservasPorHotel(int hotelId) {
        return datos.buscarReservasPorHotel(hotelId);
    }

    public Lista<Reserva> buscarReservasPorFechaEntrada(LocalDate fecha) {
        return datos.buscarReservasPorFechaEntrada(fecha);
    }

    public Lista<Reserva> buscarReservasPorFechaSalida(LocalDate fecha) {
        return datos.buscarReservasPorFechaSalida(fecha);
    }

    public Lista<Reserva> buscarReservasPorEstado(EstadoReserva estado) {
        return datos.buscarReservasPorEstado(estado);
    }
}