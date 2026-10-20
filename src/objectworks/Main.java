package objectworks;

import objectworks.controlador.Controlador;
import objectworks.vista.VistaPrincipal;

public class Main {
    public static void main(String[] args) {
        Controlador controlador = new Controlador();
        VistaPrincipal vista = new VistaPrincipal();
        vista.inicio();
    }
}