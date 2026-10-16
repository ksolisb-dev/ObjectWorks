package objectworks.controlador;

import java.util.ArrayList;
import java.util.List;
import objectworks.modelo.Cliente;
import objectworks.modelo.ClienteDuplicadoException;
import objectworks.modelo.ClienteNoEncontradoException;
import objectworks.modelo.DatoInvalidoException;
import objectworks.modelo.Datos;

public class Controlador {
    private Datos datos;

    public Controlador() {
        datos = new Datos();
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
}