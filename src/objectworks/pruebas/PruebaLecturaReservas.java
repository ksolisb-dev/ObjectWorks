package objectworks.pruebas;

import objectworks.modelo.Lista;
import objectworks.modelo.Reserva;
import objectworks.modelo.dao.jpa.JPAReservaDAO;
import objectworks.modelo.util.JPAUtil;

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

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JPAUtil.close();
        }
    }
}