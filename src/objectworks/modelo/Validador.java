package objectworks.modelo;

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






}
