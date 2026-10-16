package objectworks.modelo;

import java.util.Objects;

public class Cliente {
    private String dni;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;

    // Constructor vacío
    public Cliente() {
    }

    // Constructor completo
    public Cliente(String dni, String nombre, String apellidos, String email, String telefono) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
    }

    // Getters y setters
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    // Dos clientes son el mismo si tienen el mismo DNI
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente)) return false;
        Cliente otro = (Cliente) o;
        return Objects.equals(dni, otro.dni);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dni);
    }

    @Override
    public String toString() {
        return "DNI: " + dni +
               " | " + nombre + " " + apellidos +
               " | Email: " + email +
               " | Tel: " + telefono;
    }
}