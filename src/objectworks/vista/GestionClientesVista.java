package objectworks.vista;

import java.util.List;
import java.util.Scanner;
import objectworks.controlador.Controlador;

public class GestionClientesVista {
    private Controlador controlador;
    private Scanner teclado;

    public GestionClientesVista(Controlador controlador, Scanner teclado) {
        this.controlador = controlador;
        this.teclado = teclado;
    }

    public void menu() {
        boolean volver = false;
        do {
            System.out.println("\n--- Gestion de clientes ---");
            System.out.println("1. Listar clientes");
            System.out.println("2. Buscar por nombre o apellido");
            System.out.println("3. Anadir cliente");
            System.out.println("4. Modificar cliente");
            System.out.println("5. Eliminar cliente");
            System.out.println("6. Ver reservas de un cliente");
            System.out.println("0. Volver");
            System.out.print("Elige una opcion: ");
            String opcion = teclado.nextLine();

            switch (opcion) {
                case "1": listar(); break;
                case "2": buscar(); break;
                case "3": anadir(); break;
                case "4": modificar(); break;
                case "5": eliminar(); break;
                case "6": System.out.println("Disponible cuando se implementen las reservas (Parte 4)"); break;
                case "0": volver = true; break;
                default: System.out.println("Opcion no valida");
            }
        } while (!volver);
    }

    private void listar() {
        mostrarLista(controlador.listarClientes());
    }

    private void buscar() {
        String texto = pedir("Nombre o apellido a buscar: ");
        mostrarLista(controlador.buscarClientes(texto));
    }

    private void anadir() {
        String dni = pedir("DNI: ");
        String nombre = pedir("Nombre: ");
        String apellidos = pedir("Apellidos: ");
        String email = pedir("Email: ");
        String telefono = pedir("Telefono: ");
        try {
            controlador.anadirCliente(dni, nombre, apellidos, email, telefono);
            System.out.println("Cliente anadido correctamente");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void modificar() {
        String dni = pedir("DNI del cliente a modificar: ");
        try {
            System.out.println("Datos actuales: " + controlador.verCliente(dni));
            String nombre = pedir("Nuevo nombre: ");
            String apellidos = pedir("Nuevos apellidos: ");
            String email = pedir("Nuevo email: ");
            String telefono = pedir("Nuevo telefono: ");
            controlador.modificarCliente(dni, nombre, apellidos, email, telefono);
            System.out.println("Cliente modificado correctamente");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void eliminar() {
        String dni = pedir("DNI del cliente a eliminar: ");
        try {
            System.out.println("Vas a eliminar: " + controlador.verCliente(dni));
            String confirma = pedir("Seguro? (s/n): ");
            if (confirma.equalsIgnoreCase("s")) {
                controlador.eliminarCliente(dni);
                System.out.println("Cliente eliminado");
            } else {
                System.out.println("Operacion cancelada");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private String pedir(String mensaje) {
        System.out.print(mensaje);
        return teclado.nextLine();
    }

    private void mostrarLista(List<String> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay resultados");
        } else {
            for (String linea : lista) {
                System.out.println(linea);
            }
        }
    }
}