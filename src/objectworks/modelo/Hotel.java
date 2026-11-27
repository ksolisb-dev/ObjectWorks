package objectworks.modelo;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidad JPA Hotel (Parte 2 - Producto 4).
 *
 * Se mapea con la tabla "hotel" que ya crea sql/objectworks_bd.sql.
 * Relacion 1:N con Habitacion: un hotel tiene muchas habitaciones
 * y cada habitacion pertenece a un solo hotel.
 */
@Entity
@Table(name = "hotel")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // AUTO_INCREMENT de MySQL
    @Column(name = "id_hotel")
    private int id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "ciudad", nullable = false, length = 60)
    private String ciudad;

    @Column(name = "direccion", nullable = false, length = 150)
    private String direccion;

    @Column(name = "estrellas", nullable = false)
    private int estrellas;

    @Column(name = "telefono", length = 15)
    private String telefono;

    /*
     * Lado "uno" de la relacion 1:N.
     * mappedBy = "hotel": la clave ajena (id_hotel) la gestiona el atributo
     * "hotel" de la clase Habitacion, no esta lista.
     * LAZY: las habitaciones solo se cargan cuando se piden (mas eficiente).
     * JPA necesita una interfaz de coleccion (List), por eso aqui no usamos Lista.
     */
    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY)
    @OrderBy("numero ASC")
    private List<Habitacion> habitaciones = new ArrayList<>();

    // Constructor vacio: JPA lo necesita para crear los objetos al leer de la BD
    public Hotel() {
    }

    // Constructor para hoteles nuevos (la BD les asigna el id al insertarlos)
    public Hotel(String nombre, String ciudad, String direccion, int estrellas, String telefono) {
        this(0, nombre, ciudad, direccion, estrellas, telefono);
    }

    // Constructor completo (lo siguen usando los DAO JDBC y los tests)
    public Hotel(int id, String nombre, String ciudad,
                 String direccion, int estrellas, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.estrellas = estrellas;
        this.telefono = telefono;
    }

    // Getters y setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public int getEstrellas() { return estrellas; }
    public void setEstrellas(int estrellas) { this.estrellas = estrellas; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    /**
     * Anade la habitacion y mantiene sincronizados los dos lados
     * de la relacion (la habitacion tambien apunta a este hotel).
     */
    public void agregarHabitacion(Habitacion habitacion) {
        if (habitacion != null && !habitaciones.contains(habitacion)) {
            habitaciones.add(habitacion);
            habitacion.setHotel(this);
        }
    }

    /** Devuelve una copia en nuestra clase Lista para que nadie modifique la relacion desde fuera. */
    public Lista<Habitacion> getHabitaciones() {
        return new Lista<>(habitaciones);
    }

    // Dos hoteles son el mismo si tienen el mismo id (id 0 = todavia no guardado)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hotel)) return false;
        Hotel otro = (Hotel) o;
        return id != 0 && id == otro.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // No incluimos las habitaciones en el toString para no forzar su carga
    @Override
    public String toString() {
        return "ID: " + id +
                " | " + nombre +
                " | " + ciudad + " (" + direccion + ")" +
                " | " + "*".repeat(Math.max(0, estrellas)) +
                " | Tel: " + (telefono == null ? "-" : telefono);
    }
}
