package objectworks.vista;

import objectworks.controlador.Controlador;
import objectworks.modelo.Lista;
import objectworks.modelo.enums.EstadoReserva;
import objectworks.modelo.Reserva;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class GestionConsultasVista {
    private Controlador controlador;
    private Scanner teclado;

    public GestionConsultasVista(Controlador controlador, Scanner teclado) {
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
                System.out.println("Error en la busqueda: " + e.getMessage());
            }
        } while (!volver);
    }

    private String pedir(String mensaje) {
        System.out.println(mensaje);
        return teclado.nextLine();
    }

    private void mostrarLista(Lista<Reserva> lista) {
        if (lista.isEmpty()) {
            System.out.println("No existen reservas con esos datos.");
        } else {
            System.out.println("Resultados encontrados: ");
            //TODO: SE NECESITAN DEPENDENCIAS DE LA PARTE 4 - RESERVAS
            // Se necesita un toString para mostrar correctamente la información para compilar
            //for (Reserva reserva : lista) System.out.println(reserva.toString());
        }
    }

    private void buscarPorCliente() {
        String dni = pedir("DNI del cliente: ");
        mostrarLista(controlador.buscarReservasPorCliente(dni));
    }

    private void buscarPorHotel() {
        int id = Integer.parseInt(pedir("Id del hotel: "));
        mostrarLista(controlador.buscarReservassPorHotel(id));
    }

    private void buscarPorFechaEntrada() {
        LocalDate fecha = LocalDate.parse(pedir("Fecha entrada (yyyy-MM-dd): "));
        mostrarLista(controlador.buscarReservasPorFechaEntrada(fecha));
    }

    private void buscarPorFechaSalida() {
        LocalDate fecha = LocalDate.parse(pedir("Fecha Salida (yyyy-MM-dd): "));
        mostrarLista(controlador.buscarReservasPorFechaSalida(fecha));
    }

    private void buscarPorEstado() {
        System.out.println("Estados disponibles: ");
        System.out.println("1. CONFIRMADA");
        System.out.println("2. CANCELADA");
        System.out.println("3. FINALIZADA");
        int opcionEstado = Integer.parseInt("Elige el estado: ");

        if (opcionEstado < 1 || opcionEstado > EstadoReserva.values().length) {
            System.out.println("Estado no valido");
            return;
        }

        EstadoReserva estado = EstadoReserva.values()[opcionEstado -1];
        mostrarLista(controlador.buscarReservasPorEstado(estado));
    }

    private void mostrarTodasLasReservas() {
        mostrarLista(controlador.listarTodasLasReservas());
    }
}
