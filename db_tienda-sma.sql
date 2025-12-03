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


-- Volcando estructura de base de datos para dbtienda
CREATE DATABASE IF NOT EXISTS `dbtienda` /*!40100 DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci */;
USE `dbtienda`;

-- Volcando estructura para tabla dbtienda.tbcargo
CREATE TABLE IF NOT EXISTS `tbcargo` (
  `estado` int(11) DEFAULT NULL,
  `idcargo` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`idcargo`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Volcando datos para la tabla dbtienda.tbcargo: ~3 rows (aproximadamente)
INSERT INTO `tbcargo` (`estado`, `idcargo`, `nombre`) VALUES
	(1, 1, 'ADMINISTRADOR'),
	(1, 2, 'EMPLEADO 01'),
	(1, 3, 'CLIENTE');

-- Volcando estructura para tabla dbtienda.tbempleado
CREATE TABLE IF NOT EXISTS `tbempleado` (
  `idempleado` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  `apellido` varchar(255) DEFAULT NULL,
  `idcargo` bigint(20) NOT NULL,
  `usuario` varchar(255) DEFAULT NULL,
  `clave` varchar(255) DEFAULT NULL,
  `idtipodocumento` bigint(20) DEFAULT NULL,
  `numerodocumento` varchar(255) DEFAULT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `estado` int(11) DEFAULT NULL,
  PRIMARY KEY (`idempleado`),
  KEY `FK182uvjnepdsy743yuspollti5` (`idcargo`),
  CONSTRAINT `FK182uvjnepdsy743yuspollti5` FOREIGN KEY (`idcargo`) REFERENCES `tbcargo` (`idcargo`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Volcando datos para la tabla dbtienda.tbempleado: ~2 rows (aproximadamente)
INSERT INTO `tbempleado` (`idempleado`, `nombre`, `apellido`, `idcargo`, `usuario`, `clave`, `idtipodocumento`, `numerodocumento`, `telefono`, `estado`) VALUES
	(1, 'Stevie', 'Marca', 2, 'Stevie', '123456', 1, '72405382', '979739029', 1),
	(2, 'Mary', 'Laura', 1, 'Mary', '123456', 0, '1234567888', '99999999', 0);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
