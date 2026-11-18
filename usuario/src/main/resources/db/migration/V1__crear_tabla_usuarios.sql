CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rut VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    contrasena VARCHAR(100) NOT NULL,
    cargo VARCHAR(100),
    turno VARCHAR(50),
    rol_id BIGINT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    primer_ingreso BOOLEAN NOT NULL DEFAULT TRUE,
    pregunta_seguridad VARCHAR(150),
    respuesta_seguridad VARCHAR(100),
    avatar VARCHAR(100)
);
