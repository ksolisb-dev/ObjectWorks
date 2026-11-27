package objectworks.modelo;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import objectworks.modelo.enums.CategoriaHabitacion;

@Entity
@DiscriminatorValue("SUITE")
public class Suite extends Habitacion {

    // Usa el atributo precioBaseNoche heredado de Habitacion (columna precio_base_noche)

    // Constructor vacio que necesita JPA
    protected Suite() {
    }

    public Suite(int id, String numero, String planta,
                 int capacidad, double precioBaseNoche) {
        super(id, numero, planta, capacidad);
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

    @Override
    public String toString() {
        return getCategoria() + "{" +
                super.toString() +
                '}';
    }
}