package com.pagantis.pagacoin.model;

import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class User extends BaseEntity {
	
	@Id
    @Column(name = "id", updatable = false, nullable = false)
	@Type(type="uuid-char")
	private UUID id;
	
	@Column(name = "name", nullable = false)
	private String name;
	
	@Column(name = "surname")
	private String surname;
	
	@Column(name = "second_surname")
	private String secondSurname;
	
	@Column(name = "date_of_birth")
	private LocalDate dateOfBirth;
	
	@Column(name = "email", unique=true)
	private String email;
	
	@Column(name = "phone")
	private String phone;
	
	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval = true)
	@JsonIgnoreProperties("owner")	
	private Collection<Wallet> wallets;
	
	public static class Builder {
		
		private final User user;
		
		public Builder() {
			user = new User();
		}
		
		public Builder withId(UUID id) {
            user.id = id;
            return this;
        }
		
		public Builder withName(String name) {
            user.name = name;
            return this;
        }
		
		public Builder withSurname(String surname) {
            user.surname = surname;
            return this;
        }
		
		public Builder withSecondSurname(String surname) {
			user.secondSurname = surname;
			return this;
		}
		
		public Builder withDateOfBirth(LocalDate dateOfBirth) {
			user.dateOfBirth = dateOfBirth;
			return this;
		}
		
		public Builder withEmail(String email) {
			user.email = email;
			return this;
		}
		
		public Builder withPhone(String phone) {
			user.phone = phone;
			return this;
		}
		
		public Builder withCreatedAt(LocalDate createdAt) {
			user.createdAt = createdAt;
			return this;
		}
		
		public Builder withCreatedBy(String createdBy) {
			user.createdBy = createdBy;
			return this;
		}
		
		public Builder withUpdatedAt(LocalDate updatedAt) {
			user.updatedAt = updatedAt;
			return this;
		}
		
		public Builder withUpdatedBy(String updatedBy) {
			user.updatedBy = updatedBy;
			return this;
		}
		
        public User build() {
            return user;
        }
	}
	
	private static final long serialVersionUID = -6549259923990929797L;
}
