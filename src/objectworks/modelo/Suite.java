package objectworks.modelo;

import objectworks.modelo.enums.CategoriaHabitacion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SUITE")
public class Suite extends Habitacion {

    public Suite() {}

    public Suite(String numero, String planta, int capacidad, double precioBaseNoche, Hotel hotel) {
        super(numero, planta, capacidad, precioBaseNoche, hotel);
    }

    public Suite(int id, String numero, String planta, int capacidad, double precioBaseNoche) {
        super(numero, planta, capacidad, precioBaseNoche, null);
        setId(id);
    }

    @Override
    public CategoriaHabitacion getCategoria() {
        return CategoriaHabitacion.SUITE;
    }

    @Override
    public double calcularPrecioNoche() {
        return getPrecioBaseNoche() * 2;
    }
}