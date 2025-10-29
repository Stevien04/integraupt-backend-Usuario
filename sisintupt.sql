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

-- Volcando estructura para tabla sisintupt.auditoriareserva
CREATE TABLE IF NOT EXISTS `auditoriareserva` (
  `IdAudit` int(11) NOT NULL AUTO_INCREMENT,
  `IdReserva` int(11) NOT NULL,
  `EstadoAnterior` varchar(50) DEFAULT NULL,
  `EstadoNuevo` varchar(50) DEFAULT NULL,
  `FechaCambio` datetime DEFAULT current_timestamp(),
  `UsuarioCambio` int(11) DEFAULT NULL,
  PRIMARY KEY (`IdAudit`),
  KEY `IdReserva` (`IdReserva`),
  KEY `UsuarioCambio` (`UsuarioCambio`),
  CONSTRAINT `auditoriareserva_ibfk_1` FOREIGN KEY (`IdReserva`) REFERENCES `reserva` (`IdReserva`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `auditoriareserva_ibfk_2` FOREIGN KEY (`UsuarioCambio`) REFERENCES `usuario` (`IdUsuario`) ON DELETE SET NULL ON UPDATE CASCADE
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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.bloqueshorarios: ~1 rows (aproximadamente)
INSERT INTO `bloqueshorarios` (`IdBloque`, `Orden`, `Nombre`, `HoraInicio`, `HoraFinal`) VALUES
	(1, 1, '1', '02:00:00', '04:00:00');

-- Volcando estructura para tabla sisintupt.escuela
CREATE TABLE IF NOT EXISTS `escuela` (
  `IdEscuela` int(11) NOT NULL AUTO_INCREMENT,
  `IdFacultad` int(11) NOT NULL,
  `Nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`IdEscuela`),
  KEY `FK__facultad` (`IdFacultad`),
  CONSTRAINT `FK__facultad` FOREIGN KEY (`IdFacultad`) REFERENCES `facultad` (`IdFacultad`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.escuela: ~1 rows (aproximadamente)
INSERT INTO `escuela` (`IdEscuela`, `IdFacultad`, `Nombre`) VALUES
	(1, 1, 'EPIS');

-- Volcando estructura para tabla sisintupt.espacio
CREATE TABLE IF NOT EXISTS `espacio` (
  `IdEspacio` int(11) NOT NULL AUTO_INCREMENT,
  `Codigo` varchar(20) NOT NULL DEFAULT '',
  `Nombre` varchar(100) NOT NULL,
  `Tipo` varchar(20) NOT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.espacio: ~1 rows (aproximadamente)
INSERT INTO `espacio` (`IdEspacio`, `Codigo`, `Nombre`, `Tipo`, `Capacidad`, `Equipamiento`, `Facultad`, `Escuela`, `Estado`) VALUES
	(1, 'P-301', 'P-301', 'Salon', 15, 'COOL', 1, 1, 1);

-- Volcando estructura para tabla sisintupt.facultad
CREATE TABLE IF NOT EXISTS `facultad` (
  `IdFacultad` int(11) NOT NULL AUTO_INCREMENT,
  `Nombre` varchar(10) NOT NULL,
  PRIMARY KEY (`IdFacultad`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.facultad: ~1 rows (aproximadamente)
INSERT INTO `facultad` (`IdFacultad`, `Nombre`) VALUES
	(1, 'FAING');

-- Volcando estructura para tabla sisintupt.horarios
CREATE TABLE IF NOT EXISTS `horarios` (
  `IdHorario` int(11) NOT NULL AUTO_INCREMENT,
  `espacio` int(11) NOT NULL,
  `bloque` int(11) NOT NULL,
  `diaSemana` enum('Lunes','Martes','Miercoles','Jueves','Viernes','Sabado') NOT NULL,
  `ocupado` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`IdHorario`),
  KEY `FK_horario_espacio` (`espacio`),
  KEY `FK_horario_bloque` (`bloque`),
  CONSTRAINT `FK_horario_bloque` FOREIGN KEY (`bloque`) REFERENCES `bloqueshorarios` (`IdBloque`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_horario_espacio` FOREIGN KEY (`espacio`) REFERENCES `espacio` (`IdEspacio`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.horarios: ~1 rows (aproximadamente)
INSERT INTO `horarios` (`IdHorario`, `espacio`, `bloque`, `diaSemana`, `ocupado`) VALUES
	(1, 1, 1, 'Lunes', 0);

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
  KEY `FK_reserva_usuario` (`usuario`),
  KEY `FK_reserva_espacio` (`espacio`),
  KEY `FK_reserva_bloque` (`bloque`),
  CONSTRAINT `FK_reserva_bloque` FOREIGN KEY (`bloque`) REFERENCES `bloqueshorarios` (`IdBloque`),
  CONSTRAINT `FK_reserva_espacio` FOREIGN KEY (`espacio`) REFERENCES `espacio` (`IdEspacio`),
  CONSTRAINT `FK_reserva_usuario` FOREIGN KEY (`usuario`) REFERENCES `usuario` (`IdUsuario`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.reserva: ~1 rows (aproximadamente)
INSERT INTO `reserva` (`IdReserva`, `usuario`, `espacio`, `fechaReserva`, `bloque`, `estado`, `fechaSolicitud`, `Descripcion`, `Motivo`) VALUES
	(1, 1, 1, '2025-10-29', 1, 'Pendiente', '2025-10-29 01:15:33', 'COOL\r\n', NULL);

-- Volcando estructura para evento sisintupt.reset_horarios_domingo
DELIMITER //
CREATE EVENT `reset_horarios_domingo` ON SCHEDULE EVERY 1 WEEK STARTS '2025-09-14 00:00:00' ON COMPLETION NOT PRESERVE ENABLE DO UPDATE horarios SET ocupado = 0//
DELIMITER ;

-- Volcando estructura para tabla sisintupt.rol
CREATE TABLE IF NOT EXISTS `rol` (
  `IdRol` int(11) NOT NULL,
  `Nombre` varchar(15) NOT NULL,
  PRIMARY KEY (`IdRol`),
  UNIQUE KEY `Nombre` (`Nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.rol: ~0 rows (aproximadamente)
INSERT INTO `rol` (`IdRol`, `Nombre`) VALUES
	(1, 'TECNICO');

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
  CONSTRAINT `FK_usuario_escuela` FOREIGN KEY (`Escuela`) REFERENCES `escuela` (`IdEscuela`),
  CONSTRAINT `FK_usuario_facultad` FOREIGN KEY (`Facultad`) REFERENCES `facultad` (`IdFacultad`),
  CONSTRAINT `FK_usuario_rol` FOREIGN KEY (`Rol`) REFERENCES `rol` (`IdRol`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla sisintupt.usuario: ~1 rows (aproximadamente)
INSERT INTO `usuario` (`IdUsuario`, `Nombre`, `Apellido`, `CodigoU`, `CorreoU`, `TipoDoc`, `NumDoc`, `Rol`, `Facultad`, `Escuela`, `Celular`, `Genero`, `Password`, `Estado`, `Sesion`) VALUES
	(1, 'Stevie', 'Marca', '2023076802', 'S@GMAIL.COM', 'DNI\r\n', '72405382', 1, 1, 1, '44', b'1', '123', 1, 0);

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
        ELSE 'Lunes'
    END;
    IF NEW.estado = 'Aprobada' THEN
        UPDATE horarios h
        SET h.ocupado = 1
        WHERE h.espacio = NEW.espacio AND h.bloque = NEW.bloque AND h.diaSemana = dia;
    ELSEIF NEW.estado IN ('Pendiente','Cancelado') THEN
        UPDATE horarios h
        SET h.ocupado = 0
        WHERE h.espacio = NEW.espacio AND h.bloque = NEW.bloque AND h.diaSemana = dia;
    END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- Volcando estructura para disparador sisintupt.trg_auditoria_reserva
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER trg_auditoria_reserva
AFTER UPDATE ON reserva
FOR EACH ROW
BEGIN
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
CREATE TRIGGER trg_crear_horarios
AFTER INSERT ON bloqueshorarios
FOR EACH ROW
BEGIN
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
CREATE TRIGGER trg_prioridad_reservas
BEFORE UPDATE ON reserva
FOR EACH ROW
BEGIN
    DECLARE usuario_rol INT;
    DECLARE usuario_nombre VARCHAR(30);
    DECLARE usuario_apellido VARCHAR(30);
    DECLARE reservas_afectadas INT;

    IF NEW.estado = 'Aprobada' AND OLD.estado = 'Pendiente' THEN
        SELECT Rol, Nombre, Apellido INTO usuario_rol, usuario_nombre, usuario_apellido
        FROM usuario WHERE IdUsuario = NEW.usuario;

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
