USE objectworks;
DELIMITER $$

-- Chic@s os hago en archivos aparte lo mio (Parte4) por si se lia o algo
-- Así no os toco los vuestros y arreglar cosas es mas sencillo
--                                                                  ~Albert

-- Crear reserva + sus habitaciones asociadas
DROP PROCEDURE IF EXISTS sp_crear_reserva $$
CREATE PROCEDURE sp_crear_reserva(
    IN  p_id_cliente     INT UNSIGNED,
    IN  p_id_hotel       INT UNSIGNED,
    IN  p_fecha_entrada  DATE,
    IN  p_fecha_salida   DATE,
    IN  p_num_personas   TINYINT UNSIGNED,
    IN  p_importe        DECIMAL(10,2),
    OUT p_id_reserva     INT UNSIGNED
)
BEGIN
    -- Escribidme si veeis que falta alguna aunque creo que no
    IF p_fecha_salida <= p_fecha_entrada THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La fecha de salida debe ser posterior a la de entrada';
    END IF;

    IF p_num_personas <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El numero de personas debe ser mayor que 0';
    END IF;

    INSERT INTO reserva (id_cliente, id_hotel, fecha_entrada, fecha_salida,
                         num_personas, importe, fecha_creacion, estado)
    VALUES (p_id_cliente, p_id_hotel, p_fecha_entrada, p_fecha_salida,
            p_num_personas, p_importe, CURRENT_DATE, 'CONFIRMADA');

    SET p_id_reserva = LAST_INSERT_ID();
END $$

-- Modificar datos de una reserva - - - -
DROP PROCEDURE IF EXISTS sp_modificar_reserva $$
CREATE PROCEDURE sp_modificar_reserva(
    IN p_id_reserva     INT UNSIGNED,
    IN p_fecha_entrada  DATE,
    IN p_fecha_salida   DATE,
    IN p_num_personas   TINYINT UNSIGNED,
    IN p_importe        DECIMAL(10,2)
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM reserva WHERE id_reserva = p_id_reserva) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No existe la reserva que se quiere modificar';
    END IF;

    IF p_fecha_salida <= p_fecha_entrada THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La fecha de salida debe ser posterior a la de entrada';
    END IF;

    UPDATE reserva
       SET fecha_entrada = p_fecha_entrada,
           fecha_salida  = p_fecha_salida,
           num_personas  = p_num_personas,
           importe       = p_importe
     WHERE id_reserva = p_id_reserva
       AND estado = 'CONFIRMADA';   -- no se modifican canceladas/finalizadas

    IF ROW_COUNT() = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Solo se pueden modificar reservas en estado CONFIRMADA';
    END IF;
END $$

-- CAncelar Reserva - - - - - -
DROP PROCEDURE IF EXISTS sp_cancelar_reserva $$
CREATE PROCEDURE sp_cancelar_reserva(
    IN p_id_reserva INT UNSIGNED
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM reserva WHERE id_reserva = p_id_reserva) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No existe la reserva que se quiere cancelar';
    END IF;

    UPDATE reserva
       SET estado = 'CANCELADA'
     WHERE id_reserva = p_id_reserva
       AND estado = 'CONFIRMADA';

    IF ROW_COUNT() = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Solo se pueden cancelar reservas en estado CONFIRMADA';
    END IF;
END $$

DELIMITER ;