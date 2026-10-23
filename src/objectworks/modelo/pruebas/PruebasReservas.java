package objectworks.modelo.pruebas;

import objectworks.modelo.Datos;
import objectworks.modelo.Habitacion;
import objectworks.modelo.Lista;
import objectworks.modelo.Reserva;

import java.time.LocalDate;

public class PruebasReservas {

    public static void main(String[] args) {
        Datos datos = new Datos();

        try {
            // 1. Comprobar la carga inicial.
            Lista<Reserva> reservas = datos.listarReservas();
            System.out.println("Reservas cargadas: " + reservas.size());

            for (Reserva reserva : reservas.getArrayList()) {
                System.out.println(reserva);
            }

            // 2. Recuperar la primera reserva.
            Reserva reserva = datos.buscarReservaPorId(1);
            Habitacion habitacion = reserva.getHabitaciones().getAt(0);

            LocalDate entrada = reserva.getFechaEntrada();
            LocalDate salida = reserva.getFechaSalida();

            System.out.println("Noches: " + reserva.calcularNoches());

            // 3. La propia reserva ocupa la habitación.
            System.out.println(
                    "Disponible sin excluir: "
                            + datos.estaDisponible(
                            habitacion, entrada, salida)
            );

            // 4. Al excluirla, no debería haber otro conflicto.
            System.out.println(
                    "Disponible excluyendo la propia: "
                            + datos.estaDisponible(
                            habitacion, entrada, salida, reserva)
            );

            // 5. Modificar la duración manteniendo las habitaciones.
            datos.modificarReserva(
                    reserva.getId(),
                    entrada,
                    entrada.plusDays(4),
                    reserva.getNumPersonas(),
                    reserva.getHabitaciones()
            );

            System.out.println(
                    "Noches después de modificar: "
                            + reserva.calcularNoches()
            );
            System.out.println(
                    "Importe después de modificar: "
                            + reserva.getImporte()
            );

            // 6. Cancelar y comprobar que libera la habitación.
            datos.cancelarReserva(reserva.getId());

            System.out.println("Estado: " + reserva.getEstado());
            System.out.println(
                    "Disponible después de cancelar: "
                            + datos.estaDisponible(
                            habitacion,
                            reserva.getFechaEntrada(),
                            reserva.getFechaSalida())
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}