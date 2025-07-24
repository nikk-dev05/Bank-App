package in.sp.main.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.sp.main.entity.Bank;
import in.sp.main.entity.User1;
import java.util.List;


@Repository
public interface Bankrepository extends JpaRepository<Bank, Integer> {

   
    Page<Bank> findAll(Pageable pageable);
    List<Bank> findByUser(User1 user);
    Bank findByAccountNumber(int accountNumber);
    Optional<Bank> findById(Integer id); 

  


   

}

