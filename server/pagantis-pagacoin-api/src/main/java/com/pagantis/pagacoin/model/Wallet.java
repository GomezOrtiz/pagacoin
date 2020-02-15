package com.pagantis.pagacoin.model;

import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Wallet extends BaseEntity {
	
	@Id
    @Column(name = "id", updatable = false, nullable = false)
	private UUID id;
	
	@ManyToOne
	@JsonIgnoreProperties("wallets")	
	private User owner;	
	
	@Column
	private Double balance;
	
	public static class Builder {
		
		private final Wallet wallet;
		
		public Builder() {
			wallet = new Wallet();
			wallet.balance = 0D;
		}
		
		public Builder withId(UUID id) {
            wallet.id = id;
            return this;
        }
		
		public Builder withOwner(User owner) {
			wallet.owner = owner;
			return this;
		}
		
		public Builder withCreatedAt(LocalDate createdAt) {
			wallet.createdAt = createdAt;
			return this;
		}
		
		public Builder withCreatedBy(String createdBy) {
			wallet.createdBy = createdBy;
			return this;
		}
		
		public Builder withUpdatedAt(LocalDate updatedAt) {
			wallet.updatedAt = updatedAt;
			return this;
		}
		
		public Builder withUpdatedBy(String updatedBy) {
			wallet.updatedBy = updatedBy;
			return this;
		}
		
        public Wallet build() {
            return wallet;
        }
	}
	
	private static final long serialVersionUID = -2361050175094416467L;
}
