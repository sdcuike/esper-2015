package com.doctor.esper.event;

import java.math.BigDecimal;

import com.alibaba.fastjson.JSON;

public class Withdrawal {
	private String account;

	private BigDecimal amount;

	public Withdrawal(String account, BigDecimal amount) {
		this.account = account;
		this.amount = amount;
	}

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

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
