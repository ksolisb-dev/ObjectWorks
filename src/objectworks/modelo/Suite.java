package objectworks.modelo;

import objectworks.modelo.enums.CategoriaHabitacion;

public class Suite extends Habitacion {

    private double precioBaseNoche;

    public Suite(int id, String numero, String planta,
                 int capacidad, double precioBaseNoche) {
        super(id, numero, planta, capacidad);
        this.precioBaseNoche = precioBaseNoche;
    }

    //Getters y setters

    public void setPrecioBaseNoche(double precioBaseNoche) {
        this.precioBaseNoche = precioBaseNoche;
    }

    //Funciones

    @Override
    public CategoriaHabitacion getCategoria() {
        return CategoriaHabitacion.SUITE;
    }

    @Override
    public double calcularPrecioNoche() {
        return precioBaseNoche * 2;
    }

    public double getPrecioBaseNoche() {
        return precioBaseNoche;
    }

    @Override
    public String toString() {
        return getCategoria() + "{" +
                super.toString() +
                '}';
    }
}
