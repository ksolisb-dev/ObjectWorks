package objectworks.modelo;

import objectworks.modelo.enums.CategoriaHabitacion;
import jakarta.persistence.*;

@Entity
@Table(name = "habitacion")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "categoria", discriminatorType = DiscriminatorType.STRING)
public abstract class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_habitacion")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hotel", nullable = false)
    private Hotel hotel;

    @Column(nullable = false, length = 10)
    private String numero;

    @Column(nullable = false, length = 10)
    private String planta;

    @Column(nullable = false)
    private int capacidad;

    @Column(name = "precio_base_noche", nullable = false)
    protected double precioBaseNoche;

    public Habitacion() {}

    public Habitacion(String numero, String planta, int capacidad, double precioBaseNoche, Hotel hotel) {
        this.numero = numero;
        this.planta = planta;
        this.capacidad = capacidad;
        this.precioBaseNoche = precioBaseNoche;
        this.hotel = hotel;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getPlanta() { return planta; }
    public void setPlanta(String planta) { this.planta = planta; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public int getIdHotel() {
        return hotel != null ? hotel.getId() : 0;
    }

    public void setIdHotel(int idHotel) {
        hotel.setId(idHotel);
    }

    public double getPrecioBaseNoche() { return precioBaseNoche; }
    public void setPrecioBaseNoche(double precioBaseNoche) { this.precioBaseNoche = precioBaseNoche; }

    public abstract CategoriaHabitacion getCategoria();

    public double calcularPrecioNoche() {
        return precioBaseNoche;
    }

    @Override
    public String toString() {
        return "Habitacion{id=" + id + ", numero='" + numero + "', planta='" + planta +
                "', capacidad=" + capacidad + ", categoria=" + getCategoria() +
                ", precioBaseNoche=" + precioBaseNoche + "}";
    }
}