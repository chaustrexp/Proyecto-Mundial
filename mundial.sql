-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 18-06-2026 a las 01:52:17
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `mundial`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `apostadores`
--

CREATE TABLE `apostadores` (
  `id` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `puntos_total` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `apostadores`
--

INSERT INTO `apostadores` (`id`, `nombre`, `puntos_total`) VALUES
(1, 'kevin', 5),
(2, 'juan', 0),
(3, 'pipe', 0),
(4, 'osi', 0),
(5, 'jose', 0),
(6, 'lucas', 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `equipos`
--

CREATE TABLE `equipos` (
  `id` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `confederacion` varchar(50) DEFAULT NULL,
  `bandera_url` varchar(255) DEFAULT NULL,
  `grupo` char(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `equipos`
--

INSERT INTO `equipos` (`id`, `nombre`, `confederacion`, `bandera_url`, `grupo`) VALUES
(1, 'México', 'Por definir', NULL, 'A'),
(2, 'Sudáfrica', 'Por definir', NULL, 'A'),
(3, 'República de Corea', 'Por definir', NULL, 'A'),
(4, 'Chequia', 'Por definir', NULL, 'A'),
(5, 'Canadá', 'Por definir', NULL, 'B'),
(6, 'Bosnia y Herzegovina', 'Por definir', NULL, 'B'),
(7, 'Catar', 'Por definir', NULL, 'B'),
(8, 'Suiza', 'Por definir', NULL, 'B'),
(9, 'Brasil', 'Por definir', NULL, 'C'),
(10, 'Marruecos', 'Por definir', NULL, 'C'),
(11, 'Haití', 'Por definir', NULL, 'C'),
(12, 'Escocia', 'Por definir', NULL, 'C'),
(13, 'Estados Unidos', 'Por definir', NULL, 'D'),
(14, 'Paraguay', 'Por definir', NULL, 'D'),
(15, 'Australia', 'Por definir', NULL, 'D'),
(16, 'Turquía', 'Por definir', NULL, 'D'),
(17, 'Alemania', 'Por definir', NULL, 'E'),
(18, 'Curazao', 'Por definir', NULL, 'E'),
(19, 'Costa de Marfil', 'Por definir', NULL, 'E'),
(20, 'Ecuador', 'Por definir', NULL, 'E'),
(21, 'Países Bajos', 'Por definir', NULL, 'F'),
(22, 'Japón', 'Por definir', NULL, 'F'),
(23, 'Suecia', 'Por definir', NULL, 'F'),
(24, 'Túnez', 'Por definir', NULL, 'F'),
(25, 'Bélgica', 'Por definir', NULL, 'G'),
(26, 'Egipto', 'Por definir', NULL, 'G'),
(27, 'RI de Irán', 'Por definir', NULL, 'G'),
(28, 'Nueva Zelanda', 'Por definir', NULL, 'G'),
(29, 'España', 'Por definir', NULL, 'H'),
(30, 'Cabo Verde', 'Por definir', NULL, 'H'),
(31, 'Arabia Saudí', 'Por definir', NULL, 'H'),
(32, 'Uruguay', 'Por definir', NULL, 'H'),
(33, 'Francia', 'Por definir', NULL, 'I'),
(34, 'Senegal', 'Por definir', NULL, 'I'),
(35, 'Irak', 'Por definir', NULL, 'I'),
(36, 'Noruega', 'Por definir', NULL, 'I'),
(37, 'Argentina', 'Por definir', NULL, 'J'),
(38, 'Argelia', 'Por definir', NULL, 'J'),
(39, 'Austria', 'Por definir', NULL, 'J'),
(40, 'Jordania', 'Por definir', NULL, 'J'),
(41, 'Portugal', 'Por definir', NULL, 'K'),
(42, 'RD Congo', 'Por definir', NULL, 'K'),
(43, 'Uzbekistán', 'Por definir', NULL, 'K'),
(44, 'Colombia', 'Por definir', NULL, 'K'),
(45, 'Inglaterra', 'Por definir', NULL, 'L'),
(46, 'Croacia', 'Por definir', NULL, 'L'),
(47, 'Ghana', 'Por definir', NULL, 'L'),
(48, 'Panamá', 'Por definir', NULL, 'L');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `partidos`
--

CREATE TABLE `partidos` (
  `id` int(11) NOT NULL,
  `equipo_local_id` int(11) NOT NULL,
  `equipo_visita_id` int(11) NOT NULL,
  `fecha` date NOT NULL,
  `fase` varchar(50) NOT NULL,
  `goles_local` int(11) DEFAULT NULL,
  `goles_visita` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `partidos`
--

INSERT INTO `partidos` (`id`, `equipo_local_id`, `equipo_visita_id`, `fecha`, `fase`, `goles_local`, `goles_visita`) VALUES
(1, 1, 2, '2026-06-17', 'Grupo', 2, 0),
(2, 4, 1, '2026-06-17', 'Grupo', NULL, NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `predicciones`
--

CREATE TABLE `predicciones` (
  `id` int(11) NOT NULL,
  `apostador_id` int(11) NOT NULL,
  `partido_id` int(11) NOT NULL,
  `goles_pred_eq1` int(11) NOT NULL,
  `goles_pred_eq2` int(11) NOT NULL,
  `puntos_ganados` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `predicciones`
--

INSERT INTO `predicciones` (`id`, `apostador_id`, `partido_id`, `goles_pred_eq1`, `goles_pred_eq2`, `puntos_ganados`) VALUES
(1, 1, 1, 2, 0, 5),
(2, 2, 1, 2, 2, 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `resultados`
--

CREATE TABLE `resultados` (
  `id` int(11) NOT NULL,
  `partido_id` int(11) NOT NULL,
  `goles_local` int(11) NOT NULL,
  `goles_visita` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `resultados`
--

INSERT INTO `resultados` (`id`, `partido_id`, `goles_local`, `goles_visita`) VALUES
(1, 1, 2, 0);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `apostadores`
--
ALTER TABLE `apostadores`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_apostador_nombre` (`nombre`);

--
-- Indices de la tabla `equipos`
--
ALTER TABLE `equipos`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `partidos`
--
ALTER TABLE `partidos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `equipo_local_id` (`equipo_local_id`),
  ADD KEY `equipo_visita_id` (`equipo_visita_id`),
  ADD KEY `idx_partido_fase` (`fase`);

--
-- Indices de la tabla `predicciones`
--
ALTER TABLE `predicciones`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_prediccion_apostador` (`apostador_id`),
  ADD KEY `idx_prediccion_partido` (`partido_id`);

--
-- Indices de la tabla `resultados`
--
ALTER TABLE `resultados`
  ADD PRIMARY KEY (`id`),
  ADD KEY `partido_id` (`partido_id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `apostadores`
--
ALTER TABLE `apostadores`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `equipos`
--
ALTER TABLE `equipos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=49;

--
-- AUTO_INCREMENT de la tabla `partidos`
--
ALTER TABLE `partidos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `predicciones`
--
ALTER TABLE `predicciones`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `resultados`
--
ALTER TABLE `resultados`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `partidos`
--
ALTER TABLE `partidos`
  ADD CONSTRAINT `partidos_ibfk_1` FOREIGN KEY (`equipo_local_id`) REFERENCES `equipos` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `partidos_ibfk_2` FOREIGN KEY (`equipo_visita_id`) REFERENCES `equipos` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `predicciones`
--
ALTER TABLE `predicciones`
  ADD CONSTRAINT `predicciones_ibfk_1` FOREIGN KEY (`apostador_id`) REFERENCES `apostadores` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `predicciones_ibfk_2` FOREIGN KEY (`partido_id`) REFERENCES `partidos` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `resultados`
--
ALTER TABLE `resultados`
  ADD CONSTRAINT `resultados_ibfk_1` FOREIGN KEY (`partido_id`) REFERENCES `partidos` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
