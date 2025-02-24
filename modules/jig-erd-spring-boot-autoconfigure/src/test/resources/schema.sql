CREATE TABLE Owner
(
    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(100)        NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE Pet
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    species    VARCHAR(50)  NOT NULL,
    birth_date DATE,
    owner_id   BIGINT,
    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES Owner (id) ON DELETE CASCADE
);