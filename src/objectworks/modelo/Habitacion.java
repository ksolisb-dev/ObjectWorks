package objectworks.modelo;

import objectworks.modelo.enums.CategoriaHabitacion;

public abstract class Habitacion {
    private int id;
    private String numero;
    private String planta;
    private int capacidad;

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

    public abstract double calcularPrecioNoche();

    public abstract double getPrecioBaseNoche();

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
