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
    private static EntityManagerFactory emf;

    public static boolean inicializar() {

        if(emf != null && !emf.isOpen()) {
            return true;
        }

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
        map.put("hibernate.show_sql", "true");
        map.put("hibernate.hbm2ddl.auto", "update");
        map.put("hibernate.format_sql", "true");
        map.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

        //3. Creamos el EntityManagerFactory para pasarle las propiedades
        try {
            emf = Persistence.createEntityManagerFactory("objectworksPU", map);
            return true;
        } catch (RuntimeException e) {
            System.err.println("Error al iniciar JPA: " + e.getMessage());
            emf = null;
            return false;
        }
    }

    public static EntityManager getEntityManager() {
        if (emf == null || !emf.isOpen()) {
            throw new IllegalStateException("No se puede establecer la conexion");
        }
        return emf.createEntityManager();
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
        emf = null;
    }
}
