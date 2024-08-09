
# Warehouse
Este projeto foi desenvolvido para a disciplina `Projeto e Modelagem de Banco de Dados`, do curso de Ciência da Computação da Cesar School. É um sistema de gerenciamento de armazéns desenvolvido em Java utilizando JavaFX para a interface gráfica, MySQL como banco de dados, e Docker para facilitar a configuração do ambiente. O projeto permite gerenciar pedidos, produtos, clientes, funcionários e fornecedores.

### Ferramentas
- **Java 17**
- **JavaFX**
- **IntelliJ IDEA**
- **Maven**
- **Scene Builder**
- **MySQL**
- **Docker**

## Configuração do Ambiente

### Pré-requisitos

Certifique-se de ter as seguintes ferramentas instaladas em seu sistema:

- Docker e Docker Compose
- IntelliJ IDEA
- Java 17
- Maven

### Clone o repositório do projeto:

    git clone https://github.com/Kal-0/BD-GerenciamentoDeArmazens-.git
    cd BD-GerenciamentoDeArmazens-

### Levantar/derrubar o banco de dados:
```bash
docker compose up
```
```bash
docker compose down
```
### Abrir terminal do container:
```bash
docker exec -it warehouse_db bash
```
Alternativamente, através do Docker Desktop `Containers -> warehouse_db -> Exec`

### Abrir shell do mysql:
```bash
mysql -uroot -padmin
```

### Rodar os scripts init e populate

Use uma IDE, como IntelliJ, Dbeaver...

Alternativamente, dentro do shell do mysql:
```bash
source /opt/db/initdb.sql
```
```bash
source /opt/db/populatedb2.sql
```

### Rodar a aplicação
Run na classe `MainApp` em `src/main/java/com/warehouse/warehouse`

## Estrutura do Projeto

A estrutura básica do projeto é a seguinte:

- `src/main/java/com/warehouse/warehouse`: Contém as classes principais do projeto.
  - `controller`: Controladores JavaFX para interação com a interface gráfica.
  - `database`: Classes de conexão com o banco de dados.
  - `util`: Outros recursos e utilitários.
  - `MainApp`: Ponto de entrada da aplicação
- `src/main/resources`: Contém os arquivos FXML para a interface gráfica.
- `database`: Contém os scripts SQL para inicialização e população do banco de dados.

### Modelo Conceitual
![armazem_logico]![image](https://github.com/Kal-0/DECOY/blob/main/warehouse/armazem_conceitual.png)

### Modelo Lógico
![armazem_logico](https://github.com/Kal-0/DECOY/blob/main/warehouse/armazem_logico.png)

### Diagrama de Fluxo
![warehouse_flux](https://github.com/Kal-0/DECOY/blob/main/warehouse/warehouse_flux.png)

### Screencast
[![Screencast](https://img.youtube.com/vi/ZzyJMsNny6M/0.jpg)](https://youtu.be/ZzyJMsNny6M)

## Equipe
<table>
  <tr>
    <td align="center">
      <a href="https://github.com/Kal-0">
        <img src="https://avatars.githubusercontent.com/u/106926790?s=400&u=d51d91a8d447afbb4a9d0be21d664b82d7091fc5&v=4" width="100px;" alt="Foto Kal"/><br>
        <sub>
          <b>Caio Cesar</b>
        </sub>
      </a>
    </td>
          <td align="center">
      <a href="https://github.com/DiogoHMC">
        <img src="https://avatars.githubusercontent.com/u/116087739?s=400&u=7b127b8ccdb42826d3ab422ea188bc3e0c6f5c23&v=4" width="100px;" alt="Foto Kal"/><br>
        <sub>
          <b>Diogo Henrique</b>
        </sub>
      </a>
    </td>
          <td align="center">
      <a href="https://github.com/pedro-coelho-dr">
        <img src="https://avatars.githubusercontent.com/u/111138996?v=4" width="100px;" alt="Foto Kal"/><br>
        <sub>
          <b>Pedro Coelho</b>
        </sub>
      </a>
    </td>
  </table>
