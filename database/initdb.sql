CREATE DATABASE IF NOT EXISTS warehouse
    CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;

USE warehouse;

CREATE TABLE pessoa (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        email VARCHAR(100) UNIQUE,
                        tipo VARCHAR(2) CHECK (tipo IN ('PF', 'PJ')),

    -- Pessoa Física
                        nome VARCHAR(100),
                        cpf VARCHAR(14) UNIQUE,

    -- Pessoa Jurídica
                        razao_social VARCHAR(100),
                        cnpj VARCHAR(18) UNIQUE,

                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE cliente (
                         fk_pessoa_id INT PRIMARY KEY,
                         FOREIGN KEY (fk_pessoa_id) REFERENCES pessoa (id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE fornecedor (
                            fk_pessoa_id INT PRIMARY KEY,
                            FOREIGN KEY (fk_pessoa_id) REFERENCES pessoa (id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE departamento (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              nome VARCHAR(100),
                              descricao TEXT
);

CREATE TABLE funcionario (
                             data_contratacao DATE,
                             salario DECIMAL(10,2),
                             status VARCHAR(15) CHECK (status IN ('Ativo', 'Inativo')),

                             fk_pessoa_id INT PRIMARY KEY,
                             gerente_fk_funcionario_id INT,
                             fk_departamento_id INT,
                             FOREIGN KEY (fk_pessoa_id) REFERENCES pessoa (id) ON DELETE RESTRICT ON UPDATE CASCADE,
                             FOREIGN KEY (fk_departamento_id) REFERENCES departamento (id) ON DELETE SET NULL ON UPDATE CASCADE,
                             FOREIGN KEY (gerente_fk_funcionario_id) REFERENCES funcionario (fk_pessoa_id) ON DELETE SET NULL ON UPDATE CASCADE
);


CREATE TABLE categoria (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           nome VARCHAR(100),
                           descricao TEXT
);

CREATE TABLE produto (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         nome VARCHAR(100),
                         descricao TEXT,
                         preco_venda DECIMAL(10,2),
                         preco_aluguel DECIMAL(10,2),
                         quantidade_estoque INT DEFAULT 0,

                         fk_categoria_id INT,
                         FOREIGN KEY (fk_categoria_id) REFERENCES categoria (id) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE pedido (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        valor_total DECIMAL(10,2),
                        desconto DECIMAL(10,2),
                        data_expedicao DATE,

                        fk_cliente_id INT,
                        fk_funcionario_id INT,
                        FOREIGN KEY (fk_cliente_id) REFERENCES cliente (fk_pessoa_id) ON DELETE RESTRICT ON UPDATE CASCADE,
                        FOREIGN KEY (fk_funcionario_id) REFERENCES funcionario (fk_pessoa_id) ON DELETE RESTRICT ON UPDATE CASCADE,

                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);



CREATE TABLE carrinho (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          quantidade INT,

                          fk_pedido_id INT,
                          FOREIGN KEY (fk_pedido_id) REFERENCES pedido (id) ON DELETE CASCADE ON UPDATE CASCADE,
                          fk_produto_id INT,
                          FOREIGN KEY (fk_produto_id) REFERENCES produto (id) ON DELETE RESTRICT ON UPDATE CASCADE

);


CREATE TABLE venda (
                       fk_pedido_id INT PRIMARY KEY,
                       FOREIGN KEY (fk_pedido_id) REFERENCES pedido (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE aluguel (
                         data_devolucao DATE,
                         fk_pedido_id INT PRIMARY KEY,
                         status VARCHAR(15) CHECK (status IN ('Entregue', 'Devolvido')),
                         FOREIGN KEY (fk_pedido_id) REFERENCES pedido (id) ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE fornece (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         preco_compra DECIMAL(10,2),
                         quantidade INT,
                         fk_fornecedor_id INT,
                         fk_produto_id INT,

                         FOREIGN KEY (fk_fornecedor_id) REFERENCES fornecedor (fk_pessoa_id) ON DELETE RESTRICT ON UPDATE CASCADE,
                         FOREIGN KEY (fk_produto_id) REFERENCES produto (id) ON DELETE RESTRICT ON UPDATE CASCADE,

                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);




CREATE TABLE telefone (
                          id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
                          telefone VARCHAR(20),

                          fk_pessoa_id INT,
                          FOREIGN KEY (fk_pessoa_id) REFERENCES pessoa (id) ON DELETE CASCADE ON UPDATE CASCADE,

                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE endereco (
                          id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
                          rua VARCHAR(50),
                          numero INT,
                          bairro VARCHAR(50),
                          cidade VARCHAR(50),
                          estado VARCHAR(2),
                          cep VARCHAR(9),

                          fk_pessoa_id INT,
                          FOREIGN KEY (fk_pessoa_id) REFERENCES pessoa (id) ON DELETE CASCADE ON UPDATE CASCADE,

                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

DELIMITER $$

CREATE TRIGGER atualizar_quantidade_produto_fornecimento
    AFTER INSERT ON fornece
    FOR EACH ROW
BEGIN
    UPDATE produto
    SET quantidade_estoque = quantidade_estoque + NEW.quantidade
    WHERE id = NEW.fk_produto_id;
END$$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER atualizar_quantidade_produto_venda
    AFTER INSERT ON carrinho
    FOR EACH ROW
BEGIN
    DECLARE preco DECIMAL(10,2);
    -- Obter o preço de venda do produto
    SELECT preco_venda INTO preco FROM produto WHERE id = NEW.fk_produto_id;

    -- Atualizar a quantidade em estoque
    UPDATE produto
    SET quantidade_estoque = quantidade_estoque - NEW.quantidade
    WHERE id = NEW.fk_produto_id;

    -- Atualizar o valor total do pedido
    UPDATE pedido
    SET valor_total = valor_total + (preco * NEW.quantidade)
    WHERE id = NEW.fk_pedido_id;
END$$

DELIMITER ;



