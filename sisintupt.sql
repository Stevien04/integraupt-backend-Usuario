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

-- Volcando estructura para tabla sisintupt.auditoria
CREATE TABLE IF NOT EXISTS `auditoria` (
  `id` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
  `accion` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `estado` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `ip_address` varchar(45) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `metadata` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `modulo` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `motivo` text COLLATE utf8mb4_general_ci,
  `usuario_id` varchar(36) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `usuario_nombre` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `auditoria_chk_1` CHECK (json_valid(`metadata`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.auditoria: ~0 rows (aproximadamente)

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
  `IdEspacio` int NOT NULL AUTO_INCREMENT,
  `Codigo` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `Nombre` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `Ubicacion` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.espacio: ~4 rows (aproximadamente)
INSERT INTO `espacio` (`IdEspacio`, `Codigo`, `Nombre`, `Ubicacion`, `Tipo`, `Capacidad`, `Equipamiento`, `Facultad`, `Escuela`, `Estado`) VALUES
	(1, 'ESP-001', 'a', '123', 'Laboratorio', 15, 'Equipos de computo, proyector', 1, 1, 1),
	(2, 'P-100', 'bbbbb', '1', 'Salon', 10, '...', 1, 2, 1),
	(12, 'aaaaa', 'aaaaa', 'aaaaa', 'Laboratorio', 22, 'aaaaa', 1, 2, 0),
	(13, 'bbbbb', 'bbbbb', 'bbbbb', 'Laboratorio', 10, 'bbbbb', 1, 2, 1);

-- Volcando estructura para tabla sisintupt.espacios
CREATE TABLE IF NOT EXISTS `espacios` (
  `id` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
  `capacidad` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `escuela` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `estado` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `facultad` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `recursos` text COLLATE utf8mb4_general_ci,
  `tipo` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `ubicacion` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.espacios: ~0 rows (aproximadamente)

-- Volcando estructura para tabla sisintupt.eventos
CREATE TABLE IF NOT EXISTS `eventos` (
  `id` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `descripcion` text COLLATE utf8mb4_general_ci,
  `estado` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `fecha_fin` datetime(6) DEFAULT NULL,
  `fecha_inicio` datetime(6) NOT NULL,
  `imagen_url` text COLLATE utf8mb4_general_ci,
  `organizador` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `tipo` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `titulo` varchar(200) COLLATE utf8mb4_general_ci NOT NULL,
  `ubicacion` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.eventos: ~0 rows (aproximadamente)

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
	(258, 1, 10, 'Lunes', 0),
	(259, 1, 10, 'Martes', 0),
	(260, 1, 10, 'Miercoles', 0),
	(261, 1, 10, 'Jueves', 0),
	(262, 1, 10, 'Viernes', 1),
	(263, 1, 10, 'Sabado', 1),
	(321, 1, 11, 'Lunes', 0),
	(322, 1, 11, 'Martes', 0),
	(323, 1, 11, 'Miercoles', 0),
	(324, 1, 11, 'Jueves', 0),
	(325, 1, 11, 'Viernes', 0),
	(326, 1, 11, 'Sabado', 0),
	(417, 1, 13, 'Lunes', 0),
	(418, 1, 13, 'Martes', 0),
	(419, 1, 13, 'Miercoles', 0),
	(420, 1, 13, 'Jueves', 0),
	(421, 1, 13, 'Viernes', 0),
	(422, 1, 13, 'Sabado', 0);

-- Volcando estructura para tabla sisintupt.perfiles
CREATE TABLE IF NOT EXISTS `perfiles` (
  `id` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
  `apellidos` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `avatar_url` text COLLATE utf8mb4_general_ci,
  `celular` varchar(15) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `codigo` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `escuela` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `estado` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `facultad` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `genero` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `nombres` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `numero_documento` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `rol` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `tipo_documento` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `tipo_login` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_65h771rtmbrasq1ceu7aoaw1k` (`codigo`),
  UNIQUE KEY `UK_rpu4j8kj0tyad8uf76f9yadnc` (`email`),
  UNIQUE KEY `UK_tbyo1m0hhj2avvhndkaib0ksy` (`numero_documento`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.perfiles: ~0 rows (aproximadamente)

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

-- Volcando estructura para tabla sisintupt.reservas
CREATE TABLE IF NOT EXISTS `reservas` (
  `id` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
  `aprobado_por` varchar(36) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `ciclo` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `curso` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `espacio_id` varchar(36) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `estado` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `fecha` date NOT NULL,
  `hora_fin` time(6) NOT NULL,
  `hora_inicio` time(6) NOT NULL,
  `motivo` text COLLATE utf8mb4_general_ci,
  `motivo_rechazo` text COLLATE utf8mb4_general_ci,
  `tipo` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `usuario_id` varchar(36) COLLATE utf8mb4_general_ci DEFAULT NULL,
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
  `IdRol` int NOT NULL,
  `Nombre` varchar(15) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`IdRol`),
  UNIQUE KEY `Nombre` (`Nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.rol: ~3 rows (aproximadamente)
INSERT INTO `rol` (`IdRol`, `Nombre`) VALUES
	(3, 'Administrador'),
	(2, 'Estudiante'),
	(1, 'Profesor');

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
	(5, 'STEVIE', 'MARCA', '2023076802', '1@upt.pe', 'DNI', '72405382', 1, 3, 9, '979793902', b'1', '123', 1, 0),
	(7, 'DAYAN', 'JAHUIRA', '2022075749', 'Dayan@hotmail.com', 'DNI', '12345678', 3, 1, 1, '123456789', b'1', '123', 0, 0),
	(10, 'STEVIE', 'AGUILAR', '2023076808', 'stevie@upt.edu', 'DNI', '12345679', 3, 1, 1, '987654321', b'1', '123', 1, 0),
	(11, 'CRISTIAN', 'MAMANI', '2023076801', 'A@upt.pe', 'DNI', '72405638', 3, 1, 1, '979739029', b'1', '123', 1, 0);

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
