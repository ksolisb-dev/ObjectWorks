-- =====================================================================
--  ObjectWorks - Producto 3 (FP447)
--  Base de datos MySQL para la gestion de una cadena de hoteles
--
--  Contenido:
--    1. Creacion de la base de datos
--    2. Tablas (PK autonumericas, FK, UNIQUE, CHECK e indices)
--    3. Procedimientos almacenados (DML)
--    4. Datos de prueba
--
--  Como ejecutarlo: abrir en MySQL Workbench y pulsar el rayo,
--  o desde terminal:  mysql -u root -p < sql/objectworks_bd.sql
--
--  Las transacciones (START/COMMIT/ROLLBACK) se controlan desde Java
--  (setAutoCommit(false) + commit/rollback en los DAO). Por eso los
--  procedimientos NO hacen COMMIT por su cuenta: asi varias llamadas
--  pueden formar parte de la misma transaccion.
-- =====================================================================

DROP DATABASE IF EXISTS objectworks;
CREATE DATABASE objectworks
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_spanish_ci;
USE objectworks;

-- =====================================================================
-- 2. TABLAS
-- =====================================================================

-- ---------------------------------------------------------------------
-- CLIENTE
-- id_cliente: clave primaria autonumerica (identificador interno).
-- dni: clave alternativa (UNIQUE), es lo que usa el usuario para buscar.
-- ---------------------------------------------------------------------
CREATE TABLE cliente (
    id_cliente  INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    dni         VARCHAR(9)    NOT NULL,
    nombre      VARCHAR(50)   NOT NULL,
    apellidos   VARCHAR(100)  NOT NULL,
    email       VARCHAR(100)  NOT NULL,
    telefono    VARCHAR(15)   NULL,
    CONSTRAINT pk_cliente      PRIMARY KEY (id_cliente),
    CONSTRAINT uq_cliente_dni  UNIQUE (dni),
    CONSTRAINT ck_cliente_email CHECK (email LIKE '%_@_%._%')
) ENGINE = InnoDB;

-- Busqueda por nombre o apellido (opcion "Buscar cliente")
CREATE INDEX idx_cliente_apellidos ON cliente (apellidos, nombre);

-- ---------------------------------------------------------------------
-- HOTEL
-- ---------------------------------------------------------------------
CREATE TABLE hotel (
    id_hotel    INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(100)  NOT NULL,
    ciudad      VARCHAR(60)   NOT NULL,
    direccion   VARCHAR(150)  NOT NULL,
    estrellas   TINYINT UNSIGNED NOT NULL,
    telefono    VARCHAR(15)   NULL,
    CONSTRAINT pk_hotel            PRIMARY KEY (id_hotel),
    CONSTRAINT ck_hotel_estrellas  CHECK (estrellas BETWEEN 1 AND 5)
) ENGINE = InnoDB;

-- Busqueda de hoteles por ciudad
CREATE INDEX idx_hotel_ciudad ON hotel (ciudad);

-- ---------------------------------------------------------------------
-- HABITACION
-- La herencia de Java (HabitacionEstandar, HabitacionSuperior, Suite)
-- se resuelve con una sola tabla y la columna "categoria"
-- (estrategia "una tabla por jerarquia"), porque las subclases
-- no tienen atributos propios, solo cambia el calculo del precio.
-- ---------------------------------------------------------------------
CREATE TABLE habitacion (
    id_habitacion      INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    id_hotel           INT UNSIGNED  NOT NULL,
    numero             VARCHAR(10)   NOT NULL,
    planta             VARCHAR(10)   NOT NULL,
    capacidad          TINYINT UNSIGNED NOT NULL,
    categoria          ENUM('ESTANDAR', 'SUPERIOR', 'SUITE') NOT NULL,
    precio_base_noche  DECIMAL(8,2)  NOT NULL,
    CONSTRAINT pk_habitacion             PRIMARY KEY (id_habitacion),
    CONSTRAINT fk_habitacion_hotel       FOREIGN KEY (id_hotel)
        REFERENCES hotel (id_hotel)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    -- En un mismo hotel no puede haber dos habitaciones con el mismo numero
    CONSTRAINT uq_habitacion_hotel_num   UNIQUE (id_hotel, numero),
    CONSTRAINT ck_habitacion_capacidad   CHECK (capacidad > 0),
    CONSTRAINT ck_habitacion_precio      CHECK (precio_base_noche >= 0)
) ENGINE = InnoDB;

CREATE INDEX idx_habitacion_categoria ON habitacion (id_hotel, categoria);

-- ---------------------------------------------------------------------
-- RESERVA
-- ON DELETE RESTRICT en cliente: no se puede borrar un cliente que
-- tenga reservas (requisito "eliminar siempre que sea posible segun
-- las restricciones de la base de datos").
-- ---------------------------------------------------------------------
CREATE TABLE reserva (
    id_reserva      INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    id_cliente      INT UNSIGNED  NOT NULL,
    id_hotel        INT UNSIGNED  NOT NULL,
    fecha_entrada   DATE          NOT NULL,
    fecha_salida    DATE          NOT NULL,
    num_personas    TINYINT UNSIGNED NOT NULL,
    importe         DECIMAL(10,2) NOT NULL DEFAULT 0,
    fecha_creacion  DATE          NOT NULL DEFAULT (CURRENT_DATE),
    estado          ENUM('CONFIRMADA', 'CANCELADA', 'FINALIZADA') NOT NULL DEFAULT 'CONFIRMADA',
    CONSTRAINT pk_reserva          PRIMARY KEY (id_reserva),
    CONSTRAINT fk_reserva_cliente  FOREIGN KEY (id_cliente)
        REFERENCES cliente (id_cliente)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_reserva_hotel    FOREIGN KEY (id_hotel)
        REFERENCES hotel (id_hotel)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_reserva_fechas   CHECK (fecha_salida > fecha_entrada),
    CONSTRAINT ck_reserva_personas CHECK (num_personas > 0),
    CONSTRAINT ck_reserva_importe  CHECK (importe >= 0)
) ENGINE = InnoDB;

-- Consultas de reservas por fecha y por estado
CREATE INDEX idx_reserva_fechas ON reserva (fecha_entrada, fecha_salida);
CREATE INDEX idx_reserva_estado ON reserva (estado);

-- ---------------------------------------------------------------------
-- RESERVA_HABITACION
-- Relacion N:M: una reserva puede incluir varias habitaciones y una
-- habitacion aparece en muchas reservas (en fechas distintas).
-- ---------------------------------------------------------------------
CREATE TABLE reserva_habitacion (
    id_reserva     INT UNSIGNED NOT NULL,
    id_habitacion  INT UNSIGNED NOT NULL,
    CONSTRAINT pk_reserva_habitacion PRIMARY KEY (id_reserva, id_habitacion),
    CONSTRAINT fk_rh_reserva    FOREIGN KEY (id_reserva)
        REFERENCES reserva (id_reserva)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_rh_habitacion FOREIGN KEY (id_habitacion)
        REFERENCES habitacion (id_habitacion)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

-- =====================================================================
-- 3. PROCEDIMIENTOS ALMACENADOS
--    Parte 1 (clientes). Cada parte anade aqui los suyos
--    (hoteles, habitaciones, reservas) siguiendo el mismo formato.
-- =====================================================================

DELIMITER $$

-- Inserta un cliente y devuelve el id autonumerico generado
DROP PROCEDURE IF EXISTS sp_insertar_cliente $$
CREATE PROCEDURE sp_insertar_cliente(
    IN  p_dni        VARCHAR(9),
    IN  p_nombre     VARCHAR(50),
    IN  p_apellidos  VARCHAR(100),
    IN  p_email      VARCHAR(100),
    IN  p_telefono   VARCHAR(15),
    OUT p_id_cliente INT UNSIGNED
)
BEGIN
    IF EXISTS (SELECT 1 FROM cliente WHERE dni = p_dni) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Ya existe un cliente con ese DNI';
    END IF;

    INSERT INTO cliente (dni, nombre, apellidos, email, telefono)
    VALUES (p_dni, p_nombre, p_apellidos, p_email, p_telefono);

    SET p_id_cliente = LAST_INSERT_ID();
END $$

-- Modifica los datos de un cliente (el DNI no se modifica)
DROP PROCEDURE IF EXISTS sp_modificar_cliente $$
CREATE PROCEDURE sp_modificar_cliente(
    IN p_id_cliente INT UNSIGNED,
    IN p_nombre     VARCHAR(50),
    IN p_apellidos  VARCHAR(100),
    IN p_email      VARCHAR(100),
    IN p_telefono   VARCHAR(15)
)
BEGIN
    UPDATE cliente
       SET nombre    = p_nombre,
           apellidos = p_apellidos,
           email     = p_email,
           telefono  = p_telefono
     WHERE id_cliente = p_id_cliente;

    IF ROW_COUNT() = 0 AND NOT EXISTS (SELECT 1 FROM cliente WHERE id_cliente = p_id_cliente) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'No existe el cliente que se quiere modificar';
    END IF;
END $$

-- Elimina un cliente solo si no tiene reservas
DROP PROCEDURE IF EXISTS sp_eliminar_cliente $$
CREATE PROCEDURE sp_eliminar_cliente(
    IN p_id_cliente INT UNSIGNED
)
BEGIN
    IF EXISTS (SELECT 1 FROM reserva WHERE id_cliente = p_id_cliente) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'No se puede eliminar: el cliente tiene reservas registradas';
    END IF;

    DELETE FROM cliente WHERE id_cliente = p_id_cliente;

    IF ROW_COUNT() = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'No existe el cliente que se quiere eliminar';
    END IF;
END $$

DELIMITER ;

-- =====================================================================
-- 4. DATOS DE PRUEBA (los mismos que habia en Datos.java)
-- =====================================================================

INSERT INTO cliente (dni, nombre, apellidos, email, telefono) VALUES
    ('11111111A', 'Ana',   'Garcia Lopez',  'ana@mail.com',   '600111111'),
    ('22222222B', 'Marc',  'Puig Ferrer',   'marc@mail.com',  '600222222'),
    ('33333333C', 'Laura', 'Martin Ruiz',   'laura@mail.com', '600333333'),
    ('44444444D', 'Jordi', 'Vidal Soler',   'jordi@mail.com', '600444444'),
    ('55555555E', 'Sara',  'Lopez Navarro', 'sara@mail.com',  '600555555');

INSERT INTO hotel (nombre, ciudad, direccion, estrellas, telefono) VALUES
    ('Hotel QueSueño',               'Barcelona', 'Paseo de Gracia 1',    4, '931234567'),
    ('Hotel Doritos',                'Madrid',    'Gran Vía 10',          3, '911234567'),
    ('Hotel Cocacola del Mercadona', 'Valencia',  'Avenida del Puerto 5', 4, '961234567');

-- Las mismas 5 habitaciones en cada hotel
INSERT INTO habitacion (id_hotel, numero, planta, capacidad, categoria, precio_base_noche)
SELECT h.id_hotel, t.numero, t.planta, t.capacidad, t.categoria, t.precio
  FROM hotel h
 CROSS JOIN (
        SELECT '101' AS numero, '1' AS planta, 2 AS capacidad, 'ESTANDAR' AS categoria,  85.00 AS precio
        UNION ALL SELECT '102', '1', 3, 'ESTANDAR', 115.00
        UNION ALL SELECT '201', '2', 2, 'SUPERIOR', 120.00
        UNION ALL SELECT '202', '2', 3, 'SUPERIOR', 145.00
        UNION ALL SELECT '301', '3', 4, 'SUITE',    180.00
      ) t;

-- Dos reservas de ejemplo (3 noches dentro de 10 dias)
-- Importe = noches * precio noche (ESTANDAR x1, SUPERIOR x1.5, SUITE x2)
INSERT INTO reserva (id_cliente, id_hotel, fecha_entrada, fecha_salida, num_personas, importe)
VALUES (1, 1, CURRENT_DATE + INTERVAL 10 DAY, CURRENT_DATE + INTERVAL 13 DAY, 2, 3 * 85.00),
       (2, 2, CURRENT_DATE + INTERVAL 10 DAY, CURRENT_DATE + INTERVAL 13 DAY, 2, 3 * 120.00 * 1.5);

INSERT INTO reserva_habitacion (id_reserva, id_habitacion)
SELECT 1, id_habitacion FROM habitacion WHERE id_hotel = 1 AND numero = '101'
UNION ALL
SELECT 2, id_habitacion FROM habitacion WHERE id_hotel = 2 AND numero = '201';
