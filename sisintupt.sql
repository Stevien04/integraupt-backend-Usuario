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
  `IdAudit` int(11) NOT NULL AUTO_INCREMENT,
  `IdReserva` int(11) NOT NULL,
  `EstadoAnterior` varchar(50) DEFAULT NULL,
  `EstadoNuevo` varchar(50) DEFAULT NULL,
  `FechaCambio` datetime DEFAULT current_timestamp(),
  `UsuarioCambio` int(11) DEFAULT NULL,
  PRIMARY KEY (`IdAudit`),
  KEY `FK_auditoriareserva_reserva` (`IdReserva`),
  KEY `FK_auditoriareserva_usuario` (`UsuarioCambio`),
  CONSTRAINT `FK_auditoriareserva_reserva` FOREIGN KEY (`IdReserva`) REFERENCES `reserva` (`IdReserva`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_auditoriareserva_usuario` FOREIGN KEY (`UsuarioCambio`) REFERENCES `usuario` (`IdUsuario`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.auditoriareserva: ~0 rows (aproximadamente)

-- Volcando estructura para tabla sisintupt.bloqueshorarios
CREATE TABLE IF NOT EXISTS `bloqueshorarios` (
  `IdBloque` int(11) NOT NULL AUTO_INCREMENT,
  `Orden` int(11) NOT NULL,
  `Nombre` varchar(50) NOT NULL,
  `HoraInicio` time NOT NULL,
  `HoraFinal` time NOT NULL,
  PRIMARY KEY (`IdBloque`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.bloqueshorarios: ~8 rows (aproximadamente)
INSERT INTO `bloqueshorarios` (`IdBloque`, `Orden`, `Nombre`, `HoraInicio`, `HoraFinal`) VALUES
	(10, 1, 'B1', '08:00:00', '08:50:00'),
	(11, 2, 'B2', '08:50:00', '09:40:00'),
	(13, 3, 'B3', '09:40:00', '10:30:00'),
	(14, 4, 'B4', '10:30:00', '11:20:00'),
	(15, 5, 'B5', '11:20:00', '12:10:00'),
	(16, 6, 'B6', '12:10:00', '12:50:00'),
	(17, 7, 'B7', '13:40:00', '14:30:00'),
	(18, 8, 'B8', '14:30:00', '16:00:00');

-- Volcando estructura para tabla sisintupt.escuela
CREATE TABLE IF NOT EXISTS `escuela` (
  `IdEscuela` int(11) NOT NULL AUTO_INCREMENT,
  `IdFacultad` int(11) NOT NULL,
  `Nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`IdEscuela`),
  KEY `FK__facultad` (`IdFacultad`),
  CONSTRAINT `FK__facultad` FOREIGN KEY (`IdFacultad`) REFERENCES `facultad` (`IdFacultad`)
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
  `Ubicacion` text DEFAULT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=969 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.horarios: ~288 rows (aproximadamente)
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
	(464, 6, 13, 'Sabado', 0),
	(654, 1, 14, 'Lunes', 0),
	(655, 1, 14, 'Martes', 0),
	(656, 1, 14, 'Miercoles', 0),
	(657, 1, 14, 'Jueves', 0),
	(658, 1, 14, 'Viernes', 0),
	(659, 1, 14, 'Sabado', 0),
	(660, 2, 14, 'Lunes', 0),
	(661, 2, 14, 'Martes', 0),
	(662, 2, 14, 'Miercoles', 0),
	(663, 2, 14, 'Jueves', 0),
	(664, 2, 14, 'Viernes', 0),
	(665, 2, 14, 'Sabado', 0),
	(666, 3, 14, 'Lunes', 0),
	(667, 3, 14, 'Martes', 0),
	(668, 3, 14, 'Miercoles', 0),
	(669, 3, 14, 'Jueves', 0),
	(670, 3, 14, 'Viernes', 0),
	(671, 3, 14, 'Sabado', 0),
	(672, 4, 14, 'Lunes', 0),
	(673, 4, 14, 'Martes', 0),
	(674, 4, 14, 'Miercoles', 0),
	(675, 4, 14, 'Jueves', 0),
	(676, 4, 14, 'Viernes', 0),
	(677, 4, 14, 'Sabado', 0),
	(678, 5, 14, 'Lunes', 0),
	(679, 5, 14, 'Martes', 0),
	(680, 5, 14, 'Miercoles', 0),
	(681, 5, 14, 'Jueves', 0),
	(682, 5, 14, 'Viernes', 0),
	(683, 5, 14, 'Sabado', 0),
	(684, 6, 14, 'Lunes', 0),
	(685, 6, 14, 'Martes', 0),
	(686, 6, 14, 'Miercoles', 0),
	(687, 6, 14, 'Jueves', 0),
	(688, 6, 14, 'Viernes', 0),
	(689, 6, 14, 'Sabado', 0),
	(690, 7, 14, 'Lunes', 0),
	(691, 7, 14, 'Martes', 0),
	(692, 7, 14, 'Miercoles', 0),
	(693, 7, 14, 'Jueves', 0),
	(694, 7, 14, 'Viernes', 0),
	(695, 7, 14, 'Sabado', 0),
	(696, 8, 14, 'Lunes', 0),
	(697, 8, 14, 'Martes', 0),
	(698, 8, 14, 'Miercoles', 0),
	(699, 8, 14, 'Jueves', 0),
	(700, 8, 14, 'Viernes', 0),
	(701, 8, 14, 'Sabado', 0),
	(717, 1, 15, 'Lunes', 0),
	(718, 1, 15, 'Martes', 0),
	(719, 1, 15, 'Miercoles', 0),
	(720, 1, 15, 'Jueves', 0),
	(721, 1, 15, 'Viernes', 0),
	(722, 1, 15, 'Sabado', 0),
	(723, 2, 15, 'Lunes', 0),
	(724, 2, 15, 'Martes', 0),
	(725, 2, 15, 'Miercoles', 0),
	(726, 2, 15, 'Jueves', 0),
	(727, 2, 15, 'Viernes', 0),
	(728, 2, 15, 'Sabado', 0),
	(729, 3, 15, 'Lunes', 0),
	(730, 3, 15, 'Martes', 0),
	(731, 3, 15, 'Miercoles', 0),
	(732, 3, 15, 'Jueves', 0),
	(733, 3, 15, 'Viernes', 0),
	(734, 3, 15, 'Sabado', 0),
	(735, 4, 15, 'Lunes', 0),
	(736, 4, 15, 'Martes', 0),
	(737, 4, 15, 'Miercoles', 0),
	(738, 4, 15, 'Jueves', 0),
	(739, 4, 15, 'Viernes', 0),
	(740, 4, 15, 'Sabado', 0),
	(741, 5, 15, 'Lunes', 0),
	(742, 5, 15, 'Martes', 0),
	(743, 5, 15, 'Miercoles', 0),
	(744, 5, 15, 'Jueves', 0),
	(745, 5, 15, 'Viernes', 0),
	(746, 5, 15, 'Sabado', 0),
	(747, 6, 15, 'Lunes', 0),
	(748, 6, 15, 'Martes', 0),
	(749, 6, 15, 'Miercoles', 0),
	(750, 6, 15, 'Jueves', 0),
	(751, 6, 15, 'Viernes', 0),
	(752, 6, 15, 'Sabado', 0),
	(753, 7, 15, 'Lunes', 0),
	(754, 7, 15, 'Martes', 0),
	(755, 7, 15, 'Miercoles', 0),
	(756, 7, 15, 'Jueves', 0),
	(757, 7, 15, 'Viernes', 0),
	(758, 7, 15, 'Sabado', 0),
	(759, 8, 15, 'Lunes', 0),
	(760, 8, 15, 'Martes', 0),
	(761, 8, 15, 'Miercoles', 0),
	(762, 8, 15, 'Jueves', 0),
	(763, 8, 15, 'Viernes', 0),
	(764, 8, 15, 'Sabado', 0),
	(780, 1, 16, 'Lunes', 0),
	(781, 1, 16, 'Martes', 0),
	(782, 1, 16, 'Miercoles', 0),
	(783, 1, 16, 'Jueves', 0),
	(784, 1, 16, 'Viernes', 0),
	(785, 1, 16, 'Sabado', 0),
	(786, 2, 16, 'Lunes', 0),
	(787, 2, 16, 'Martes', 0),
	(788, 2, 16, 'Miercoles', 0),
	(789, 2, 16, 'Jueves', 0),
	(790, 2, 16, 'Viernes', 0),
	(791, 2, 16, 'Sabado', 0),
	(792, 3, 16, 'Lunes', 0),
	(793, 3, 16, 'Martes', 0),
	(794, 3, 16, 'Miercoles', 0),
	(795, 3, 16, 'Jueves', 0),
	(796, 3, 16, 'Viernes', 0),
	(797, 3, 16, 'Sabado', 0),
	(798, 4, 16, 'Lunes', 0),
	(799, 4, 16, 'Martes', 0),
	(800, 4, 16, 'Miercoles', 0),
	(801, 4, 16, 'Jueves', 0),
	(802, 4, 16, 'Viernes', 0),
	(803, 4, 16, 'Sabado', 0),
	(804, 5, 16, 'Lunes', 0),
	(805, 5, 16, 'Martes', 0),
	(806, 5, 16, 'Miercoles', 0),
	(807, 5, 16, 'Jueves', 0),
	(808, 5, 16, 'Viernes', 0),
	(809, 5, 16, 'Sabado', 0),
	(810, 6, 16, 'Lunes', 0),
	(811, 6, 16, 'Martes', 0),
	(812, 6, 16, 'Miercoles', 0),
	(813, 6, 16, 'Jueves', 0),
	(814, 6, 16, 'Viernes', 0),
	(815, 6, 16, 'Sabado', 0),
	(816, 7, 16, 'Lunes', 0),
	(817, 7, 16, 'Martes', 0),
	(818, 7, 16, 'Miercoles', 0),
	(819, 7, 16, 'Jueves', 0),
	(820, 7, 16, 'Viernes', 0),
	(821, 7, 16, 'Sabado', 0),
	(822, 8, 16, 'Lunes', 0),
	(823, 8, 16, 'Martes', 0),
	(824, 8, 16, 'Miercoles', 0),
	(825, 8, 16, 'Jueves', 0),
	(826, 8, 16, 'Viernes', 0),
	(827, 8, 16, 'Sabado', 0),
	(843, 1, 17, 'Lunes', 0),
	(844, 1, 17, 'Martes', 0),
	(845, 1, 17, 'Miercoles', 0),
	(846, 1, 17, 'Jueves', 0),
	(847, 1, 17, 'Viernes', 0),
	(848, 1, 17, 'Sabado', 0),
	(849, 2, 17, 'Lunes', 0),
	(850, 2, 17, 'Martes', 0),
	(851, 2, 17, 'Miercoles', 0),
	(852, 2, 17, 'Jueves', 0),
	(853, 2, 17, 'Viernes', 0),
	(854, 2, 17, 'Sabado', 0),
	(855, 3, 17, 'Lunes', 0),
	(856, 3, 17, 'Martes', 0),
	(857, 3, 17, 'Miercoles', 0),
	(858, 3, 17, 'Jueves', 0),
	(859, 3, 17, 'Viernes', 0),
	(860, 3, 17, 'Sabado', 0),
	(861, 4, 17, 'Lunes', 0),
	(862, 4, 17, 'Martes', 0),
	(863, 4, 17, 'Miercoles', 0),
	(864, 4, 17, 'Jueves', 0),
	(865, 4, 17, 'Viernes', 0),
	(866, 4, 17, 'Sabado', 0),
	(867, 5, 17, 'Lunes', 0),
	(868, 5, 17, 'Martes', 0),
	(869, 5, 17, 'Miercoles', 0),
	(870, 5, 17, 'Jueves', 0),
	(871, 5, 17, 'Viernes', 0),
	(872, 5, 17, 'Sabado', 0),
	(873, 6, 17, 'Lunes', 0),
	(874, 6, 17, 'Martes', 0),
	(875, 6, 17, 'Miercoles', 0),
	(876, 6, 17, 'Jueves', 0),
	(877, 6, 17, 'Viernes', 0),
	(878, 6, 17, 'Sabado', 0),
	(879, 7, 17, 'Lunes', 0),
	(880, 7, 17, 'Martes', 0),
	(881, 7, 17, 'Miercoles', 0),
	(882, 7, 17, 'Jueves', 0),
	(883, 7, 17, 'Viernes', 0),
	(884, 7, 17, 'Sabado', 0),
	(885, 8, 17, 'Lunes', 0),
	(886, 8, 17, 'Martes', 0),
	(887, 8, 17, 'Miercoles', 0),
	(888, 8, 17, 'Jueves', 0),
	(889, 8, 17, 'Viernes', 0),
	(890, 8, 17, 'Sabado', 0),
	(906, 1, 18, 'Lunes', 0),
	(907, 1, 18, 'Martes', 0),
	(908, 1, 18, 'Miercoles', 0),
	(909, 1, 18, 'Jueves', 0),
	(910, 1, 18, 'Viernes', 0),
	(911, 1, 18, 'Sabado', 0),
	(912, 2, 18, 'Lunes', 0),
	(913, 2, 18, 'Martes', 0),
	(914, 2, 18, 'Miercoles', 0),
	(915, 2, 18, 'Jueves', 0),
	(916, 2, 18, 'Viernes', 0),
	(917, 2, 18, 'Sabado', 0),
	(918, 3, 18, 'Lunes', 0),
	(919, 3, 18, 'Martes', 0),
	(920, 3, 18, 'Miercoles', 0),
	(921, 3, 18, 'Jueves', 0),
	(922, 3, 18, 'Viernes', 0),
	(923, 3, 18, 'Sabado', 0),
	(924, 4, 18, 'Lunes', 0),
	(925, 4, 18, 'Martes', 0),
	(926, 4, 18, 'Miercoles', 0),
	(927, 4, 18, 'Jueves', 0),
	(928, 4, 18, 'Viernes', 0),
	(929, 4, 18, 'Sabado', 0),
	(930, 5, 18, 'Lunes', 0),
	(931, 5, 18, 'Martes', 0),
	(932, 5, 18, 'Miercoles', 0),
	(933, 5, 18, 'Jueves', 0),
	(934, 5, 18, 'Viernes', 0),
	(935, 5, 18, 'Sabado', 0),
	(936, 6, 18, 'Lunes', 0),
	(937, 6, 18, 'Martes', 0),
	(938, 6, 18, 'Miercoles', 0),
	(939, 6, 18, 'Jueves', 0),
	(940, 6, 18, 'Viernes', 0),
	(941, 6, 18, 'Sabado', 0),
	(942, 7, 18, 'Lunes', 0),
	(943, 7, 18, 'Martes', 0),
	(944, 7, 18, 'Miercoles', 0),
	(945, 7, 18, 'Jueves', 0),
	(946, 7, 18, 'Viernes', 0),
	(947, 7, 18, 'Sabado', 0),
	(948, 8, 18, 'Lunes', 0),
	(949, 8, 18, 'Martes', 0),
	(950, 8, 18, 'Miercoles', 0),
	(951, 8, 18, 'Jueves', 0),
	(952, 8, 18, 'Viernes', 0),
	(953, 8, 18, 'Sabado', 0);

-- Volcando estructura para tabla sisintupt.horario_curso
CREATE TABLE IF NOT EXISTS `horario_curso` (
  `IdHorarioCurso` int(11) NOT NULL AUTO_INCREMENT,
  `Curso` varchar(100) NOT NULL,
  `Docente` int(11) NOT NULL,
  `Espacio` int(11) NOT NULL,
  `Bloque` int(11) NOT NULL,
  `DiaSemana` enum('Lunes','Martes','Miercoles','Jueves','Viernes','Sabado') NOT NULL,
  `FechaInicio` date NOT NULL,
  `FechaFin` date NOT NULL,
  `Estado` tinyint(1) NOT NULL DEFAULT 1,
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
  `IdRol` int(11) NOT NULL,
  `Nombre` varchar(15) NOT NULL,
  PRIMARY KEY (`IdRol`),
  UNIQUE KEY `Nombre` (`Nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.rol: ~4 rows (aproximadamente)
INSERT INTO `rol` (`IdRol`, `Nombre`) VALUES
	(1, 'Profesor'),
	(2, 'Estudiante'),
	(3, 'Administrador'),
	(4, 'Supervisor');

-- Volcando estructura para tabla sisintupt.usuario
CREATE TABLE IF NOT EXISTS `usuario` (
  `IdUsuario` int(11) NOT NULL AUTO_INCREMENT,
  `Nombre` varchar(30) NOT NULL,
  `Apellido` varchar(30) NOT NULL,
  `CodigoU` varchar(20) NOT NULL DEFAULT '',
  `CorreoU` varchar(30) NOT NULL,
  `TipoDoc` varchar(30) NOT NULL,
  `NumDoc` varchar(20) NOT NULL DEFAULT '',
  `Rol` int(11) NOT NULL,
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
  CONSTRAINT `FK_usuario_escuela` FOREIGN KEY (`Escuela`) REFERENCES `escuela` (`IdEscuela`),
  CONSTRAINT `FK_usuario_facultad` FOREIGN KEY (`Facultad`) REFERENCES `facultad` (`IdFacultad`),
  CONSTRAINT `FK_usuario_rol` FOREIGN KEY (`Rol`) REFERENCES `rol` (`IdRol`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.usuario: ~4 rows (aproximadamente)
INSERT INTO `usuario` (`IdUsuario`, `Nombre`, `Apellido`, `CodigoU`, `CorreoU`, `TipoDoc`, `NumDoc`, `Rol`, `Facultad`, `Escuela`, `Celular`, `Genero`, `Password`, `Estado`, `Sesion`) VALUES
	(5, 'STEVIE', 'MARCA', '2023076802', '1@upt.pe', 'DNI', '72405382', 2, 1, 1, '979793902', b'1', '123', 1, 0),
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
