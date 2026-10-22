package objectworks.controlador;

import java.util.ArrayList;
import java.util.List;
import objectworks.modelo.*; // Truco, con esto importa todas las clases que haya en modelo
import objectworks.modelo.enums.CategoriaHabitacion;
import objectworks.modelo.exceptions.*;

import java.time.LocalDate;

public class Controlador {
    private Datos datos;

    public Controlador() {
        this.datos = new Datos(); // ahora datos() ya carga las cosas asi que te borro lo de cargar lo de prueba
    }

    // ===================== CLIENTES =====================
    // Devolvemos texto para que la Vista no toque clases del Modelo

    public List<String> listarClientes() {
        return aTexto(datos.listarClientes());
    }

    public List<String> buscarClientes(String texto) {
        return aTexto(datos.buscarClientes(texto));
    }

    public String verCliente(String dni) throws ClienteNoEncontradoException {
        return datos.getCliente(dni).toString();
    }

    public void anadirCliente(String dni, String nombre, String apellidos, String email, String telefono)
            throws DatoInvalidoException, ClienteDuplicadoException {
        datos.anadirCliente(dni, nombre, apellidos, email, telefono);
    }

    public void modificarCliente(String dni, String nombre, String apellidos, String email, String telefono)
            throws ClienteNoEncontradoException, DatoInvalidoException {
        datos.modificarCliente(dni, nombre, apellidos, email, telefono);
    }

    public void eliminarCliente(String dni) throws ClienteNoEncontradoException {
        datos.eliminarCliente(dni);
    }

    private List<String> aTexto(List<Cliente> lista) {
        List<String> resultado = new ArrayList<>();
        for (Cliente c : lista) {
            resultado.add(c.toString());
        }
        return resultado;
    }

    // ===================== HOTELES =====================
    // No las he testeado mucho....
    public List<Hotel> listarHoteles() {
        return datos.listarHoteles();
    }

    public List<Hotel> buscarHotelesPorCiudad(String ciudad) throws DatoObligatorioException {
        return datos.buscarHotelesPorCiudad(ciudad);
    }

    public List<Habitacion> habitacionesDisponibles(int hotelId, LocalDate entrada, LocalDate salida)
            throws HotelNoEncontradoException, FechasInvalidasException {
        return datos.getHabitacionesDisponibles(hotelId, entrada, salida);
    }

    public List<Habitacion> habitacionesPorCategoria(int hotelId, CategoriaHabitacion categoria)
            throws HotelNoEncontradoException {
        return datos.getHabitacionesPorCategoria(hotelId, categoria);
    }

    public int numHabitacionesDisponibles(int hotelId, LocalDate entrada, LocalDate salida)
            throws HotelNoEncontradoException, FechasInvalidasException {
        return datos.getNumHabitacionesDisponibles(hotelId, entrada, salida);
    }
}