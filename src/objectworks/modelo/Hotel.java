package objectworks.modelo;

import java.util.ArrayList;
import java.util.List;

public class Hotel {
    private int id;
    private String nombre;
    private String ciudad;
    private String direccion;
    private int estrellas;
    private String telefono;

    private Lista<Habitacion> habitaciones = new Lista<>();

    public Hotel(int id, String nombre, String ciudad,
                 String direccion, int estrellas, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.estrellas = estrellas;
        this.telefono = telefono;
    }

    //Getters y setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getEstrellas() {
        return estrellas;
    }

    public void setEstrellas(int estrellas) {
        this.estrellas = estrellas;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void agregarHabitacion(Habitacion habitacion) {
        if (habitacion != null && !habitaciones.lista.contains(habitacion)) {
            habitaciones.add(habitacion);
        }
    }

    public Lista<Habitacion> getHabitaciones() {
        return habitaciones;
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", direccion='" + direccion + '\'' +
                ", estrellas=" + estrellas +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}
