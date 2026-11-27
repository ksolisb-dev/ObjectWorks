package objectworks.modelo.exceptions;

/** No se puede eliminar un hotel que todavia tiene habitaciones. */
public class HotelConHabitacionesException extends Exception {
    public HotelConHabitacionesException(String mensaje) {
        super(mensaje);
    }
}