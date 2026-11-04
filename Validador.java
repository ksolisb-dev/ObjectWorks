package objectworks.modelo;

import objectworks.modelo.exceptions.DatoInvalidoException;
import objectworks.modelo.exceptions.DatoObligatorioException;
import objectworks.modelo.exceptions.FechasInvalidasException;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class Validador {

    // He consultado a ChatGPT brevemente acerca del patrón RegEx ya que
    // es algo que me ha sido imposible memorizar su uso y función.
    // Prompt usado: "Generame y explicame un patrón RegEx en Java para validar correos electrónicos"
    private static final Pattern PATRON_CORREOREGEX = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // Función simple que valida si un campo está vacio o nulo directamente
    public static void validarCampoObligatorio(String valor, String nombreCampo) throws DatoObligatorioException {


        if (valor == null || valor.trim().isEmpty()) {
            throw new DatoObligatorioException("El campo '" + nombreCampo + "' es obligatorio.");
        }
    }

    // Función simple que mediante el patrón de RegEx consulta si lo cumple o no.
    public static void validarEmail(String email) throws DatoObligatorioException {

        validarCampoObligatorio(email, "email");

        if (!PATRON_CORREOREGEX.matcher(email).matches()) {
            throw new DatoObligatorioException("El formato del email no es válido: " + email);
        }
    }

    // Función simple que comprueba que las fechas no estén vacias ni imposibles (Entrar después de la salida o cosas asi...)
    public static void validarFechas(LocalDate entrada, LocalDate salida) throws FechasInvalidasException {

        if (entrada == null || salida == null) {
            throw new FechasInvalidasException("Las fechas de entrada y salida son obligatorias.");
        }

        if (!salida.isAfter(entrada)) {
            throw new FechasInvalidasException("La fecha de salida debe ser posterior a la de entrada.");
        }
    }

    // Función mega simple que simplemente vigila que no se hagan reservas a personas en negativo o 0
    public static void validarNumeroPersonas(int numPersonas) throws DatoObligatorioException {

        if (numPersonas <= 0) {
            throw new DatoObligatorioException("El número de personas debe ser mayor que 0.");
        }
    }

    // ===================== CLIENTES =====================

    // DNI (8 numeros + letra) o NIE (X/Y/Z + 7 numeros + letra)
    private static final Pattern PATRON_DNI = Pattern.compile("^[0-9XYZ][0-9]{7}[A-Z]$");

    // Comprueba los datos de un cliente antes de guardarlo en la BD.
    // Los tamanos maximos coinciden con los de la tabla cliente.
    public static void validarDatosCliente(String dni, String nombre, String apellidos,
                                           String email, String telefono) throws DatoInvalidoException {
        if (estaVacio(dni) || estaVacio(nombre) || estaVacio(apellidos) || estaVacio(email)) {
            throw new DatoInvalidoException("DNI, nombre, apellidos y email son obligatorios");
        }
        if (!PATRON_DNI.matcher(dni.trim().toUpperCase()).matches()) {
            throw new DatoInvalidoException("El DNI '" + dni + "' no es valido (ej.: 12345678A)");
        }
        if (!PATRON_CORREOREGEX.matcher(email.trim()).matches()) {
            throw new DatoInvalidoException("El email '" + email + "' no tiene un formato valido");
        }
        validarLongitud(nombre, 50, "nombre");
        validarLongitud(apellidos, 100, "apellidos");
        validarLongitud(email, 100, "email");
        if (telefono != null && !telefono.isBlank() && !telefono.trim().matches("^\\+?[0-9 ]{6,15}$")) {
            throw new DatoInvalidoException("El telefono '" + telefono + "' no es valido");
        }
    }

    private static void validarLongitud(String valor, int maximo, String campo) throws DatoInvalidoException {
        if (valor.trim().length() > maximo) {
            throw new DatoInvalidoException("El campo '" + campo + "' no puede superar " + maximo + " caracteres");
        }
    }

    private static boolean estaVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
