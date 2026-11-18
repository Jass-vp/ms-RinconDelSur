CREATE TABLE conteos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    puerta_acceso VARCHAR(20) NOT NULL,
    cantidad_transito INT NOT NULL,
    cantidad_clientes INT NOT NULL,
    rango_etario VARCHAR(50) NOT NULL,
    compania VARCHAR(50) NOT NULL,
    observador VARCHAR(100) NOT NULL,
    hora VARCHAR(20) NOT NULL,
    usuario_id BIGINT
);
