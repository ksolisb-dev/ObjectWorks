package objectworks.modelo;

import objectworks.modelo.enums.CategoriaHabitacion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SUPERIOR")
public class HabitacionSuperior extends Habitacion {

    public HabitacionSuperior() {}

    public HabitacionSuperior(String numero, String planta, int capacidad, double precioBaseNoche, Hotel hotel) {
        super(numero, planta, capacidad, precioBaseNoche, hotel);
    }

    public HabitacionSuperior(int id, String numero, String planta, int capacidad, double precioBaseNoche) {
        super(numero, planta, capacidad, precioBaseNoche, null);
        setId(id);
    }

    @Override
    public CategoriaHabitacion getCategoria() {
        return CategoriaHabitacion.SUPERIOR;
    }

    @Override
    public double calcularPrecioNoche() {
        return getPrecioBaseNoche() * 1.5;
    }
}