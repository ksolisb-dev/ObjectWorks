package objectworks.vista;

import java.util.Scanner;
import objectworks.controlador.Controlador;
import objectworks.modelo.*;
import objectworks.modelo.enums.CategoriaHabitacion;
import objectworks.modelo.exceptions.DatoObligatorioException;
import objectworks.modelo.exceptions.FechasInvalidasException;
import objectworks.modelo.exceptions.HotelNoEncontradoException;

import java.time.LocalDate;
import java.util.List;

public class VistaPrincipal {
    private Controlador controlador;
    private Scanner teclado = new Scanner(System.in);

    public VistaPrincipal() {
        controlador = new Controlador();
    }

    public void inicio() {
        boolean salir = false;
        do {
            System.out.println("\n=== ObjectWorks - Gestion de Hoteles ===");
            System.out.println("1. Gestion de clientes");
            System.out.println("2. Gestion de hoteles");
            System.out.println("3. Gestion de habitaciones");
            System.out.println("4. Gestion de reservas");
            System.out.println("5. Consulta de reservas");
            System.out.println("0. Salir");
            System.out.print("Elige una opcion: ");
            String opcion = teclado.nextLine();

            if (opcion.equals("1")) {
                new GestionClientesVista(controlador, teclado).menu();
            } else if (opcion.equals("2")) {
                menuHoteles();
            } else if (opcion.equals("3")) {
                System.out.println("Pendiente (Parte 3)");
            } else if (opcion.equals("4")) {
                System.out.println("Pendiente (Parte 4)");
            } else if (opcion.equals("5")) {
                System.out.println("Pendiente (Parte 5)");
            } else if (opcion.equals("0")) {
                salir = true;
            } else {
                System.out.println("Opcion no valida");
            }
        } while (!salir);
        System.out.println("Hasta luego");
    }

    private void menuHoteles() {
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
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (!volver);
    }

    private void listarHoteles() {
        List<Hotel> hoteles = controlador.listarHoteles();
        if (hoteles.isEmpty()) {
            System.out.println("No hay hoteles");
        } else {
            for (Hotel h : hoteles) {
                System.out.println(h);
            }
        }
    }

    private void buscarPorCiudad() throws DatoObligatorioException {
        System.out.print("Ciudad: ");
        String ciudad = teclado.nextLine();
        List<Hotel> hoteles = controlador.buscarHotelesPorCiudad(ciudad);
        if (hoteles.isEmpty()) {
            System.out.println("No se encontraron hoteles");
        } else {
            for (Hotel h : hoteles) {
                System.out.println(h);
            }
        }
    }

    private void habitacionesDisponibles() throws HotelNoEncontradoException, FechasInvalidasException {
        System.out.print("ID del hotel: ");
        int id = Integer.parseInt(teclado.nextLine());
        System.out.print("Fecha entrada (yyyy-MM-dd): ");
        LocalDate entrada = LocalDate.parse(teclado.nextLine());
        System.out.print("Fecha salida (yyyy-MM-dd): ");
        LocalDate salida = LocalDate.parse(teclado.nextLine());

        List<Habitacion> habs = controlador.habitacionesDisponibles(id, entrada, salida);
        if (habs.isEmpty()) {
            System.out.println("No hay habitaciones disponibles");
        } else {
            for (Habitacion h : habs) {
                System.out.println(h);
            }
        }
    }

    private void habitacionesPorCategoria() throws HotelNoEncontradoException {
        System.out.print("ID del hotel: ");
        int id = Integer.parseInt(teclado.nextLine());
        System.out.println("1. Estandar");
        System.out.println("2. Superior");
        System.out.println("3. Suite");
        System.out.print("Categoria: ");
        int cat = Integer.parseInt(teclado.nextLine());

        CategoriaHabitacion categoria = null;
        if (cat == 1) {
            categoria = CategoriaHabitacion.ESTANDAR;
        } else if (cat == 2) {
            categoria = CategoriaHabitacion.SUPERIOR;
        } else if (cat == 3) {
            categoria = CategoriaHabitacion.SUITE;
        } else {
            System.out.println("Categoria no valida");
            return;
        }

        List<Habitacion> habs = controlador.habitacionesPorCategoria(id, categoria);
        if (habs.isEmpty()) {
            System.out.println("No hay habitaciones de esa categoria");
        } else {
            for (Habitacion h : habs) {
                System.out.println(h);
            }
        }
    }

    private void numDisponibles() throws HotelNoEncontradoException, FechasInvalidasException {
        System.out.print("ID del hotel: ");
        int id = Integer.parseInt(teclado.nextLine());
        System.out.print("Fecha entrada (yyyy-MM-dd): ");
        LocalDate entrada = LocalDate.parse(teclado.nextLine());
        System.out.print("Fecha salida (yyyy-MM-dd): ");
        LocalDate salida = LocalDate.parse(teclado.nextLine());

        int num = controlador.numHabitacionesDisponibles(id, entrada, salida);
        System.out.println("Habitaciones disponibles: " + num);
    }
}