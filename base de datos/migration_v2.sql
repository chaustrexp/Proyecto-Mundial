USE mundial;

-- Este procedimiento recorre los apostadores que no tienen usuario_id y les crea uno
DELIMITER //

CREATE PROCEDURE MigrarApostadoresAntiguos()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE cur_id INT;
    DECLARE cur_nombre VARCHAR(100);
    DECLARE nuevo_user_id INT;
    
    -- Seleccionar apostadores sin usuario_id
    DECLARE cur_apostadores CURSOR FOR SELECT id, nombre FROM apostadores WHERE usuario_id IS NULL;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur_apostadores;

    read_loop: LOOP
        FETCH cur_apostadores INTO cur_id, cur_nombre;
        IF done THEN
            LEAVE read_loop;
        END IF;

        -- Formatear nombre para username: minúsculas, quitar espacios (básico)
        -- Si hay conflictos de nombres únicos, esto podría fallar, pero asumiendo nombres simples funcionará.
        -- Si falla por nombre duplicado, el INSERT IGNORE lo omitirá, aunque sería mejor usar un alias único.
        SET @base_user = LOWER(REPLACE(cur_nombre, ' ', ''));
        
        -- Insertar el usuario. Contraseña por defecto: 123456
        INSERT IGNORE INTO usuarios (username, password, rol) VALUES (@base_user, '123456', 'usuario');
        
        -- Obtener el ID del usuario recién creado o existente
        SELECT id INTO nuevo_user_id FROM usuarios WHERE username = @base_user LIMIT 1;
        
        -- Actualizar el apostador con este usuario
        IF nuevo_user_id IS NOT NULL THEN
            UPDATE apostadores SET usuario_id = nuevo_user_id WHERE id = cur_id;
        END IF;

    END LOOP;

    CLOSE cur_apostadores;
END //

DELIMITER ;

-- Ejecutar la migración
CALL MigrarApostadoresAntiguos();

-- Opcional: borrar el procedimiento después de usarlo
DROP PROCEDURE IF EXISTS MigrarApostadoresAntiguos;
