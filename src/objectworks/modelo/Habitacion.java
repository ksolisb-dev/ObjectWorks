package objectworks.modelo;

import jakarta.persistence.*;
import objectworks.modelo.enums.CategoriaHabitacion;

/*
 * Mapeo JPA minimo para la relacion Hotel 1:N Habitacion (Parte 2 - Producto 4).
 * Herencia "una tabla por jerarquia" (SINGLE_TABLE), igual que en la BD:
 * todas las subclases van en la tabla habitacion y la columna "categoria"
 * indica de que tipo es cada fila (ESTANDAR, SUPERIOR o SUITE).
 */
@Entity
@Table(name = "habitacion")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "categoria", discriminatorType = DiscriminatorType.STRING)
public abstract class Habitacion {

    // Lado "muchos" de la relacion: esta es la clave ajena id_hotel
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hotel", nullable = false)
    private Hotel hotel;

    // Copia solo lectura de la clave ajena, la siguen usando los DAO JDBC
    @Column(name = "id_hotel", insertable = false, updatable = false)
    private int id_hotel;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_habitacion")
    private int id;

    @Column(name = "numero", nullable = false, length = 10)
    private String numero;

    @Column(name = "planta", nullable = false, length = 10)
    private String planta;

    @Column(name = "capacidad", nullable = false)
    private int capacidad;

    @Column(name = "precio_base_noche", nullable = false)
    protected double precioBaseNoche; // protected para que lo usen las subclases

    // Constructor vacio que necesita JPA
    protected Habitacion() {
    }

    public Habitacion(int id, String numero, String planta,
                      int capacidad) {
        this.id = id;
        this.numero = numero;
        this.planta = planta;
        this.capacidad = capacidad;
    }

    //Getters y setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getPlanta() {
        return planta;
    }

    public void setPlanta(String planta) {
        this.planta = planta;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public int getIdHotel() { return hotel != null ? hotel.getId() : id_hotel; }

    public void setIdHotel(int id_hotel) { this.id_hotel = id_hotel; }

    public Hotel getHotel() { return hotel; }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
        if (hotel != null) {
            this.id_hotel = hotel.getId();
        }
    }

    //Funciones

    public abstract CategoriaHabitacion getCategoria();

    public double getPrecioBaseNoche() {
        return precioBaseNoche;
    }

    public void setPrecioBaseNoche(double precioBaseNoche) {
        this.precioBaseNoche = precioBaseNoche;
    }

    public double calcularPrecioNoche() {
        return precioBaseNoche;
    }

    @Override
    public String toString() {
        return "Habitacion{" +
                "id=" + id +
                ", numero='" + numero + '\'' +
                ", planta='" + planta + '\'' +
                ", capacidad=" + capacidad +
                ", categoria=" + getCategoria() +
                ", precioBaseNoche=" + getPrecioBaseNoche() +
                '}';
    }
}