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

    // ===================== CLIENTES =====================
    // Devolvemos texto para que la Vista no toque clases del Modelo

    public Lista<String> listarClientes() {
        return aTexto(datos.listarClientes());
    }

    public Lista<String> buscarClientes(String texto) {
        return aTexto(datos.buscarClientes(texto));
    }

    public String verCliente(String dni) throws ClienteNoEncontradoException {
        return datos.getCliente(dni).toString();
    }

    public void anadirCliente(String dni, String nombre, String apellidos, String email, String telefono)
            throws DatoInvalidoException, ClienteDuplicadoException {
        datos.anadirCliente(dni, nombre, apellidos, email, telefono);
    }

    public void modificarCliente(String dni, String nombre, String apellidos, String email, String telefono)
            throws ClienteNoEncontradoException, DatoInvalidoException {
        datos.modificarCliente(dni, nombre, apellidos, email, telefono);
    }

    public void eliminarCliente(String dni) throws ClienteNoEncontradoException {
        datos.eliminarCliente(dni);
    }

    private Lista<String> aTexto(Lista<Cliente> lista) {
        Lista<String> resultado = new Lista<>();
        for (Cliente c : lista.getArrayList()) {
            resultado.add(c.toString());
        }
        return resultado;
    }

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

//-------------------------PARTE 5: CONSULTA DE RESERVAS----------------------------

    public Lista<Reserva> listarTodasLasReservas() {
        return datos.listarTodasLasReservas();
    }

    public Lista<Reserva> buscarReservasPorCliente(String dni) {
        return datos.buscarReservasPorClientes(dni);
    }

    public Lista<Reserva> buscarReservassPorHotel(int hotelId) {
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