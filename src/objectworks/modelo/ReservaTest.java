package objectworks.modelo;

import objectworks.modelo.enums.EstadoReserva;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

/**
 * PRUEBAS UNITARIAS (JUnit)
 * Aquí se realizan los testeos del método calcularImporte()
 */

class ReservaTest {
    private Cliente cliente;
    private Hotel hotel;
    private Habitacion hab1;

    @BeforeEach
    void setUp() {
        cliente = new Cliente("123A", "Test", "Test", "test@test.com", "123");
        hotel = new Hotel(1, "Test Hotel", "Test City", "Dir", 5, "123");
        hab1 = new HabitacionEstandar(1, "101", "1", 2, 100.0);
    }

    @Test //Caso Normal: Reserva típica de 3 noches
    void testCalcularImporteNormal() {
        Lista<Habitacion> listaHabs = new Lista<>();
        listaHabs.add(hab1);

        Reserva r = new Reserva(1, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 4), 2, LocalDate.now(), EstadoReserva.CONFIRMADA, hotel, cliente, listaHabs);

        assertEquals(300.0, r.calcularImporte(), "El importe de 3 noches a 100 debe ser 300");
    }

    @Test //Caso Límite: Crear una reserva con el mínimo de noches posibles
    void testCalcularImporteLimite() {
        Lista<Habitacion> listaHabs = new Lista<>();
        listaHabs.add(hab1);

        Reserva r = new Reserva(2, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 2), 1, LocalDate.now(), EstadoReserva.CONFIRMADA, hotel, cliente, listaHabs);

        assertEquals(100.0, r.calcularImporte(), "El importe de 1 noche a 100 debe ser 100");
    }

    @Test //Caso de Error: Se utilizan fechas inválidas a propósito
    void testCalcularImporteError() {
        Lista<Habitacion> listaHabs = new Lista<>();
        listaHabs.add(hab1);

        /**
         * Al crear la reserva con fechas erróneas, el constructor llama a calcularImporte()
         * y debe lanzar una IllegalArgumentException. El assertThrows captura ese error y el test
         * pasa a verde
         */

        assertThrows(IllegalArgumentException.class, () -> {
                    new Reserva(3, LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 1),1, LocalDate.now(),EstadoReserva.CONFIRMADA, hotel, cliente, listaHabs);
                }, "Debería lanzar excepcion porque la salida es antes que la entrada");

    }

}