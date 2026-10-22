package objectworks.modelo;

import objectworks.modelo.enums.EstadoReserva;

import java.time.LocalDate;

import java.time.temporal.ChronoUnit;

public class Reserva {
    private int id;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private int numPersonas;
    private double importe;
    private LocalDate fechaCreacion;

    private EstadoReserva estado;
    private Hotel hotel;
    private Cliente cliente;
    private Lista<Habitacion> habitaciones = new Lista<>();

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

        if (habitaciones != null) {
            this.habitaciones.addAll(habitaciones);
        }

        this.importe = calcularImporte(); // Calculamos ahora el importe inicial de la reserva basado en las habitaciones y las fechas.
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

    public double getImporte() {
        this.importe = calcularImporte();
        return this.importe;
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

    public Lista<Habitacion> getHabitaciones() {
        return this.habitaciones;
    }

    public void agregarHabitacion(Habitacion habitacion) {
        if (habitacion != null && !habitaciones.contains(habitacion)) {
            habitaciones.add(habitacion);
            this.importe = calcularImporte();
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

    // Calcula el precio total de una reserva con n_noches * precio_por_noche de cada habitación.
    public double calcularImporte() {
        int noches = calcularNoches();
        double precioTotalPorNoche = 0.0;

        for (Habitacion habitacion : habitaciones) {
            precioTotalPorNoche += habitacion.calcularPrecioPorNoche();
        }

        return precioTotalPorNoche * noches;
    }

    public void cancelar() {
        //TODO
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