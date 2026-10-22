package objectworks.vista;

import objectworks.controlador.Controlador;
import objectworks.modelo.Habitacion;
import objectworks.modelo.Lista;
import objectworks.modelo.exceptions.FechasInvalidasException;
import objectworks.modelo.exceptions.HotelNoEncontradoException;

import java.time.LocalDate;
import java.util.Scanner;

public class GestionHabitacionesVista {
    private Controlador controlador;
    private Scanner teclado;

    public GestionHabitacionesVista(Controlador controlador, Scanner teclado) {
        this.controlador = controlador;
        this.teclado = teclado;
    }

    public void menu() {
        boolean volver = false;
        do {
            System.out.println("\n--- Gestion de Habitaciones ---");
            System.out.println("1. Listar habitaciones por hotel");
            System.out.println("2. Buscar habitaciones disponibles");
            System.out.println("3. Filtrar habitaciones por personas");
            System.out.println("0. Volver");
            System.out.print("Elige una opcion: ");
            String opcion = teclado.nextLine();

            try {
                if (opcion.equals("1")) {
                    listarHabitacionesHotel();
                } else if (opcion.equals("2")) {
                    buscarHabitacionesDisponibles();
                } else if (opcion.equals("3")) {
                    filtrarHabitacionesPorPersonas();
                } else if (opcion.equals("0")) {
                    volver = true;
                } else {
                    System.out.println("Opcion no valida");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (!volver);
    }

    public void listarHabitacionesHotel() {
        System.out.print("Introduce el ID del hotel: ");
        int hotelId = Integer.parseInt(teclado.nextLine());

        try {
            Lista<Habitacion> habitaciones =
                    controlador.listarHabitacionesHotel(hotelId);

            for (Habitacion habitacion : habitaciones.getArrayList()) {
                System.out.println(habitacion);
            }

        } catch (HotelNoEncontradoException e) {
            System.out.println(e.getMessage());
        }
    }

    public void buscarHabitacionesDisponibles() {
        System.out.print("Introduce el ID del hotel: ");
        int hotelId = Integer.parseInt(teclado.nextLine());

        System.out.print("Fecha de entrada (AAAA-MM-DD): ");
        LocalDate entrada = LocalDate.parse(teclado.nextLine());

        System.out.print("Fecha de salida (AAAA-MM-DD): ");
        LocalDate salida = LocalDate.parse(teclado.nextLine());

        try {
            Lista<Habitacion> disponibles =
                    controlador.getHabitacionesDisponibles(hotelId, entrada, salida);

            if (disponibles.isEmpty()) {
                System.out.println("No hay habitaciones disponibles.");
            } else {
                for (Habitacion habitacion : disponibles.getArrayList()) {
                    System.out.println(habitacion);
                }
            }

        } catch (HotelNoEncontradoException |
                 FechasInvalidasException e) {
            System.out.println(e.getMessage());
        }
    }

    public void filtrarHabitacionesPorPersonas() {
        System.out.print("Introduce el ID del hotel: ");
        int hotelId = Integer.parseInt(teclado.nextLine());

        System.out.print("Introduce el número de personas: ");
        int numPersonas = Integer.parseInt(teclado.nextLine());

        try {
            Lista<Habitacion> habitaciones =
                    controlador.filtrarHabitacionesPorPersonas(hotelId, numPersonas);

            if (habitaciones.isEmpty()) {
                System.out.println(
                        "No hay habitaciones con esa capacidad.");
            } else {
                for (Habitacion habitacion : habitaciones.getArrayList()) {
                    System.out.println(habitacion);
                }
            }

        } catch (HotelNoEncontradoException e) {
            System.out.println(e.getMessage());
        }
    }
}
