package objectworks.vista;

import objectworks.controlador.HabitacionController;
import objectworks.modelo.Habitacion;
import objectworks.modelo.Lista;
import objectworks.modelo.exceptions.DAOException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class GestionHabitacionesVista {

    private final HabitacionController controlador;
    private final Scanner teclado;

    public GestionHabitacionesVista(
            HabitacionController controlador, Scanner teclado) {
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
            } catch (NumberFormatException e) {
                System.out.println(
                        "Introduce un numero entero valido.");

            } catch (DateTimeParseException e) {
                System.out.println(
                        "Introduce una fecha valida con formato AAAA-MM-DD.");

            } catch (DAOException e) {
                System.out.println("Error:" + e.getMessage());
            }
        } while (!volver);
    }

    public void listarHabitacionesHotel() throws DAOException {
        System.out.print("Introduce el ID del hotel: ");
        int hotelId = Integer.parseInt(teclado.nextLine());

        Lista<Habitacion> habitaciones =
                controlador.buscarPorHotel(hotelId);

        mostrarHabitaciones(
                habitaciones,
                "No se encontraron habitaciones para ese hotel.");
    }

    public void buscarHabitacionesDisponibles() throws DAOException {
        System.out.print("Introduce el ID del hotel: ");
        int hotelId = Integer.parseInt(teclado.nextLine());

        System.out.print("Fecha de entrada (AAAA-MM-DD): ");
        LocalDate entrada = LocalDate.parse(teclado.nextLine());

        System.out.print("Fecha de salida (AAAA-MM-DD): ");
        LocalDate salida = LocalDate.parse(teclado.nextLine());

        Lista<Habitacion> disponibles =
                controlador.buscarDisponiblesPorHotel(
                        hotelId, entrada, salida);

        mostrarHabitaciones(
                disponibles,
                "No hay habitaciones disponibles para esa busqueda.");
    }

    public void filtrarHabitacionesPorPersonas() throws DAOException {
        System.out.print("Introduce el ID del hotel: ");
        int hotelId = Integer.parseInt(teclado.nextLine());

        System.out.print("Introduce el numero de personas: ");
        int personas = Integer.parseInt(teclado.nextLine());

        Lista<Habitacion> habitaciones =
                controlador.buscarPorHotelNumPersonas(
                        hotelId, personas);

        mostrarHabitaciones(
                habitaciones,
                "No hay habitaciones con capacidad suficiente.");
    }

    private void mostrarHabitaciones(
            Lista<Habitacion> habitaciones, String mensajeVacio) {

        if (habitaciones.isEmpty()) {
            System.out.println(mensajeVacio);
            return;
        }

        for (Habitacion habitacion : habitaciones.getArrayList()) {
            System.out.println(habitacion);
        }
    }
}