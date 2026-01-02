import exception.InvalidCredentialsException;
import exception.UserNotFoundException;
import model.Transaction;
import service.FinanceService;
import service.UserService;
import model.User;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class FinanceApp {

    private final UserService userService;
    private final FinanceService financeService;
    private final Scanner scanner;

    public FinanceApp() {
        userService = new UserService();
        financeService = new FinanceService();
        scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("Добро пожаловать в приложение \" Личные финансы \"");
        while (true) {
            if (!userService.isUserLoggedIn()) {
                showAuthMenu();
                handleAuthMenu();
            } else {
                showMainMenu();
                handleMainMenu();
            }
        }
    }

    private void showAuthMenu() {
        System.out.println("1. Регистрация");
        System.out.println("2. Вход");
        System.out.println("3. ВЫХОД");
        System.out.println("ВЫБЕРИТЕ действие");
    }

    private void handleAuthMenu() {
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                handleRegistration();
                break;
            case "2":
                handleLogin();
                break;
            case "3":
                System.out.println("ВЫХОД");
                System.exit(0);
                break;
            default:
                System.out.println("Неверный выбор");
        }
    }

    private void handleRegistration() {
        try {
            System.out.println("Имя");
            String userName = scanner.nextLine().trim();

            System.out.println("Password");
            String password = scanner.nextLine().trim();

            userService.registerUser(userName, password);
            System.out.println("Success");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleLogin() {
        try {
            System.out.println("Имя");
            String userName = scanner.nextLine().trim();

            System.out.println("Password");
            String password = scanner.nextLine().trim();

            userService.authenticateUser(userName, password);
            System.out.println("Success");
        } catch (UserNotFoundException | InvalidCredentialsException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showMainMenu() {
        System.out.println("1. Добавить доход");
        System.out.println("2. Добавить расход");
        System.out.println("3. Установить бюджет");
        System.out.println("4. Просмотр Статистики");
        System.out.println("5. ВЫХОД");
        System.out.println("6. HELP");
        System.out.println("7. Перевод пользователю");
        System.out.println("8. Редактировать транзакцию");
        System.out.println("9. Расходы по выбранным категориям");
        System.out.println("10. Фильтрация по периоду");
        System.out.println("11. Экспорт статистики");

        System.out.println("ВЫБЕРИТЕ действие");
    }

    private void handleMainMenu() {
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                handleAddIncome();
                break;
            case "2":
                handleAddExpense();
                break;
            case "3":
                handleSetBudget();
                break;
            case "4":
                showStatistics();
                break;
            case "5":
                userService.logout();
                break;
            case "6":
                showHelp();
                break;
            case "7":
                handleTransfer();
                break;
            case "8":
                handleEditTransaction();
                break;
            case "9":
                handleExpensesByCategories();
                break;
            case "10":
                handleTransactionsByPeriod();
                break;
            case "12":
                exportStatisticsToFile();
                break;

            default:
                System.out.println("Неверный выбор");
        }
    }

    private void showHelp() {
        System.out.println("""
        Доступные команды:
        1. add-income   — добавить доход
        2. add-expense  — добавить расход
        3. transfer     — перевод другому пользователю
        4. stats        — статистика
        5. help         — список команд
        6. exit         — выход
        """);
    }

    private void handleAddIncome() {
        try {
            System.out.println("Введите категорию");
            String category = scanner.nextLine().trim();

            System.out.println("Введите сумму дохода:");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            financeService.addIncome(userService.getCurrentUser(), category, amount);

            System.out.println("Доход добавлен");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите число");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void handleAddExpense() {
        try {
            System.out.println("Введите категорию");
            String category = scanner.nextLine().trim();

            System.out.println("Введите сумму расхода:");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            financeService.addExpense(userService.getCurrentUser(), category, amount);

            // Проверки уведомлений
            if (financeService.isBudgetWarning(userService.getCurrentUser(), category)) {
                System.out.println("Внимание: достигнуто 80% бюджета по категории " + category);
            }
            if (financeService.isBalanceZero(userService.getCurrentUser())) {
                System.out.println("Внимание: баланс равен нулю");
            }

            if (financeService.isBudgetExceeded(userService.getCurrentUser(), category)) {
                System.out.println("Бюджет по категории " + category + " превышен");
            }

            System.out.println("Расход добавлен");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите число");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }


    private void handleSetBudget() {
        try {
            System.out.println("Введите категорию");
            String category = scanner.nextLine().trim();

            System.out.println("Введите бюджет:");
            double budget = Double.parseDouble(scanner.nextLine().trim());

            financeService.setBudget(userService.getCurrentUser(), category, budget);
            System.out.println("Бюджет установлен");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите число");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void handleTransfer() {
        try {
            System.out.println("Введите имя получателя:");
            String toUsername = scanner.nextLine().trim();

            System.out.println("Введите сумму перевода:");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            User toUser = userService.getUser(toUsername); // нужно добавить метод getUser в UserService
            if (toUser == null) {
                System.out.println("Пользователь не найден");
                return;
            }

            financeService.transfer(userService.getCurrentUser(), toUser, amount);
            System.out.println("Перевод выполнен");

            if (financeService.isBalanceZero(userService.getCurrentUser())) {
                System.out.println("Внимание: баланс равен нулю");
            }


        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите число");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void handleEditTransaction() {
        try {
            System.out.println("Введите ID транзакции:");
            UUID id = UUID.fromString(scanner.nextLine().trim());

            System.out.println("Новая категория:");
            String newCategory = scanner.nextLine().trim();

            System.out.println("Новая сумма:");
            double newAmount = Double.parseDouble(scanner.nextLine().trim());

            financeService.editTransaction(userService.getCurrentUser(), id, newCategory, newAmount);
            System.out.println("Транзакция обновлена");

        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void handleExpensesByCategories() {
        System.out.println("Введите категории через запятую:");
        String[] categories = scanner.nextLine().trim().split(",");
        double sum = financeService.getExpensesByCategories(
                userService.getCurrentUser(),
                List.of(categories)
        );
        System.out.println("Сумма расходов по выбранным категориям: " + sum);
    }


    private void showStatistics() {
        User user = userService.getCurrentUser();

        System.out.println("Статистика");

        double totalIncome = financeService.getTotalIncome(user);
        double totalExpenses = financeService.getTotalExpenses(user);
        System.out.println("Общий доход: " + totalIncome);
        System.out.println("Общий расход: " + totalExpenses);

        Map<String, Double> incomeByCategory = financeService.getIncomeByCategory(user);
        if (!incomeByCategory.isEmpty()) {
            System.out.println("Доходы по категориям:");
            incomeByCategory.forEach((category, amount) ->
                    System.out.println(category + " : " + amount));
        }

        Map<String, Double> expensesByCategory = financeService.getExpensesByCategory(user);
        if (!expensesByCategory.isEmpty()) {
            System.out.println("Расходы по категориям:");
            expensesByCategory.forEach((category, amount) -> {
                System.out.println(category + " : " + amount);
                Double budget = user.getWallet().getBudget(category);

                if (budget != null) {
                    double remaining = financeService.getBudgetRemaining(user, category);
                    System.out.println("Бюджет: " + budget + " | Остаток: " + remaining);
                }
            });
        }

        if (financeService.areExpensesExceedingIncome(user)) {
            System.out.println("Ваши расходы превышают доходы");
        }
    }

    private void handleTransactionsByPeriod() {
        try {
            System.out.println("Введите дату начала (yyyy-MM-ddTHH:mm):");
            LocalDateTime from = LocalDateTime.parse(scanner.nextLine().trim());

            System.out.println("Введите дату конца (yyyy-MM-ddTHH:mm):");
            LocalDateTime to = LocalDateTime.parse(scanner.nextLine().trim());

            List<Transaction> list = financeService.getTransactionsByPeriod(userService.getCurrentUser(), from, to);
            if (list.isEmpty()) {
                System.out.println("Транзакции не найдены");
            } else {
                list.forEach(t ->
                        System.out.println(t.getTimeStamp() + " | " + t.getCategory() + " | " +
                                (t.isIncome() ? "Доход" : "Расход") + " | " + t.getAmount())
                );
            }
        } catch (Exception e) {
            System.out.println("Ошибка ввода: " + e.getMessage());
        }
    }

    private void exportStatisticsToFile() {
        User user = userService.getCurrentUser();
        String fileName = "statistics_" + user.getUsername() + ".txt";

        try (PrintWriter writer = new PrintWriter(fileName)) {
            writer.println("Общий доход: " + financeService.getTotalIncome(user));
            writer.println("Общий расход: " + financeService.getTotalExpenses(user));

            writer.println("\nДоходы по категориям:");
            financeService.getIncomeByCategory(user).forEach((cat, amount) ->
                    writer.println(cat + " : " + amount));

            writer.println("\nРасходы по категориям:");
            financeService.getExpensesByCategory(user).forEach((cat, amount) -> {
                writer.println(cat + " : " + amount);
                Double budget = user.getWallet().getBudget(cat);
                if (budget != null) {
                    double remaining = financeService.getBudgetRemaining(user, cat);
                    writer.println("Бюджет: " + budget + " | Остаток: " + remaining);
                }
            });

            System.out.println("Статистика сохранена в файл: " + fileName);
        } catch (Exception e) {
            System.out.println("Ошибка записи файла: " + e.getMessage());
        }
    }



    public static void main(String[] args) {
        new FinanceApp().run();
    }
}
