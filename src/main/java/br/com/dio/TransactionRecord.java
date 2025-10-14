package br.com.dio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Record que representa uma transação (entrada, saída ou descarte) no armazém.
 *
 * @param timestamp Data e hora da transação.
 * @param type Tipo da operação ("RECEIVE", "SOLD", "DISCARD").
 * @param quantity Quantidade de cestas envolvidas.
 * @param value Valor total da transação.
 */
public record TransactionRecord(
        LocalDateTime timestamp,
        String type,
        int quantity,
        BigDecimal value
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Retorna a transação formatada para exibição.
     */
    @Override
    public String toString() {
        return String.format("[%s] - %s: %d cestas, Valor: R$ %s",
                timestamp.format(FORMATTER),
                type,
                quantity,
                value.setScale(2, BigDecimal.ROUND_HALF_UP));
    }
}

