package in.sp.main.controller;

import in.sp.main.entity.User1;
import in.sp.main.Repository.User_Inforepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class Usercontroller {

    @Autowired
    private User_Inforepo userRepo;

    @GetMapping("/info")
    public User1 getUserInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUsername(username)
                       .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }
}
