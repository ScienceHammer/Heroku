package app.core.services;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import app.core.entities.Company;
import app.core.entities.Customer;
import app.core.exceptions.AlreadyExistsException;
import app.core.exceptions.CouponsSystemException;
import app.core.exceptions.NotAllowedException;
import app.core.exceptions.NotFoundExceptions;
import app.core.exceptions.NotNullOrBlankException;
import app.core.repositories.CompanyRepositories;
import app.core.repositories.CouponRepositories;
import app.core.repositories.CustomerRepositories;

@Service
@Transactional
public class AdminService extends ClientService {

	@Value("${service.admin.email:admin@admin.com}")
	private String email;
	@Value("${service.admin.password:admin}")
	private String password;



	@Autowired
	public AdminService(CompanyRepositories companyRepositories, CustomerRepositories customerRepositories,
			CouponRepositories couponRepositories) {
		super(companyRepositories, customerRepositories, couponRepositories);
	}

	/**
	 * return true if log in email and password valid, otherwise false.
	 * 
	 * @return boolean
	 * @param email-   admin@admin.com
	 * @param passowrd - admin
	 */
	@Override
	public boolean login(String email, String password) {
		return email.equals("admin@admin.com") && password.equals("admin");
	}

	/**
	 * Saves a company entity to company's table.
	 * 
	 * This operation places a business logic limitations on what may be saved,
	 * refusing blanks and nulls for some of fields or existed fields values.
	 * 
	 * @param company
	 * @return Company
	 * @throws CouponsSystemException
	 * @exception NotNullOrBlankException if name, email or password are null or
	 *                                    blank.
	 * @exception AlreadyExistsException  if id, name or email already exists.
	 * 
	 */
	public Company addCompany(Company company) throws CouponsSystemException {
		if (company == null || StringUtils.isBlank(company.getName()) || StringUtils.isBlank(company.getEmail())
				|| StringUtils.isBlank(company.getPassword())) {
			throw new NotNullOrBlankException("Not Null Or Blank: company's name, email or password");
		}
		if (company.getCoupons() != null) {
			throw new NotAllowedException("Not Allowed: adding coupons to company");
		}
		if (companyRepositories.existsById(company.getId())) {
			throw new AlreadyExistsException("Already Exists: company's id=" + company.getId());
		}
		if (companyRepositories.existsByName(company.getName())) {
			throw new AlreadyExistsException("Already Exists: company's name=" + company.getName());
		}
		if (companyRepositories.findByEmail(company.getEmail()).isPresent()) {
			throw new AlreadyExistsException("Already Exists: company's email=" + company.getEmail());
		}
		company.setPasswordTimeStamp(System.currentTimeMillis());
		companyRepositories.save(company);
		return company;
	}

	/**
	 * Updates a company entity in company's table.
	 * 
	 * This operation places a business logic limitations on what may be Updated,
	 * refusing blanks and nulls for some of fields, not allowed actions or existed
	 * field's value.
	 * 
	 * @param company
	 * @return Company
	 * @throws CouponsSystemException
	 * @exception NotNullOrBlankException if company is null.
	 * @exception NotAllowedException     if updating company's name.
	 * @exception AlreadyExistsException  if updating existing company's email.
	 *
	 */
	public Company updateCompany(Company company) throws CouponsSystemException {
		if (company == null) {
			throw new NotNullOrBlankException("Not Null Or Blank: company");
		}
		System.out.println(company.getName());
		if (StringUtils.isNotEmpty(company.getName())) {
			throw new NotAllowedException("Not Allowed: updating company's name");
		}
		if (company.getCoupons() != null) {
			throw new NotAllowedException("Not Allowed: updating company's coupons");
		}
		Company dbCompany = getCompanyById(company.getId());
		if (!StringUtils.isBlank(company.getEmail())) {
			if (companyRepositories.existsByIdIsNotAndEmail(company.getId(), company.getEmail())) {
				throw new AlreadyExistsException("Already Exists: company's email=" + company.getEmail());
			}
			dbCompany.setEmail(company.getEmail());
		}
		if (!StringUtils.isBlank(company.getPassword())) {
			dbCompany.setPassword(company.getPassword());
			dbCompany.setPasswordTimeStamp(System.currentTimeMillis());
		}
		dbCompany.setImage(company.getImage());
		return dbCompany;
	}

	/**
	 * Delete a company entity in company's table by supplying it's ID.
	 *
	 * @param companyID
	 * @return Company
	 * @throws CouponsSystemException if company is not be found.
	 * 
	 */
	public Company deleteCompany(int companyID) throws CouponsSystemException {
		Company company = getCompanyById(companyID);
		companyRepositories.deleteById(company.getId());
		return company;
	}

	/**
	 * Getting all companies from company's table.
	 * 
	 * @return List<Company>
	 * 
	 */
	public List<Company> getAllCompanies() {
		return companyRepositories.findAll();
	}

	/**
	 * Getting specified company by id from company's table.
	 *
	 * @param companyID
	 * @return Company
	 * @throws CouponsSystemException
	 * @exception NotFoundExceptions if company is not be found.
	 * 
	 */
	public Company getCompanyById(int companyID) throws CouponsSystemException {
		Optional<Company> optional = companyRepositories.findById(companyID);
		if (optional.isPresent()) {
			return optional.get();
		} else {
			throw new NotFoundExceptions("Not Found: company id=" + companyID);
		}
	}

	/**
	 * Saves a customer entity to company's table.
	 * 
	 * This operation places a business logic limitations on what may be saved,
	 * refusing blanks and nulls for some of fields,not allowed actions or existed
	 * field's value.
	 * 
	 * @param customer
	 * @return Customer
	 * @throws CouponsSystemException
	 * @exception NotNullOrBlankException if cusotmer's email or password are blank
	 *                                    or null.
	 * @exception NotAllowedException     if saving coupons to customer.
	 * @exception AlreadyExistsException  if saving existing customer's id or email.
	 * 
	 */
	public Customer addCustomer(Customer customer) throws CouponsSystemException {
		if (customer == null || StringUtils.isBlank(customer.getEmail()) || StringUtils.isBlank(customer.getPassword()))
			throw new NotNullOrBlankException("Not Null Or Blank: customer's email or password");
		if (customer.getCoupons() != null)
			throw new NotAllowedException("Not Allowed: adding coupons to customer");
		if (customerRepositories.existsById(customer.getId()))
			throw new AlreadyExistsException("Already Exists: customer's id=" + customer.getId());
		if (customerRepositories.findByEmail(customer.getEmail()).isPresent()) {
			throw new AlreadyExistsException("Already Exists: customer's email=" + customer.getEmail());
		}
		customer.setPasswordTimeStamp(System.currentTimeMillis());
		Customer dbCustomer = customerRepositories.save(customer);
		;
		return dbCustomer;

	}

	/**
	 * Updates a customer entity to company's table.
	 * 
	 * This operation places a business logic limitations on what may be saved,
	 * refusing blanks and nulls for some of fields, not allowed actions or existed
	 * field's value.
	 * 
	 * @param customer
	 * @return Customer
	 * @throws CouponsSystemException
	 * @exception NotNullOrBlankException if customer is null.
	 * @exception NotAllowedException     if updating coupons to customer.
	 * @exception AlreadyExistsException  if updating existing customer's id or
	 *                                    email.
	 * 
	 */
	public Customer updateCustomer(Customer customer) throws CouponsSystemException {
		if (customer == null) {
			throw new NotNullOrBlankException("Not Null Or Blank: customer's id");
		}
		if (customer.getCoupons() != null)
			throw new NotAllowedException("Not Allowed: updating customer's coupons");
		Customer dbCustomer = getCustomerById(customer.getId());
		if (!StringUtils.isBlank(customer.getEmail())) {
			if (customerRepositories.existsByIdIsNotAndEmail(customer.getId(), customer.getEmail()))
				throw new AlreadyExistsException("Already Exists: customer's email=" + customer.getEmail());
			dbCustomer.setEmail(customer.getEmail());
		}
		if (!StringUtils.isBlank(customer.getFirstName())) {			
			dbCustomer.setFirstName(customer.getFirstName());
		}
		if (!StringUtils.isBlank(customer.getLastName())) {			
			dbCustomer.setLastName(customer.getLastName());
		}
		if (!StringUtils.isBlank(customer.getPassword())) {			
			dbCustomer.setPassword(customer.getPassword());
			dbCustomer.setPasswordTimeStamp(System.currentTimeMillis());
		}
		dbCustomer.setImage(customer.getImage());
		return dbCustomer;
	}

	/**
	 * Delete a customer entity in customer's table by supplying it's ID.
	 *
	 * @param customerId
	 * @return Customer
	 * @throws CouponsSystemException if customer is not be found.
	 * 
	 */
	public Customer deleteCustomer(int customerID) throws CouponsSystemException {
		Customer customer = getCustomerById(customerID);
		customerRepositories.deleteById(customer.getId());
		return customer;
	}

	/**
	 * Getting all customers from customer's table.
	 * 
	 * @return List<Customer>
	 * 
	 */
	public List<Customer> getAllCustomers() {
		return customerRepositories.findAll();
	}

	/**
	 * Getting specified customer by id from customer's table.
	 *
	 * @param customerID
	 * @return Customer
	 * @throws CouponsSystemException
	 * @exception NotFoundExceptions if customer is not be found.
	 * 
	 */
	public Customer getCustomerById(int customerID) throws CouponsSystemException {
		Optional<Customer> optional = customerRepositories.findById(customerID);
		if (optional.isPresent()) {
			return optional.get();
		} else {
			throw new NotFoundExceptions("Not found: customer id=" + customerID);
		}
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

}
