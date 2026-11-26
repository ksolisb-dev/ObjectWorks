package objectworks.modelo;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import objectworks.modelo.enums.CategoriaHabitacion;

@Entity
@DiscriminatorValue("ESTANDAR")
public class HabitacionEstandar extends Habitacion {

    // Usa el atributo precioBaseNoche heredado de Habitacion (columna precio_base_noche)

    // Constructor vacio que necesita JPA
    protected HabitacionEstandar() {
    }

    public HabitacionEstandar(int id, String numero, String planta,
                              int capacidad, double precioBaseNoche) {
        super(id, numero, planta, capacidad);
        this.precioBaseNoche = precioBaseNoche;
    }

    //Funciones

    @Override
    public CategoriaHabitacion getCategoria() {
        return CategoriaHabitacion.ESTANDAR;
    }

    @Override
    public double calcularPrecioNoche() {
        return precioBaseNoche * 1;
    }

    @Override
    public String toString() {
        return getCategoria() + "{" +
                super.toString() +
                '}';
    }
}
