package objectworks.modelo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Datos {
    // Clientes guardados por DNI: acceso directo y sin duplicados
    private Map<String, Cliente> clientes;

    public Datos() {
        clientes = new HashMap<>();
        cargarClientesPrueba();
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

    // ===================== DATOS DE PRUEBA =====================

    private void cargarClientesPrueba() {
        clientes.put("11111111A", new Cliente("11111111A", "Ana", "Garcia Lopez", "ana@mail.com", "600111111"));
        clientes.put("22222222B", new Cliente("22222222B", "Marc", "Puig Ferrer", "marc@mail.com", "600222222"));
        clientes.put("33333333C", new Cliente("33333333C", "Laura", "Martin Ruiz", "laura@mail.com", "600333333"));
        clientes.put("44444444D", new Cliente("44444444D", "Jordi", "Vidal Soler", "jordi@mail.com", "600444444"));
        clientes.put("55555555E", new Cliente("55555555E", "Sara", "Lopez Navarro", "sara@mail.com", "600555555"));
    }
}