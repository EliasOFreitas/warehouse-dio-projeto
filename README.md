# 📦 Sistema Básico de Gerenciamento de Armazém
Este é um projeto simples de gerenciamento de estoque e caixa, desenvolvido como parte do curso de aprofundamento em Java, oferecido pela **DIO (Digital Innovation One)** em parceria com a **Riachuelo**.

O objetivo principal é demonstrar o uso de conceitos fundamentais da **Orientação a Objetos (OO)** e boas práticas de desenvolvimento em Java.

# 💻 Conceitos Chave Empregados
Este projeto foi refatorado para garantir maior robustez, legibilidade e manutenibilidade, aplicando os seguintes conceitos de Java:

# 1. Princípio da Responsabilidade Única (SRP)
O sistema foi dividido em três classes com responsabilidades bem definidas:

``BasicBasket``: Um **Record** (imutável) que modela a entidade de dados (Cesta Básica).

``WarehouseManager``: Concentra toda a **lógica de negócios** (estoque, caixa, recebimento, venda e log de transações).

``Main``: Responsável exclusivamente pela **interface com o usuário** (menu) e pelo tratamento de entradas/erros de I/O.

# 2. Tratamento de Erros e Robustez
Para garantir que o sistema não quebre com entradas inválidas do usuário:

Uso de ``try-catch`` e ``InputMismatchException`` para garantir que valores numéricos e datas sejam inseridos corretamente.

Validações de regras de negócio, como impedir vendas se o estoque for insuficiente ``(if (amount > stock.size()))``.

# 3. Collections e Streams
O estoque (stock) é gerenciado como uma ``List<BasicBasket>``.

As operações como contagem de itens vencidos, descarte e somatório de valores são realizadas de forma concisa e eficiente usando a API de Streams ``(.stream().filter().map().reduce())``.

# 4. Imutabilidade e Precisão
Uso do tipo **Record** ``(BasicBasket)`` para garantir que os dados de cada cesta, como preço e validade, não sejam alterados após a criação.

Uso da classe BigDecimal para realizar cálculos monetários com alta precisão, evitando erros comuns de arredondamento inerentes aos tipos float e double.

# ✅ Funcionalidades do Sistema
O sistema permite as seguintes operações:

``Verificar Estoque:`` Visualiza a quantidade total de cestas e quantas estão vencidas.

``Verificar Caixa:`` Exibe o saldo atual de dinheiro.

``Receber Cestas:`` Adiciona novo lote ao estoque, calcula o preço de custo, aplica um markup de 20% para definir o preço de venda e registra a transação.

``Vender Cestas:`` Realiza a venda, priorizando as cestas de menor preço (para otimizar o fluxo de caixa), atualiza o caixa e registra a transação.

``Remover Itens Vencidos:`` Descarta cestas fora da validade e calcula o prejuízo (com base no preço de venda não realizado).

``Visualizar Log de Transações:`` Exibe um histórico detalhado de todas as operações de ``RECEBIMENTO``, ``VENDA`` e ``DESCARTE``.

# 🙏 Agradecimentos
Gostaria de expressar meu sincero agradecimento à **DIO (Digital Innovation One)** e à **Riachuelo** por proporcionarem esta oportunidade de aprendizado prático em Java. A aplicação de conceitos como **SRP, Records e Streams** em um projeto real foi fundamental para consolidar meus conhecimentos e aprimorar minhas habilidades de programação.
