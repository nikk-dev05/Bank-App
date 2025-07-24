package in.sp.main.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import in.sp.main.Repository.Bankrepository;
import in.sp.main.Repository.User_Inforepo;
import in.sp.main.entity.Bank;
import in.sp.main.entity.User1;

import in.sp.main.services.JwtService;

@RestController
@RequestMapping("/auth")
public class Authcontroller {
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private Bankrepository bankrepository;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
   private User_Inforepo user_Inforepo;
    @PostMapping("/register")
    @Transactional
    public ResponseEntity<String> register(@RequestBody User1 request) {
        if (user_Inforepo.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User1 user = new User1();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles("ROLE_USER"); 

        User1 savedUser = user_Inforepo.saveAndFlush(user); 

       
        Bank bank = new Bank();
        bank.setUser(savedUser);
        bank.setAccountHolderName(savedUser.getUsername());
        bank.setAccountNumber(generateAccountNumber()); 
        bank.setAccountBalance(0); 
        bankrepository.save(bank);
        return ResponseEntity.ok("User registered and Bank account Created  successfully");
    }

    private int generateAccountNumber() {
        return (int)(Math.random() * 900000) + 100000; 
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User1 user) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

       
        User1 loggedInUser = user_Inforepo.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

       
        String token = jwtService.generateToken(userDetails, loggedInUser.getId());

   
        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

}
