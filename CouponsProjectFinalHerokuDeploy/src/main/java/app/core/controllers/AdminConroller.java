package app.core.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import app.core.entities.Company;
import app.core.entities.Customer;
import app.core.exceptions.CouponsSystemException;
import app.core.exceptions.NotFoundExceptions;
import app.core.services.AdminService;

@CrossOrigin
@RestController
@RequestMapping("/admin")
public class AdminConroller {

	public AdminService adminService;

	@Autowired
	public AdminConroller(AdminService adminService) {
		super();
		this.adminService = adminService;
	}

	@PostMapping("/addCompany")
	public Company addCompany(@RequestHeader String token, @ModelAttribute Company company) {
		try {
			return adminService.addCompany(company);
		} catch (CouponsSystemException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PutMapping("/updateCompany")
	public Company updateCompany(@RequestHeader String token, @ModelAttribute Company company) {
		try {
			return adminService.updateCompany(company);
		} catch (NotFoundExceptions e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		} catch (CouponsSystemException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/getAllCompanies")
	public List<Company> getAllCompanies(@RequestHeader String token) {
		return adminService.getAllCompanies();
	}

	@GetMapping("/getCompany")
	public Company getCompany(@RequestHeader String token, @RequestParam int companyId) {
		try {
			return adminService.getCompanyById(companyId);
		} catch (CouponsSystemException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@DeleteMapping("/deleteCompany")
	public String deleteCompany(@RequestHeader String token, @RequestParam int companyId) {
		try {
			adminService.deleteCompany(companyId);
			return "Company with id = " + companyId + ", is deleted";
		} catch (CouponsSystemException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@PostMapping("/addCustomer")
	public Customer addCustomer(@RequestHeader String token, @ModelAttribute Customer customer) {
		try {
			return adminService.addCustomer(customer);
		} catch (CouponsSystemException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PutMapping("/updateCustomer")
	public Customer updateCustomer(@RequestHeader String token, @ModelAttribute Customer customer) {
		try {
			return adminService.updateCustomer(customer);
		} catch (NotFoundExceptions e) {

			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());

		} catch (CouponsSystemException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/getAllCustomers")
	public List<Customer> getAllCustomers(@RequestHeader String token) {
		return adminService.getAllCustomers();
	}

	@GetMapping("/getCustomer")
	public Customer getCustomer(@RequestHeader String token, @RequestParam int customerId) {
		try {
			return adminService.getCustomerById(customerId);
		} catch (CouponsSystemException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@DeleteMapping("/deleteCustomer")
	public String deleteCustomer(@RequestHeader String token, @RequestParam int customerId) {
		try {
			adminService.deleteCustomer(customerId);
			return "Customer with id = " + customerId + ", is deleted";
		} catch (CouponsSystemException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

}
