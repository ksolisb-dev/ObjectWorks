package objectworks.pruebas;

import objectworks.modelo.Lista;
import objectworks.modelo.Reserva;
import objectworks.modelo.dao.jpa.JPAReservaDAO;
import objectworks.modelo.util.JPAUtil;

import java.time.LocalDate;
import objectworks.modelo.enums.EstadoReserva;

public class PruebaLecturaReservas {

    public static void main(String[] args) {
        try {
            JPAReservaDAO dao = new JPAReservaDAO();

            Lista<Reserva> reservas = dao.listarTodos();

            System.out.println(
                    "Reservas recuperadas: " + reservas.size()
            );

            for (Reserva reserva : reservas.getArrayList()) {
                System.out.println(reserva);

                System.out.println(
                        "Cliente: " + reserva.getCliente().getNombre()
                );

                System.out.println(
                        "Hotel: " + reserva.getHotel().getNombre()
                );

                System.out.println(
                        "Habitaciones: "
                                + reserva.getHabitaciones().size()
                );
            }

            if (!reservas.isEmpty()) {
                int id = reservas.getAt(0).getId();

                Reserva encontrada = dao.buscarPorId(id);

                System.out.println(
                        "Resultado de buscar por ID: " + encontrada
                );
            }

            Lista<Reserva> existentes = dao.listarTodos();

            if (!existentes.isEmpty()) {
                Reserva referencia = existentes.getAt(0);

                LocalDate entrada = LocalDate.now().plusDays(1);

                for (Reserva existente : existentes.getArrayList()) {
                    if (!existente.getFechaSalida().isBefore(entrada)) {
                        entrada = existente.getFechaSalida().plusDays(1);
                    }
                }

                LocalDate salida = entrada.plusDays(2);

                Reserva solicitud = new Reserva(
                        0,
                        entrada,
                        salida,
                        1,
                        LocalDate.now(),
                        EstadoReserva.CONFIRMADA,
                        referencia.getHotel(),
                        referencia.getCliente(),
                        referencia.getHabitaciones()
                );

                int id = dao.insertar(solicitud);

                Reserva guardada = dao.buscarPorId(id);

                System.out.println("Reserva guardada: " + guardada);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JPAUtil.close();
        }
    }
}