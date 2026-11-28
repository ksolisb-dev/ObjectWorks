package objectworks.modelo;

import objectworks.modelo.enums.EstadoReserva;

import java.time.LocalDate;

import java.time.temporal.ChronoUnit;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reserva") // Correspondencia con la tabla reserva
public class Reserva {

    // Generamos el id mediante un AUTO_INCREMENT
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private int id;

    @Column(name = "fecha_entrada",  nullable = false)
    private LocalDate fechaEntrada;

    @Column(name = "fecha_salida",  nullable = false)
    private LocalDate fechaSalida;

    @Column(name = "num_personas",  nullable = false)
    private int numPersonas;

    @Column(name = "importe",  nullable = false, precision = 10, scale = 2)
    private BigDecimal importe = new BigDecimal("0.00");

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    // Guardamos las 3 opciones del enum como strings (CONFIRMADA, CANCELADA, FINALIZADA)
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoReserva estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Respetamos las relaciones entre tablas con el ManyToOne
    @JoinColumn(name = "id_hotel", nullable = false) // JoinColumn nos indica que contiene una FK
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Respetamos las relaciones entre tablas con el ManyToOne
    @JoinColumn(name = "id_cliente",  nullable = false) // JoinColumn nos indica que contiene una FK
    private Cliente cliente;

    // Gestionamos la asociación mediante la tabla intermedia de reserva_habitacion
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "reserva_habitacion",
            joinColumns = @JoinColumn(name = "id_reserva"),
            inverseJoinColumns = @JoinColumn(name = "id_habitacion")
    )
    private List<Habitacion> habitaciones = new ArrayList<>();


    public Reserva(int id, LocalDate fechaEntrada,
                   LocalDate fechaSalida, int numPersonas,
                   LocalDate fechaCreacion, EstadoReserva estado,
                   Hotel hotel, Cliente cliente,
                   Lista<Habitacion> habitaciones)
    {
        this.id = id;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.numPersonas = numPersonas;
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
        this.hotel = hotel;
        this.cliente = cliente;

        setHabitaciones(habitaciones); // nuevo Metod0 añadido que copia las habitaciones y calcula el importe inicial
    }

    protected Reserva() {
        // Lo utiliza JPA al recuperar reservas de la bbdd
    }

    //Getters y setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(LocalDate fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public int getNumPersonas() {
        return numPersonas;
    }

    public void setNumPersonas(int numPersonas) {
        this.numPersonas = numPersonas;
    }

    // Actualizado para JPA y Hibernate
    public double getImporte() {
        return importe.doubleValue();
    }

    // Ahora se calcula con ImporteDecimal
    public void recalcularImporte() {
        this.importe = calcularImporteDecimal();
    }

    public double calcularImporte() {
        return calcularImporteDecimal().doubleValue();
    }

    // Calcula el importe decimal utilizando BigDecimal, así podemos mantener la estructura de devolver un double
    // Calcula el precio total de una reserva con n_noches * precio_por_noche de cada habitación
    private BigDecimal calcularImporteDecimal() {
        int noches = calcularNoches();

        BigDecimal totalPorNoche = BigDecimal.ZERO;

        for (Habitacion habitacion : habitaciones) {
            BigDecimal precioNoche = BigDecimal.valueOf(habitacion.calcularPrecioNoche());

            totalPorNoche = totalPorNoche.add(precioNoche);
        }

        // retornamos el importe total redondeado a 2 decimales con HALF_UP
        return totalPorNoche.multiply(BigDecimal.valueOf(noches).setScale(2, RoundingMode.HALF_UP));
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    // Adaptado para JPA y Hibernate
    public Lista<Habitacion> getHabitaciones() {
        return new Lista<>(habitaciones);
    }

    // Actualiza la lista de habitaciones rellenandola y calculando los importes de nuevo
    public void setHabitaciones(Lista<Habitacion> nuevasHabitaciones) {
        habitaciones.clear();

        if (nuevasHabitaciones != null) {
            for (Habitacion habitacion : nuevasHabitaciones.getArrayList()) {
                if (habitacion != null && !contieneHabitacion(habitacion)){
                    habitaciones.add(habitacion);
                }
            }
        }

        recalcularImporte();
    }

    // Adaptado para JPA y Hibernate
    public void agregarHabitacion(Habitacion habitacion) {
        if (habitacion != null && !contieneHabitacion(habitacion)) {
            habitaciones.add(habitacion);
            recalcularImporte();
        }
    }

    //Funciones

    // Validar que las fechas no sean nulas y que la fecha de salida sea posterior a la fecha de entrada
    // La diferencia entre entrada y salida representa el número de noches de la reserva
    public int calcularNoches() {
        if (fechaEntrada == null || fechaSalida == null) {
            throw new IllegalStateException("Las fechas de entrada y salida deben estar definidas.");
        }

        if (!fechaSalida.isAfter(fechaEntrada)) {
            throw new IllegalArgumentException("La fecha de salida debe ser posterior a la fecha de entrada.");
        }

        return Math.toIntExact(ChronoUnit.DAYS.between(fechaEntrada, fechaSalida));
    }

    // Cambia el estado de la reserva a CANCELADA, de momento tiene sentido.
    public void cancelar() {
        this.estado = EstadoReserva.CANCELADA;
    }

    /**
     * Indica si esta reserva impide reservar la habitacion entre esas fechas.
     * (Antes estaba en Datos.estaDisponible; la BD hace la misma comprobacion en SQL.)
     * - Una reserva cancelada no ocupa nada.
     * - El dia de salida la habitacion ya queda libre: se puede entrar el mismo dia que otro sale.
     */
    public boolean ocupaHabitacion(Habitacion habitacion, LocalDate entrada, LocalDate salida) {
        if (estado == EstadoReserva.CANCELADA || habitacion == null) {
            return false;
        }
        if (!contieneHabitacion(habitacion)) {
            return false;
        }
        // Hay solapamiento de fechas
        return entrada.isBefore(fechaSalida) && salida.isAfter(fechaEntrada);
    }

    /**
     * Se compara por id porque los objetos que vienen de la BD son instancias distintas
     * Actualizado para JPA y Hibernate
     */
    private boolean contieneHabitacion(Habitacion habitacion) {
        if (habitacion == null) {
            return false;
        }

        for (Habitacion actual : habitaciones){
            if (actual == habitacion || (habitacion.getId() > 0 && actual.getId() == habitacion.getId())){
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "id=" + id +
                ", fechaEntrada=" + fechaEntrada +
                ", fechaSalida=" + fechaSalida +
                ", numPersonas=" + numPersonas +
                ", importe=" + getImporte() +
                ", fechaCreacion=" + fechaCreacion +
                ", estado=" + estado +
                ", hotelId=" + (hotel != null ? hotel.getId() : null) +
                ", clienteId=" + (cliente != null ? cliente.getDni() : null) +
                ", habitaciones=" + habitaciones.size() +
                '}';
    }
}