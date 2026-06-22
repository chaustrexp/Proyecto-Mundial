USE mundial;

-- Modificar tabla partidos para que fecha sea DATETIME
ALTER TABLE partidos MODIFY COLUMN fecha DATETIME NOT NULL;

-- Añadir usuario_id a apostadores
ALTER TABLE apostadores ADD COLUMN usuario_id INT DEFAULT NULL;
ALTER TABLE apostadores ADD CONSTRAINT fk_apostador_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE;

-- Crear un usuario de prueba para poder apostar
INSERT INTO usuarios (username, password, rol) VALUES ('usuario1', 'usuario1', 'usuario');
-- Y su apostador vinculado
INSERT INTO apostadores (nombre, puntos_total, usuario_id) VALUES ('Usuario Uno', 0, LAST_INSERT_ID());
