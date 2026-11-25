package objectworks.modelo.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;

public class JPAUtil {
    private static final EntityManagerFactory emf;

    static {
        // 1. Cargar el archivo db.properties
        Properties props = new Properties();
        try (InputStream in = new FileInputStream("db.properties")){
            props.load(in);
        }  catch (IOException e) {
            System.err.println("No se ha encontrado el archivo db.properties");
        }

        // 2. Meter las propiedades en un Map para Hibernate
        Map<String,String> map = new HashMap<>();
        map.put("jakarta.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver");
        map.put("jakarta.persistence.jdbc.url", props.getProperty("db.url", "jdbc:mysql://localhost:3306/objectworks?serverTimezone=Europe/Madrid"));
        map.put("jakarta.persistence.jdbc.user", props.getProperty("db.usuario", "root"));
        map.put("jakarta.persistence.jdbc.password", props.getProperty("db.password", ""));

        //3. Creamos el EntityManagerFactory para pasarle las propiedades
        emf = Persistence.createEntityManagerFactory("objectworksPU", map);
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void close() {
        emf.close();
    }
}
