import static org.junit.Assert.*;

import model.User;
import org.junit.Test;
import service.FinanceService;

public class ApplicationTests {

  @Test
  public void testAddIncome() {
    User user = new User("test", "123");
    FinanceService service = new FinanceService();

    service.addIncome(user, "Salary", 1000);

    assertEquals(1000, service.getTotalIncome(user), 0.01);
    assertEquals(1000, service.getBalance(user), 0.01);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddExpenseNegativeAmount() {
    User user = new User("test", "123");
    FinanceService service = new FinanceService();

    service.addExpense(user, "Food", -50); // должно выбросить исключение
  }

  @Test
  public void testBudgetExceeded() {
    User user = new User("test", "123");
    FinanceService service = new FinanceService();

    service.setBudget(user, "Food", 100);
    service.addExpense(user, "Food", 120);

    assertTrue(service.isBudgetExceeded(user, "Food"));
  }

  @Test
  public void testTransfer() {
    User u1 = new User("Alice", "1");
    User u2 = new User("Bob", "2");
    FinanceService service = new FinanceService();

    service.addIncome(u1, "Salary", 500);
    service.transfer(u1, u2, 200);

    assertEquals(300, service.getBalance(u1), 0.01);
    assertEquals(200, service.getBalance(u2), 0.01);
  }

  @Test
  public void testIsBalanceZero() {
    User user = new User("test", "123");
    FinanceService service = new FinanceService();

    service.addIncome(user, "Salary", 500);
    service.addExpense(user, "Food", 500);

    assertTrue(service.isBalanceZero(user));

    service.addIncome(user, "Bonus", 100);
    assertFalse(service.isBalanceZero(user));
  }
}
