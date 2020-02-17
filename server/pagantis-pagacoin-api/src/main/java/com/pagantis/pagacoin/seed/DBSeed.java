package com.pagantis.pagacoin.seed;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.github.javafaker.Faker;
import com.pagantis.pagacoin.dao.TransactionDao;
import com.pagantis.pagacoin.dao.UserDao;
import com.pagantis.pagacoin.dao.WalletDao;
import com.pagantis.pagacoin.model.Transaction;
import com.pagantis.pagacoin.model.User;
import com.pagantis.pagacoin.model.Wallet;

@Component
@Profile("!test")
public class DBSeed {

	private UserDao userDao;
	private WalletDao walletDao;
	private TransactionDao transactionDao;

	@Autowired
	public DBSeed(UserDao userDao, WalletDao walletDao, TransactionDao transactionDao) {
		this.userDao = userDao;
		this.walletDao = walletDao;
		this.transactionDao = transactionDao;
	}

	@EventListener
	public void seedUsersTable(ContextRefreshedEvent event) {
		
		Faker faker = new Faker(new Locale("es"));

		Collection<User> users = userDao.findAll();

		if (CollectionUtils.isEmpty(users)) {

			for (int i = 0; i <= 50; i++) {
				saveRandomUser(faker);
			}
		}
		
		Collection<Transaction> transactions = transactionDao.findAll();
		
		if(CollectionUtils.isEmpty(transactions)) {
			
			List<Wallet> wallets = walletDao.findAll();

			for(int i = 0; i <= 300; i++) {
				saveRandomTransaction(wallets);
			}
		}
	}

	private void saveRandomUser(Faker faker) {
		
		LocalDate creationDate = getRandomCreationDate();
		String name = faker.name().firstName();
		String lastName = faker.name().lastName();

		UUID id = UUID.randomUUID();
		User user = new User.Builder()
				.withId(id)
				.withName(name)
				.withSurname(lastName)
				.withSecondSurname(faker.name().lastName())
				.withDateOfBirth(getRandomDate())
				.withEmail(getEmail(name, lastName))
				.withPhone(cleanPhone(faker.phoneNumber().cellPhone()))
				.withCreatedAt(creationDate)
				.withCreatedBy("ADMIN")
				.build();
		
		Collection<Wallet> wallets = new ArrayList<Wallet>();
		int numWallets = generateRandomInteger(1,5);
		
		for(int i = 0; i <= numWallets; i++) {
			Wallet wallet = getRandomWallet(user, creationDate);
			wallets.add(wallet);
		}
		
		user.setWallets(wallets);
		userDao.save(user);
	}
	
	private Wallet getRandomWallet(User owner, LocalDate creationDate) {
		
		UUID id = UUID.randomUUID();
        Wallet wallet = new Wallet.Builder()
        		.withId(id)
        		.withOwner(owner)
        		.withCreatedAt(creationDate)
        		.withCreatedBy("ADMIN")
        		.build();
        
        wallet.setBalance(generateRandomAmount(50,500));
        return wallet;
	}
	
	private void saveRandomTransaction(List<Wallet> wallets) {
		
		Wallet randomWallet = getRandomWallet(wallets, null);
				
		Transaction transaction = new Transaction(randomWallet, getRandomWallet(wallets, randomWallet.getId()), generateRandomAmount(10,150));
		transactionDao.save(transaction);
	}
	
	private Wallet getRandomWallet(List<Wallet> wallets, UUID walletId) {
		
		Wallet randomWallet = wallets.get(generateRandomInteger(0, wallets.size() - 1));
		
		while(walletId != null && walletId.equals(randomWallet.getId())) {
			randomWallet = wallets.get(generateRandomInteger(0, wallets.size() - 1));
		}
		
		return randomWallet;
	}
	
	private String cleanPhone(String phone) {
		return phone.replaceAll("[^a-zA-Z0-9]", "");
	}
	
	private String getEmail(String name, String surname) {
		return String.format("%1$s.%2$s@gmail.com", StringUtils.stripAccents(StringUtils.lowerCase(name)), StringUtils.stripAccents(StringUtils.lowerCase(surname)));
	}
	
	private LocalDate getRandomCreationDate() {
		
		int year = generateRandomInteger(2017, 2019);
		int month = generateRandomInteger(1,12);
		int day = generateRandomInteger(1,28);

		return LocalDate.of(year, month, day);
	}
	
	private LocalDate getRandomDate() {
		
		int year = generateRandomInteger(1950, 2000);
		int month = generateRandomInteger(1,12);
		int day = generateRandomInteger(1,28);

		return LocalDate.of(year, month, day);
	}

	private int generateRandomInteger(int min, int max) {
	    Random r = new Random();
	    return r.nextInt((max - min) + 1) + min;
	}
	
	private Double generateRandomAmount(int min, int max) {
	    Double amount = (Math.random()*((max-min)+1))+min;
	    Double truncatedDouble = BigDecimal.valueOf(amount)
	    	    .setScale(2, RoundingMode.HALF_UP)
	    	    .doubleValue();
	    return truncatedDouble;
	}
}
