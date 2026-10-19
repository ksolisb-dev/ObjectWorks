import Classes.*;
import Enums.EstadoReserva;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Hotel hotel = new Hotel(1,"Hoteltest","Barcelona","calle hotel",5,"938493907");
        HabitacionEstandar habitacion = new HabitacionEstandar(1,"001","0",3,40.52);
        Cliente cliente = new Cliente(1, "Julia", "Estrada","juliaestrada@gmail.com","938456798","43568789K",LocalDate.of(2026,11,9));
        List<Habitacion> habitacionesReserva = new ArrayList<Habitacion>();
        habitacionesReserva.add(habitacion);
        hotel.agregarHabitacion(habitacion);
        Reserva reserva = new Reserva(1, LocalDate.of(2026,11,10), LocalDate.of(2026,11,15),2,LocalDate.now(), EstadoReserva.CONFIRMADA, hotel, cliente, habitacionesReserva);

        System.out.println(hotel);
        System.out.println(habitacion);
        System.out.println(cliente);
        System.out.println(reserva);
    }
}