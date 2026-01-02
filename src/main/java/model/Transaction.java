package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Transaction implements Serializable {
  private String category;
  private double amount;
  private boolean isIncome;
  private LocalDateTime timeStamp;
  private static final long serialVersionUID = 1L;
  private UUID id = UUID.randomUUID();

  public Transaction(String category, boolean isIncome, double amount, LocalDateTime timeStamp) {
    this.category = category;
    this.isIncome = isIncome;
    this.amount = amount;
    this.timeStamp = timeStamp != null ? timeStamp : LocalDateTime.now();
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public boolean isIncome() {
    return isIncome;
  }

  public void setIncome(boolean income) {
    isIncome = income;
  }

  public LocalDateTime getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(LocalDateTime timeStamp) {
    this.timeStamp = timeStamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Transaction that = (Transaction) o;
    return Double.compare(that.amount, amount) == 0
        && isIncome == that.isIncome
        && Objects.equals(category, that.category)
        && Objects.equals(timeStamp, that.timeStamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(category, amount, isIncome, timeStamp);
  }

  public UUID getId() {
    return id;
  }
}
