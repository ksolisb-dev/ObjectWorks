package objectworks.modelo;

import objectworks.modelo.enums.CategoriaHabitacion;

public abstract class Habitacion {
    private int id;
    private String numero;
    private String planta;
    private int capacidad;
    protected double precioBaseNoche; // Si no le pongo protected no me lo deja usar
    // Como es un cambio de ultima hora no estoy muy seguro si esto iba aquí, pero juraría que si

    public Habitacion(int id, String nombre, String planta,
                      int capacidad) {
        this.id = id;
        this.numero = nombre;
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
