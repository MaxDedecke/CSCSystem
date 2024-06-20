package com.css.one.data;

public class CalculationResult {
	
	private double amount;
	boolean isNegative;
	
	public CalculationResult(double amount, boolean isNegative) {
		this.amount = amount;
		this.isNegative = isNegative;
	}
	
	public boolean isNegative() {
		return isNegative;
	}
	public void setNegative(boolean isNegative) {
		this.isNegative = isNegative;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	
	
}
