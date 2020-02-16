package com.pagantis.pagacoin.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TransactionRequest {
	
	private String senderId;

	private String receiverId;
	
	private Double amount;
	
	public TransactionRequest(String senderId, String receiverId, Double amount) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.amount = amount;
	}
}
