package objectworks.vista;

import objectworks.controlador.HotelController;
import objectworks.controlador.HabitacionController;
import objectworks.modelo.Habitacion;
import objectworks.modelo.Hotel;
import objectworks.modelo.Lista;
import objectworks.modelo.enums.CategoriaHabitacion;
import objectworks.modelo.exceptions.DatoObligatorioException;
import objectworks.modelo.exceptions.DAOException;
import objectworks.modelo.exceptions.DatoInvalidoException;
import objectworks.modelo.exceptions.HotelConHabitacionesException;
import objectworks.modelo.exceptions.HotelNoEncontradoException;

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
            System.out.println("3. Ver hotel y sus habitaciones");
            System.out.println("4. Alta de hotel");
            System.out.println("5. Modificar hotel");
            System.out.println("6. Eliminar hotel");
            System.out.println("7. Habitaciones disponibles");
            System.out.println("8. Habitaciones por categoria");
            System.out.println("9. Numero de habitaciones disponibles");
            System.out.println("0. Volver");
            System.out.print("Elige una opcion: ");
            String opcion = teclado.nextLine();

            try {
                if (opcion.equals("1")) {
                    listarHoteles();
                } else if (opcion.equals("2")) {
                    buscarPorCiudad();
                } else if (opcion.equals("3")) {
                    verHotelConHabitaciones();
                } else if (opcion.equals("4")) {
                    altaHotel();
                } else if (opcion.equals("5")) {
                    modificarHotel();
                } else if (opcion.equals("6")) {
                    eliminarHotel();
                } else if (opcion.equals("7")) {
                    habitacionesDisponibles();
                } else if (opcion.equals("8")) {
                    habitacionesPorCategoria();
                } else if (opcion.equals("9")) {
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

            } catch (DatoObligatorioException | DatoInvalidoException e) {
                System.out.println("Error de validacion: " + e.getMessage());

            } catch (HotelNoEncontradoException | HotelConHabitacionesException e) {
                System.out.println(e.getMessage());
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

// --- ALTA, MODIFICACION, BAJA Y DETALLE DE HOTELES (Parte 2 - JPA) ---

    private void verHotelConHabitaciones() throws DAOException, HotelNoEncontradoException {
        System.out.print("ID del hotel: ");
        int id = Integer.parseInt(teclado.nextLine());

        Hotel hotel = hotelController.verHotelConHabitaciones(id);
        System.out.println(hotel);
        mostrarHabitaciones(hotel.getHabitaciones(), "Este hotel todavia no tiene habitaciones.");
    }

    private void altaHotel() throws DAOException, DatoInvalidoException {
        System.out.println("-- Alta de hotel --");
        System.out.print("Nombre: ");
        String nombre = teclado.nextLine();
        System.out.print("Ciudad: ");
        String ciudad = teclado.nextLine();
        System.out.print("Direccion: ");
        String direccion = teclado.nextLine();
        System.out.print("Estrellas (1-5): ");
        int estrellas = Integer.parseInt(teclado.nextLine().trim());
        System.out.print("Telefono (opcional): ");
        String telefono = teclado.nextLine();

        int id = hotelController.altaHotel(nombre, ciudad, direccion, estrellas, telefono);
        System.out.println("Hotel guardado con ID " + id);
    }

    private void modificarHotel() throws DAOException, DatoInvalidoException, HotelNoEncontradoException {
        System.out.print("ID del hotel a modificar: ");
        int id = Integer.parseInt(teclado.nextLine());
        Hotel actual = hotelController.verHotel(id);
        System.out.println("Datos actuales: " + actual);
        System.out.println("(Pulsa Intro para dejar el valor actual)");

        String nombre = pedirConValorActual("Nombre", actual.getNombre());
        String ciudad = pedirConValorActual("Ciudad", actual.getCiudad());
        String direccion = pedirConValorActual("Direccion", actual.getDireccion());
        int estrellas = Integer.parseInt(pedirConValorActual("Estrellas (1-5)", String.valueOf(actual.getEstrellas())).trim());
        String telefono = pedirConValorActual("Telefono", actual.getTelefono() == null ? "" : actual.getTelefono());

        hotelController.modificarHotel(id, nombre, ciudad, direccion, estrellas, telefono);
        System.out.println("Hotel modificado.");
    }

    private void eliminarHotel() throws DAOException, HotelNoEncontradoException, HotelConHabitacionesException {
        System.out.print("ID del hotel a eliminar: ");
        int id = Integer.parseInt(teclado.nextLine());
        Hotel hotel = hotelController.verHotel(id);
        System.out.print("Seguro que quieres eliminar " + hotel.getNombre() + "? (s/n): ");
        if (teclado.nextLine().trim().equalsIgnoreCase("s")) {
            hotelController.eliminarHotel(id);
            System.out.println("Hotel eliminado.");
        } else {
            System.out.println("Operacion cancelada.");
        }
    }

    // Muestra el valor actual entre corchetes; si el usuario no escribe nada, se queda el actual
    private String pedirConValorActual(String campo, String valorActual) {
        System.out.print(campo + " [" + valorActual + "]: ");
        String valor = teclado.nextLine();
        return valor.isBlank() ? valorActual : valor;
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