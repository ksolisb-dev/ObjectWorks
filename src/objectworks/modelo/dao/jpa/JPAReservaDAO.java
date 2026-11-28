package objectworks.modelo.dao.jpa;

import jakarta.persistence.EntityTransaction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import objectworks.modelo.Lista;
import objectworks.modelo.Cliente;
import objectworks.modelo.Hotel;
import objectworks.modelo.Habitacion;
import objectworks.modelo.Reserva;
import objectworks.modelo.enums.EstadoReserva;

import objectworks.modelo.exceptions.DAOException;
import objectworks.modelo.util.JPAUtil;

import java.util.List;
import java.util.ArrayList;
import java.util.TreeSet;
import java.time.LocalDate;

public class JPAReservaDAO {

    // CONSULTAS SQL

    // Importante utilizar el FETCH para cerrar el EntityManager antes de volver el resultado de la consulta.
    private static final String JPQL_BASE =
            "SELECT DISTINCT r FROM Reserva r "
            + "JOIN FETCH r.cliente "
            + "JOIN FETCH r.hotel "
            + "LEFT JOIN FETCH r.habitaciones ";

    private static final String JPQL_POR_ID =
            JPQL_BASE + "WHERE r.id = :id";

    private static final String JPQL_LISTAR =
            JPQL_BASE + "ORDER BY r.fechaEntrada DESC, r.id";

    // Consulta para comprobar si existen conflictos en la reserva
    private static final String JPQL_CONFLICTOS =
            "SELECT COUNT(r) FROM Reserva r "
                    + "JOIN r.habitaciones h "
                    + "WHERE h.id IN :ids "
                    + "AND r.estado <> :cancelada "
                    + "AND r.fechaEntrada < :salida "
                    + "AND r.fechaSalida > :entrada";

    // METOD0S

    public Reserva buscarPorId(Integer id) throws DAOException {
        if (id == null || id <= 0) {
            throw new DAOException(
                    "El identificador de reserva debe ser positivo"
            );
        }

        EntityManager em = null;

        try {
            em = JPAUtil.getEntityManager();

            List<Reserva> resultado = em
                    .createQuery(JPQL_POR_ID, Reserva.class)
                    .setParameter("id", id) // asigna el identificador a :id sin concatenarlo en la consulta
                    .getResultList();

            return resultado.isEmpty() ? null : resultado.getFirst();

        } catch (RuntimeException e) {
            throw new DAOException(
                    "Error al buscar la reserva " + id, e
            );
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Lista<Reserva> listarTodos() throws DAOException {
        EntityManager em = null;

        try {
            em = JPAUtil.getEntityManager();

            List<Reserva> resultado = em
                    .createQuery(JPQL_LISTAR, Reserva.class)
                    .getResultList();

            return new Lista<>(resultado);

        } catch (RuntimeException e) {
            throw new DAOException(
                    "Error al listar las reservas", e
            );
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Metodo INSERTAR una reserva:
     * Entre el begin() y commit() comprobamos las referencias, la capacidad y la disponibilidad.
     * Después, persist(nueva) nos permite que Hibernate guarde tanto la fila de reserva como sus asociaciones
     * en reserva_habitación.
     * Si falla la operación antes de confirmarse se ejecuta rollback().
     * @param solicitud
     * @return reserva insertada o transacción cancelada
     * @throws DAOException
     */
    public Integer insertar(Reserva solicitud) throws DAOException {
        List<Integer> ids = validarDatosReserva(solicitud);

        if (solicitud.getId() != 0) {
            throw new DAOException(
                    "Para insertar se necesita una reserva nueva, con ID 0"
            );
        }

        EntityManager em = null;
        EntityTransaction tx = null;

        try {
            em = JPAUtil.getEntityManager();
            tx = em.getTransaction();
            tx.begin();

            // Primero solicitamos un bloqueo de las filas seleccionadas hasta terminar la transacción.
            // Esto nos permite coordinar las operaciones de reserva que lo respeten.
            List<Habitacion> habitaciones = new ArrayList<>();

            for (Integer id : ids) {
                Habitacion habitacion = em.find(
                        Habitacion.class,
                        id,
                        LockModeType.PESSIMISTIC_WRITE
                );

                if (habitacion == null) {
                    throw new DAOException(
                            "No existe la habitación " + id
                    );
                }

                habitaciones.add(habitacion);
            }

            // Recuperamos las referencias dentro de este contexto JPA.
            Cliente cliente = em.find(
                    Cliente.class, solicitud.getCliente().getId()
            );

            Hotel hotel = em.find(
                    Hotel.class, solicitud.getHotel().getId()
            );

            if (cliente == null || hotel == null) {
                throw new DAOException(
                        "El cliente o el hotel ya no existe"
                );
            }

            long capacidadTotal = 0;

            for (Habitacion habitacion : habitaciones) {
                if (habitacion.getIdHotel() != hotel.getId()) {
                    throw new DAOException(
                            "Todas las habitaciones deben pertenecer "
                                    + "al hotel seleccionado"
                    );
                }

                capacidadTotal += habitacion.getCapacidad();
            }

            if (solicitud.getNumPersonas() > capacidadTotal) {
                throw new DAOException(
                        "Las personas superan la capacidad total "
                                + "de las habitaciones"
                );
            }

            Long conflictos = em
                    .createQuery(JPQL_CONFLICTOS, Long.class)
                    .setParameter("ids", ids)
                    .setParameter(
                            "cancelada", EstadoReserva.CANCELADA
                    )
                    .setParameter(
                            "entrada", solicitud.getFechaEntrada()
                    )
                    .setParameter(
                            "salida", solicitud.getFechaSalida()
                    )
                    .getSingleResult();

            if (conflictos > 0) {
                throw new DAOException(
                        "Alguna habitación ya está reservada "
                                + "para esas fechas"
                );
            }

            // El constructor calcula el importe con las tarifas
            // recuperadas de la BD, no con las de la solicitud.
            Reserva nueva = new Reserva(
                    0,
                    solicitud.getFechaEntrada(),
                    solicitud.getFechaSalida(),
                    solicitud.getNumPersonas(),
                    LocalDate.now(),
                    EstadoReserva.CONFIRMADA,
                    hotel,
                    cliente,
                    new Lista<>(habitaciones)
            );

            em.persist(nueva);

            tx.commit();

            // Actualizamos la solicitud solo tras confirmar el guardado.
            solicitud.setId(nueva.getId());

            return nueva.getId();

        } catch (DAOException | RuntimeException e) {
            deshacer(tx, e);

            if (e instanceof DAOException daoException) {
                throw daoException;
            }

            throw new DAOException(
                    "No se ha podido crear la reserva", e
            );

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Metodo ACTUALIZAR una reserva
     * Actualiza una reserva comprobando que todos los datos son los correctos.
     *
     * @param solicitud --> contiene los cambios propuestos por el controlador.
     *        guardada --> entidad recuperada y gestionada por este EntityManager.
     * @throws DAOException
     */
    public void actualizar(Reserva solicitud) throws DAOException {
        List<Integer> ids = validarDatosReserva(solicitud);

        if (solicitud.getId() <= 0) {
            throw new DAOException(
                    "Para modificar se necesita una reserva existente"
            );
        }

        EntityManager em = null;
        EntityTransaction tx = null;

        try {
            em = JPAUtil.getEntityManager();
            tx = em.getTransaction();
            tx.begin();

            // Recuperamos y bloqueamos la reserva que modificaremos.
            Reserva guardada = em.find(
                    Reserva.class,
                    solicitud.getId(),
                    LockModeType.PESSIMISTIC_WRITE
            );

            if (guardada == null) {
                throw new DAOException(
                        "No existe la reserva " + solicitud.getId()
                );
            }

            if (guardada.getEstado() != EstadoReserva.CONFIRMADA) {
                throw new DAOException(
                        "Solo se pueden modificar reservas CONFIRMADA"
                );
            }

            // Bloqueamos las habitaciones seleccionadas en orden de ID.
            List<Habitacion> habitaciones = new ArrayList<>();

            for (Integer id : ids) {
                Habitacion habitacion = em.find(
                        Habitacion.class,
                        id,
                        LockModeType.PESSIMISTIC_WRITE
                );

                if (habitacion == null) {
                    throw new DAOException(
                            "No existe la habitación " + id
                    );
                }

                habitaciones.add(habitacion);
            }

            // La modificación mantiene el cliente y el hotel originales.
            int idHotel = guardada.getHotel().getId();

            if (solicitud.getHotel().getId() != idHotel
                    || solicitud.getCliente().getId()
                    != guardada.getCliente().getId()) {
                throw new DAOException(
                        "Esta operación no permite cambiar "
                                + "el cliente ni el hotel"
                );
            }

            long capacidadTotal = 0;

            for (Habitacion habitacion : habitaciones) {
                if (habitacion.getIdHotel() != idHotel) {
                    throw new DAOException(
                            "Todas las habitaciones deben pertenecer "
                                    + "al hotel de la reserva"
                    );
                }

                capacidadTotal += habitacion.getCapacidad();
            }

            if (solicitud.getNumPersonas() > capacidadTotal) {
                throw new DAOException(
                        "Las personas superan la capacidad total "
                                + "de las habitaciones"
                );
            }

            // La propia reserva no debe considerarse un conflicto.
            Long conflictos = em.createQuery(
                            JPQL_CONFLICTOS + " AND r.id <> :idIgnorada", // Esto evita que la reserva encuentre sus propias habitaciones ocupadas por ella misma.
                            Long.class
                    )
                    .setParameter("ids", ids)
                    .setParameter(
                            "cancelada", EstadoReserva.CANCELADA
                    )
                    .setParameter(
                            "entrada", solicitud.getFechaEntrada()
                    )
                    .setParameter(
                            "salida", solicitud.getFechaSalida()
                    )
                    .setParameter(
                            "idIgnorada", guardada.getId()
                    )
                    .getSingleResult();

            if (conflictos > 0) {
                throw new DAOException(
                        "Alguna habitación ya está reservada "
                                + "para las nuevas fechas"
                );
            }

            // Aplicamos los cambios sobre la entidad gestionada.
            guardada.setFechaEntrada(solicitud.getFechaEntrada());
            guardada.setFechaSalida(solicitud.getFechaSalida());
            guardada.setNumPersonas(solicitud.getNumPersonas());

            // Sustituye las asociaciones y recalcula el importe
            // con las habitaciones recuperadas de la BD.
            guardada.setHabitaciones(new Lista<>(habitaciones));

            tx.commit();

        } catch (DAOException | RuntimeException e) {
            deshacer(tx, e);

            if (e instanceof DAOException daoException) {
                throw daoException;
            }

            throw new DAOException(
                    "No se ha podido modificar la reserva", e
            );

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }


    /**
     * Metodo CANCELAR reserva
     * La cancelación de una reserva cambiará el estado de esta a CANCELADA, conservando la reserva y sus habitaciones asociadas.
     * De esta forma se puede mantener el historial.
     * @param idReserva
     * @throws DAOException
     */
    public void cancelar(int idReserva) throws DAOException {
        if (idReserva <= 0) {
            throw new DAOException(
                    "El identificador de reserva debe ser positivo"
            );
        }

        EntityManager em = null;
        EntityTransaction tx = null;

        try {
            em = JPAUtil.getEntityManager();
            tx = em.getTransaction();
            tx.begin();

            Reserva guardada = em.find(
                    Reserva.class,
                    idReserva,
                    LockModeType.PESSIMISTIC_WRITE
            );

            if (guardada == null) {
                throw new DAOException(
                        "No existe la reserva " + idReserva
                );
            }

            if (guardada.getEstado() != EstadoReserva.CONFIRMADA) {
                throw new DAOException(
                        "Solo se pueden cancelar reservas CONFIRMADA"
                );
            }

            guardada.cancelar();

            tx.commit();

        } catch (DAOException | RuntimeException e) {
            deshacer(tx, e);

            if (e instanceof DAOException daoException) {
                throw daoException;
            }

            throw new DAOException(
                    "No se ha podido cancelar la reserva", e
            );

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Metodo ELIMINAR reserva
     * Mera formalidad. Lo mismo que el metodo cancelar.
     * @param idReserva
     * @throws DAOException
     */
    public void eliminar(Integer idReserva) throws DAOException {
        if (idReserva == null) {
            throw new DAOException(
                    "El identificador de reserva es obligatorio"
            );
        }

        cancelar(idReserva);
    }

    // METOD0S AUXILIARES

    /**
     * Valida la solicitud
     * @param reserva
     * @return Lista de ids
     * @throws DAOException
     */
    private List<Integer> validarDatosReserva(Reserva reserva)
            throws DAOException {

        if (reserva == null) {
            throw new DAOException(
                    "La reserva es obligatoria"
            );
        }

        if (reserva.getFechaEntrada() == null
                || reserva.getFechaSalida() == null
                || !reserva.getFechaSalida()
                .isAfter(reserva.getFechaEntrada())) {
            throw new DAOException(
                    "La salida debe ser posterior a la entrada"
            );
        }

        if (reserva.getNumPersonas() <= 0
                || reserva.getNumPersonas() > 255) {
            throw new DAOException(
                    "El número de personas debe estar entre 1 y 255"
            );
        }

        if (reserva.getCliente() == null
                || reserva.getCliente().getId() <= 0
                || reserva.getHotel() == null
                || reserva.getHotel().getId() <= 0) {
            throw new DAOException(
                    "Debes seleccionar un cliente y un hotel existentes"
            );
        }

        // El TreeSet también nos ordena los ids. Lo vamos a utilizar para poder bloquear siempre las habitaciones en el mismo orden
        TreeSet<Integer> ids = new TreeSet<>();

        for (Habitacion habitacion
                : reserva.getHabitaciones().getArrayList()) {

            if (habitacion == null || habitacion.getId() <= 0) {
                throw new DAOException(
                        "Todas las habitaciones deben tener un ID válido"
                );
            }

            if (!ids.add(habitacion.getId())) {
                throw new DAOException(
                        "No se puede repetir una habitación"
                );
            }
        }

        if (ids.isEmpty()) {
            throw new DAOException(
                    "Debes seleccionar al menos una habitación"
            );
        }

        return new ArrayList<>(ids);
    }

    /**
     * ROLLBACK
     * @param tx
     * @param causa
     */
    private void deshacer(EntityTransaction tx, Throwable causa) {

        if (tx != null && tx.isActive()) {
            try {
                tx.rollback();
            } catch (RuntimeException errorRollback) {
                causa.addSuppressed(errorRollback);
            }
        }
    }
}
