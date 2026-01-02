package service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import model.Transaction;
import model.User;

public class FinanceService {
  public void addIncome(User user, String category, double amount) {
    if (category == null || category.isBlank()) {
      throw new IllegalArgumentException("Категория пуста");
    }
    if (amount <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }

    Transaction transaction = new Transaction(category, true, amount, LocalDateTime.now());
    user.getWallet().addTransaction(transaction);
  }

  public void addExpense(User user, String category, double amount) {
    if (category == null || category.isBlank()) {
      throw new IllegalArgumentException("Категория пуста");
    }
    if (amount <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }

    Transaction transaction = new Transaction(category, false, amount, LocalDateTime.now());

    user.getWallet().addTransaction(transaction);
  }

  public void setBudget(User user, String category, double amount) {
    if (category == null || category.isBlank()) {
      throw new IllegalArgumentException("Категория пуста");
    }
    if (amount < 0) {
      throw new IllegalArgumentException("Budget amount cannot be negative");
    }
    user.getWallet().setBudget(category, amount);
  }

  public double getTotalIncome(User user) {
    return user.getWallet().getTransactions().stream()
        .filter(Transaction::isIncome)
        .mapToDouble(Transaction::getAmount)
        .sum();
  }

  public double getTotalExpenses(User user) {
    return user.getWallet().getTransactions().stream()
        .filter(transaction -> !transaction.isIncome())
        .mapToDouble(Transaction::getAmount)
        .sum();
  }

  public Map<String, Double> getIncomeByCategory(User user) {
    return user.getWallet().getTransactions().stream()
        .filter(Transaction::isIncome)
        .collect(
            Collectors.groupingBy(
                Transaction::getCategory, Collectors.summingDouble(Transaction::getAmount)));
  }

  public Map<String, Double> getExpensesByCategory(User user) {
    return user.getWallet().getTransactions().stream()
        .filter(transaction -> !transaction.isIncome())
        .collect(
            Collectors.groupingBy(
                Transaction::getCategory, Collectors.summingDouble(Transaction::getAmount)));
  }

  public double getBudgetRemaining(User user, String category) {
    double budget =
        user.getWallet().getBudget(category) != null ? user.getWallet().getBudget(category) : 0.0;
    double spend = getExpensesByCategory(user).getOrDefault(category, 0.0);
    return budget - spend;
  }

  public boolean isBudgetExceeded(User user, String category) {
    return getBudgetRemaining(user, category) < 0;
  }

  public boolean areExpensesExceedingIncome(User user) {
    return getTotalExpenses(user) > getTotalIncome(user);
  }

  public double getBalance(User user) {
    double balance = 0;

    for (Transaction t : user.getWallet().getTransactions()) {
      if (t.isIncome()) {
        balance += t.getAmount();
      } else {
        balance -= t.getAmount();
      }
    }

    return balance;
  }

  public void transfer(User from, User to, double amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }

    if (getBalance(from) < amount) {
      throw new IllegalArgumentException("Недостаточно средств");
    }

    // Списание у отправителя
    from.getWallet()
        .addTransaction(
            new Transaction("TRANSFER_TO_" + to.getUsername(), false, amount, LocalDateTime.now()));

    // Пополнение у получателя
    to.getWallet()
        .addTransaction(
            new Transaction(
                "TRANSFER_FROM_" + from.getUsername(), true, amount, LocalDateTime.now()));
  }

  public boolean isBudgetWarning(User user, String category) {
    Double budget = user.getWallet().getBudget(category);
    if (budget == null) return false;

    double spent = getExpensesByCategory(user).getOrDefault(category, 0.0);

    return spent >= budget * 0.8 && spent < budget;
  }

  public boolean isBalanceZero(User user) {
    return getBalance(user) == 0;
  }

  public double getExpensesByCategories(User user, List<String> categories) {
    return user.getWallet().getTransactions().stream()
        .filter(t -> !t.isIncome())
        .filter(t -> categories.contains(t.getCategory()))
        .mapToDouble(Transaction::getAmount)
        .sum();
  }

  public List<Transaction> getTransactionsByPeriod(
      User user, LocalDateTime from, LocalDateTime to) {

    return user.getWallet().getTransactions().stream()
        .filter(t -> !t.getTimeStamp().isBefore(from))
        .filter(t -> !t.getTimeStamp().isAfter(to))
        .toList();
  }

  public void editTransaction(User user, UUID id, String newCategory, double newAmount) {

    Transaction t =
        user.getWallet().getTransactions().stream()
            .filter(tr -> tr.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

    t.setCategory(newCategory);
    t.setAmount(newAmount);
  }
}
