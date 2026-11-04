# ObjectWorks
'Producto 3 FP447 - Gestión de hoteles' (persistencia con JDBC + MySQL, patrones DAO y Factory)

## Cómo ponerlo en marcha
1. Instala MySQL 8 (y MySQL Workbench si quieres interfaz gráfica).
2. Ejecuta el script `sql/objectworks_bd.sql` (crea la BD `objectworks`, tablas, procedimientos y datos de prueba).
3. Copia `db.properties.example` como `db.properties` en la raíz del proyecto y pon tu usuario y contraseña de MySQL.
4. Abre el proyecto en IntelliJ. El driver `mysql-connector-j` está configurado como librería Maven del proyecto: si sale en rojo, *File > Project Structure > Libraries* y pulsa el botón de refrescar/descargar.
5. Ejecuta `objectworks.Main`.

## Estructura de la persistencia
```
modelo/util/ConexionBD.java        Clase utilidad de conexiones (lee db.properties)
modelo/dao/DAO.java                Interfaz genérica CRUD  DAO<T, K>
modelo/dao/ClienteDAO.java         Interfaz DAO de clientes
modelo/dao/DAOFactory.java         Patrón Factory (elige el motor de BD)
modelo/dao/mysql/MySQLDAOFactory   Fábrica concreta MySQL
modelo/dao/mysql/MySQLClienteDAO   Implementación JDBC (PreparedStatement + procedimientos + transacciones)
controlador/ClienteController      Un controlador por gestión
```

## Cómo añadir vuestra parte (hoteles, habitaciones, reservas)
1. Crea tu rama desde `main`.
2. Tablas: ya están en `sql/objectworks_bd.sql`. Añade al final de la sección 3 tus procedimientos (`sp_insertar_...`, etc.).
3. Crea `XxxDAO extends DAO<Xxx, Integer>` en `modelo/dao` y `MySQLXxxDAO` en `modelo/dao/mysql` (copia el patrón de `MySQLClienteDAO`).
4. Añade `getXxxDAO()` en `DAOFactory` (abstracto) y en `MySQLDAOFactory`.
5. Crea `XxxController` y pásaselo a tu vista desde `Main` / `VistaPrincipal`, quitando tu parte del `Controlador` antiguo.
