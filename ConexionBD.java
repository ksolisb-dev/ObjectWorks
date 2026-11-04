package objectworks.modelo.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase utilidad que centraliza las conexiones a la base de datos.
 *
 * Los datos de conexion se leen del fichero "db.properties" de la raiz
 * del proyecto (ver db.properties.example). Ese fichero NO se sube a
 * GitHub, asi cada miembro del equipo usa su propio usuario y contrasena.
 * Si no existe, se usan los valores por defecto.
 *
 * Cada operacion pide una conexion nueva con getConexion() y la cierra
 * con try-with-resources, asi no quedan conexiones abiertas.
 */
public final class ConexionBD {

    private static final String FICHERO_CONFIG = "db.properties";

    private static final String URL_DEFECTO =
            "jdbc:mysql://localhost:3306/objectworks?serverTimezone=Europe/Madrid";
    private static final String USUARIO_DEFECTO = "root";
    private static final String PASSWORD_DEFECTO = "";

    private static final String url;
    private static final String usuario;
    private static final String password;

    // Se ejecuta una sola vez, al usar la clase por primera vez
    static {
        Properties props = cargarPropiedades();
        url = props.getProperty("db.url", URL_DEFECTO);
        usuario = props.getProperty("db.usuario", USUARIO_DEFECTO);
        password = props.getProperty("db.password", PASSWORD_DEFECTO);
    }

    // Clase utilidad: no se instancia
    private ConexionBD() {
    }

    /** Devuelve una conexion nueva. Quien la pide es responsable de cerrarla. */
    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(url, usuario, password);
    }

    /** Comprueba si se puede conectar con la base de datos. */
    public static boolean probarConexion() {
        try (Connection con = getConexion()) {
            return con.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    /** Deshace la transaccion sin lanzar excepciones (para usar dentro de un catch). */
    public static void rollback(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException ignorada) {
                // Si falla el rollback no podemos hacer nada mas; la conexion se cerrara igualmente
            }
        }
    }

    private static Properties cargarPropiedades() {
        Properties props = new Properties();
        // 1) Fichero en la raiz del proyecto
        try (InputStream in = new FileInputStream(FICHERO_CONFIG)) {
            props.load(in);
            return props;
        } catch (IOException noEsta) {
            // 2) Si no esta, lo buscamos dentro del classpath (carpeta src)
        }
        try (InputStream in = ConexionBD.class.getClassLoader().getResourceAsStream(FICHERO_CONFIG)) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException ignorada) {
            // Sin fichero: se usan los valores por defecto
        }
        return props;
    }
}
