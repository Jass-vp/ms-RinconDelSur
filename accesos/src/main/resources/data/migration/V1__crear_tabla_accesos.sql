CREATE TABLE accesos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    esta_habilitado BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO accesos (codigo, nombre, descripcion, esta_habilitado) VALUES
('A1', 'Acceso Principal', 'Patio de comidas del mall / Pasillo central', 1),
('A2', 'Acceso Lateral', 'Pasillo del mall, lado norte', 1),
('A3', 'Acceso Estacionamiento', 'Estacionamiento subterráneo (señal de red débil)', 1),
('A4', 'Acceso Conexión', 'Conexión directa con tienda vecina de mejoramiento del hogar', 1);