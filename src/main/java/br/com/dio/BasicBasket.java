package br.com.dio;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Record que representa uma Cesta Básica no estoque.
 * Records são uma forma concisa de criar classes para dados imutáveis (Java 16+).
 * * @param validate A data de validade da cesta.
 * @param price O preço de custo final (incluindo markup) usado para cálculo de prejuízo e caixa.
 */
public record BasicBasket(
        LocalDate validate,
        BigDecimal price
) {}
