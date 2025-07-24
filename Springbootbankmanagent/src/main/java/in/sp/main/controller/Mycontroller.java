package in.sp.main.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import in.sp.main.Repository.Bankrepository;
import in.sp.main.Repository.TransactionRepo;
import in.sp.main.Repository.User_Inforepo;
import in.sp.main.entity.Bank;
import in.sp.main.entity.Transaction;
import in.sp.main.entity.User1;
import in.sp.main.exception.Myexception;
import in.sp.main.services.Bankservice;
import in.sp.main.services.JwtService;
import in.sp.main.services.User1service;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/bank")
public class Mycontroller {

    @Autowired
    private Bankservice bankservice;
    @Autowired
    private Bankrepository bankrepository;
    @Autowired
    private User_Inforepo user_Inforepo;
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private TransactionRepo transactionRepo;
    @Autowired
    private User1service user1service;

   
    @PostMapping
    public ResponseEntity<Bank> createAccount(@RequestBody Bank bank, Principal principal) {
        if (bank.getAccountHolderName() == null || bank.getAccountHolderName().trim().isEmpty()
                || bank.getAccountBalance() <= 0) {
            throw new Myexception("All fields are mandatory and balance must be greater than 0");
        }

       
        String username = principal.getName();

     
        User1 user = user1service.getUserByUsername(username);
        if (user == null) {
            throw new Myexception("No user found for username: " + username);
        }

        
        bank.setUser(user);

        Bank createdAccount = bankservice.create_account(bank);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getDetails(@PathVariable int id, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long userIdFromToken = jwtService.extractUserId(token);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && id != userIdFromToken.intValue()) {
            return new ResponseEntity<>("Access Denied: You can only view your own account", HttpStatus.FORBIDDEN);
        }

        Bank details = bankservice.getAccountdetailsByNmuber(id);
        return ResponseEntity.ok(details);
    }

    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Bank>> getAllDetails() {
        List<Bank> userDetails = bankservice.getAccountDetailsAll();
        return ResponseEntity.ok(userDetails);
    }
    @PutMapping("/{id}/deposit")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> depositMoney(@PathVariable int id, @RequestParam double amount, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long userIdFromToken = jwtService.extractUserId(token);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Bank account = bankrepository.findByAccountNumber(id);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        if (!isAdmin && account.getUser().getId() != userIdFromToken) {
            return new ResponseEntity<>("Access Denied: You can only deposit to your own account", HttpStatus.FORBIDDEN);
        }

        Bank updated = bankservice.depositMoney(id, amount);
        return ResponseEntity.ok(updated);
    }
    @PutMapping("/{id}/withdraw")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> withdrawMoney(@PathVariable int id, @RequestParam double amount, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long userIdFromToken = jwtService.extractUserId(token);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Bank bank = bankservice.getBankByAccountNumber(id); // ← fetch Bank account

        if (!isAdmin && !userIdFromToken.equals(Long.valueOf(bank.getUser().getId()))) {

            return new ResponseEntity<>("Access Denied: You can only withdraw from your own account", HttpStatus.FORBIDDEN);
        }

           
        

        Bank updated = bankservice.withdrawMoney(id, amount);
        return ResponseEntity.ok(updated);
    }


    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAccount(@PathVariable int id) {
        bankservice.deleteAccount(id);
        return ResponseEntity.ok("Account deleted successfully.");
    }

    
    @GetMapping("/paginated")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Bank>> getAccountsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Bank> pagedAccounts = bankservice.getPaginatedAccounts(pageable);
        return ResponseEntity.ok(pagedAccounts);
    }
   @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{accountNumber}/transactions")
    public ResponseEntity<?> getTransactionHistory(@PathVariable int accountNumber, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
       Long userIdFromToken = jwtService.extractUserId(token);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); 
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
       if (isAdmin || accountNumber == userIdFromToken.intValue()) {
            return ResponseEntity.ok(transactionRepo.findByBankAccountNumber(accountNumber));
           

        } else {
            return new ResponseEntity<>("Access Denied", HttpStatus.FORBIDDEN);
        }


     
    }
   


  
   @PreAuthorize("hasAnyRole('USER','ADMIN')")
   @GetMapping("/my")
   public ResponseEntity<?> getMyAccountDetails(Authentication authentication) {
       String username = authentication.getName();
       System.out.println("Username from token: " + username); 

       Optional<User1> userOpt = user_Inforepo.findByUsername(username);
       if (userOpt.isEmpty()) {
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found: " + username);
       }

       User1 user = userOpt.get();
       List<Bank> bankAccounts = bankrepository.findByUser(user);

       if (bankAccounts.isEmpty()) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No bank accounts found for user");
       }

       return ResponseEntity.ok(bankAccounts);
   }






    // Handle custom exceptions
    @ExceptionHandler(Myexception.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public String handleCustomException(Myexception e) {
        return e.getMsg();
    }

    // Handle other runtime exceptions
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleRuntimeException(Exception e) {
        return e.getMessage();
    }
}
