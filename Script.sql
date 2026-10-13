-- =====================================================================
--  Script para el proyecto LoginSimple (funciona en XAMPP/MariaDB, MySQL y Azure MySQL)
--  Crea la base "tarea", la tabla "usuarios" y 10 usuarios de ejemplo.
--  OJO: borra la tabla "usuarios" si ya existía.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS tarea CHARACTER SET utf8mb4;
USE tarea;

-- Clave de cifrado AES: debe ser IGUAL a "aes.key" de tu config.properties
SET @clave = 'mi-clave-secreta-123';

DROP TABLE IF EXISTS usuarios;

CREATE TABLE usuarios (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    username       VARCHAR(20)  NOT NULL UNIQUE,
    password_hash  CHAR(64)     NOT NULL,      -- SHA-256(sal + contraseña)
    salt           CHAR(32)     NOT NULL,
    rol            VARCHAR(15)  NOT NULL,      -- ADMINISTRADOR / EMPLEADO
    nombre         VARCHAR(50)  NOT NULL,
    ap_paterno     VARCHAR(50)  NOT NULL,
    ap_materno     VARCHAR(50),
    email          VARCHAR(100) NOT NULL,
    fecha_nac      DATE         NOT NULL,
    genero         VARCHAR(15)  NOT NULL,
    puesto         VARCHAR(60)  NOT NULL,
    departamento   VARCHAR(60)  NOT NULL,
    fecha_ingreso  DATE         NOT NULL,
    direccion      VARCHAR(150) NOT NULL,
    curp_enc       VARBINARY(64) NOT NULL,     -- AES_ENCRYPT
    telefono_enc   VARBINARY(64) NOT NULL,     -- AES_ENCRYPT
    salario_enc    VARBINARY(64) NOT NULL      -- AES_ENCRYPT
) CHARACTER SET utf8mb4;

-- Se insertan con salt aleatorio y hash vacío; el hash se calcula en el UPDATE de abajo.
INSERT INTO usuarios
    (username, rol, nombre, ap_paterno, ap_materno, email, fecha_nac, genero, puesto, departamento,
     fecha_ingreso, direccion, curp_enc, telefono_enc, salario_enc, salt, password_hash)
VALUES
('admin',     'ADMINISTRADOR', 'Ana',     'Gómez',     'Martínez', 'admin@correo.com',     '1990-01-15', 'Femenino',  'Administradora del sistema', 'Sistemas',          '2022-03-01', 'Av. Reforma 100, Col. Centro',
    AES_ENCRYPT('GOMA900115MGTMRN05', @clave), AES_ENCRYPT('5512345678', @clave), AES_ENCRYPT('45000.00', @clave), MD5(UUID()), ''),
('empleado1', 'EMPLEADO',      'Luis',    'López',     'Pérez',    'luis.lopez@correo.com','1995-03-20', 'Masculino', 'Analista de soporte',        'Soporte',           '2023-06-12', 'Calle Hidalgo 45, Col. Jardines',
    AES_ENCRYPT('LOPL950320HGTPRS09', @clave), AES_ENCRYPT('4151234567', @clave), AES_ENCRYPT('18500.00', @clave), MD5(UUID()), ''),
('maria.r',   'EMPLEADO',      'María',   'Ruiz',      'Torres',   'maria.ruiz@correo.com','1992-06-10', 'Femenino',  'Contadora',                 'Finanzas',          '2021-09-01', 'Calle 5 de Mayo 12, Col. Centro',
    AES_ENCRYPT('RUTM920610MGTZRR04', @clave), AES_ENCRYPT('4151112233', @clave), AES_ENCRYPT('22000.50', @clave), MD5(UUID()), ''),
('jorge.t',   'EMPLEADO',      'Jorge',   'Torres',    'Salinas',  'jorge.torres@correo.com','1988-04-25','Masculino','Ejecutivo de ventas',       'Ventas',            '2020-02-17', 'Av. Juárez 230, Col. Obrera',
    AES_ENCRYPT('TOSJ880425HGTRLR07', @clave), AES_ENCRYPT('4152223344', @clave), AES_ENCRYPT('16800.00', @clave), MD5(UUID()), ''),
('sofia.v',   'EMPLEADO',      'Sofía',   'Vega',      'Luna',     'sofia.vega@correo.com','2000-07-14', 'Femenino',  'Reclutadora',               'Recursos Humanos',  '2024-01-08', 'Calle Allende 78, Col. Lomas',
    AES_ENCRYPT('VELS000714MGTGNF03', @clave), AES_ENCRYPT('4153334455', @clave), AES_ENCRYPT('19250.75', @clave), MD5(UUID()), ''),
('carlos.m',  'EMPLEADO',      'Carlos',  'Mendoza',   'Ríos',     'carlos.mendoza@correo.com','1991-09-03','Masculino','Desarrollador',           'Sistemas',          '2022-11-21', 'Blvd. Las Torres 410, Col. Moderna',
    AES_ENCRYPT('MERC910903HGTNDR08', @clave), AES_ENCRYPT('4154445566', @clave), AES_ENCRYPT('32000.00', @clave), MD5(UUID()), ''),
('laura.h',   'EMPLEADO',      'Laura',   'Hernández', 'Cruz',     'laura.hernandez@correo.com','1997-02-22','Femenino','Analista de soporte',      'Soporte',           '2023-03-30', 'Calle Morelos 15, Col. San Rafael',
    AES_ENCRYPT('HECL970222MGTRRR05', @clave), AES_ENCRYPT('4155556677', @clave), AES_ENCRYPT('17400.00', @clave), MD5(UUID()), ''),
('diego.s',   'EMPLEADO',      'Diego',   'Sánchez',   'Mora',     'diego.sanchez@correo.com','1985-01-18','Masculino','Coordinador de operaciones','Operaciones',       '2019-05-06', 'Priv. del Roble 9, Col. Bosques',
    AES_ENCRYPT('SAMD850118HGTNRG02', @clave), AES_ENCRYPT('4156667788', @clave), AES_ENCRYPT('27500.00', @clave), MD5(UUID()), ''),
('paola.d',   'EMPLEADO',      'Paola',   'Díaz',      'Ortega',   'paola.diaz@correo.com','1993-11-29', 'Femenino',  'Diseñadora',                'Sistemas',          '2022-08-15', 'Calle Aldama 54, Col. Centro',
    AES_ENCRYPT('DIOP931129MGTZRL06', @clave), AES_ENCRYPT('4157778899', @clave), AES_ENCRYPT('21000.00', @clave), MD5(UUID()), ''),
('ricardo.p', 'EMPLEADO',      'Ricardo', 'Pérez',     'Navarro',  'ricardo.perez@correo.com','1980-05-07','Masculino','Auditor',                  'Finanzas',          '2018-10-01', 'Av. Hidalgo 300, Col. Residencial',
    AES_ENCRYPT('PENR800507HGTRVC01', @clave), AES_ENCRYPT('4158889900', @clave), AES_ENCRYPT('29800.00', @clave), MD5(UUID()), '');

-- Hash SHA-256 de la contraseña (con la sal de cada usuario), igual que lo hace el programa en Java:
--   admin     -> Admin#2026
--   los demás -> Empleado#2026
UPDATE usuarios
SET password_hash = SHA2(CONCAT(salt, IF(rol = 'ADMINISTRADOR', 'Admin#2026', 'Empleado#2026')), 256);

-- ---------------------------------------------------------------------
--  Comprobaciones (opcional)
-- ---------------------------------------------------------------------
-- Lo que ve un EMPLEADO (datos cifrados ilegibles):
SELECT id, username, rol, nombre, ap_paterno, departamento, HEX(curp_enc) AS curp_cifrada FROM usuarios;

-- Lo que ve el ADMINISTRADOR (descifrado con la clave):
SELECT id, username, rol, nombre,
       CAST(AES_DECRYPT(curp_enc,    @clave) AS CHAR) AS curp,
       CAST(AES_DECRYPT(telefono_enc,@clave) AS CHAR) AS telefono,
       CAST(AES_DECRYPT(salario_enc, @clave) AS CHAR) AS salario
FROM usuarios;
