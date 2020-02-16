package com.pagantis.pagacoin.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Transaction extends BaseEntity {
	
	@Id
    @Column(name = "id", updatable = false, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="sender_id")
	Wallet sender;
	
	@ManyToOne
	@JoinColumn(name="receiver_id")
	Wallet receiver;
	
	@Column(nullable = false)
	Double amount;
	
	public Transaction(Wallet sender, Wallet receiver, Double amount) {
		this.sender = sender;
		this.receiver = receiver;
		this.amount = amount;
		this.createdAt = LocalDate.now();
		this.createdBy = "ADMIN";
	}
	
	private static final long serialVersionUID = -4363098503970503137L;

}
