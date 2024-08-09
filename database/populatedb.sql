USE warehouse;
-- Insert pessoas
INSERT INTO pessoa (email, tipo, nome, cpf, razao_social, cnpj) VALUES
                                                                    ('joao.silva@example.com', 'PF', 'João Silva', '12345678909', NULL, NULL),
                                                                    ('maria.santos@example.com', 'PF', 'Maria Santos', '98765432100', NULL, NULL),
                                                                    ('construtora_pe@example.com', 'PJ', NULL, NULL, 'Construtora Recife LTDA', '12345678000199');

-- Insert clientes
INSERT INTO cliente (fk_pessoa_id) VALUES
                                       (1),
                                       (2);

-- Insert fornecedores
INSERT INTO fornecedor (fk_pessoa_id) VALUES
    (3);

-- Insert departamentos
INSERT INTO departamento (nome, descricao) VALUES
                                               ('Vendas', 'Responsável pelas vendas de produtos no armazém'),
                                               ('Logística', 'Gerencia o estoque e a distribuição de produtos');

-- Insert funcionarios
INSERT INTO funcionario (data_contratacao, salario, status, fk_pessoa_id, gerente_fk_funcionario_id, fk_departamento_id) VALUES
    ('2022-07-20', 2200.00, 'Ativo', 2, NULL, 2);

-- Insert categorias
INSERT INTO categoria (nome, descricao) VALUES
                                            ('Material Básico', 'Cimento, areia, tijolos e outros materiais básicos para construção'),
                                            ('Ferramentas', 'Ferramentas diversas para construção e reforma');

-- Insert produtos
INSERT INTO produto (nome, descricao, preco_venda, preco_aluguel, quantidade_estoque, fk_categoria_id) VALUES
                                                                                                           ('Cimento Tododia', 'Saco de cimento 50kg', 25.00, 0.00, 500, 1),
                                                                                                           ('Martelo de Aço', 'Martelo robusto para construção', 45.00, 5.00, 50, 2);

-- Insert pedidos
INSERT INTO pedido (valor_total, desconto, data_expedicao, fk_cliente_id, fk_funcionario_id) VALUES
                                                                                 (475.00, 25.00, '2022-07-20', 1, 2),
                                                                                 (90.00, 0.00, '2022-07-21', 1, 2);

-- Insert carrinhos
INSERT INTO carrinho (quantidade, fk_pedido_id, fk_produto_id) VALUES
                                                                   (10, 1, 1),
                                                                   (5, 2, 2);

-- Insert vendas
INSERT INTO venda (fk_pedido_id) VALUES
    (2);

-- Insert alugueis
INSERT INTO aluguel (data_devolucao, fk_pedido_id, status) VALUES
    ('2024-07-15', 1, 'Entregue');

-- Insert fornecimentos
INSERT INTO fornece (preco_compra, quantidade, fk_fornecedor_id, fk_produto_id) VALUES
                                                                                    (23.00, 1000, 3, 1),
                                                                                    (40.00, 100, 3, 2);

-- Insert telefones
INSERT INTO telefone (telefone, fk_pessoa_id) VALUES
                                                  ('8132321234', 1),
                                                  ('8132325678', 2);

-- Insert enderecos
INSERT INTO endereco (rua, numero, bairro, cidade, estado, cep, fk_pessoa_id) VALUES
                                                                                  ('Rua das Palmeiras', 102, 'Casa Amarela', 'Recife', 'PE', '52070230', 1),
                                                                                  ('Avenida Norte', 205, 'Tamarineira', 'Recife', 'PE', '52110000', 2);
