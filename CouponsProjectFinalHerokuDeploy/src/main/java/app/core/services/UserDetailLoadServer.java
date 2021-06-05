package app.core.services;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import app.core.entities.Company;
import app.core.entities.Customer;
import app.core.exceptions.NotFoundExceptions;
import app.core.repositories.CompanyRepositories;
import app.core.repositories.CustomerRepositories;

@Service
@Transactional
public class UserDetailLoadServer {
	
	@Value("${service.admin.email:'admin@admin.com'}")
	private String adminEmail;
	@Value("${service.admin.password:admin}")
	private String adminPassword;
	
	private CompanyRepositories companyRepositories;
	private CustomerRepositories customerRepositories;

	@Autowired
	public UserDetailLoadServer(CompanyRepositories companyRepositories, CustomerRepositories customerRepositories) {
		super();
		this.companyRepositories = companyRepositories;
		this.customerRepositories = customerRepositories;
	}
	
	public Company getCompanyDetailsByEmail(String email) throws NotFoundExceptions {
		Optional<Company> optional = companyRepositories.findByEmail(email);
		if(optional.isPresent()) {
			return optional.get();
		} else {
			throw new NotFoundExceptions("Not Found: company");
		}
	}

	public Customer getCustomerDetailsByEmail(String email) throws NotFoundExceptions {
		Optional<Customer> optional = customerRepositories.findByEmail(email);
		if(optional.isPresent()) {
			return optional.get();
		} else {
			throw new NotFoundExceptions("Not Found: customer");
		}
	}

	public String getAdminEmail() {
		return adminEmail;
	}

	public String getAdminPassword() {
		return adminPassword;
	}
	

}
