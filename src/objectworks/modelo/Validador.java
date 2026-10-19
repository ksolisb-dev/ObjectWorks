package objectworks.modelo;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class Validador {

    // He consultado a ChatGPT brevemente acerca del patrón RegEx ya que
    // es algo que me ha sido imposible memorizar su uso y función.
    // Prompt usado: "Generame y explicame un patrón RegEx en Java para validar correos electrónicos"
    private static final Pattern PATRÓN_CORREOREGEX = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public static void validarCampoObligatorio(String valor, String nombreCampo) throws DatoObligatorioException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new DatoObligatorioException("El campo '" + nombreCampo + "' es obligatorio.");
        }
    }


}
