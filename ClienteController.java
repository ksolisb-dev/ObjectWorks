package objectworks.controlador;

import objectworks.modelo.Cliente;
import objectworks.modelo.Lista;
import objectworks.modelo.Validador;
import objectworks.modelo.dao.ClienteDAO;
import objectworks.modelo.dao.DAOFactory;
import objectworks.modelo.exceptions.ClienteConReservasException;
import objectworks.modelo.exceptions.ClienteDuplicadoException;
import objectworks.modelo.exceptions.ClienteNoEncontradoException;
import objectworks.modelo.exceptions.DAOException;
import objectworks.modelo.exceptions.DatoInvalidoException;

/**
 * Controlador de la gestion de clientes (Parte 1).
 *
 * Hace de puente entre GestionClientesVista y el Modelo:
 * valida los datos con Validador y guarda/lee con el ClienteDAO.
 * No sabe que la base de datos es MySQL: recibe el DAO de la DAOFactory.
 * Devuelve texto para que la Vista no toque clases del Modelo.
 */
public class ClienteController {

    private final ClienteDAO clienteDAO;

    public ClienteController(DAOFactory factory) {
        this.clienteDAO = factory.getClienteDAO();
    }

    public Lista<String> listarClientes() throws DAOException {
        return aTexto(clienteDAO.listarTodos());
    }

    public Lista<String> buscarClientes(String texto) throws DAOException, DatoInvalidoException {
        if (texto == null || texto.trim().isEmpty()) {
            throw new DatoInvalidoException("Escribe un nombre o apellido para buscar");
        }
        return aTexto(clienteDAO.buscarPorNombreOApellido(texto.trim()));
    }

    public String verCliente(String dni) throws ClienteNoEncontradoException, DAOException {
        return obtenerPorDni(dni).toString();
    }

    public void anadirCliente(String dni, String nombre, String apellidos, String email, String telefono)
            throws DatoInvalidoException, ClienteDuplicadoException, DAOException {
        Validador.validarDatosCliente(dni, nombre, apellidos, email, telefono);
        String dniNormalizado = normalizarDni(dni);

        if (clienteDAO.buscarPorDni(dniNormalizado) != null) {
            throw new ClienteDuplicadoException("Ya existe un cliente con DNI " + dniNormalizado);
        }

        Cliente nuevo = new Cliente(dniNormalizado, nombre.trim(), apellidos.trim(),
                email.trim(), limpiar(telefono));
        clienteDAO.insertar(nuevo);
    }

    public void modificarCliente(String dni, String nombre, String apellidos, String email, String telefono)
            throws ClienteNoEncontradoException, DatoInvalidoException, DAOException {
        Cliente cliente = obtenerPorDni(dni);
        Validador.validarDatosCliente(cliente.getDni(), nombre, apellidos, email, telefono);

        cliente.setNombre(nombre.trim());
        cliente.setApellidos(apellidos.trim());
        cliente.setEmail(email.trim());
        cliente.setTelefono(limpiar(telefono));
        clienteDAO.actualizar(cliente);
    }

    public void eliminarCliente(String dni)
            throws ClienteNoEncontradoException, ClienteConReservasException, DAOException {
        Cliente cliente = obtenerPorDni(dni);

        // Primera comprobacion aqui; el procedimiento sp_eliminar_cliente y la FK
        // de la tabla reserva lo vuelven a impedir en la BD
        if (clienteDAO.tieneReservas(cliente.getId())) {
            throw new ClienteConReservasException(
                    "No se puede eliminar a " + cliente.getNombre() + " " + cliente.getApellidos()
                    + " porque tiene reservas registradas");
        }
        clienteDAO.eliminar(cliente.getId());
    }

    // ===================== AUXILIARES =====================

    private Cliente obtenerPorDni(String dni) throws ClienteNoEncontradoException, DAOException {
        Cliente cliente = clienteDAO.buscarPorDni(normalizarDni(dni));
        if (cliente == null) {
            throw new ClienteNoEncontradoException("No existe ningun cliente con DNI " + dni);
        }
        return cliente;
    }

    private Lista<String> aTexto(Lista<Cliente> lista) {
        Lista<String> resultado = new Lista<>();
        for (Cliente c : lista.getArrayList()) {
            resultado.add(c.toString());
        }
        return resultado;
    }

    private String normalizarDni(String dni) {
        return dni == null ? "" : dni.trim().toUpperCase();
    }

    private String limpiar(String texto) {
        return texto == null ? null : texto.trim();
    }
}
