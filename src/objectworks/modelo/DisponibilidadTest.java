package objectworks.modelo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

/**
 * PRUEBAS UNITARIAS (JUnit)
 * Aquí se realizan los testeos del método estaDisponible() de la clase Datos.
 * Con esto evaluamos si una habitación está libre u ocupada en un rango de fechas
 */

class DisponibilidadTest {
    private Datos datos;
    private Habitacion habOcupada;
    private Habitacion habLibre;
    private LocalDate entradaOcupada;
    private LocalDate salidaOcupada;

    @BeforeEach
    void setUp() throws Exception {
        datos = new Datos();

        Reserva reservaPrueba = datos.buscarReservaPorId(1);
        habOcupada = reservaPrueba.getHabitaciones().getAt(0);

        entradaOcupada = reservaPrueba.getFechaEntrada();
        salidaOcupada = reservaPrueba.getFechaSalida();

        /**
         * He creado una habitación inventada y que no está en ningún hotel para realizar el caso normal
         */

        habLibre = new HabitacionEstandar(999, "999", "9", 2, 100.0);
    }

    @Test //Caso Normal: Buscamos fechas muy lejanas donde no hay ninguna reserva.
    void testDisponibilidadNormal() {

        LocalDate entrada = LocalDate.now().plusMonths(2);
        LocalDate salida = LocalDate.now().plusMonths(2).plusDays(3);

        //Esperamos true
        assertTrue(datos.estaDisponible(habLibre, entrada, salida), "Debería estar disponible, no hay solapamiento");
    }

    @Test //Caso Límite: Intentamos entrar justo el mismo día que otra persona sale de la habitación
    void testDisponibilidadLimite() {
        LocalDate entrada = salidaOcupada;
        LocalDate salida = salidaOcupada.plusDays(2);

        //Aquí también esperamos true ya que el día de salida cuenta como que la habitación está libre
        assertTrue(datos.estaDisponible(habOcupada, entrada, salida), "Debería estar disponible, entra el mismo día que sale la otra");
    }

    @Test //Caso Error: Se intenta reservr en fechas en que se pisan con una reserva existente
    void testDisponibilidadError() {
        LocalDate entrada = entradaOcupada.plusDays(1);
        LocalDate salida = salidaOcupada.minusDays(1);

        //Esperamos false ya que la habitación está ocupada dentro del rango de fechas
        assertFalse(datos.estaDisponible(habOcupada, entrada, salida), "Debería estar ocupada, las fechas se solapan");
    }
}