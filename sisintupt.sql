-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versión del servidor:         8.0.19 - MySQL Community Server - GPL
-- SO del servidor:              Win64
-- HeidiSQL Versión:             12.10.0.7000
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
CREATE DATABASE IF NOT EXISTS `sisintupt` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `sisintupt`;

-- Volcando estructura para evento sisintupt.aplicar_horarios_fijos
DELIMITER //
CREATE EVENT `aplicar_horarios_fijos` ON SCHEDULE EVERY 1 DAY STARTS '2025-10-29 07:14:02' ON COMPLETION NOT PRESERVE ENABLE DO BEGIN
    UPDATE horarios h
    JOIN horario_curso hc ON h.espacio = hc.Espacio
                           AND h.bloque = hc.Bloque
                           AND h.diaSemana = hc.DiaSemana
    SET h.ocupado = 1
    WHERE CURDATE() BETWEEN hc.FechaInicio AND hc.FechaFin
      AND hc.Estado = 1;
END//
DELIMITER ;

-- Volcando estructura para tabla sisintupt.auditoriareserva
CREATE TABLE IF NOT EXISTS `auditoriareserva` (
  `IdAudit` int NOT NULL AUTO_INCREMENT,
  `IdReserva` int NOT NULL,
  `EstadoAnterior` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `EstadoNuevo` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `FechaCambio` datetime DEFAULT CURRENT_TIMESTAMP,
  `UsuarioCambio` int DEFAULT NULL,
  PRIMARY KEY (`IdAudit`),
  KEY `FK_auditoriareserva_reserva` (`IdReserva`),
  KEY `FK_auditoriareserva_usuario` (`UsuarioCambio`),
  CONSTRAINT `FK_auditoriareserva_reserva` FOREIGN KEY (`IdReserva`) REFERENCES `reserva` (`IdReserva`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_auditoriareserva_usuario` FOREIGN KEY (`UsuarioCambio`) REFERENCES `usuario` (`IdUsuario`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.auditoriareserva: ~0 rows (aproximadamente)

-- Volcando estructura para tabla sisintupt.bloqueshorarios
CREATE TABLE IF NOT EXISTS `bloqueshorarios` (
  `IdBloque` int NOT NULL AUTO_INCREMENT,
  `Orden` int NOT NULL,
  `Nombre` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
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
  `IdEscuela` int NOT NULL AUTO_INCREMENT,
  `IdFacultad` int NOT NULL,
  `Nombre` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`IdEscuela`),
  KEY `FK__facultad` (`IdFacultad`),
  CONSTRAINT `FK__facultad` FOREIGN KEY (`IdFacultad`) REFERENCES `facultad` (`IdFacultad`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.escuela: ~9 rows (aproximadamente)
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
  `IdEspacio` int NOT NULL AUTO_INCREMENT,
  `Codigo` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `Nombre` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `Ubicacion` text COLLATE utf8mb4_general_ci,
  `Tipo` enum('Laboratorio','Salon') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'Laboratorio',
  `Capacidad` int NOT NULL,
  `Equipamiento` text COLLATE utf8mb4_general_ci,
  `Facultad` int NOT NULL,
  `Escuela` int NOT NULL,
  `Estado` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`IdEspacio`),
  UNIQUE KEY `Codigo` (`Codigo`),
  KEY `FK_espacio_facultad` (`Facultad`),
  KEY `FK_espacio_escuela` (`Escuela`),
  CONSTRAINT `FK_espacio_escuela` FOREIGN KEY (`Escuela`) REFERENCES `escuela` (`IdEscuela`),
  CONSTRAINT `FK_espacio_facultad` FOREIGN KEY (`Facultad`) REFERENCES `facultad` (`IdFacultad`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.espacio: ~8 rows (aproximadamente)
INSERT INTO `espacio` (`IdEspacio`, `Codigo`, `Nombre`, `Ubicacion`, `Tipo`, `Capacidad`, `Equipamiento`, `Facultad`, `Escuela`, `Estado`) VALUES
	(1, 'ESP-001', 'a', NULL, 'Laboratorio', 15, 'Equipos de computo, proyector', 1, 1, 1),
	(2, 'ESP-002', 'LAB C', NULL, 'Laboratorio', 15, 'Computadoras, software especializado', 1, 1, 0),
	(3, 'ESP-003', 'LAB D', NULL, 'Laboratorio', 15, 'Equipos de redes, servidores', 1, 1, 0),
	(4, 'ESP-004', 'LAB F', NULL, 'Laboratorio', 15, 'PCs de alto rendimiento', 1, 1, 1),
	(5, 'ESP-005', 'LAB E', NULL, 'Laboratorio', 15, 'Laboratorio multimedia', 1, 1, 1),
	(6, 'ESP-006', 'Aula-301', NULL, 'Salon', 15, 'Pizarra acrílica, proyector', 2, 7, 1),
	(7, 'ESP-007', 'LAB Ñ', NULL, 'Laboratorio', 20, 'Equipos de última generación', 1, 1, 1),
	(8, 'ESP-008', 'LAB A', NULL, 'Laboratorio', 20, 'Computadoras, impresora 3D', 1, 1, 1);

-- Volcando estructura para tabla sisintupt.facultad
CREATE TABLE IF NOT EXISTS `facultad` (
  `IdFacultad` int NOT NULL AUTO_INCREMENT,
  `Nombre` varchar(10) COLLATE utf8mb4_general_ci NOT NULL,
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
  `IdHorario` int NOT NULL AUTO_INCREMENT,
  `espacio` int NOT NULL,
  `bloque` int NOT NULL,
  `diaSemana` enum('Lunes','Martes','Miercoles','Jueves','Viernes','Sabado') COLLATE utf8mb4_general_ci NOT NULL,
  `ocupado` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`IdHorario`) USING BTREE,
  KEY `FK_horario_espacio` (`espacio`) USING BTREE,
  KEY `FK_horario_bloque` (`bloque`) USING BTREE,
  CONSTRAINT `FK_horario_bloque` FOREIGN KEY (`bloque`) REFERENCES `bloqueshorarios` (`IdBloque`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_horario_espacio` FOREIGN KEY (`espacio`) REFERENCES `espacio` (`IdEspacio`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=465 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.horarios: ~144 rows (aproximadamente)
INSERT INTO `horarios` (`IdHorario`, `espacio`, `bloque`, `diaSemana`, `ocupado`) VALUES
	(258, 1, 10, 'Lunes', 1),
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

-- Volcando estructura para tabla sisintupt.horario_curso
CREATE TABLE IF NOT EXISTS `horario_curso` (
  `IdHorarioCurso` int NOT NULL AUTO_INCREMENT,
  `Curso` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `Docente` int NOT NULL,
  `Espacio` int NOT NULL,
  `Bloque` int NOT NULL,
  `DiaSemana` enum('Lunes','Martes','Miercoles','Jueves','Viernes','Sabado') COLLATE utf8mb4_general_ci NOT NULL,
  `FechaInicio` date NOT NULL,
  `FechaFin` date NOT NULL,
  `Estado` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`IdHorarioCurso`),
  KEY `FK_horario_curso_usuario` (`Docente`),
  KEY `FK_horario_curso_espacio` (`Espacio`),
  KEY `FK_horario_curso_bloqueshorarios` (`Bloque`),
  CONSTRAINT `FK_horario_curso_bloqueshorarios` FOREIGN KEY (`Bloque`) REFERENCES `bloqueshorarios` (`IdBloque`),
  CONSTRAINT `FK_horario_curso_espacio` FOREIGN KEY (`Espacio`) REFERENCES `espacio` (`IdEspacio`),
  CONSTRAINT `FK_horario_curso_usuario` FOREIGN KEY (`Docente`) REFERENCES `usuario` (`IdUsuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.horario_curso: ~0 rows (aproximadamente)

-- Volcando estructura para evento sisintupt.liberar_horarios_fijos
DELIMITER //
CREATE EVENT `liberar_horarios_fijos` ON SCHEDULE EVERY 1 DAY STARTS '2025-10-29 07:14:02' ON COMPLETION NOT PRESERVE ENABLE DO BEGIN
    UPDATE horario_curso
    SET Estado = 0
    WHERE FechaFin < CURDATE();
END//
DELIMITER ;

-- Volcando estructura para tabla sisintupt.reserva
CREATE TABLE IF NOT EXISTS `reserva` (
  `IdReserva` int NOT NULL AUTO_INCREMENT,
  `usuario` int NOT NULL,
  `espacio` int NOT NULL,
  `fechaReserva` date NOT NULL,
  `bloque` int NOT NULL,
  `estado` varchar(50) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'Pendiente',
  `fechaSolicitud` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Descripcion` tinytext COLLATE utf8mb4_general_ci NOT NULL,
  `Motivo` tinytext COLLATE utf8mb4_general_ci,
  PRIMARY KEY (`IdReserva`),
  KEY `FK_reserva_espacio` (`espacio`),
  KEY `FK_reserva_usuario` (`usuario`),
  KEY `FK_reserva_bloqueshorarios` (`bloque`),
  CONSTRAINT `FK_reserva_bloqueshorarios` FOREIGN KEY (`bloque`) REFERENCES `bloqueshorarios` (`IdBloque`),
  CONSTRAINT `FK_reserva_espacio` FOREIGN KEY (`espacio`) REFERENCES `espacio` (`IdEspacio`),
  CONSTRAINT `FK_reserva_usuario` FOREIGN KEY (`usuario`) REFERENCES `usuario` (`IdUsuario`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.reserva: ~2 rows (aproximadamente)
INSERT INTO `reserva` (`IdReserva`, `usuario`, `espacio`, `fechaReserva`, `bloque`, `estado`, `fechaSolicitud`, `Descripcion`, `Motivo`) VALUES
	(19, 5, 1, '2025-09-15', 10, 'Aprobada', '2025-09-15 22:44:01', 'Curso:1-Tema:1', NULL),
	(20, 5, 1, '2025-09-16', 10, 'Cancelado', '2025-09-16 00:33:02', 'Curso:Base de Datos-Tema:Jugar', NULL);

-- Volcando estructura para evento sisintupt.reset_horarios_domingo
DELIMITER //
CREATE EVENT `reset_horarios_domingo` ON SCHEDULE EVERY 1 WEEK STARTS '2025-09-14 00:00:00' ON COMPLETION NOT PRESERVE ENABLE DO UPDATE horarios h
JOIN reserva r ON h.espacio = r.espacio AND h.bloque = r.bloque
SET h.ocupado = 0//
DELIMITER ;

-- Volcando estructura para tabla sisintupt.rol
CREATE TABLE IF NOT EXISTS `rol` (
  `IdRol` int NOT NULL,
  `Nombre` varchar(15) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`IdRol`),
  UNIQUE KEY `Nombre` (`Nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.rol: ~0 rows (aproximadamente)
INSERT INTO `rol` (`IdRol`, `Nombre`) VALUES
	(3, 'Administrador'),
	(2, 'Estudiante'),
	(1, 'Profesor'),
	(4, 'Supervisor');

-- Volcando estructura para tabla sisintupt.usuario
CREATE TABLE IF NOT EXISTS `usuario` (
  `IdUsuario` int NOT NULL AUTO_INCREMENT,
  `Nombre` varchar(30) COLLATE utf8mb4_general_ci NOT NULL,
  `Apellido` varchar(30) COLLATE utf8mb4_general_ci NOT NULL,
  `CodigoU` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `CorreoU` varchar(30) COLLATE utf8mb4_general_ci NOT NULL,
  `TipoDoc` varchar(30) COLLATE utf8mb4_general_ci NOT NULL,
  `NumDoc` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `Rol` int NOT NULL,
  `Facultad` int NOT NULL,
  `Escuela` int NOT NULL,
  `Celular` varchar(11) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `Genero` bit(1) DEFAULT NULL,
  `Password` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `Estado` int NOT NULL,
  `Sesion` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`IdUsuario`),
  UNIQUE KEY `CodigoU` (`CodigoU`),
  UNIQUE KEY `NumDoc` (`NumDoc`),
  UNIQUE KEY `CorreoU` (`CorreoU`),
  KEY `FK_usuario_facultad` (`Facultad`),
  KEY `FK_usuario_escuela` (`Escuela`),
  KEY `FK_usuario_rol` (`Rol`),
  CONSTRAINT `FK_usuario_escuela` FOREIGN KEY (`Escuela`) REFERENCES `escuela` (`IdEscuela`),
  CONSTRAINT `FK_usuario_facultad` FOREIGN KEY (`Facultad`) REFERENCES `facultad` (`IdFacultad`),
  CONSTRAINT `FK_usuario_rol` FOREIGN KEY (`Rol`) REFERENCES `rol` (`IdRol`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.usuario: ~4 rows (aproximadamente)
INSERT INTO `usuario` (`IdUsuario`, `Nombre`, `Apellido`, `CodigoU`, `CorreoU`, `TipoDoc`, `NumDoc`, `Rol`, `Facultad`, `Escuela`, `Celular`, `Genero`, `Password`, `Estado`, `Sesion`) VALUES
	(5, 'STEVIE', 'MARCA', '2023076802', '1@upt.pe', 'DNI', '72405382', 1, 1, 1, '979793902', b'1', 'APRt0kF8p6Kv+q8Zw7EcG/4Ob7WXZE3pNsNzByTy6mg=', 1, 0),
	(7, 'DAYAN', 'JAHUIRA', '2022075749', 'Dayan@hotmail.com', 'DNI', '12345678', 3, 1, 1, '123456789', b'1', '123', 0, 0),
	(10, 'STEVIE', 'AGUILAR', '2023076808', 'stevie@upt.edu', 'DNI', '12345679', 3, 1, 1, '987654321', b'1', 'APRt0kF8p6Kv+q8Zw7EcG/4Ob7WXZE3pNsNzByTy6mg=', 0, 0),
	(11, 'CRISTIAN', 'MAMANI', '2023076801', 'A@upt.pe', 'DNI', '72405638', 3, 1, 1, '979739029', b'1', 'enyx1M2OSSqIcCCNf5Cp3nmup3XCZCqT6mB5ji6kGII=', 1, 0);

-- Volcando estructura para disparador sisintupt.trg_actualizar_horario_update
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trg_actualizar_horario_update` AFTER UPDATE ON `reserva` FOR EACH ROW BEGIN
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

-- Volcando estructura para disparador sisintupt.trg_auditoria_reserva
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trg_auditoria_reserva` AFTER UPDATE ON `reserva` FOR EACH ROW BEGIN
    IF OLD.estado <> NEW.estado THEN
        INSERT INTO auditoriaReserva (IdReserva, EstadoAnterior, EstadoNuevo, UsuarioCambio)
        VALUES (NEW.IdReserva, OLD.estado, NEW.estado, NEW.usuario);
    END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- Volcando estructura para disparador sisintupt.trg_crear_horarios
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trg_crear_horarios` AFTER INSERT ON `bloqueshorarios` FOR EACH ROW BEGIN
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
        
        -- 1. SI SE APRUEBA UN PROFESOR (Rol 1)
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
            
        -- 2. SI SE APRUEBA UN ESTUDIANTE (Rol 2)
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
