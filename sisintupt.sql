-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versión del servidor:         10.4.32-MariaDB - mariadb.org binary distribution
-- SO del servidor:              Win64
-- HeidiSQL Versión:             12.11.0.7065
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Volcando estructura de base de datos para sisintupt
CREATE DATABASE IF NOT EXISTS `sisintupt` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `sisintupt`;

-- Volcando estructura para tabla sisintupt.auditoria
CREATE TABLE IF NOT EXISTS `auditoria` (
  `id` varchar(36) NOT NULL,
  `accion` varchar(100) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `estado` varchar(50) DEFAULT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `metadata` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`metadata`)),
  `modulo` varchar(100) NOT NULL,
  `motivo` text DEFAULT NULL,
  `usuario_id` varchar(36) DEFAULT NULL,
  `usuario_nombre` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.auditoria: ~0 rows (aproximadamente)

-- Volcando estructura para tabla sisintupt.bloqueshorarios
CREATE TABLE IF NOT EXISTS `bloqueshorarios` (
  `IdBloque` int(11) NOT NULL AUTO_INCREMENT,
  `Orden` int(11) NOT NULL,
  `Nombre` varchar(50) NOT NULL,
  `HoraInicio` time NOT NULL,
  `HoraFinal` time NOT NULL,
  PRIMARY KEY (`IdBloque`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.bloqueshorarios: ~3 rows (aproximadamente)
INSERT INTO `bloqueshorarios` (`IdBloque`, `Orden`, `Nombre`, `HoraInicio`, `HoraFinal`) VALUES
	(10, 1, 'B1', '08:00:00', '08:50:00'),
	(11, 2, 'B2', '08:50:00', '09:40:00'),
	(13, 3, 'B3', '09:40:00', '10:30:00');

-- Volcando estructura para tabla sisintupt.escuela
CREATE TABLE IF NOT EXISTS `escuela` (
  `IdEscuela` int(11) NOT NULL AUTO_INCREMENT,
  `IdFacultad` int(11) NOT NULL,
  `Nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`IdEscuela`),
  KEY `FK__facultad` (`IdFacultad`),
  CONSTRAINT `FK__facultad` FOREIGN KEY (`IdFacultad`) REFERENCES `facultad` (`IdFacultad`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.escuela: ~19 rows (aproximadamente)
INSERT INTO `escuela` (`IdEscuela`, `IdFacultad`, `Nombre`) VALUES
	(1, 1, 'Ing. Civil'),
	(2, 1, 'Ing. de Sistemas'),
	(3, 1, 'Ing. Electronica'),
	(4, 1, 'Ing. Agroindustrial'),
	(5, 1, 'Ing. Ambiental'),
	(6, 1, 'Ing. Industrial'),
	(7, 2, 'Derecho'),
	(8, 3, 'Ciencias Contables y Financieras'),
	(9, 3, 'Economia y Microfinanzas'),
	(10, 3, 'Administracion'),
	(11, 3, 'Administracion Turistico-Hotel'),
	(12, 3, 'Administracion de Negocios Internacionales'),
	(13, 4, 'Educacion'),
	(14, 4, 'Ciencias de la Comunicacion'),
	(15, 4, 'Humanidades - Psicologia'),
	(16, 5, 'Medicina Humana'),
	(17, 5, 'Odontologia'),
	(18, 5, 'Tecnologia Medica'),
	(19, 6, 'Arquitectira');

-- Volcando estructura para tabla sisintupt.espacio
CREATE TABLE IF NOT EXISTS `espacio` (
  `IdEspacio` int(11) NOT NULL AUTO_INCREMENT,
  `Codigo` varchar(20) NOT NULL DEFAULT '',
  `Nombre` varchar(100) NOT NULL,
  `Tipo` enum('Laboratorio','Salon') NOT NULL DEFAULT 'Laboratorio',
  `Capacidad` int(11) NOT NULL,
  `Equipamiento` text DEFAULT NULL,
  `Facultad` int(11) NOT NULL,
  `Escuela` int(11) NOT NULL,
  `Estado` int(11) NOT NULL DEFAULT 1,
  PRIMARY KEY (`IdEspacio`),
  UNIQUE KEY `Codigo` (`Codigo`),
  KEY `FK_espacio_facultad` (`Facultad`),
  KEY `FK_espacio_escuela` (`Escuela`),
  CONSTRAINT `FK_espacio_escuela` FOREIGN KEY (`Escuela`) REFERENCES `escuela` (`IdEscuela`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_espacio_facultad` FOREIGN KEY (`Facultad`) REFERENCES `facultad` (`IdFacultad`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.espacio: ~8 rows (aproximadamente)
INSERT INTO `espacio` (`IdEspacio`, `Codigo`, `Nombre`, `Tipo`, `Capacidad`, `Equipamiento`, `Facultad`, `Escuela`, `Estado`) VALUES
	(1, 'ESP-001', 'a', 'Laboratorio', 15, 'Equipos de computo, proyector', 1, 1, 1),
	(2, 'ESP-002', 'LAB C', 'Laboratorio', 15, 'Computadoras, software especializado', 1, 1, 0),
	(3, 'ESP-003', 'LAB D', 'Laboratorio', 15, 'Equipos de redes, servidores', 1, 1, 0),
	(4, 'ESP-004', 'LAB F', 'Laboratorio', 15, 'PCs de alto rendimiento', 1, 1, 1),
	(5, 'ESP-005', 'LAB E', 'Laboratorio', 15, 'Laboratorio multimedia', 1, 1, 1),
	(6, 'ESP-006', 'Aula-301', 'Salon', 15, 'Pizarra acrílica, proyector', 2, 7, 1),
	(7, 'ESP-007', 'LAB Ñ', 'Laboratorio', 20, 'Equipos de última generación', 1, 1, 1),
	(8, 'ESP-008', 'LAB A', 'Laboratorio', 20, 'Computadoras, impresora 3D', 1, 1, 1);

-- Volcando estructura para tabla sisintupt.espacios
CREATE TABLE IF NOT EXISTS `espacios` (
  `id` varchar(36) NOT NULL,
  `capacidad` int(11) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `escuela` varchar(100) NOT NULL,
  `estado` varchar(50) DEFAULT NULL,
  `facultad` varchar(100) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `recursos` text DEFAULT NULL,
  `tipo` varchar(50) NOT NULL,
  `ubicacion` varchar(200) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.espacios: ~0 rows (aproximadamente)

-- Volcando estructura para tabla sisintupt.eventos
CREATE TABLE IF NOT EXISTS `eventos` (
  `id` varchar(36) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `descripcion` text DEFAULT NULL,
  `estado` varchar(50) DEFAULT NULL,
  `fecha_fin` datetime(6) DEFAULT NULL,
  `fecha_inicio` datetime(6) NOT NULL,
  `imagen_url` text DEFAULT NULL,
  `organizador` varchar(200) DEFAULT NULL,
  `tipo` varchar(50) DEFAULT NULL,
  `titulo` varchar(200) NOT NULL,
  `ubicacion` varchar(200) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.eventos: ~0 rows (aproximadamente)

-- Volcando estructura para tabla sisintupt.facultad
CREATE TABLE IF NOT EXISTS `facultad` (
  `IdFacultad` int(11) NOT NULL AUTO_INCREMENT,
  `Nombre` varchar(10) NOT NULL,
  PRIMARY KEY (`IdFacultad`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.facultad: ~6 rows (aproximadamente)
INSERT INTO `facultad` (`IdFacultad`, `Nombre`) VALUES
	(1, 'FAING'),
	(2, 'FADE'),
	(3, 'FACEM'),
	(4, 'FAEDCOH'),
	(5, 'FACSA'),
	(6, 'FAU');

-- Volcando estructura para tabla sisintupt.horarios
CREATE TABLE IF NOT EXISTS `horarios` (
  `IdHorario` int(11) NOT NULL AUTO_INCREMENT,
  `espacio` int(11) NOT NULL,
  `bloque` int(11) NOT NULL,
  `diaSemana` enum('Lunes','Martes','Miercoles','Jueves','Viernes','Sabado') NOT NULL,
  `ocupado` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`IdHorario`) USING BTREE,
  KEY `FK_horario_espacio` (`espacio`) USING BTREE,
  KEY `FK_horario_bloque` (`bloque`) USING BTREE,
  CONSTRAINT `FK_horario_bloque` FOREIGN KEY (`bloque`) REFERENCES `bloqueshorarios` (`IdBloque`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_horario_espacio` FOREIGN KEY (`espacio`) REFERENCES `espacio` (`IdEspacio`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=465 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.horarios: ~144 rows (aproximadamente)
INSERT INTO `horarios` (`IdHorario`, `espacio`, `bloque`, `diaSemana`, `ocupado`) VALUES
	(258, 1, 10, 'Lunes', 0),
	(259, 1, 10, 'Martes', 0),
	(260, 1, 10, 'Miercoles', 0),
	(261, 1, 10, 'Jueves', 0),
	(262, 1, 10, 'Viernes', 1),
	(263, 1, 10, 'Sabado', 1),
	(264, 2, 10, 'Lunes', 0),
	(265, 2, 10, 'Martes', 0),
	(266, 2, 10, 'Miercoles', 0),
	(267, 2, 10, 'Jueves', 0),
	(268, 2, 10, 'Viernes', 0),
	(269, 2, 10, 'Sabado', 0),
	(270, 3, 10, 'Lunes', 0),
	(271, 3, 10, 'Martes', 0),
	(272, 3, 10, 'Miercoles', 0),
	(273, 3, 10, 'Jueves', 0),
	(274, 3, 10, 'Viernes', 0),
	(275, 3, 10, 'Sabado', 0),
	(276, 4, 10, 'Lunes', 0),
	(277, 4, 10, 'Martes', 0),
	(278, 4, 10, 'Miercoles', 0),
	(279, 4, 10, 'Jueves', 0),
	(280, 4, 10, 'Viernes', 0),
	(281, 4, 10, 'Sabado', 0),
	(282, 5, 10, 'Lunes', 0),
	(283, 5, 10, 'Martes', 0),
	(284, 5, 10, 'Miercoles', 0),
	(285, 5, 10, 'Jueves', 0),
	(286, 5, 10, 'Viernes', 0),
	(287, 5, 10, 'Sabado', 0),
	(288, 7, 10, 'Lunes', 0),
	(289, 7, 10, 'Martes', 0),
	(290, 7, 10, 'Miercoles', 0),
	(291, 7, 10, 'Jueves', 0),
	(292, 7, 10, 'Viernes', 0),
	(293, 7, 10, 'Sabado', 0),
	(294, 8, 10, 'Lunes', 0),
	(295, 8, 10, 'Martes', 0),
	(296, 8, 10, 'Miercoles', 0),
	(297, 8, 10, 'Jueves', 0),
	(298, 8, 10, 'Viernes', 0),
	(299, 8, 10, 'Sabado', 0),
	(300, 6, 10, 'Lunes', 0),
	(301, 6, 10, 'Martes', 0),
	(302, 6, 10, 'Miercoles', 0),
	(303, 6, 10, 'Jueves', 0),
	(304, 6, 10, 'Viernes', 0),
	(305, 6, 10, 'Sabado', 0),
	(321, 1, 11, 'Lunes', 0),
	(322, 1, 11, 'Martes', 0),
	(323, 1, 11, 'Miercoles', 0),
	(324, 1, 11, 'Jueves', 0),
	(325, 1, 11, 'Viernes', 0),
	(326, 1, 11, 'Sabado', 0),
	(327, 2, 11, 'Lunes', 0),
	(328, 2, 11, 'Martes', 0),
	(329, 2, 11, 'Miercoles', 0),
	(330, 2, 11, 'Jueves', 0),
	(331, 2, 11, 'Viernes', 0),
	(332, 2, 11, 'Sabado', 0),
	(333, 3, 11, 'Lunes', 0),
	(334, 3, 11, 'Martes', 0),
	(335, 3, 11, 'Miercoles', 0),
	(336, 3, 11, 'Jueves', 0),
	(337, 3, 11, 'Viernes', 0),
	(338, 3, 11, 'Sabado', 0),
	(339, 4, 11, 'Lunes', 0),
	(340, 4, 11, 'Martes', 0),
	(341, 4, 11, 'Miercoles', 0),
	(342, 4, 11, 'Jueves', 0),
	(343, 4, 11, 'Viernes', 0),
	(344, 4, 11, 'Sabado', 0),
	(345, 5, 11, 'Lunes', 0),
	(346, 5, 11, 'Martes', 0),
	(347, 5, 11, 'Miercoles', 0),
	(348, 5, 11, 'Jueves', 0),
	(349, 5, 11, 'Viernes', 0),
	(350, 5, 11, 'Sabado', 0),
	(351, 7, 11, 'Lunes', 0),
	(352, 7, 11, 'Martes', 0),
	(353, 7, 11, 'Miercoles', 0),
	(354, 7, 11, 'Jueves', 0),
	(355, 7, 11, 'Viernes', 0),
	(356, 7, 11, 'Sabado', 0),
	(357, 8, 11, 'Lunes', 0),
	(358, 8, 11, 'Martes', 0),
	(359, 8, 11, 'Miercoles', 0),
	(360, 8, 11, 'Jueves', 0),
	(361, 8, 11, 'Viernes', 0),
	(362, 8, 11, 'Sabado', 0),
	(363, 6, 11, 'Lunes', 0),
	(364, 6, 11, 'Martes', 0),
	(365, 6, 11, 'Miercoles', 0),
	(366, 6, 11, 'Jueves', 0),
	(367, 6, 11, 'Viernes', 0),
	(368, 6, 11, 'Sabado', 0),
	(417, 1, 13, 'Lunes', 0),
	(418, 1, 13, 'Martes', 0),
	(419, 1, 13, 'Miercoles', 0),
	(420, 1, 13, 'Jueves', 0),
	(421, 1, 13, 'Viernes', 0),
	(422, 1, 13, 'Sabado', 0),
	(423, 2, 13, 'Lunes', 0),
	(424, 2, 13, 'Martes', 0),
	(425, 2, 13, 'Miercoles', 0),
	(426, 2, 13, 'Jueves', 0),
	(427, 2, 13, 'Viernes', 0),
	(428, 2, 13, 'Sabado', 0),
	(429, 3, 13, 'Lunes', 0),
	(430, 3, 13, 'Martes', 0),
	(431, 3, 13, 'Miercoles', 0),
	(432, 3, 13, 'Jueves', 0),
	(433, 3, 13, 'Viernes', 0),
	(434, 3, 13, 'Sabado', 0),
	(435, 4, 13, 'Lunes', 0),
	(436, 4, 13, 'Martes', 0),
	(437, 4, 13, 'Miercoles', 0),
	(438, 4, 13, 'Jueves', 0),
	(439, 4, 13, 'Viernes', 0),
	(440, 4, 13, 'Sabado', 0),
	(441, 5, 13, 'Lunes', 0),
	(442, 5, 13, 'Martes', 0),
	(443, 5, 13, 'Miercoles', 0),
	(444, 5, 13, 'Jueves', 0),
	(445, 5, 13, 'Viernes', 0),
	(446, 5, 13, 'Sabado', 0),
	(447, 7, 13, 'Lunes', 0),
	(448, 7, 13, 'Martes', 0),
	(449, 7, 13, 'Miercoles', 0),
	(450, 7, 13, 'Jueves', 0),
	(451, 7, 13, 'Viernes', 0),
	(452, 7, 13, 'Sabado', 0),
	(453, 8, 13, 'Lunes', 0),
	(454, 8, 13, 'Martes', 0),
	(455, 8, 13, 'Miercoles', 0),
	(456, 8, 13, 'Jueves', 0),
	(457, 8, 13, 'Viernes', 0),
	(458, 8, 13, 'Sabado', 0),
	(459, 6, 13, 'Lunes', 0),
	(460, 6, 13, 'Martes', 0),
	(461, 6, 13, 'Miercoles', 0),
	(462, 6, 13, 'Jueves', 0),
	(463, 6, 13, 'Viernes', 0),
	(464, 6, 13, 'Sabado', 0);

-- Volcando estructura para tabla sisintupt.perfiles
CREATE TABLE IF NOT EXISTS `perfiles` (
  `id` varchar(36) NOT NULL,
  `apellidos` varchar(100) NOT NULL,
  `avatar_url` text DEFAULT NULL,
  `celular` varchar(15) DEFAULT NULL,
  `codigo` varchar(20) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `escuela` varchar(100) DEFAULT NULL,
  `estado` varchar(20) DEFAULT NULL,
  `facultad` varchar(100) DEFAULT NULL,
  `genero` varchar(20) DEFAULT NULL,
  `nombres` varchar(100) NOT NULL,
  `numero_documento` varchar(20) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `rol` varchar(50) DEFAULT NULL,
  `tipo_documento` varchar(20) DEFAULT NULL,
  `tipo_login` varchar(20) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_65h771rtmbrasq1ceu7aoaw1k` (`codigo`),
  UNIQUE KEY `UK_rpu4j8kj0tyad8uf76f9yadnc` (`email`),
  UNIQUE KEY `UK_tbyo1m0hhj2avvhndkaib0ksy` (`numero_documento`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.perfiles: ~0 rows (aproximadamente)

-- Volcando estructura para tabla sisintupt.reserva
CREATE TABLE IF NOT EXISTS `reserva` (
  `IdReserva` int(11) NOT NULL AUTO_INCREMENT,
  `usuario` int(11) NOT NULL,
  `espacio` int(11) NOT NULL,
  `fechaReserva` date NOT NULL,
  `bloque` int(11) NOT NULL,
  `estado` varchar(50) NOT NULL DEFAULT 'Pendiente',
  `fechaSolicitud` datetime NOT NULL DEFAULT current_timestamp(),
  `Descripcion` tinytext NOT NULL,
  `Motivo` tinytext DEFAULT NULL,
  PRIMARY KEY (`IdReserva`),
  KEY `FK_reserva_espacio` (`espacio`),
  KEY `FK_reserva_usuario` (`usuario`),
  KEY `FK_reserva_bloqueshorarios` (`bloque`),
  CONSTRAINT `FK_reserva_bloqueshorarios` FOREIGN KEY (`bloque`) REFERENCES `bloqueshorarios` (`IdBloque`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_reserva_espacio` FOREIGN KEY (`espacio`) REFERENCES `espacio` (`IdEspacio`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_reserva_usuario` FOREIGN KEY (`usuario`) REFERENCES `usuario` (`IdUsuario`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.reserva: ~2 rows (aproximadamente)
INSERT INTO `reserva` (`IdReserva`, `usuario`, `espacio`, `fechaReserva`, `bloque`, `estado`, `fechaSolicitud`, `Descripcion`, `Motivo`) VALUES
	(19, 5, 1, '2025-09-15', 10, 'Aprobada', '2025-09-15 22:44:01', 'Curso:1-Tema:1', NULL),
	(20, 5, 1, '2025-09-16', 10, 'Cancelado', '2025-09-16 00:33:02', 'Curso:Base de Datos-Tema:Jugar', NULL);

-- Volcando estructura para tabla sisintupt.reservas
CREATE TABLE IF NOT EXISTS `reservas` (
  `id` varchar(36) NOT NULL,
  `aprobado_por` varchar(36) DEFAULT NULL,
  `ciclo` varchar(10) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `curso` varchar(200) DEFAULT NULL,
  `espacio_id` varchar(36) DEFAULT NULL,
  `estado` varchar(50) DEFAULT NULL,
  `fecha` date NOT NULL,
  `hora_fin` time(6) NOT NULL,
  `hora_inicio` time(6) NOT NULL,
  `motivo` text DEFAULT NULL,
  `motivo_rechazo` text DEFAULT NULL,
  `tipo` varchar(50) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `usuario_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKew1ulm4nh4aimufhpmwqnlgif` (`aprobado_por`),
  KEY `FKfp1hb6safjpuo5ehubembn6hu` (`espacio_id`),
  KEY `FKccdkla46x13r8diqsh8ayw8xb` (`usuario_id`),
  CONSTRAINT `FKccdkla46x13r8diqsh8ayw8xb` FOREIGN KEY (`usuario_id`) REFERENCES `perfiles` (`id`),
  CONSTRAINT `FKew1ulm4nh4aimufhpmwqnlgif` FOREIGN KEY (`aprobado_por`) REFERENCES `perfiles` (`id`),
  CONSTRAINT `FKfp1hb6safjpuo5ehubembn6hu` FOREIGN KEY (`espacio_id`) REFERENCES `espacios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.reservas: ~0 rows (aproximadamente)

-- Volcando estructura para evento sisintupt.reset_horarios_domingo
DELIMITER //
CREATE EVENT `reset_horarios_domingo` ON SCHEDULE EVERY 1 WEEK STARTS '2025-09-15 00:00:00' ON COMPLETION NOT PRESERVE ENABLE DO UPDATE horarios SET ocupado = 0//
DELIMITER ;

-- Volcando estructura para tabla sisintupt.rol
CREATE TABLE IF NOT EXISTS `rol` (
  `IdRol` int(11) NOT NULL,
  `Nombre` varchar(15) NOT NULL,
  PRIMARY KEY (`IdRol`),
  UNIQUE KEY `Nombre` (`Nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.rol: ~3 rows (aproximadamente)
INSERT INTO `rol` (`IdRol`, `Nombre`) VALUES
	(1, 'Profesor'),
	(2, 'Estudiante'),
	(3, 'Administrador');

-- Volcando estructura para tabla sisintupt.usuario
CREATE TABLE IF NOT EXISTS `usuario` (
  `IdUsuario` int(11) NOT NULL AUTO_INCREMENT,
  `Nombre` varchar(30) NOT NULL,
  `Apellido` varchar(30) NOT NULL,
  `CodigoU` varchar(20) NOT NULL DEFAULT '',
  `CorreoU` varchar(30) NOT NULL,
  `TipoDoc` varchar(30) NOT NULL,
  `NumDoc` varchar(20) NOT NULL DEFAULT '',
  `Rol` int(9) NOT NULL,
  `Facultad` int(11) NOT NULL,
  `Escuela` int(11) NOT NULL,
  `Celular` varchar(11) DEFAULT NULL,
  `Genero` bit(1) DEFAULT NULL,
  `Password` varchar(255) NOT NULL DEFAULT '',
  `Estado` int(11) NOT NULL,
  `Sesion` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`IdUsuario`),
  UNIQUE KEY `CodigoU` (`CodigoU`),
  UNIQUE KEY `NumDoc` (`NumDoc`),
  UNIQUE KEY `CorreoU` (`CorreoU`),
  KEY `FK_usuario_facultad` (`Facultad`),
  KEY `FK_usuario_escuela` (`Escuela`),
  KEY `FK_usuario_rol` (`Rol`),
  CONSTRAINT `FK_usuario_escuela` FOREIGN KEY (`Escuela`) REFERENCES `escuela` (`IdEscuela`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_usuario_facultad` FOREIGN KEY (`Facultad`) REFERENCES `facultad` (`IdFacultad`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_usuario_rol` FOREIGN KEY (`Rol`) REFERENCES `rol` (`IdRol`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.usuario: ~4 rows (aproximadamente)
INSERT INTO `usuario` (`IdUsuario`, `Nombre`, `Apellido`, `CodigoU`, `CorreoU`, `TipoDoc`, `NumDoc`, `Rol`, `Facultad`, `Escuela`, `Celular`, `Genero`, `Password`, `Estado`, `Sesion`) VALUES
	(5, 'STEVIE', 'MARCA', '2023076802', '1@upt.pe', 'DNI', '72405382', 1, 1, 1, '979793902', b'1', '123', 1, 0),
	(7, 'DAYAN', 'JAHUIRA', '2023076800', 'Dayan@hotmail.com', 'DNI', '12345678', 3, 1, 1, '123456789', b'1', '123', 0, 0),
	(10, 'STEVIE', 'AGUILAR', '2023076808', 'stevie@upt.edu', 'DNI', '12345679', 3, 1, 1, '987654321', b'1', '123', 1, 0),
	(11, 'CRISTIAN', 'MAMANI', '2023076801', 'A@upt.pe', 'DNI', '72405638', 3, 1, 1, '979739029', b'1', '123', 1, 0);

-- Volcando estructura para disparador sisintupt.trg_actualizar_horario_update
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER trg_actualizar_horario_update
AFTER UPDATE ON reserva
FOR EACH ROW
BEGIN
    DECLARE dia ENUM('Lunes','Martes','Miercoles','Jueves','Viernes','Sabado');

    SET dia = CASE DAYOFWEEK(NEW.fechaReserva)
        WHEN 2 THEN 'Lunes'
        WHEN 3 THEN 'Martes'
        WHEN 4 THEN 'Miercoles'
        WHEN 5 THEN 'Jueves'
        WHEN 6 THEN 'Viernes'
        WHEN 7 THEN 'Sabado'
        ELSE 'Lunes' -- Default si cae domingo
    END;

    IF NEW.estado = 'Aprobada' THEN
        UPDATE horarios h
        SET h.ocupado = 1
        WHERE h.espacio = NEW.espacio
          AND h.bloque = NEW.bloque
          AND h.diaSemana = dia;
    ELSEIF NEW.estado IN ('Pendiente','Cancelado') THEN
        UPDATE horarios h
        SET h.ocupado = 0
        WHERE h.espacio = NEW.espacio
          AND h.bloque = NEW.bloque
          AND h.diaSemana = dia;
    END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- Volcando estructura para disparador sisintupt.trg_crear_horarios
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER trg_crear_horarios
AFTER INSERT ON bloqueshorarios
FOR EACH ROW
BEGIN
    -- Insertar horarios automáticamente para cada espacio y cada día de la semana
    INSERT INTO horarios (espacio, bloque, diaSemana, ocupado)
    SELECT e.IdEspacio, NEW.IdBloque, d.dia, 0
    FROM espacio e
    CROSS JOIN (
        SELECT 'Lunes' AS dia
        UNION ALL SELECT 'Martes'
        UNION ALL SELECT 'Miercoles'
        UNION ALL SELECT 'Jueves'
        UNION ALL SELECT 'Viernes'
        UNION ALL SELECT 'Sabado'
    ) d;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- Volcando estructura para disparador sisintupt.trg_prioridad_reservas
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trg_prioridad_reservas` BEFORE UPDATE ON `reserva` FOR EACH ROW BEGIN
    DECLARE usuario_rol INT;
    DECLARE usuario_nombre VARCHAR(30);
    DECLARE usuario_apellido VARCHAR(30);
    DECLARE reservas_afectadas INT;
    
    -- Solo procesar si el estado cambió a "Aprobada"
    IF NEW.estado = 'Aprobada' AND OLD.estado = 'Pendiente' THEN
        
        -- Obtener rol y nombre del usuario
        SELECT Rol, Nombre, Apellido INTO usuario_rol, usuario_nombre, usuario_apellido 
        FROM usuario 
        WHERE IdUsuario = NEW.usuario;
        
        -- 1. SI SE APRUEBA UN PROFESOR
        IF usuario_rol = 1 THEN
            UPDATE reserva 
            SET estado = 'Rechazada',
                Motivo = CONCAT('Docente ', usuario_nombre, ' ', usuario_apellido, 
                               ' reservó el espacio para clase')
            WHERE espacio = NEW.espacio
            AND fechaReserva = NEW.fechaReserva
            AND bloque = NEW.bloque
            AND estado = 'Pendiente'
            AND IdReserva != NEW.IdReserva;
            
            SET NEW.Motivo = 'Reserva aprobada - Uso docente prioritario';
            
        -- 2. SI SE APRUEBA UN ESTUDIANTE
        ELSEIF usuario_rol = 2 THEN
            SELECT COUNT(*) INTO reservas_afectadas
            FROM reserva r
            JOIN usuario u ON r.usuario = u.IdUsuario
            WHERE r.espacio = NEW.espacio
            AND r.fechaReserva = NEW.fechaReserva
            AND r.bloque = NEW.bloque
            AND r.estado = 'Pendiente'
            AND u.Rol = 1;
            
            IF reservas_afectadas = 0 THEN
                UPDATE reserva 
                SET estado = 'Rechazada',
                    Motivo = 'Espacio ocupado - Otro usuario reservó primero'
                WHERE espacio = NEW.espacio
                AND fechaReserva = NEW.fechaReserva
                AND bloque = NEW.bloque
                AND estado = 'Pendiente'
                AND IdReserva != NEW.IdReserva;
                
                SET NEW.Motivo = 'Reserva confirmada - Espacio disponible';
            ELSE
                SET NEW.Motivo = 'Reserva aprobada - Sujeta a disponibilidad final';
            END IF;
        END IF;
        
    END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
