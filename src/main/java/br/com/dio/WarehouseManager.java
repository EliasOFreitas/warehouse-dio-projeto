package br.com.dio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Classe responsável por toda a lógica de gerenciamento do estoque e caixa.
 * Segue o princípio da Responsabilidade Única e agora inclui um registro de transações.
 */
public class WarehouseManager {

    // Lista que armazena o estoque de cestas.
    private List<BasicBasket> stock = new ArrayList<>();
    // Registro de todas as operações de estoque/caixa.
    private final List<TransactionRecord> transactionLog = new ArrayList<>();
    // Valor total em caixa.
    private BigDecimal money = BigDecimal.ZERO;

    // Markup de 20% aplicado sobre o preço de custo unitário.
    private static final BigDecimal MARKUP_PERCENT = new BigDecimal("0.20");
    private static final int SCALE = 2; // Precisão para valores monetários

    public BigDecimal getMoney() {
        return money.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public List<TransactionRecord> getTransactionLog() {
        return transactionLog;
    }

    public void checkStock() {
        int amount = stock.size();
        long outOfDate = stock.stream()
                .filter(b -> b.validate().isBefore(LocalDate.now()))
                .count();
        System.out.printf("Existem **%d** cestas em estoque. Dessas, **%d** estão fora do prazo de validade.\n", amount, outOfDate);
    }

    /**
     * Adiciona novas cestas ao estoque a partir de uma entrega e registra a transação.
     *
     * @param deliveryPrice O valor total da entrega (custo).
     * @param amount A quantidade de cestas na entrega.
     * @param validate A data de validade da entrega.
     */
    public void receiveItems(BigDecimal deliveryPrice, long amount, LocalDate validate) {
        if (amount <= 0 || deliveryPrice.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("ERRO: A quantidade e o preço da entrega devem ser positivos.");
            return;
        }

        try {
            // 1. Calcula o preço de venda final
            BigDecimal unitCost = deliveryPrice.divide(
                    new BigDecimal(amount),
                    SCALE,
                    RoundingMode.HALF_UP
            );
            BigDecimal markup = unitCost.multiply(MARKUP_PERCENT);
            BigDecimal finalPrice = unitCost.add(markup).setScale(SCALE, RoundingMode.HALF_UP);

            // 2. Cria e adiciona as cestas ao estoque
            List<BasicBasket> newBaskets = Stream.generate(() -> new BasicBasket(validate, finalPrice))
                    .limit(amount)
                    .toList();
            stock.addAll(newBaskets);

            // 3. REGISTRA A TRANSAÇÃO DE RECEBIMENTO
            transactionLog.add(new TransactionRecord(
                    LocalDateTime.now(),
                    "RECEBIMENTO",
                    (int) amount,
                    deliveryPrice.negate() // Representa um custo (saída de dinheiro)
            ));

            System.out.printf("SUCESSO: Foram adicionadas **%d** cestas ao estoque com preço de venda final de R$ %s (validade: %s).\n",
                    newBaskets.size(), finalPrice, validate);

        } catch (ArithmeticException e) {
            System.out.println("ERRO de cálculo: Divisão por zero ou erro de precisão. Verifique a quantidade fornecida.");
        }
    }

    /**
     * Realiza a venda de um número específico de cestas e registra a transação.
     *
     * @param amount A quantidade de cestas a serem vendidas.
     */
    /**
     * Realiza a venda de um número específico de cestas e registra a transação.
     *
     * @param amount A quantidade de cestas a serem vendidas.
     */
    public void soldItems(int amount) {
        if (amount <= 0) {
            System.out.println("ERRO: A quantidade a ser vendida deve ser um número positivo.");
            return;
        }

        if (amount > stock.size()) {
            System.out.printf("ERRO: Não há estoque suficiente. Pedido: %d | Disponível: %d.\n", amount, stock.size());
            return;
        }

        // 1. Ordena pelo menor preço.
        stock.sort(Comparator.comparing(BasicBasket::price));

        // 2. Cria a lista de cestas a serem vendidas (os N primeiros elementos).
        // NOTA: É necessário copiar os itens para calcular o valor da venda ANTES de removê-los.
        List<BasicBasket> toSold = new ArrayList<>(stock.subList(0, amount));

        // 3. CORREÇÃO DE BUG: Remove os N primeiros elementos do estoque de forma segura.
        // Usar subList(0, amount).clear() remove apenas as N instâncias que acabamos de vender,
        // evitando que todas as cestas idênticas sejam removidas.
        stock.subList(0, amount).clear();

        // 4. Soma o valor total da venda
        BigDecimal value = toSold.stream()
                .map(BasicBasket::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(SCALE, RoundingMode.HALF_UP);

        // 5. Atualiza o caixa
        money = money.add(value);

        // 6. REGISTRA A TRANSAÇÃO DE VENDA
        transactionLog.add(new TransactionRecord(
                LocalDateTime.now(),
                "VENDA",
                amount,
                value
        ));

        System.out.printf("SUCESSO: Venda de **%d** cestas realizada. Valor total da venda: **R$ %s**.\n", amount, value);
    }

    /**
     * Remove itens que estão fora da data de validade e registra o descarte.
     */
    public void removeItemsOutOfDate() {
        LocalDate today = LocalDate.now();

        List<BasicBasket> outOfDate = stock.stream()
                .filter(b -> b.validate().isBefore(today))
                .toList();

        if (outOfDate.isEmpty()) {
            System.out.println("Não há cestas vencidas no estoque.");
            return;
        }

        // 1. Calcula o prejuízo
        BigDecimal lost = outOfDate.stream()
                .map(BasicBasket::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(SCALE, RoundingMode.HALF_UP);

        // 2. Cria o novo estoque
        stock = stock.stream()
                .filter(b -> !b.validate().isBefore(today))
                .collect(Collectors.toList());

        // 3. REGISTRA A TRANSAÇÃO DE DESCARTE
        transactionLog.add(new TransactionRecord(
                LocalDateTime.now(),
                "DESCARTE",
                outOfDate.size(),
                lost.negate() // Representa uma perda (impacto negativo no valor)
        ));

        System.out.printf("AVISO: Foram descartadas **%d** cestas vencidas. O prejuízo (valor de venda não realizado) foi de **R$ %s**.\n", outOfDate.size(), lost);
    }
}

