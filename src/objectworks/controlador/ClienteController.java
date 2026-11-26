package objectworks.controlador;

import jakarta.persistence.*;
import objectworks.modelo.Cliente;
import objectworks.modelo.Lista;
import objectworks.modelo.Validador;
import objectworks.modelo.exceptions.ClienteConReservasException;
import objectworks.modelo.exceptions.ClienteDuplicadoException;
import objectworks.modelo.exceptions.ClienteNoEncontradoException;
import objectworks.modelo.exceptions.DatoInvalidoException;
import objectworks.modelo.util.JPAUtil;

import java.util.List;

/**
 * Controlador de la gestion de clientes (Parte 1).
 *
 * Hace de puente entre GestionClientesVista y el Modelo:
 * Devuelve texto para que la Vista no toque clases del Modelo.
 */
public class ClienteController {

    public Lista<String> listarClientes() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Cliente> query = em.createQuery("SELECT c FROM Cliente c ORDER BY c.apellidos", Cliente.class);
            return aTexto(query.getResultList());
        } finally {
            em.close();
        }
    }



    public Lista<String> buscarClientes(String texto) throws DatoInvalidoException {
        if (texto == null || texto.trim().isEmpty()) {
            throw new DatoInvalidoException("Escribe un nombre o apellido para buscar");
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT c FROM Cliente c WHERE LOWER(c.nombre) LIKE :texto OR LOWER(c.apellidos) LIKE :texto ORDER BY c.apellidos";
            TypedQuery<Cliente> query = em.createQuery(jpql, Cliente.class);
            query.setParameter("texto", "%" + texto.trim().toLowerCase() + "%");
            return  aTexto(query.getResultList());
        } finally {
            em.close();
        }
    }

    public String verCliente(String dni) throws ClienteNoEncontradoException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return obtenerPorDni(em, dni).toString();
        } finally {
            em.close();
        }
    }

    public void anadirCliente(String dni, String nombre, String apellidos, String email, String telefono)
            throws DatoInvalidoException, ClienteDuplicadoException {
        Validador.validarDatosCliente(dni, nombre, apellidos, email, telefono);
        String dniNormalizado = normalizarDni(dni);

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            if (buscarPorDni(em, dniNormalizado) != null) {
                throw new ClienteDuplicadoException("Ya existe un cliente con ese dni");
            }

            Cliente nuevo = new Cliente(dniNormalizado, nombre.trim(), apellidos.trim(),
                    email.trim(), limpiar(telefono));
            tx.begin();
            em.persist(nuevo);
            tx.commit();
        } catch (ClienteDuplicadoException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void modificarCliente(String dni, String nombre, String apellidos, String email, String telefono)
            throws ClienteNoEncontradoException, DatoInvalidoException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            Cliente cliente = obtenerPorDni(em, dni);
            Validador.validarDatosCliente(cliente.getDni(), nombre, apellidos, email, telefono);

            //Con una entidad gestionada no hace falta merge:
            //JPA detecta los cambios de los setters y hace el UPDATE en el commit
            tx.begin();
            cliente.setNombre(nombre.trim());
            cliente.setApellidos(apellidos.trim());
            cliente.setEmail(email.trim());
            cliente.setTelefono(limpiar(telefono));
            tx.commit();
        } catch (ClienteNoEncontradoException | DatoInvalidoException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }  finally {
            em.close();
        }
    }

    public void eliminarCliente(String dni) throws ClienteNoEncontradoException, ClienteConReservasException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            Cliente cliente = obtenerPorDni(em, dni);

            //Como Reserva todavía no es una entidad JPA (parte 4) se hace la consulta con SQL nativo
            Number numReservas = (Number) em.createNativeQuery("SELECT COUNT(*) FROM reserva WHERE id_cliente = ?1").setParameter(1, cliente.getId()).getSingleResult();

            //La FK no deja borrar un cliente con reservas: se comprueba antes para dar un mensaje claro
            if (numReservas.longValue() > 0) {
                throw new ClienteConReservasException("No se puede eliminar al cliente " + cliente.getDni() + " porque tiene reservas");
            }

            tx.begin();
            em.remove(cliente);
            tx.commit();
        } catch (ClienteNoEncontradoException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }


    // ===================== AUXILIARES =====================

    private Cliente buscarPorDni(EntityManager em, String dni) {
        List<Cliente> resultado = em.createQuery("SELECT c FROM Cliente c WHERE c.dni = :dni", Cliente.class).setParameter("dni", normalizarDni(dni)).getResultList();
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    private Cliente obtenerPorDni(EntityManager em, String dni) throws ClienteNoEncontradoException {
        Cliente cliente = buscarPorDni(em, dni);
        if (cliente == null) {
            throw new ClienteNoEncontradoException("No existe el cliente con ese dni");
        }
        return cliente;
    }

    private Lista<String> aTexto(List<Cliente> lista) {
        Lista<String> resultado = new Lista<>();
        for (Cliente c : lista) {
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
