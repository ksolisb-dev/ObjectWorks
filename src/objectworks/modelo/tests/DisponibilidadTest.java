package objectworks.modelo.tests;

import objectworks.modelo.*;
import objectworks.modelo.enums.EstadoReserva;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class DisponibilidadTest {
    private Reserva reservaPrueba;
    private Habitacion habOcupada;
    private Habitacion habLibre;
    private LocalDate entradaOcupada;
    private LocalDate salidaOcupada;

    @BeforeEach
    void setUp() {
        Cliente cliente = new Cliente("123A", "Test", "Test", "test@test.com", "123");
        Hotel hotel = new Hotel(1, "Test Hotel", "Test City", "Dir", 5, "123");
        habOcupada = new HabitacionEstandar(1, "101", "1", 2, 100.0);

        entradaOcupada = LocalDate.now().plusDays(5);
        salidaOcupada = LocalDate.now().plusDays(10);

        Lista<Habitacion> habitaciones = new Lista<>();
        habitaciones.add(habOcupada);
        reservaPrueba = new Reserva(1, entradaOcupada, salidaOcupada, 2, LocalDate.now(),
                EstadoReserva.CONFIRMADA, hotel, cliente, habitaciones);

        habLibre = new HabitacionEstandar(999, "999", "9", 2, 100.0);
    }

    @Test //Caso Normal: Buscamos fechas muy lejanas donde no hay ninguna reserva.
    void testDisponibilidadNormal() {

        LocalDate entrada = LocalDate.now().plusMonths(2);
        LocalDate salida = LocalDate.now().plusMonths(2).plusDays(3);

        //Esperamos que no la ocupe (disponible)
        assertFalse(reservaPrueba.ocupaHabitacion(habLibre, entrada, salida), "Debería estar disponible, no hay solapamiento");
    }

    @Test //Caso Límite: Intentamos entrar justo el mismo día que otra persona sale de la habitación
    void testDisponibilidadLimite() {
        LocalDate entrada = salidaOcupada;
        LocalDate salida = salidaOcupada.plusDays(2);

        //Aquí también está disponible ya que el día de salida cuenta como que la habitación está libre
        assertFalse(reservaPrueba.ocupaHabitacion(habOcupada, entrada, salida), "Debería estar disponible, entra el mismo día que sale la otra");
    }

    @Test //Caso Error: Se intenta reservar en fechas en que se pisan con una reserva existente
    void testDisponibilidadError() {
        LocalDate entrada = entradaOcupada.plusDays(1);
        LocalDate salida = salidaOcupada.minusDays(1);

        //Esperamos que la ocupe ya que la habitación está reservada dentro del rango de fechas
        assertTrue(reservaPrueba.ocupaHabitacion(habOcupada, entrada, salida), "Debería estar ocupada, las fechas se solapan");
    }
}
