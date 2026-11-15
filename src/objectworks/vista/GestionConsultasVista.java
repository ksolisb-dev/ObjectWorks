package objectworks.vista;

import objectworks.controlador.ConsultasController;
import objectworks.modelo.Lista;
import objectworks.modelo.enums.EstadoReserva;
import objectworks.modelo.Reserva;
import objectworks.modelo.exceptions.DAOException;
import objectworks.modelo.exceptions.DatoObligatorioException;

import java.time.LocalDate;
import java.util.Scanner;

public class GestionConsultasVista {
    private final ConsultasController controlador;
    private final Scanner teclado;

    public GestionConsultasVista(ConsultasController controlador, Scanner teclado) {
        this.controlador = controlador;
        this.teclado = teclado;
    }

    public void menu() {
        boolean volver = false;
        do {
            System.out.println("-----Consulta de Reservas-----");
            System.out.println("1. Buscar por Cliente");
            System.out.println("2. Buscar por Hotel");
            System.out.println("3. Buscar por Fecha de Entrada");
            System.out.println("4. Buscar por Fecha de Salida");
            System.out.println("5. Buscar por Estado");
            System.out.println("6. Mostrar todas las reservas");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");
            String opcion = teclado.nextLine();

            try {
                switch (opcion) {
                    case "1": buscarPorCliente(); break;
                    case "2": buscarPorHotel(); break;
                    case "3": buscarPorFechaEntrada(); break;
                    case "4": buscarPorFechaSalida(); break;
                    case "5": buscarPorEstado(); break;
                    case "6": mostrarTodasLasReservas(); break;
                    case "0": volver = true; break;
                    default:
                        System.out.println("Opción no válida");
                }
            } catch (Exception e) {
                // Si el usuario se equivoca con una fecha errónea o un ID que no sea numérico, lo capturamos aquí.
                System.out.println("Error en la busqueda: " + e.getMessage());
            }
        } while (!volver);
    }

    /**
     * Se ha creado este método auxiliar para no estar repitiendo el System.out.print +
     *  teclado.nextLine() en cada método que se ha creado.
     */

    private String pedir(String mensaje) {
        System.out.println(mensaje);
        return teclado.nextLine();
    }

    private void mostrarLista(Lista<Reserva> lista) {
        if (lista.isEmpty()) {
            System.out.println("No existen reservas con esos datos.");
        } else {
            System.out.println("Resultados encontrados: ");
            for (int i = 0; i < lista.size(); i++) {
                Reserva reserva = lista.getAt(i);
                System.out.println(reserva.toString());
            }
        }
    }

    private void buscarPorCliente() throws Exception {
        String dni = pedir("DNI del cliente: ");
        mostrarLista(controlador.buscarReservasPorCliente(dni));
    }

    private void buscarPorHotel() throws Exception {
        int id = Integer.parseInt(pedir("Id del hotel: ").trim());
        mostrarLista(controlador.buscarReservasPorHotel(id));
    }

    private void buscarPorFechaEntrada() throws DatoObligatorioException, DAOException {
        LocalDate fecha = LocalDate.parse(pedir("Fecha entrada (yyyy-MM-dd): ").trim());
        mostrarLista(controlador.buscarReservasPorFechaEntrada(fecha));
    }

    private void buscarPorFechaSalida() throws DatoObligatorioException, DAOException {
        LocalDate fecha = LocalDate.parse(pedir("Fecha Salida (yyyy-MM-dd): ").trim());
        mostrarLista(controlador.buscarReservasPorFechaSalida(fecha));
    }

    private void buscarPorEstado() throws DatoObligatorioException, DAOException {
        System.out.println("Estados disponibles: ");
        System.out.println("1. CONFIRMADA");
        System.out.println("2. CANCELADA");
        System.out.println("3. FINALIZADA");

        int opcionEstado = Integer.parseInt(pedir("Elige el estado: ").trim());

        if (opcionEstado < 1 || opcionEstado > EstadoReserva.values().length) {
            System.out.println("Estado no valido");
            return;
        }

        /**
         * Convertimos el número que ha introducido el usuario con el valor del
         * Enum (CONFIRMADA, CANCELADA, ...)
         */

        EstadoReserva estado = EstadoReserva.values()[opcionEstado -1];
        mostrarLista(controlador.buscarReservasPorEstado(estado));
    }

    private void mostrarTodasLasReservas() throws DAOException {
        mostrarLista(controlador.listarTodasLasReservas());
    }
}
