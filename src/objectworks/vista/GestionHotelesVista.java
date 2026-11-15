package objectworks.vista;

import objectworks.controlador.HotelController;
import objectworks.controlador.HabitacionController;
import objectworks.modelo.Habitacion;
import objectworks.modelo.Hotel;
import objectworks.modelo.Lista;
import objectworks.modelo.enums.CategoriaHabitacion;
import objectworks.modelo.exceptions.DatoObligatorioException;
import objectworks.modelo.exceptions.DAOException;

import java.time.LocalDate;
import java.util.Scanner;
import java.time.format.DateTimeParseException;

public class GestionHotelesVista {

    private final HotelController hotelController;
    private final HabitacionController habitacionController;
    private Scanner teclado;

    public GestionHotelesVista(HotelController hotelController,HabitacionController habitacionController, Scanner teclado) {
        this.hotelController = hotelController;
        this.habitacionController = habitacionController;
        this.teclado = teclado;
    }

    public void menu() {
        boolean volver = false;
        do {
            System.out.println("\n--- Gestion de Hoteles ---");
            System.out.println("1. Listar hoteles");
            System.out.println("2. Buscar por ciudad");
            System.out.println("3. Habitaciones disponibles");
            System.out.println("4. Habitaciones por categoria");
            System.out.println("5. Numero de habitaciones disponibles");
            System.out.println("0. Volver");
            System.out.print("Elige una opcion: ");
            String opcion = teclado.nextLine();

            try {
                if (opcion.equals("1")) {
                    listarHoteles();
                } else if (opcion.equals("2")) {
                    buscarPorCiudad();
                } else if (opcion.equals("3")) {
                    habitacionesDisponibles();
                } else if (opcion.equals("4")) {
                    habitacionesPorCategoria();
                } else if (opcion.equals("5")) {
                    numDisponibles();
                } else if (opcion.equals("0")) {
                    volver = true;
                } else {
                    System.out.println("Opcion no valida");
                }
            } catch (NumberFormatException e) {
                System.out.println("Introduce un numero entero valido.");

            } catch (DateTimeParseException e) {
                System.out.println(
                        "Introduce una fecha valida con formato AAAA-MM-DD.");

            } catch (DAOException e) {
                System.out.println("Error: " + e.getMessage());

            } catch (DatoObligatorioException e) {
                System.out.println("Error de validacion: " + e.getMessage());
            }
        } while (!volver);
    }

    private void listarHoteles() {
        try {
            Lista<Hotel> hoteles = hotelController.listarHoteles();
            if (hoteles.isEmpty()) {
                System.out.println("No hay hoteles");
            } else {
                for (Hotel h : hoteles.getArrayList()) {
                    System.out.println(h);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al listar: " + e.getMessage());
        }
    }

    private void buscarPorCiudad() throws DatoObligatorioException {
        System.out.print("Ciudad: ");
        String ciudad = teclado.nextLine();
        try {
            Lista<Hotel> hoteles = hotelController.buscarHotelesPorCiudad(ciudad);
            if (hoteles.isEmpty()) {
                System.out.println("No se encontraron hoteles");
            } else {
                for (Hotel h : hoteles.getArrayList()) {
                    System.out.println(h);
                }
            }
        } catch (DatoObligatorioException e) {
            System.out.println("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }

// --- METOD0S DE HOTEL CON GESTIÓN DE HABITACIONES ---

    private void habitacionesDisponibles() throws DAOException {
        System.out.print("ID del hotel: ");
        int idHotel = Integer.parseInt(teclado.nextLine());

        System.out.print("Fecha entrada (AAAA-MM-DD): ");
        LocalDate entrada = LocalDate.parse(teclado.nextLine());

        System.out.print("Fecha salida (AAAA-MM-DD): ");
        LocalDate salida = LocalDate.parse(teclado.nextLine());

        Lista<Habitacion> habitaciones = habitacionController.buscarDisponiblesPorHotel(idHotel, entrada, salida);

        mostrarHabitaciones(habitaciones, "No hay habitaciones disponibles para esa busqueda.");
    }

    private void habitacionesPorCategoria() throws DAOException {
        System.out.print("ID del hotel: ");
        int idHotel = Integer.parseInt(teclado.nextLine());

        System.out.println("1. Estandar");
        System.out.println("2. Superior");
        System.out.println("3. Suite");
        System.out.print("Categoria: ");

        String opcion = teclado.nextLine();

        CategoriaHabitacion categoria;

        switch (opcion) {
            case "1" -> categoria = CategoriaHabitacion.ESTANDAR;
            case "2" -> categoria = CategoriaHabitacion.SUPERIOR;
            case "3" -> categoria = CategoriaHabitacion.SUITE;
            default -> {
                System.out.println("Categoria no valida.");
                return;
            }
        }

        Lista<Habitacion> habitaciones = habitacionController.buscarPorHotelCategoria(idHotel, categoria);

        mostrarHabitaciones(habitaciones, "No se encontraron habitaciones de esa categoria.");
    }

    private void numDisponibles() throws DAOException {
        System.out.print("ID del hotel: ");
        int idHotel = Integer.parseInt(teclado.nextLine());

        System.out.print("Fecha entrada (AAAA-MM-DD): ");
        LocalDate entrada = LocalDate.parse(teclado.nextLine());

        System.out.print("Fecha salida (AAAA-MM-DD): ");
        LocalDate salida = LocalDate.parse(teclado.nextLine());

        Lista<Habitacion> disponibles = habitacionController.buscarDisponiblesPorHotel(idHotel, entrada, salida);

        System.out.println("Habitaciones disponibles: " + disponibles.size());
    }

    //AUXILIARES HABITACIONES

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
