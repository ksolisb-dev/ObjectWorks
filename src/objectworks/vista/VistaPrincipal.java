package objectworks.vista;

import java.util.Scanner;
import objectworks.controlador.Controlador;

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

            switch (opcion) {
                case "1": new GestionClientesVista(controlador, teclado).menu(); break;
                case "2": System.out.println("Pendiente (Parte 2)"); break;
                case "3": System.out.println("Pendiente (Parte 3)"); break;
                case "4": System.out.println("Pendiente (Parte 4)"); break;
                case "5": System.out.println("Pendiente (Parte 5)"); break;
                case "0": salir = true; break;
                default: System.out.println("Opcion no valida");
            }
        } while (!salir);
        System.out.println("Hasta luego");
    }
}