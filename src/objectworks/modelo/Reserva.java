package objectworks.modelo;

import objectworks.modelo.enums.EstadoReserva;

import java.time.LocalDate;

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
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
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
            importe = calcularImporte();
        }
    }

    //Funciones

    public int calcularNoches() {
        //TODO
        return 0;
    }

    public double calcularImporte() {
        //TODO
        return 0;
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
                ", importe=" + importe +
                ", fechaCreacion=" + fechaCreacion +
                ", estado=" + estado +
                ", hotelId=" + (hotel != null ? hotel.getId() : null) +
                ", clienteId=" + (cliente != null ? cliente.getDni() : null) +
                ", habitaciones=" + habitaciones.size() +
                '}';
    }
}