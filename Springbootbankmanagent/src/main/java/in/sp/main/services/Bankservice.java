package in.sp.main.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import in.sp.main.entity.Bank;
import in.sp.main.entity.User1;

public interface Bankservice {

	public Bank create_account(Bank bank);
	public Bank getAccountdetailsByNmuber(int id);
	public List<Bank> getAccountDetailsAll();
	public Bank depositMoney(int id, double amount);
	public Bank withdrawMoney(int id,double amount);
	public void deleteAccount(int id);

	Page<Bank> getPaginatedAccounts(Pageable pageable); 
	public Bank getAccountDetailsByUsername(String username);
	public Bank getBankByAccountNumber(int id);


}
