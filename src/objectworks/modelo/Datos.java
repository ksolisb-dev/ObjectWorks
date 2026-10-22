package objectworks.modelo;

import objectworks.modelo.enums.CategoriaHabitacion;
import objectworks.modelo.enums.EstadoReserva;
import objectworks.modelo.exceptions.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

public class Datos {
    // Maps referentes a los Hoteles!
    private final Map<Integer, Hotel> hoteles = new HashMap<>();
    private final Map<Integer, Reserva> reservas = new HashMap<>();

    // Clientes guardados por DNI: acceso directo y sin duplicados
    private Map<String, Cliente> clientes;

    public Datos() {
        clientes = new HashMap<>();
        try {
            // Ir añadiendo aqui las cosas que quereis que cargue de prueba
            // Como le puse validador han de pasar por aquí así si peta el catch
            // coge lo que sea que haya petado!
            cargarClientesPrueba();
            cargarHotelesPrueba();
            cargarHabitacionesPrueba();
        } catch (Exception e) {
            System.err.println("Error cargando datos de prueba: " + e.getMessage());
        }
    }

    // ===================== CLIENTES =====================

    public List<Cliente> listarClientes() {
        List<Cliente> lista = new ArrayList<>(clientes.values());
        lista.sort(Comparator.comparing(Cliente::getApellidos));
        return lista;
    }

    public List<Cliente> buscarClientes(String texto) {
        List<Cliente> resultado = new ArrayList<>();
        String busqueda = texto.trim().toLowerCase();
        for (Cliente c : clientes.values()) {
            if (c.getNombre().toLowerCase().contains(busqueda)
                    || c.getApellidos().toLowerCase().contains(busqueda)) {
                resultado.add(c);
            }
        }
        resultado.sort(Comparator.comparing(Cliente::getApellidos));
        return resultado;
    }

    public Cliente getCliente(String dni) throws ClienteNoEncontradoException {
        Cliente c = clientes.get(normalizarDni(dni));
        if (c == null) {
            throw new ClienteNoEncontradoException("No existe ningun cliente con DNI " + dni);
        }
        return c;
    }

    public void anadirCliente(String dni, String nombre, String apellidos, String email, String telefono)
            throws DatoInvalidoException, ClienteDuplicadoException {
        validarDatosCliente(dni, nombre, apellidos, email);
        String clave = normalizarDni(dni);
        if (clientes.containsKey(clave)) {
            throw new ClienteDuplicadoException("Ya existe un cliente con DNI " + dni);
        }
        clientes.put(clave, new Cliente(clave, nombre.trim(), apellidos.trim(), email.trim(), telefono.trim()));
    }

    public void modificarCliente(String dni, String nombre, String apellidos, String email, String telefono)
            throws ClienteNoEncontradoException, DatoInvalidoException {
        Cliente c = getCliente(dni);
        validarDatosCliente(dni, nombre, apellidos, email);
        c.setNombre(nombre.trim());
        c.setApellidos(apellidos.trim());
        c.setEmail(email.trim());
        c.setTelefono(telefono.trim());
    }

    public void eliminarCliente(String dni) throws ClienteNoEncontradoException {
        getCliente(dni); // lanza excepcion si no existe
        // TODO: cuando exista Reserva (Parte 4), impedir borrar si el cliente tiene reservas
        clientes.remove(normalizarDni(dni));
    }

    // ===================== VALIDACIONES =====================

    private void validarDatosCliente(String dni, String nombre, String apellidos, String email)
            throws DatoInvalidoException {
        if (estaVacio(dni) || estaVacio(nombre) || estaVacio(apellidos) || estaVacio(email)) {
            throw new DatoInvalidoException("DNI, nombre, apellidos y email son obligatorios");
        }
        if (!email.trim().matches("^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$")) {
            throw new DatoInvalidoException("El email '" + email + "' no tiene un formato valido");
        }
    }

    private boolean estaVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    private String normalizarDni(String dni) {
        return dni == null ? "" : dni.trim().toUpperCase();
    }

    // ===================== HOTELES =====================

    // Añade un hotel sin más, con la comprobación de las excepciones básicas
    public void addHotel(Hotel hotel) throws DatoObligatorioException {
        if (hotel == null) {
            throw new DatoObligatorioException("El hotel no puede ser nulo");
        }

        Validador.validarCampoObligatorio(hotel.getNombre(), "nombre del hotel");
        Validador.validarCampoObligatorio(hotel.getCiudad(), "ciudad");


        hoteles.put(hotel.getId(), hotel);
    }

    // Lista lo que sea que hay en el Array de hoteles
    public List<Hotel> listarHoteles() {
        return new ArrayList<>(hoteles.values());
    }

    // Función con mínimo control de excepciones para buscar hoteles por ciudad
    public List<Hotel> buscarHotelesPorCiudad(String ciudad) throws DatoObligatorioException {
        Validador.validarCampoObligatorio(ciudad, "ciudad");


        List<Hotel> resultado = new ArrayList<>();

        String busqueda = ciudad.trim().toLowerCase();

        for (Hotel h : hoteles.values()) {
            if (h.getCiudad() != null && h.getCiudad().toLowerCase().contains(busqueda)) {
                resultado.add(h);
            }
        }
        return resultado;
    }

    // Función que trata de buscar en el array de hoteles el que tenga la id que le envias
    public Hotel buscarHotelPorId(int id) throws HotelNoEncontradoException {
        Hotel hotel = hoteles.get(id);

        if (hotel == null) {
            throw new HotelNoEncontradoException("No existe ningún hotel con id " + id);
        }
        return hotel;
    }

    // Funcion que comprueba si dada una habitación y un rango de fechas está disponible.
    // PD: Podría dejarla en private pero por si acaso os la dejo public aunque en teoria solo la usará este codigo.
    public boolean estaDisponible(Habitacion habitacion, LocalDate entrada, LocalDate salida) {

        for (Reserva r : reservas.values()) {
            if (r.getEstado() == EstadoReserva.CANCELADA) {
                continue;
            }


            if (r.getHabitaciones() != null && r.getHabitaciones().contains(habitacion)) {
                // Hay solapamiento de fechas!!
                if (entrada.isBefore(r.getFechaSalida()) && salida.isAfter(r.getFechaEntrada())) {
                    return false;
                }
            }
        }
        return true;
    }

    // Función que dado un hotel y un rango de fechas devuelve la lista de habitaciones disponibles
    public List<Habitacion> getHabitacionesDisponibles(int hotelId, LocalDate entrada, LocalDate salida)
            throws HotelNoEncontradoException, FechasInvalidasException {

        Validador.validarFechas(entrada, salida);
        Hotel hotel = buscarHotelPorId(hotelId);

        List<Habitacion> listaDisponibles = new ArrayList<>();


        for (Habitacion habitacion : hotel.getHabitaciones().getArrayList()) {
            if (estaDisponible(habitacion, entrada, salida)) {
                listaDisponibles.add(habitacion);
            }
        }

        return listaDisponibles;
    }

    // Función clonada de la de arriba pero que itera por categorías sin tener en cuenta fechas
    public List<Habitacion> getHabitacionesPorCategoria(int hotelId, CategoriaHabitacion categoria)
            throws HotelNoEncontradoException {

        Hotel hotel = buscarHotelPorId(hotelId);
        List<Habitacion> habitacionesCategoria = new ArrayList<>();

        for (Habitacion h : hotel.getHabitaciones().getArrayList()) {
            if (h.getCategoria() == categoria) {
                habitacionesCategoria.add(h);
            }
        }

        return habitacionesCategoria;
    }

    // Devuelve un Int diciendo cuantas habitaciones están disponibles sin mas
    public int getNumHabitacionesDisponibles(int hotelId, LocalDate entrada, LocalDate salida)
            throws HotelNoEncontradoException, FechasInvalidasException {

        List<Habitacion> disponibles = getHabitacionesDisponibles(hotelId, entrada, salida);
        return disponibles.size();
    }

    // ===================== DATOS DE PRUEBA =====================


    private void cargarClientesPrueba() throws DatoInvalidoException, ClienteDuplicadoException {
        clientes.put("11111111A", new Cliente("11111111A", "Ana", "Garcia Lopez", "ana@mail.com", "600111111"));
        clientes.put("22222222B", new Cliente("22222222B", "Marc", "Puig Ferrer", "marc@mail.com", "600222222"));
        clientes.put("33333333C", new Cliente("33333333C", "Laura", "Martin Ruiz", "laura@mail.com", "600333333"));
        clientes.put("44444444D", new Cliente("44444444D", "Jordi", "Vidal Soler", "jordi@mail.com", "600444444"));
        clientes.put("55555555E", new Cliente("55555555E", "Sara", "Lopez Navarro", "sara@mail.com", "600555555"));
    }

    public void cargarHotelesPrueba() throws DatoObligatorioException {
        Hotel h1 = new Hotel(1, "Hotel QueSueño", "Barcelona", "Paseo de Gracia 1", 4, "931234567");
        Hotel h2 = new Hotel(2, "Hotel Doritos", "Madrid", "Gran Vía 10", 3, "911234567");
        Hotel h3 = new Hotel(3, "Hotel Cocacola del Mercadona", "Valencia", "Avenida del Puerto 5", 4, "961234567");

        addHotel(h1);
        addHotel(h2);
        addHotel(h3);

    }

    public void cargarHabitacionesPrueba() {
        for (int i = 1; i<=hoteles.size(); i++) {
            Hotel hotel = hoteles.get(i);
            hotel.agregarHabitacion(new HabitacionEstandar(101, "101", "1", 2, 85.0));
            hotel.agregarHabitacion(new HabitacionEstandar(102, "102", "1", 3, 115.0));
            hotel.agregarHabitacion(new HabitacionSuperior(201, "201", "2", 2, 120.0));
            hotel.agregarHabitacion(new HabitacionSuperior(202, "202", "2", 3, 145.0));
            hotel.agregarHabitacion(new Suite(301, "301", "3", 4, 180.0));
        }
    }
}