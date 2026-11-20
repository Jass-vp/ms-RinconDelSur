CREATE TABLE IF NOT EXISTS solicitudes_correo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rut_usuario VARCHAR(20) NOT NULL,
    nombre_usuario VARCHAR(150) NOT NULL,
    correo_actual VARCHAR(150) NOT NULL,
    correo_nuevo VARCHAR(150) NOT NULL,
    motivo VARCHAR(255),
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
);