package objectworks.modelo;

import objectworks.modelo.enums.CategoriaHabitacion;
import objectworks.modelo.enums.EstadoReserva;
import objectworks.modelo.exceptions.*;

import java.util.Comparator;
import java.util.HashMap;
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
            cargarReservasPrueba();
        } catch (Exception e) {
            System.err.println("Error cargando datos de prueba: " + e.getMessage());
        }
    }

    // ===================== CLIENTES =====================

    public Lista<Cliente> listarClientes() {
        Lista<Cliente> lista = new Lista<>(clientes.values());
        lista.sort(Comparator.comparing(Cliente::getApellidos));
        return lista;
    }

    public Lista<Cliente> buscarClientes(String texto) {
        Lista<Cliente> resultado = new Lista<>();
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
    public Lista<Hotel> listarHoteles() {
        return new Lista<>(hoteles.values());
    }

    // Función con mínimo control de excepciones para buscar hoteles por ciudad
    public Lista<Hotel> buscarHotelesPorCiudad(String ciudad) throws DatoObligatorioException {
        Validador.validarCampoObligatorio(ciudad, "ciudad");


        Lista<Hotel> resultado = new Lista<>();

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

    public Lista<Habitacion> getHabitacionesHotel(int hotelId) throws HotelNoEncontradoException{
        Hotel hotel = buscarHotelPorId(hotelId);
        return hotel.getHabitaciones();
    }

    // Función que dado un hotel y un rango de fechas devuelve la lista de habitaciones disponibles
    public Lista<Habitacion> getHabitacionesDisponibles(int hotelId, LocalDate entrada, LocalDate salida)
            throws HotelNoEncontradoException, FechasInvalidasException {

        Validador.validarFechas(entrada, salida);
        Hotel hotel = buscarHotelPorId(hotelId);

        Lista<Habitacion> listaDisponibles = new Lista<>();


        for (Habitacion habitacion : hotel.getHabitaciones().getArrayList()) {
            if (estaDisponible(habitacion, entrada, salida)) {
                listaDisponibles.add(habitacion);
            }
        }

        return listaDisponibles;
    }

    // Función clonada de la de arriba pero que itera por categorías sin tener en cuenta fechas
    public Lista<Habitacion> getHabitacionesPorCategoria(int hotelId, CategoriaHabitacion categoria)
            throws HotelNoEncontradoException {

        Hotel hotel = buscarHotelPorId(hotelId);
        Lista<Habitacion> habitacionesCategoria = new Lista<>();

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

        Lista<Habitacion> disponibles = getHabitacionesDisponibles(hotelId, entrada, salida);
        return disponibles.size();
    }

    public Lista<Habitacion> filtrarHabitacionesPorPersonas(
            int hotelId, int numPersonas)
            throws HotelNoEncontradoException {

        Hotel hotel = buscarHotelPorId(hotelId);
        Lista<Habitacion> resultado = new Lista<>();

        if (numPersonas <= 0) {
            return resultado;
        }

        for (Habitacion habitacion : hotel.getHabitaciones().getArrayList()) {
            if (habitacion.getCapacidad() >= numPersonas) {
                resultado.add(habitacion);
            }
        }

        return resultado;
    }

    // ===================== RESERVAS =====================

    // Comprueba la disponibilidad de una habitación por fechas, sin tener en cuenta ninguna reserva concreta (para crear una nueva reserva)
    // Esta va a retornar su misma función pero con null como reserva a ignorar, para no tener que duplicar código
    public boolean estaDisponible(Habitacion habitacion, LocalDate entrada, LocalDate salida) {

        return estaDisponible(habitacion, entrada, salida, null);
    }

    // Sobrecarga de la función anterior, para poder ignorar una reserva concreta (para modificar una reserva existente)
    public boolean estaDisponible(Habitacion habitacion, LocalDate entrada, LocalDate salida, Reserva reservaIgnorada) {

        for (Reserva r : reservas.values()) {

            // Si la reserva está cancelada, no nos importa, podemos reservar la habitación igualmente
            // Esto va a funcionar ahora si la reserva que estamos ignorando es la misma de nuestra colección en Datos, si creamos un nuevo objeto con el mismo id no va a funcionar, pero para lo que tenemos ahora mismo es suficiente
            if (r == reservaIgnorada || r.getEstado() == EstadoReserva.CANCELADA) {
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

    // Crear reserva con todas las validaciones necesarias
    public Reserva crearReserva(String dniCliente, int hotelId, LocalDate entrada, LocalDate salida, int numPersonas, Lista<Habitacion> habitacionesSeleccionadas)
            throws DatoObligatorioException, ClienteNoEncontradoException, HotelNoEncontradoException, FechasInvalidasException, HabitacionOcupadaException {

        // Validamos fechas y numero de personas
        // Obtener cliente y hotel
        Validador.validarNumeroPersonas(numPersonas);
        Validador.validarFechas(entrada, salida);
        Cliente cliente = getCliente(dniCliente);
        Hotel hotel = buscarHotelPorId(hotelId);

        // Comprobamos que se ha seleccionado al menos una habitación
        if (habitacionesSeleccionadas == null || habitacionesSeleccionadas.isEmpty()) {
            throw new HabitacionOcupadaException("Debe seleccionar al menos una habitación para la reserva.");
        }

        int totalCapacidad = 0;
        Lista<Habitacion> habitacionesComprobadas = new Lista<>(); // Lista para almacenar habitaciones válidas y evitar duplicados

        // Revisamos las habitaciones seleccionadas
        for (Habitacion habitacion : habitacionesSeleccionadas.getArrayList()) {
            if (habitacion == null) { // Comprobamos si la habitación es nula
                throw new HabitacionOcupadaException("Se ha seleccionado una habitación nula.");
            }

            if (!hotel.getHabitaciones().contains(habitacion)) { // Comprobamos si la habitación pertenece al hotel
                throw new HabitacionOcupadaException("La habitación " + habitacion.getNumero() + " no pertenece al hotel seleccionado.");
            }

            if (!estaDisponible(habitacion, entrada, salida)) { // Comprobamos disponibilidad
                throw new HabitacionOcupadaException("La habitación " + habitacion.getNumero() + " no está disponible en las fechas seleccionadas.");
            }

            habitacionesComprobadas.add(habitacion); // Añadimos la habitación a la lista de habitaciones válidas
            totalCapacidad += habitacion.getCapacidad(); // Sumamos la capacidad de la habitación

        }

        // Comprobamos que la capacidad total de las habitaciones seleccionadas es suficiente para el número de personas
        if (numPersonas > totalCapacidad) {
            throw new HabitacionOcupadaException("La capacidad total de las habitaciones seleccionadas (" + totalCapacidad + ") no es suficiente para el número de personas (" + numPersonas + ").");
        }

        // Buscamos un id único para la reserva
        int idReserva = 1;

        while (reservas.containsKey(idReserva)) {
            idReserva++;
        }

        // Creamos la reserva
        Reserva nuevaReserva = new Reserva(idReserva, entrada, salida, numPersonas, LocalDate.now(), EstadoReserva.CONFIRMADA, hotel, cliente, habitacionesComprobadas);
        
        // Guardamos la reserva en el mapa de reservas después de validar todo
        reservas.put(idReserva, nuevaReserva);

        return nuevaReserva;
    }

    // Busca una reserva por su id, lanza excepción si no existe
    public Reserva buscarReservaPorId(int idReserva)
            throws ReservaNoEncontradaException {

        Reserva reserva = reservas.get(idReserva);
        
        if (reserva == null) {
            throw new ReservaNoEncontradaException("No existe ninguna reserva con id " + idReserva);
        }

        return reserva;
    }

    // Cancelar una reserva por Id
    public void cancelarReserva(int idReserva)
            throws ReservaNoEncontradaException {
        
        Reserva reserva = buscarReservaPorId(idReserva);
        reserva.cancelar();
    }

    // Lista todas las reservas
    public Lista<Reserva> listarReservas() {
        Lista<Reserva> lista = new Lista<>(reservas.values());

        lista.sort(Comparator.comparing(Reserva::getId));

        return lista;
    }

    // Modificar una reserva existente, con todas las validaciones necesarias
    public Reserva modificarReserva(int idReserva, LocalDate nuevaEntrada, LocalDate nuevaSalida, int nuevoNumPersonas, Lista<Habitacion> nuevasHabitaciones)
            throws DatoObligatorioException, ReservaNoEncontradaException, FechasInvalidasException, HabitacionOcupadaException {

        // Buscamos la reserva a modificar
        Reserva reserva = buscarReservaPorId(idReserva);
        Hotel hotel = reserva.getHotel();

        // Validamos fechas y numero de personas
        Validador.validarFechas(nuevaEntrada, nuevaSalida);
        Validador.validarNumeroPersonas(nuevoNumPersonas);

        // Comprobamos que se ha seleccionado al menos una habitación
        if (nuevasHabitaciones == null || nuevasHabitaciones.isEmpty()) {
            throw new HabitacionOcupadaException("Debe seleccionar al menos una habitación para la reserva.");
        }

        int totalCapacidad = 0;
        Lista<Habitacion> habitacionesComprobadas = new Lista<>(); // Lista para almacenar habitaciones válidas y evitar duplicados

        // Revisamos las habitaciones seleccionadas
        for (Habitacion habitacion : nuevasHabitaciones.getArrayList()) {
            if (habitacion == null) { // Comprobamos si la habitación es nula
                throw new HabitacionOcupadaException("Se ha seleccionado una habitación nula.");
            }

            if (!hotel.getHabitaciones().contains(habitacion)) { // Comprobamos si la habitación pertenece al hotel
                throw new HabitacionOcupadaException("La habitación " + habitacion.getNumero() + " no pertenece al hotel de la reserva.");
            }

            if (habitacionesComprobadas.contains(habitacion)) { //
                throw new HabitacionOcupadaException("La habitación " + habitacion.getNumero() + " está repetida en la selección.");
            }

            if (!estaDisponible(habitacion, nuevaEntrada, nuevaSalida, reserva)) { // Comprobamos disponibilidad ignorando la reserva actual
                throw new HabitacionOcupadaException("La habitación " + habitacion.getNumero() + " no está disponible en las fechas seleccionadas.");
            }

            habitacionesComprobadas.add(habitacion); // Añadimos la habitación a la lista de habitaciones válidas
            totalCapacidad += habitacion.getCapacidad(); // Sumamos la capacidad de la habitación
        }

        // Comprobamos que la capacidad total de las habitaciones seleccionadas es suficiente para el número de personas
        if (nuevoNumPersonas > totalCapacidad) {
            throw new HabitacionOcupadaException("La capacidad total de las habitaciones seleccionadas (" + totalCapacidad + ") no es suficiente para el número de personas (" + nuevoNumPersonas + ").");
        }

        // Si es correcto, actualizamos la reserva
        reserva.setFechaEntrada(nuevaEntrada);
        reserva.setFechaSalida(nuevaSalida);
        reserva.setNumPersonas(nuevoNumPersonas);

        reserva.getHabitaciones().clear();
        reserva.getHabitaciones().addAll(habitacionesComprobadas);

        // Recalculamos el importe de la reserva después de modificarla
        reserva.recalcularImporte();

        return reserva;

    }

    public Lista<Reserva> listarReservasCliente(String dniCliente)
            throws ClienteNoEncontradoException {

        Cliente cliente = getCliente(dniCliente);
        Lista<Reserva> reservasCliente = new Lista<>();

        for (Reserva reserva : reservas.values()) {
            if (reserva.getCliente().equals(cliente)) {
                reservasCliente.add(reserva);
            }
        }

        reservasCliente.sort(Comparator.comparing(Reserva::getFechaCreacion));

        return reservasCliente;
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

    // Reservas de prueba
    private void cargarReservasPrueba()
            throws DatoObligatorioException, ClienteNoEncontradoException, HotelNoEncontradoException, FechasInvalidasException, HabitacionOcupadaException {
        
        String[] dnis = {"11111111A", "22222222B", "33333333C", "44444444D", "55555555E"};

        int[] idHoteles = {1, 2, 3, 1, 2};
        int[] posicionesHabitaciones = {0, 1, 2, 3, 4}; // Posiciones de las habitaciones en la lista del hotel

        LocalDate entrada = LocalDate.now().plusDays(10);
        LocalDate salida = entrada.plusDays(3);

        for (int i = 0; i < dnis.length; i++) {
            Hotel hotel = buscarHotelPorId(idHoteles[i]);

            Habitacion habitacion = hotel.getHabitaciones().getAt(posicionesHabitaciones[i]);

            Lista<Habitacion> seleccion = new Lista<>();
            seleccion.add(habitacion);

            crearReserva(dnis[i], hotel.getId(), entrada, salida, 2, seleccion);
        }
    }
    
}