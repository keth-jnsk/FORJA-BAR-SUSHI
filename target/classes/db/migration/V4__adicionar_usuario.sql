CREATE TABLE usuarios (
id     SERIAL PRIMARY KEY,
login  VARCHAR(50) NOT NULL UNIQUE,
senha  VARCHAR(100) NOT NULL,
perfil VARCHAR(20) DEFAULT 'ADMIN'
);

INSERT INTO usuarios (login, senha, perfil)
VALUES ('admin', 'admin123', 'ADMIN');