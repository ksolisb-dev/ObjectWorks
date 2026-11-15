package objectworks.vista;

import objectworks.controlador.HabitacionController;
import objectworks.modelo.Habitacion;
import objectworks.modelo.Lista;
import objectworks.modelo.enums.CategoriaHabitacion;
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
            System.out.println("2. Buscar disponibles por hotel");
            System.out.println("3. Filtrar por personas dentro de un hotel");
            System.out.println("4. Listar todas las habitaciones");
            System.out.println("5. Buscar por categoria en todos los hoteles");
            System.out.println("6. Filtrar por personas en todos los hoteles");
            System.out.println("7. Buscar disponibles en todos los hoteles");
            System.out.println("8. Buscar por categoria dentro de un hotel");
            System.out.println("9. Buscar habitacion por ID");
            System.out.println("0. Volver");
            System.out.print("Elige una opcion: ");

            String opcion = teclado.nextLine();

            try {
                switch (opcion) {
                    case "1" -> listarHabitacionesHotel();
                    case "2" -> buscarHabitacionesDisponibles();
                    case "3" -> filtrarHabitacionesPorPersonas();
                    case "4" -> listarTodas();
                    case "5" -> buscarPorCategoriaGlobal();
                    case "6" -> buscarPorPersonasGlobal();
                    case "7" -> buscarDisponiblesGlobal();
                    case "8" -> buscarPorCategoriaHotel();
                    case "9" -> buscarHabitacionPorId();
                    case "0" -> volver = true;
                    default -> System.out.println("Opcion no valida.");
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

    private void listarTodas() throws DAOException {
        mostrarHabitaciones(
                controlador.listarHabitaciones(),
                "No hay habitaciones registradas.");
    }

    private void buscarPorCategoriaGlobal() throws DAOException {
        CategoriaHabitacion categoria = leerCategoria();

        mostrarHabitaciones(
                controlador.buscarPorCategoria(categoria),
                "No hay habitaciones de esa categoria.");
    }

    private void buscarPorPersonasGlobal() throws DAOException {
        int personas = leerEntero("Numero de personas: ");

        mostrarHabitaciones(
                controlador.buscarPorNumPersonas(personas),
                "No hay habitaciones con capacidad suficiente.");
    }

    private void buscarDisponiblesGlobal() throws DAOException {
        LocalDate entrada = leerFecha("Fecha de entrada (AAAA-MM-DD): ");
        LocalDate salida = leerFecha("Fecha de salida (AAAA-MM-DD): ");

        mostrarHabitaciones(
                controlador.buscarDisponibles(entrada, salida),
                "No hay habitaciones disponibles para esas fechas.");
    }

    private void buscarPorCategoriaHotel() throws DAOException {
        int idHotel = leerEntero("ID del hotel: ");
        CategoriaHabitacion categoria = leerCategoria();

        mostrarHabitaciones(
                controlador.buscarPorHotelCategoria(idHotel, categoria),
                "No hay habitaciones de esa categoria en ese hotel.");
    }

    private void buscarHabitacionPorId() throws DAOException {
        int id = leerEntero("ID de la habitacion: ");

        Habitacion habitacion = controlador.buscarPorId(id);

        if (habitacion == null) {
            System.out.println("No existe una habitacion con ese ID.");
        } else {
            mostrarHabitacion(habitacion);
        }
    }

    // ---FUNCIONES AUXILIARES---

    private void mostrarHabitaciones(
            Lista<Habitacion> habitaciones, String mensajeVacio) {

        if (habitaciones.isEmpty()) {
            System.out.println(mensajeVacio);
            return;
        }

        for (Habitacion habitacion : habitaciones.getArrayList()) {
            mostrarHabitacion(habitacion);
        }

        System.out.println("Total de resultados: " + habitaciones.size());
    }

    private void mostrarHabitacion(Habitacion habitacion) {
        System.out.printf(
                "ID: %d | Hotel: %d | Habitacion: %s | Planta: %s"
                        + " | Capacidad: %d | Categoria: %s"
                        + " | Precio/noche: %.2f%n",
                habitacion.getId(),
                habitacion.getIdHotel(),
                habitacion.getNumero(),
                habitacion.getPlanta(),
                habitacion.getCapacidad(),
                habitacion.getCategoria(),
                habitacion.calcularPrecioNoche());
    }

    private int leerEntero(String mensaje) {
        System.out.print(mensaje);
        return Integer.parseInt(teclado.nextLine().trim());
    }

    private LocalDate leerFecha(String mensaje) {
        System.out.print(mensaje);
        return LocalDate.parse(teclado.nextLine().trim());
    }

    private CategoriaHabitacion leerCategoria() {
        System.out.println("1. Estandar");
        System.out.println("2. Superior");
        System.out.println("3. Suite");

        int opcion = leerEntero("Categoria: ");

        return switch (opcion) {
            case 1 -> CategoriaHabitacion.ESTANDAR;
            case 2 -> CategoriaHabitacion.SUPERIOR;
            case 3 -> CategoriaHabitacion.SUITE;
            default -> throw new IllegalArgumentException(
                    "Categoria no valida. Selecciona 1, 2 o 3.");
        };
    }
}