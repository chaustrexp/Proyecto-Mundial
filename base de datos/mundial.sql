/* -------------------------------------------------------- */
/*  ESTRUCTURA DEFINITIVA DE LA BASE DE DATOS mundial_db   */
/* -------------------------------------------------------- */

DROP DATABASE IF EXISTS mundial;
CREATE DATABASE mundial;
USE mundial;

/* ------------------ TABLA: usuarios ---------------------- */
CREATE TABLE usuarios (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(50) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    rol          VARCHAR(50) DEFAULT 'admin'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO usuarios (username, password, rol) VALUES ('admin', 'admin', 'admin');

/* ------------------ TABLA: apostadores ------------------- */
CREATE TABLE apostadores (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL,
    puntos_total INT DEFAULT 0,
    usuario_id   INT DEFAULT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* ------------------ TABLA: equipos ---------------------- */
CREATE TABLE equipos (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    confederacion VARCHAR(50) NULL,
    bandera_url   VARCHAR(255) NULL,
    grupo         CHAR(1) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* ------------------ TABLA: partidos --------------------- */
CREATE TABLE partidos (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    equipo_local_id  INT NOT NULL,
    equipo_visita_id INT NOT NULL,
    fecha      DATETIME NOT NULL,
    fase       VARCHAR(50) NOT NULL,
    goles_local  INT DEFAULT NULL,
    goles_visita INT DEFAULT NULL,
    FOREIGN KEY (equipo_local_id)  REFERENCES equipos(id)  ON DELETE CASCADE,
    FOREIGN KEY (equipo_visita_id) REFERENCES equipos(id)  ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* ------------------ TABLA: predicciones ----------------- */
CREATE TABLE predicciones (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    apostador_id     INT NOT NULL,
    partido_id       INT NOT NULL,
    goles_pred_eq1   INT NOT NULL,
    goles_pred_eq2   INT NOT NULL,
    puntos_ganados   INT DEFAULT 0,
    FOREIGN KEY (apostador_id) REFERENCES apostadores(id) ON DELETE CASCADE,
    FOREIGN KEY (partido_id)   REFERENCES partidos(id)   ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* ------------------ TRIGGER: historial de marcador ------------------ */
DELIMITER $$
CREATE TRIGGER trg_guardar_historial AFTER UPDATE ON partidos
FOR EACH ROW
BEGIN
    -- Solo cuando cambian los goles
    IF (NEW.goles_local <> OLD.goles_local) OR (NEW.goles_visita <> OLD.goles_visita) THEN
        INSERT INTO resultados (partido_id, goles_local, goles_visita)
        VALUES (NEW.id, NEW.goles_local, NEW.goles_visita);
    END IF;
END$$
DELIMITER ;


/* ------------------ Índices para rendimiento ------------- */
CREATE INDEX idx_apostador_nombre ON apostadores(nombre);
CREATE INDEX idx_partido_fase       ON partidos(fase);
CREATE INDEX idx_prediccion_apostador ON predicciones(apostador_id);
CREATE INDEX idx_prediccion_partido   ON predicciones(partido_id);
