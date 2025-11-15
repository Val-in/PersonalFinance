import exception.InvalidCredentialsException;
import exception.UserNotFound;
import service.FinanceService;
import service.UserService;
import model.User;

import java.util.Map;
import java.util.Scanner;

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
        } catch (UserNotFound | InvalidCredentialsException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showMainMenu() {
        System.out.println("1. Добавить доход");
        System.out.println("2. Добавить расход");
        System.out.println("3. Установить бюджет");
        System.out.println("4. Просмотр Статистики");
        System.out.println("5. ВЫХОД");
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
            default:
                System.out.println("Неверный выбор");
        }
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

    public static void main(String[] args) {
        new FinanceApp().run();
    }
}
