package objectworks.modelo;

import objectworks.modelo.enums.CategoriaHabitacion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ESTANDAR")
public class HabitacionEstandar extends Habitacion {

    public HabitacionEstandar() {}

    public HabitacionEstandar(String numero, String planta, int capacidad, double precioBaseNoche, Hotel hotel) {
        super(numero, planta, capacidad, precioBaseNoche, hotel);
    }

    public HabitacionEstandar(int id, String numero, String planta, int capacidad, double precioBaseNoche) {
        super(numero, planta, capacidad, precioBaseNoche, null);
        setId(id);
    }

    @Override
    public CategoriaHabitacion getCategoria() {
        return CategoriaHabitacion.ESTANDAR;
    }
}