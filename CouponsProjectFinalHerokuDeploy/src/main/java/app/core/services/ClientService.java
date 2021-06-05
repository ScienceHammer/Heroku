package app.core.services;

import app.core.exceptions.ServiceException;
import app.core.repositories.CompanyRepositories;
import app.core.repositories.CouponRepositories;
import app.core.repositories.CustomerRepositories;

public abstract class ClientService {

	protected CompanyRepositories companyRepositories;

	protected CustomerRepositories customerRepositories;

	protected CouponRepositories couponRepositories;

	public ClientService(CompanyRepositories companyRepositories, CustomerRepositories customerRepositories,
			CouponRepositories couponRepositories) {
		super();
		this.companyRepositories = companyRepositories;
		this.customerRepositories = customerRepositories;
		this.couponRepositories = couponRepositories;
	}
	
	public ClientService(CustomerRepositories customerRepositories, CouponRepositories couponRepositories) {
		super();
		this.customerRepositories = customerRepositories;
		this.couponRepositories = couponRepositories;
	}
	
	public ClientService(CompanyRepositories companyRepositories, CouponRepositories couponRepositories) {
		super();
		this.companyRepositories = companyRepositories;
		this.couponRepositories = couponRepositories;
	}
	
	/**
	 * authenticates client log in if email and password are valid returns true else
	 * false.
	 *
	 * @param email
	 * @param password
	 * @return boolean
	 */
	public abstract boolean login(String email, String password) throws ServiceException;


}
