package in.sp.main.services;




import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import in.sp.main.Repository.Bankrepository;
import in.sp.main.Repository.TransactionRepo;
import in.sp.main.Repository.User_Inforepo;
import in.sp.main.entity.Bank;
import in.sp.main.entity.Transaction;
import in.sp.main.entity.User1;

@Service
public class Bankserviceimpl implements Bankservice {
     @Autowired
	private Bankrepository bankrepository;
     @Autowired
     private TransactionRepo transactionRepo;
     @Autowired 
     private User_Inforepo user_Inforepo;
	@Override
	public Bank create_account(Bank bank) {
	
		return  bankrepository.save(bank);
	}
	@Override
	public Bank getAccountdetailsByNmuber(int id) {
	   Bank get_Details= bankrepository.findById(id).orElseThrow(()-> new RuntimeException("Account not present with this id :"+id) );
	 
	  
		  return get_Details;
	  
	   
			}
	@Override
	public List<Bank> getAccountDetailsAll() {
	
		
		List<Bank> user = bankrepository.findAll();
		return user;
	}
	@Override

	public Bank depositMoney(int id, double amount) {
	    Bank money_added = bankrepository.findById(id)
	        .orElseThrow(() -> new RuntimeException("Account not found with this id: " + id));

	    Double money = money_added.getAccountBalance();
	    money_added.setAccountBalance(money + amount);
	    Bank updatedAccount = bankrepository.save(money_added);

	    
	    Transaction tx = new Transaction();
	    tx.setBank(updatedAccount);
	    tx.setType("DEPOSIT");
	    tx.setAmount(amount);
	    tx.setTimestamp(java.time.LocalDateTime.now());
	    transactionRepo.save(tx);

	    return updatedAccount;
	}

	@Override
	public Bank withdrawMoney(int id, double amount) {
	    Bank account = bankrepository.findById(id)
	        .orElseThrow(() -> new RuntimeException("Account not found with ID: " + id));
	    
	    double currentBalance = account.getAccountBalance();
	    if (amount > currentBalance) {
	        throw new RuntimeException("Insufficient Balance");
	    }

	    account.setAccountBalance(currentBalance - amount);
	    Bank updatedAccount = bankrepository.save(account);

	   
	    Transaction tx = new Transaction();
	    tx.setBank(updatedAccount);
	    tx.setType("WITHDRAW");
	    tx.setAmount(amount);
	    tx.setTimestamp(java.time.LocalDateTime.now());
	    transactionRepo.save(tx);

	    return updatedAccount;
	}


	@Override
	public Bank getAccountDetailsByUsername(String username) {
	    User1 user = user_Inforepo.findByUsername(username)
	        .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

	    List<Bank> accounts = bankrepository.findByUser(user);

	    if (accounts.isEmpty()) {
	        throw new RuntimeException("Bank details not found for user: " + username);
	    }

	    return accounts.get(0); 
	}



    
		   	 
			
	@Override
	public void deleteAccount(int id) {
	     
		Bank account = bankrepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found with ID: " + id));
			bankrepository.delete(account);

	}

	@Override
	public Page<Bank> getPaginatedAccounts(Pageable pageable) {
	
		return bankrepository.findAll(pageable);
	}
	@Override
	public Bank getBankByAccountNumber(int id) {
	    return bankrepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Bank account not found"));
	}

	
	

}
