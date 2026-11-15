package objectworks.vista;

import objectworks.controlador.ClienteController;
import objectworks.controlador.ReservaController;
import objectworks.controlador.Controlador;
import objectworks.modelo.Habitacion;
import objectworks.modelo.Lista;
import objectworks.modelo.Reserva;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class GestionReservasVista {

    private final ReservaController reservaController;
    // Los clientes ya se leen de la BD a traves de su propio controlador
    private final ClienteController clienteController;
    private final Scanner teclado;

    public GestionReservasVista(
            ReservaController reservaController,
            ClienteController clienteController,
            Scanner teclado) {

        this.reservaController = reservaController;
        this.clienteController = clienteController;
        this.teclado = teclado;
    }

    // ===================== MENÚ =====================

    public void menu() {
        boolean volver = false;

        do {
            System.out.println("\n=== Gestión de reservas ===");
            System.out.println("1. Crear reserva");
            System.out.println("2. Modificar reserva");
            System.out.println("3. Cancelar reserva");
            System.out.println("4. Listar reservas");
            System.out.println("0. Volver");
            System.out.print("Elige una opción: ");

            String opcion = teclado.nextLine().trim();

            try {
                switch (opcion) {
                    case "1":
                        crearReserva();
                        break;

                    case "2":
                        modificarReserva();
                        break;

                    case "3":
                        cancelarReserva();
                        break;

                    case "4":
                        mostrarLista(reservaController.listarReservas());
                        break;

                    case "0":
                        volver = true;
                        break;

                    default:
                        System.out.println("Opción no válida.");
                }

            } catch (NumberFormatException e) {
                System.out.println(
                        "Debes introducir un número entero válido."
                );

            } catch (DateTimeParseException e) {
                System.out.println(
                        "Fecha no válida. Utiliza el formato AAAA-MM-DD."
                );

            } catch (Exception e) {
                // Recoge las excepciones que comunique el controlador.
                System.out.println(
                        "No se ha completado la operación: "
                                + e.getMessage()
                );
            }

        } while (!volver);
    }

    // ===================== CREAR =====================

    private void crearReserva() throws Exception {
        System.out.println("\n--- Crear reserva ---");

        // Seleccionar un cliente existente.
        mostrarLista(clienteController.listarClientes());

        String dni = leerTexto("DNI del cliente: ");

        // Comprobar que existe antes de seguir preguntando.
        System.out.println(clienteController.verCliente(dni));

        // Seleccionar hotel y fechas.
        mostrarLista(reservaController.listarHoteles());

        int hotelId = leerEntero("ID del hotel: ");
        LocalDate entrada = leerFecha("Fecha de entrada");
        LocalDate salida = leerFecha("Fecha de salida");

        // Consultar las opciones disponibles.
        Lista<Habitacion> disponibles =
                reservaController.getHabitacionesDisponibles(
                        hotelId, entrada, salida
                );

        if (disponibles.isEmpty()) {
            System.out.println(
                    "No hay habitaciones disponibles para esas fechas."
            );
            return;
        }

        Lista<Habitacion> seleccionadas =
                seleccionarHabitaciones(disponibles);

        int numPersonas = leerEntero("Número total de personas: ");

        mostrarResumen(
                entrada, salida, numPersonas, seleccionadas
        );

        if (!confirmar("¿Crear la reserva?")) {
            System.out.println("Operación descartada.");
            return;
        }

        Reserva reserva = reservaController.crearReserva(
                dni,
                hotelId,
                entrada,
                salida,
                numPersonas,
                seleccionadas
        );

        System.out.println("Reserva creada correctamente.");
        System.out.println(reserva);
    }

    // ===================== MODIFICAR =====================

    private void modificarReserva() throws Exception {
        System.out.println("\n--- Modificar reserva ---");

        Lista<Reserva> reservas = reservaController.listarReservas();

        if (reservas.isEmpty()) {
            System.out.println("No hay reservas registradas.");
            return;
        }

        mostrarLista(reservas);

        int idReserva = leerEntero("ID de la reserva: ");

        Reserva reserva =
                reservaController.buscarReservaPorId(idReserva);

        System.out.println("Datos actuales:");
        System.out.println(reserva);

        // En esta versión se vuelven a introducir todos estos datos.
        LocalDate entrada = leerFecha("Nueva fecha de entrada");
        LocalDate salida = leerFecha("Nueva fecha de salida");

        Lista<Habitacion> disponibles =
                reservaController.habitacionesDisponiblesParaModificar(
                        idReserva, entrada, salida
                );

        if (disponibles.isEmpty()) {
            System.out.println(
                    "No hay habitaciones disponibles para esas fechas."
            );
            return;
        }

        Lista<Habitacion> seleccionadas =
                seleccionarHabitaciones(disponibles);

        int numPersonas =
                leerEntero("Nuevo número total de personas: ");

        mostrarResumen(
                entrada, salida, numPersonas, seleccionadas
        );

        if (!confirmar("¿Guardar los cambios?")) {
            System.out.println("Operación descartada.");
            return;
        }

        Reserva modificada = reservaController.modificarReserva(
                idReserva,
                entrada,
                salida,
                numPersonas,
                seleccionadas
        );

        System.out.println("Reserva modificada correctamente.");
        System.out.println(modificada);
    }

    // ===================== CANCELAR =====================

    private void cancelarReserva() throws Exception {
        System.out.println("\n--- Cancelar reserva ---");

        Lista<Reserva> reservas = reservaController.listarReservas();

        if (reservas.isEmpty()) {
            System.out.println("No hay reservas registradas.");
            return;
        }

        mostrarLista(reservas);

        int idReserva = leerEntero("ID de la reserva: ");

        Reserva reserva =
                reservaController.buscarReservaPorId(idReserva);

        System.out.println(reserva);

        if (!confirmar("¿Cancelar esta reserva?")) {
            System.out.println("Operación descartada.");
            return;
        }

        reservaController.cancelarReserva(idReserva);

        System.out.println("Reserva cancelada correctamente.");
    }

    // ===================== SELECCIÓN =====================

    private Lista<Habitacion> seleccionarHabitaciones(
            Lista<Habitacion> disponibles) {

        System.out.println("\nHabitaciones disponibles:");

        for (int i = 0; i < disponibles.size(); i++) {
            System.out.println(
                    (i + 1) + ". " + disponibles.getAt(i)
            );
        }

        System.out.println(
                "Selecciona las posiciones separadas por comas."
        );
        System.out.println(
                "Ejemplo: 1,3 para elegir la primera y la tercera."
        );

        String texto = leerTexto("Selección: ");

        Lista<Habitacion> seleccionadas = new Lista<>();

        if (texto.isEmpty()) {
            return seleccionadas;
        }

        for (String parte : texto.split(",", -1)) {
            int posicion = Integer.parseInt(parte.trim());

            if (posicion < 1 || posicion > disponibles.size()) {
                throw new IllegalArgumentException(
                        "La posición " + posicion
                                + " no aparece en la lista."
                );
            }

            // Recuperamos el objeto original; no creamos otra habitación.
            seleccionadas.add(disponibles.getAt(posicion - 1));
        }

        return seleccionadas;
    }

    // ===================== UTILIDADES =====================

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return teclado.nextLine().trim();
    }

    private int leerEntero(String mensaje) {
        return Integer.parseInt(leerTexto(mensaje));
    }

    private LocalDate leerFecha(String mensaje) {
        return LocalDate.parse(
                leerTexto(mensaje + " (AAAA-MM-DD): ")
        );
    }

    private boolean confirmar(String mensaje) {
        return leerTexto(mensaje + " (s/n): ")
                .equalsIgnoreCase("s");
    }

    private void mostrarLista(Lista<?> elementos) {
        if (elementos.isEmpty()) {
            System.out.println("No hay resultados.");
            return;
        }

        for (Object elemento : elementos.getArrayList()) {
            System.out.println(elemento);
        }
    }

    private void mostrarResumen(
            LocalDate entrada,
            LocalDate salida,
            int numPersonas,
            Lista<Habitacion> habitaciones) {

        System.out.println("\n--- Datos propuestos ---");
        System.out.println("Entrada: " + entrada);
        System.out.println("Salida: " + salida);
        System.out.println("Personas: " + numPersonas);
        System.out.println("Habitaciones:");

        mostrarLista(habitaciones);
    }
}