CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NOT NULL,
    habilitado BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO roles (nombre, descripcion, habilitado) VALUES
('P-01', 'Jefa de Tienda', TRUE),
('P-02', 'Subgerente', TRUE),
('P-03', 'Supervisor', TRUE),
('P-04', 'Observador', TRUE);