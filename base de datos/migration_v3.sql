USE mundial;

-- Agregar columna estado a partidos para tracking en tiempo real
ALTER TABLE partidos 
ADD COLUMN estado VARCHAR(20) NOT NULL DEFAULT 'programado' 
AFTER fase;

-- Agregar columnas para goles en vivo (antes de finalizar)
ALTER TABLE partidos
ADD COLUMN goles_local_vivo INT DEFAULT 0 AFTER estado,
ADD COLUMN goles_visita_vivo INT DEFAULT 0 AFTER goles_local_vivo,
ADD COLUMN minuto_actual INT DEFAULT 0 AFTER goles_visita_vivo;

-- Actualizar partidos ya finalizados (tienen goles) al estado correcto
UPDATE partidos SET estado = 'finalizado' WHERE goles_local IS NOT NULL;
