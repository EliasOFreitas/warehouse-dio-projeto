package br.com.dio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private final static Scanner scanner = new Scanner(System.in);
    private final static WarehouseManager manager = new WarehouseManager();
    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("      Bem-vindo ao Sistema de Armazém     ");
        System.out.println("         (Com Log de Transações)          ");
        System.out.println("==========================================");

        int option = -1;

        while (true) {
            printMenu();
            try {
                option = scanner.nextInt();
                // Consome a quebra de linha pendente após nextInt()
                scanner.nextLine();

                switch (option) {
                    case 1 -> manager.checkStock();
                    case 2 -> checkMoney();
                    case 3 -> receiveItems();
                    case 4 -> soldItems();
                    case 5 -> manager.removeItemsOutOfDate();
                    case 6 -> displayTransactions(); // NOVA OPÇÃO
                    case 7 -> {
                        System.out.println("Obrigado por usar o sistema. Saindo...");
                        System.exit(0);
                    }
                    default -> System.out.println("Opção inválida. Por favor, selecione uma opção entre 1 e 7.");
                }
            } catch (InputMismatchException e) {
                System.out.println("ERRO: Entrada inválida. Por favor, digite apenas o número da opção desejada.");
                // Limpa o buffer do scanner para evitar loop infinito
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Ocorreu um erro inesperado: " + e.getMessage());
                // Em um sistema real, aqui você registraria o erro (logging)
            }
            System.out.println("------------------------------------------");
        }
    }

    private static void printMenu() {
        System.out.println("\nSelecione a opção desejada:");
        System.out.println("1 - Verificar estoque de cesta básica");
        System.out.println("2 - Verificar caixa");
        System.out.println("3 - Receber Cestas (Nova Entrega)");
        System.out.println("4 - Vender Cestas");
        System.out.println("5 - Remover itens vencidos");
        System.out.println("6 - Visualizar Log de Transações"); // Opção 6 agora é o Log
        System.out.println("7 - Sair"); // Sair foi movido para 7
        System.out.print(">> ");
    }

    private static void checkMoney() {
        System.out.printf("O caixa no momento é de **R$ %s**.\n", manager.getMoney());
    }

    // --- Métodos de Entrada com Validação ---

    private static BigDecimal getBigDecimalInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                // Tenta ler o BigDecimal
                BigDecimal value = scanner.nextBigDecimal();
                // Consome a quebra de linha
                scanner.nextLine();
                if (value.compareTo(BigDecimal.ZERO) < 0) {
                    System.out.println("ERRO: O valor não pode ser negativo.");
                    continue;
                }
                return value;
            } catch (InputMismatchException e) {
                System.out.println("ERRO: Entrada inválida. Por favor, digite um número válido (ex: 100,00).");
                scanner.nextLine(); // Limpa o buffer
            }
        }
    }

    private static int getPositiveIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = scanner.nextInt();
                scanner.nextLine();
                if (value <= 0) {
                    System.out.println("ERRO: A quantidade deve ser um número positivo.");
                    continue;
                }
                return value;
            } catch (InputMismatchException e) {
                System.out.println("ERRO: Entrada inválida. Por favor, digite um número inteiro.");
                scanner.nextLine();
            }
        }
    }

    private static LocalDate getDateInput(String prompt) {
        while (true) {
            System.out.print(prompt + " (formato DD/MM/AAAA): ");
            String dateStr = scanner.nextLine().trim();
            try {
                LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
                if (date.isBefore(LocalDate.now())) {
                    System.out.println("ATENÇÃO: A data de validade está no passado. Confirme se está correta.");
                }
                return date;
            } catch (DateTimeParseException e) {
                System.out.println("ERRO: Formato de data inválido. Use o formato DD/MM/AAAA (ex: 25/12/2025).");
            }
        }
    }

    private static void receiveItems() {
        System.out.println("\n--- RECEBER CESTAS ---");
        // Validação e loop de entrada movidos para métodos auxiliares
        BigDecimal price = getBigDecimalInput("Informe o valor total da entrega (custo): R$ ");
        long amount = getPositiveIntInput("Informe a quantidade de cestas da entrega: ");
        LocalDate validate = getDateInput("Informe a data de vencimento (validade): ");

        manager.receiveItems(price, amount, validate);
    }

    private static void soldItems() {
        System.out.println("\n--- VENDER CESTAS ---");
        int amount = getPositiveIntInput("Quantas cestas serão vendidas: ");
        manager.soldItems(amount);
    }

    private static void displayTransactions() {
        System.out.println("\n--- LOG DE TRANSAÇÕES ---");
        List<TransactionRecord> logs = manager.getTransactionLog();

        if (logs.isEmpty()) {
            System.out.println("Nenhuma transação registrada ainda.");
            return;
        }

        // Exibe o log em ordem cronológica reversa (mais recente primeiro)
        logs.stream()
                .sorted((a, b) -> b.timestamp().compareTo(a.timestamp()))
                .forEach(System.out::println);

        System.out.printf("Total de %d transações registradas.\n", logs.size());
    }
}