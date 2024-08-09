INSERT INTO pessoa (email, tipo, nome, cpf, razao_social, cnpj) VALUES
                                                                    ('joao.silva@example.com', 'PF', 'Joao Silva', '12345678909', NULL, NULL),
                                                                    ('maria.santos@example.com', 'PF', 'Maria Santos', '98765432100', NULL, NULL),
                                                                    ('pedro.moura@example.com', 'PF', 'Pedro Moura', '34567890123', NULL, NULL),
                                                                    ('ana.paula@example.com', 'PF', 'Ana Paula', '45678901234', NULL, NULL),
                                                                    ('carlos.mendes@example.com', 'PF', 'Carlos Mendes', '56789012345', NULL, NULL),
                                                                    ('juliana.souza@example.com', 'PF', 'Juliana Souza', '67890123456', NULL, NULL),
                                                                    ('lucas.almeida@example.com', 'PF', 'Lucas Almeida', '78901234567', NULL, NULL),
                                                                    ('roberto.costa@example.com', 'PF', 'Roberto Costa', '89012345678', NULL, NULL),
                                                                    ('mariana.lima@example.com', 'PF', 'Mariana Lima', '90123456789', NULL, NULL),
                                                                    ('beatriz.silva@example.com', 'PF', 'Beatriz Silva', '01234567890', NULL, NULL),
                                                                    ('construtora_rio@example.com', 'PJ', NULL, NULL, 'Construtora Rio Ltda', '11222333000144'),
                                                                    ('construtora_sp@example.com', 'PJ', NULL, NULL, 'Construtora SP Ltda', '22334455000166'),
                                                                    ('materiais_bh@example.com', 'PJ', NULL, NULL, 'Materiais BH Ltda', '33445566000188'),
                                                                    ('ferragens_porto@example.com', 'PJ', NULL, NULL, 'Ferragens Porto Ltda', '44556677000199'),
                                                                    ('cimento_total@example.com', 'PJ', NULL, NULL, 'Cimento Total Ltda', '55667788000111'),
                                                                    ('areia_fina@example.com', 'PJ', NULL, NULL, 'Areia Fina Ltda', '66778899000122'),
                                                                    ('pedra_brita@example.com', 'PJ', NULL, NULL, 'Pedra Brita Ltda', '77889900000133'),
                                                                    ('construtora_norte@example.com', 'PJ', NULL, NULL, 'Construtora Norte Ltda', '88990011000144'),
                                                                    ('construtora_sul@example.com', 'PJ', NULL, NULL, 'Construtora Sul Ltda', '99001122000155');

-- Insert clientes
INSERT INTO cliente (fk_pessoa_id) VALUES
                                       (1), (2), (3), (4), (5), (6), (7), (8), (9), (10);

-- Insert fornecedores
INSERT INTO fornecedor (fk_pessoa_id) VALUES
                                          (11), (12), (13), (14), (15), (16), (17), (18), (19);

-- Insert departamentos
INSERT INTO departamento (nome, descricao) VALUES
                                               ('Vendas', 'Responsavel pelas vendas de produtos no armazem'),
                                               ('Logistica', 'Gerencia o estoque e a distribuicao de produtos'),
                                               ('Financeiro', 'Responsavel pelas financas do armazem'),
                                               ('RH', 'Gerencia recursos humanos e contratacoes');

-- Insert funcionarios
INSERT INTO funcionario (data_contratacao, salario, status, fk_pessoa_id, gerente_fk_funcionario_id, fk_departamento_id) VALUES
                                                                                                                             ('2022-07-20', 2200.00, 'Ativo', 2, NULL, 1),
                                                                                                                             ('2022-07-21', 2500.00, 'Ativo', 3, 2, 1),
                                                                                                                             ('2022-07-22', 2000.00, 'Ativo', 4, 3, 2),
                                                                                                                             ('2022-07-23', 2300.00, 'Ativo', 5, 4, 3),
                                                                                                                             ('2022-07-24', 2100.00, 'Ativo', 6, 5, 4);

-- Insert categorias
INSERT INTO categoria (nome, descricao) VALUES
                                            ('Material Basico', 'Cimento, areia, tijolos e outros materiais basicos para construcao'),
                                            ('Ferramentas', 'Ferramentas diversas para construcao e reforma'),
                                            ('Eletrica', 'Materiais eletricos para construcao'),
                                            ('Hidraulica', 'Materiais hidraulicos para construcao');

-- Insert produtos
INSERT INTO produto (nome, descricao, preco_venda, preco_aluguel, quantidade_estoque, fk_categoria_id) VALUES
                                                                                                           ('Cimento Tododia', 'Saco de cimento 50kg', 25.00, NULL, 500, 1),
                                                                                                           ('Areia Fina', 'Metro cubico de areia fina', 70.00, NULL, 200, 1),
                                                                                                           ('Tijolo Comum', 'Milheiro de tijolos comuns', 450.00, NULL, 50, 1),
                                                                                                           ('Martelo de Aco', 'Martelo robusto para construcao', 45.00, 5.00, 50, 2),
                                                                                                           ('Serrote', 'Serrote de 20 polegadas', 30.00, 3.00, 100, 2),
                                                                                                           ('Furadeira', 'Furadeira eletrica', 150.00, 15.00, 20, 2),
                                                                                                           ('Parafuso', 'Pacote de 100 unidades', 10.00, NULL, 500, 2),
                                                                                                           ('Fio Eletrico', 'Rolo de 100 metros', 100.00, NULL, 80, 3),
                                                                                                           ('Tomada', 'Tomada 20A', 15.00, NULL, 200, 3),
                                                                                                           ('Torneira', 'Torneira de metal', 50.00, NULL, 150, 4),
                                                                                                           ('Cano PVC', 'Cano de PVC 100mm', 25.00, NULL, 300, 4),
                                                                                                           ('Registro', 'Registro de pressao', 40.00, NULL, 100, 4);

-- Insert pedidos
INSERT INTO pedido (valor_total, desconto, data_expedicao, fk_cliente_id, fk_funcionario_id) VALUES
                                                                                                 (475.00, 25.00, '2022-07-20', 1, 2),
                                                                                                 (90.00, 0.00, '2022-07-21', 2, 3),
                                                                                                 (100.00, 10.00, '2022-07-22', 3, 4),
                                                                                                 (300.00, 30.00, '2022-07-23', 4, 5),
                                                                                                 (150.00, 15.00, '2022-07-24', 5, 2);

-- Insert carrinhos
INSERT INTO carrinho (quantidade, fk_pedido_id, fk_produto_id) VALUES
                                                                   (10, 1, 1),
                                                                   (5, 2, 4),
                                                                   (3, 3, 8),
                                                                   (20, 4, 11),
                                                                   (2, 5, 9);

-- Insert vendas
INSERT INTO venda (fk_pedido_id) VALUES
                                     (2), (3), (4), (5);

-- Insert alugueis
INSERT INTO aluguel (data_devolucao, fk_pedido_id, status) VALUES
    ('2024-07-15', 1, 'Entregue');

-- Insert fornecimentos
INSERT INTO fornece (preco_compra, quantidade, fk_fornecedor_id, fk_produto_id) VALUES
                                                                                    (23.00, 1000, 11, 1),
                                                                                    (40.00, 100, 12, 2),
                                                                                    (35.00, 500, 13, 4),
                                                                                    (20.00, 200, 14, 3),
                                                                                    (60.00, 300, 15, 5),
                                                                                    (50.00, 150, 16, 6),
                                                                                    (10.00, 800, 17, 7),
                                                                                    (90.00, 400, 18, 8),
                                                                                    (30.00, 250, 19, 9);

-- Insert telefones
INSERT INTO telefone (telefone, fk_pessoa_id) VALUES
                                                  ('8132321234', 1),
                                                  ('8132325678', 2),
                                                  ('81987654321', 3),
                                                  ('81987654322', 4),
                                                  ('81987654323', 5),
                                                  ('81987654324', 6),
                                                  ('81987654325', 7),
                                                  ('81987654326', 8),
                                                  ('81987654327', 9),
                                                  ('81987654328', 10);

-- Insert enderecos
INSERT INTO endereco (rua, numero, bairro, cidade, estado, cep, fk_pessoa_id) VALUES
                                                                                  ('Rua das Palmeiras', 102, 'Casa Amarela', 'Recife', 'PE', '52070230', 1),
                                                                                  ('Avenida Norte', 205, 'Tamarineira', 'Recife', 'PE', '52110000', 2),
                                                                                  ('Rua do Comercio', 300, 'Boa Vista', 'Recife', 'PE', '50060000', 3),
                                                                                  ('Rua Sete de Setembro', 150, 'Santo Antonio', 'Recife', 'PE', '50030000', 4),
                                                                                  ('Rua da Aurora', 400, 'Boa Vista', 'Recife', 'PE', '50070000', 5),
                                                                                  ('Avenida Conde da Boa Vista', 250, 'Soledade', 'Recife', 'PE', '50050360', 6),
                                                                                  ('Rua do Sol', 350, 'Santo Antonio', 'Recife', 'PE', '50020460', 7),
                                                                                  ('Rua da Imperatriz', 450, 'Santo Amaro', 'Recife', 'PE', '50010000', 8),
                                                                                  ('Rua Nova', 200, 'Recife', 'Recife', 'PE', '50050450', 9),
                                                                                  ('Rua Velha', 300, 'Recife', 'Recife', 'PE', '50060730', 10);