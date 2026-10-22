package objectworks.vista;

import java.util.Scanner;
import objectworks.controlador.Controlador;
import objectworks.modelo.*;
import objectworks.modelo.enums.CategoriaHabitacion;
import objectworks.modelo.exceptions.DatoObligatorioException;
import objectworks.modelo.exceptions.FechasInvalidasException;
import objectworks.modelo.exceptions.HotelNoEncontradoException;

import java.time.LocalDate;

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
                new GestionHotelesVista(controlador, teclado).menu();
            } else if (opcion.equals("3")) {
                new GestionHabitacionesVista(controlador, teclado).menu();
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
}