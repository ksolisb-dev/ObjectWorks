package objectworks.modelo.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Clase utilidad de JPA: crea UNA sola EntityManagerFactory para toda la
 * aplicacion (es un objeto pesado) y da un EntityManager nuevo a cada DAO.
 * Las credenciales se leen de db.properties (no se sube a GitHub).
 */
public class JPAUtil {

    // Debe coincidir con <persistence-unit name="..."> de persistence.xml
    private static final String UNIDAD_PERSISTENCIA = "objectworksPU";

    private static EntityManagerFactory emf;

    private JPAUtil() {
    }

    // Se crea la primera vez que se pide (asi un error de conexion no rompe la carga de la clase)
    private static synchronized EntityManagerFactory getFactory() {
        if (emf == null) {
            // 1. Cargar el archivo db.properties
            Properties props = new Properties();
            try (InputStream in = new FileInputStream("db.properties")) {
                props.load(in);
            } catch (IOException e) {
                System.err.println("No se ha encontrado db.properties, se usan los valores por defecto");
            }

            // 2. Pasar las propiedades a JPA (nombres estandar jakarta.persistence.jdbc.*)
            Map<String, String> config = new HashMap<>();
            config.put("jakarta.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver");
            config.put("jakarta.persistence.jdbc.url",
                    props.getProperty("db.url", "jdbc:mysql://localhost:3306/objectworks?serverTimezone=Europe/Madrid"));
            config.put("jakarta.persistence.jdbc.user", props.getProperty("db.usuario", "root"));
            config.put("jakarta.persistence.jdbc.password", props.getProperty("db.password", ""));

            // 3. Crear la factoria con la unidad de persistencia de persistence.xml
            emf = Persistence.createEntityManagerFactory(UNIDAD_PERSISTENCIA, config);
        }
        return emf;
    }

    /** Cada operacion del DAO abre su propio EntityManager y lo cierra al acabar. */
    public static EntityManager getEntityManager() {
        return getFactory().createEntityManager();
    }

    /** Comprueba que JPA puede conectar con MySQL (lo usa Main al arrancar). */
    public static boolean probarConexion() {
        try {
            EntityManager em = getEntityManager();
            try {
                em.createNativeQuery("SELECT 1").getSingleResult();
                return true;
            } finally {
                em.close();
            }
        } catch (RuntimeException e) {
            System.err.println("JPA no puede conectar: " + e.getMessage());
            return false;
        }
    }

    public static synchronized void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}