package objectworks.modelo;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name="cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_cliente")
    private int id;

    @Column(name = "dni", length = 9, unique=true, nullable=false)
    private String dni;

    @Column(name = "nombre", length = 50, nullable=false)
    private String nombre;

    @Column(name = "apellidos", length = 100, nullable=false)
    private String apellidos;

    @Column( name = "email", length = 100, nullable=false)
    private String email;

    @Column(name = "telefono", length = 15)
    private String telefono;

    // Constructor vacío
    public Cliente() {
    }

    // Constructor para clientes nuevos (la BD les asigna el id al insertarlos)
    public Cliente(String dni, String nombre, String apellidos, String email, String telefono) {
        this(0, dni, nombre, apellidos, email, telefono);
    }

    // Constructor completo (lo usa el DAO al leer de la BD)
    public Cliente(int id, String dni, String nombre, String apellidos, String email, String telefono) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

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
        return "ID: " + id +
               " | DNI: " + dni +
               " | " + nombre + " " + apellidos +
               " | Email: " + email +
               " | Tel: " + (telefono == null ? "-" : telefono);
    }
}