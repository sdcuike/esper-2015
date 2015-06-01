package com.doctor.esper.event;

import java.math.BigDecimal;

public class Withdrawal {
	private String account;

	private BigDecimal amount;

	public String getAccount() {
		return account;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
