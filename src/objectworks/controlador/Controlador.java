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