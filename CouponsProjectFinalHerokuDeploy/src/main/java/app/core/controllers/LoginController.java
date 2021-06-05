package app.core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import app.core.entities.Company;
import app.core.entities.Customer;
import app.core.exceptions.CouponsSystemException;
import app.core.login.ClientType;
import app.core.login.LoginManager;
import app.core.managers.JwtUtil;
import app.core.managers.UserDetails;
import app.core.services.AdminService;
import app.core.services.ClientService;
import app.core.services.CompanyService;
import app.core.services.CustomerService;


@RestController
@RequestMapping("/login")
public class LoginController {

	private JwtUtil tokenUtil;
	private LoginManager loginManager;

	@Autowired
	public LoginController(JwtUtil tokenUtil, LoginManager loginManager) {
		super();
		this.tokenUtil = tokenUtil;
		this.loginManager = loginManager;
	}

	@PostMapping("/login")
	public String login(@RequestParam String email, @RequestParam String password,
			@RequestParam ClientType clientType) {
		try {
			UserDetails userDetails = null;
			ClientService clientService = loginManager.login(email, password, clientType);
			switch (clientType) {
			case ADMINISTRATOR:
				AdminService adminService = (AdminService) clientService;
				userDetails = new UserDetails(adminService.getEmail(), ClientType.ADMINISTRATOR);
				break;
			case COMPANY:
				Company company = ((CompanyService) clientService).getCompany();
				userDetails = new UserDetails(company.getEmail(), ClientType.COMPANY);
				break;
			case CUSTOMER:
				Customer customer = ((CustomerService) clientService).getCustomer();
				userDetails = new UserDetails(customer.getEmail(), ClientType.CUSTOMER);
				break;
			}
			return tokenUtil.generateToken(userDetails);
		} catch (CouponsSystemException e) {
			System.out.println(e.getMessage().toString());
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
		}
	}

}
