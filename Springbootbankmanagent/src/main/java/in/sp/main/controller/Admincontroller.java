package in.sp.main.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/admin")
public class Admincontroller {
	 @GetMapping("/dashboard")
	    public String adminDashboard() {
	        return "Welcome to ADMIN Dashboard";
	    }
}
