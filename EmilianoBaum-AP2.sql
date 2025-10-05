-- Comando de Creación de la Base de Datos
CREATE DATABASE SATE_DB;
USE SATE_DB; 

-- DDL: Lenguaje de Definición de Datos

-- Tabla Usuario (Entidad)
CREATE TABLE Usuario (
    idUsuario INT NOT NULL AUTO_INCREMENT PRIMARY KEY,  
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    passwordHash TEXT NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('Administrador', 'Validador', 'Ciudadano'))
);

-- Tabla FuenteDatos (Entidad)
CREATE TABLE FuenteDatos (
    idFuente INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipoConexion VARCHAR(50)
);


-- Tabla ReporteManual (Entidad)
CREATE TABLE ReporteManual (
    idReporte INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    idUsuario INT NOT NULL,
    fechaHora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ubicacion VARCHAR(255) NOT NULL, -- Ej: "Lat:-34.60, Lon:-58.38"
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('Pendiente', 'Aprobado', 'Rechazado')),
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
    ON DELETE CASCADE
);


-- Tabla AreaInteres (Entidad con campo poligono para coordenadas)
CREATE TABLE AreaInteres (
    idArea INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    poligono TEXT NOT NULL, -- Almacena el polígono en formato WKT simplificado
    frecuenciaNotif INT -- En horas
);


-- Tabla SuscripcionArea (Relación N:M entre Usuario y AreaInteres)
CREATE TABLE SuscripcionArea (
    idUsuario INT NOT NULL,
    idArea INT NOT NULL,
    PRIMARY KEY (idUsuario, idArea),
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario) ON DELETE CASCADE,
    FOREIGN KEY (idArea) REFERENCES AreaInteres(idArea) ON DELETE CASCADE
);


-- Tabla Evento (Entidad)
CREATE TABLE Evento (
    idEvento INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    idFuente INT,
    idReporte INT UNIQUE, -- Clave Foránea para Reportes validados (relación 1:1)
    tipo VARCHAR(50) NOT NULL,
    fechaHora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    areaAfectada TEXT, -- Puede ser un punto o un polígono
    
    -- Los campos pueden ser NULL para soportar ON DELETE SET NULL
    FOREIGN KEY (idFuente) REFERENCES FuenteDatos(idFuente) ON DELETE SET NULL,
    FOREIGN KEY (idReporte) REFERENCES ReporteManual(idReporte) ON DELETE SET NULL
);


-- Tabla Notificacion (Entidad)
CREATE TABLE Notificacion (
    idNotificacion INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    idUsuario INT NOT NULL,
    idEvento INT NOT NULL,
    mensaje TEXT NOT NULL,
    fechaHoraEnvio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('Enviada', 'Leída')),
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario) ON DELETE CASCADE,
    FOREIGN KEY (idEvento) REFERENCES Evento(idEvento) ON DELETE CASCADE
);

-- INSERCIÓN EN USUARIO (10 registros)
INSERT INTO Usuario (nombre, email, passwordHash, rol) VALUES
( 'Ana Gómez', 'ana.admin@sate.com', 'hash_admin1', 'Administrador'),      -- 1
( 'Carlos Ruiz', 'carlos.validador@sate.com', 'hash_valid1', 'Validador'), -- 2
( 'Laura Pérez', 'laura.validator@sate.com', 'hash_valid2', 'Validador'), -- 3
( 'Emilio Castro', 'emilio.ciudadano@sate.com', 'hash_ciudad1', 'Ciudadano'), -- 4
( 'Sofía Torres', 'sofia.torres@sate.com', 'hash_ciudad2', 'Ciudadano'),  -- 5
( 'Ricardo Diaz', 'ricardo.diaz@sate.com', 'hash_ciudad3', 'Ciudadano'),  -- 6
( 'Javier Lopez', 'javier.lopez@sate.com', 'hash_ciudad4', 'Ciudadano'),  -- 7
( 'Daniela Mora', 'daniela.mora@sate.com', 'hash_ciudad5', 'Ciudadano'),  -- 8
( 'Mariano Jefe', 'jefe@sate.com', 'hash_admin2', 'Administrador'),      -- 9
( 'Valeria Pino', 'valeria.pino@sate.com', 'hash_valid3', 'Validador');   -- 10

-- INSERCIÓN EN FUENTEDATOS (3 registros)
INSERT INTO FuenteDatos (nombre, tipoConexion) VALUES
( 'Servicio Meteorológico Nacional', 'API-REST'), -- 1
( 'CONAE - Satelital', 'FTP'),                   -- 2
( 'Eumesat', 'API-REST');             -- 3

-- INSERCIÓN EN AREAINTERES (5 registros)
-- Zona Norte Buenos Aires (Tigre/San Fernando)
INSERT INTO AreaInteres (nombre, poligono, frecuenciaNotif) VALUES
( 'Zona Norte Buenos Aires', 'POLYGON((-58.70 -34.40, -58.70 -34.50, -58.50 -34.50, -58.50 -34.40, -58.70 -34.40))', 6);
-- Cordillera Sur (Neuquén/Río Negro)
INSERT INTO AreaInteres (nombre, poligono, frecuenciaNotif) VALUES
( 'Cordillera Sur', 'POLYGON((-71.50 -41.00, -71.50 -42.00, -70.50 -42.00, -70.50 -41.00, -71.50 -41.00))', 1);
-- Cuenca del Río Salado (Provincia de Buenos Aires)
INSERT INTO AreaInteres (nombre, poligono, frecuenciaNotif) VALUES
( 'Cuenca del Río Salado', 'POLYGON((-58.50 -35.50, -59.50 -35.50, -59.50 -36.50, -58.50 -36.50, -58.50 -35.50))', 12);
-- Ciudad de Córdoba Centro (Provincia de Córdoba)
INSERT INTO AreaInteres (nombre, poligono, frecuenciaNotif) VALUES
( 'Ciudad de Córdoba Centro', 'POLYGON((-64.180 -31.410, -64.180 -31.420, -64.190 -31.420, -64.190 -31.410, -64.180 -31.410))', 24);
-- Patagonia Costera (Chubut - zona de Puerto Madryn)
INSERT INTO AreaInteres (nombre, poligono, frecuenciaNotif) VALUES
( 'Patagonia Costera', 'POLYGON((-65.00 -42.50, -65.00 -43.50, -64.00 -43.50, -64.00 -42.50, -65.00 -42.50))', 8);

-- INSERCIÓN EN REPORTEMANUAL (15 registros)
INSERT INTO ReporteManual (idUsuario, ubicacion, estado, fechaHora) VALUES
( 4, 'Lat:-34.60, Lon:-58.38', 'Pendiente', '2025-10-04 09:30:00'), -- 1 (Pendiente)
( 5, 'Lat:-34.50, Lon:-58.50', 'Aprobado', '2025-10-03 15:45:00'), -- 2 (Aprobado)
( 6, 'Lat:-31.41, Lon:-64.18', 'Aprobado', '2025-10-02 20:00:00'), -- 3 (Aprobado)
( 7, 'Lat:-34.65, Lon:-58.40', 'Rechazado', '2025-10-01 10:15:00'), -- 4 (Rechazado - para posterior borrado)
( 4, 'Lat:-34.61, Lon:-58.39', 'Pendiente', '2025-10-04 11:00:00'), -- 5 (Pendiente)
( 8, 'Lat:-40.81, Lon:-63.00', 'Aprobado', '2025-09-30 08:00:00'), -- 6 (Aprobado)
( 5, 'Lat:-34.52, Lon:-58.51', 'Pendiente', '2025-10-04 14:00:00'), -- 7 (Pendiente)
( 6, 'Lat:-31.42, Lon:-64.19', 'Aprobado', '2025-10-03 18:30:00'), -- 8 (Aprobado)
( 4, 'Lat:-34.62, Lon:-58.42', 'Aprobado', '2025-10-02 12:00:00'), -- 9 (Aprobado)
( 7, 'Lat:-31.40, Lon:-64.20', 'Pendiente', '2025-10-04 10:00:00'), -- 10 (Pendiente)
( 8, 'Lat:-40.82, Lon:-63.01', 'Rechazado', '2025-09-29 11:00:00'), -- 11 (Rechazado)
( 5, 'Lat:-34.53, Lon:-58.52', 'Aprobado', '2025-10-01 16:00:00'), -- 12 (Aprobado)
( 6, 'Lat:-31.43, Lon:-64.21', 'Pendiente', '2025-10-04 15:30:00'), -- 13 (Pendiente)
( 7, 'Lat:-34.63, Lon:-58.43', 'Aprobado', '2025-10-03 09:00:00'), -- 14 (Aprobado)
( 8, 'Lat:-40.83, Lon:-63.02', 'Aprobado', '2025-10-02 07:00:00'); -- 15 (Aprobado)

-- INSERCIÓN EN EVENTO (10 registros)
INSERT INTO Evento (idFuente, idReporte, tipo, fechaHora, areaAfectada) VALUES
( 1, NULL, 'Inundación', '2025-10-04 07:00:00', 'Zona A del Río'), -- 1 (Automático)
( NULL, 2, 'Incendio', '2025-10-03 15:45:00', 'Parque B'),           -- 2 (Manual, desde Reporte 2)
( NULL, 3, 'Inundación', '2025-10-02 20:00:00', 'Barrio C'),         
( 3, NULL, 'Incendio', '2025-10-04 06:30:00', 'Cordillera Sur'),        -- 4 (Automático)
( 1, NULL, 'Inundación', '2025-10-03 10:00:00', 'Costa Atlántica'),  -- 5 (Automático)
( NULL, 6, 'Incendio', '2025-09-30 08:00:00', 'Campos de Patag.'),  -- 6 (Manual)
( NULL, 9, 'Inundación', '2025-10-02 12:00:00', 'Microcentro BA'), -- 7 (Manual)
( NULL, 12, 'Inundación', '2025-10-01 16:00:00', 'Ruta 11'),          -- 8 (Manual)
( NULL, 14, 'Incendio', '2025-10-03 09:00:00', 'Bosque Talar'),      -- 9 (Manual)
( 3, NULL, 'Incendio', '2025-10-01 23:00:00', 'Zona Oeste');            -- 10 (Automático)

-- INSERCIÓN EN SUSCRIPCIONAREA (5 registros)
INSERT INTO SuscripcionArea (idUsuario, idArea) VALUES
( 4, 1), -- Emilio suscribe a Zona Norte BA
( 5, 1), -- Sofía suscribe a Zona Norte BA
( 7, 4), -- Javier suscribe a Cdad de Córdoba
( 8, 5), -- Daniela suscribe a Patagonia
( 1, 2); -- Ana (Admin) suscribe a Cordillera Sur

-- INSERCIÓN EN NOTIFICACION (7 registros)
INSERT INTO Notificacion (idUsuario, idEvento, mensaje, estado, fechaHoraEnvio) VALUES
( 4, 1, '¡Alerta! Inundación detectada cerca del Río.', 'Enviada', '2025-10-04 07:15:00'), -- 1
( 5, 1, '¡Alerta! Inundación detectada cerca del Río.', 'Leída', '2025-10-04 07:15:00'),   -- 2
( 1, 4, 'Alerta incendio en Cordillera Sur.', 'Enviada', '2025-10-04 06:40:00'), -- 3 (Sin leer)
( 7, 9, 'Incendio reportado cerca de Bosque Talar.', 'Leída', '2025-10-03 09:30:00'),    -- 4
( 8, 6, 'Incendio en campos de Patagonia. Tenga precaución.', 'Leída', '2025-09-30 08:15:00'), -- 5
( 4, 7, 'Inundación reportada en Microcentro BA.', 'Enviada', '2025-10-02 12:30:00'), -- 6 (Sin leer)
( 5, 7, 'Inundación reportada en Microcentro BA.', 'Enviada', '2025-10-02 12:30:00'); -- 7 (Sin leer)


-- DML: Lenguaje de Manipulación de Datos

-- Aprobar un Reporte Pendiente: Un Validador aprueba el Reporte ID 5.
UPDATE ReporteManual
SET estado = 'Aprobado'
WHERE idReporte = 5;

-- Marcar Notificaciones como leída
UPDATE Notificacion
SET estado = 'Leída'
WHERE idUsuario = 4 AND estado = 'Enviada';

-- Ajustar Frecuencia de Notificación
UPDATE AreaInteres
SET frecuenciaNotif = 4
WHERE nombre = 'Zona Norte Buenos Aires';

-- Eliminar Reportes Rechazados Antiguos
DELETE FROM ReporteManual
WHERE idReporte = 11;

-- Eliminar un Usuario y sus Dependencias

DELETE FROM Usuario
WHERE idUsuario = 6;



-- Reportes Pendientes para Validador

SELECT
    RM.idReporte,
    U.nombre AS Ciudadano,
    U.email,
    RM.ubicacion,
    RM.fechaHora
FROM
    ReporteManual RM
JOIN
    Usuario U ON RM.idUsuario = U.idUsuario
WHERE
    RM.estado = 'Pendiente'
ORDER BY
    RM.fechaHora ASC;

-- Consulta 2: Eventos Originados por Sensores

SELECT
    E.idEvento,
    FD.nombre AS Fuente,
    E.tipo,
    E.fechaHora
FROM
    Evento E
JOIN
    FuenteDatos FD ON E.idFuente = FD.idFuente
WHERE
    E.idReporte IS NULL
ORDER BY
    E.fechaHora DESC;

-- Consulta 3: Conteo de Eventos Válidos por Usuario 

SELECT
    U.nombre AS Ciudadano,
    COUNT(RM.idReporte) AS ReportesAprobados
FROM
    Usuario U
JOIN
    ReporteManual RM ON U.idUsuario = RM.idUsuario
WHERE
    RM.estado = 'Aprobado'
GROUP BY
    U.nombre
HAVING
    COUNT(RM.idReporte) > 0
ORDER BY
    ReportesAprobados DESC;


-- Consulta 4: Áreas de Interés con Más Suscripciones

SELECT
    AI.nombre AS Area,
    COUNT(SA.idUsuario) AS TotalSuscriptores
FROM
    AreaInteres AI
JOIN
    SuscripcionArea SA ON AI.idArea = SA.idArea
GROUP BY
    AI.nombre
ORDER BY
    TotalSuscriptores DESC;

-- Consulta 5: Historial de Notificaciones Leídas

SELECT
    N.mensaje,
    E.tipo AS TipoEvento,
    N.fechaHoraEnvio
FROM
    Notificacion N
JOIN
    Usuario U ON N.idUsuario = U.idUsuario
JOIN
    Evento E ON N.idEvento = E.idEvento
WHERE
    U.email = 'sofia.torres@sate.com' AND N.estado = 'Leída'
ORDER BY
    N.fechaHoraEnvio DESC;