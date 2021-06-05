package app.core.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import app.core.entities.Category;
import app.core.entities.Coupon;
import app.core.entities.Customer;
import app.core.exceptions.AlreadyExistsException;
import app.core.exceptions.CouponsSystemException;
import app.core.exceptions.NotAllowedException;
import app.core.exceptions.NotFoundExceptions;
import app.core.exceptions.NotNullOrBlankException;
import app.core.repositories.CouponRepositories;
import app.core.repositories.CustomerRepositories;

@Service
@Transactional
@Scope("prototype")
public class CustomerService extends ClientService {

	private int customerID;

	@Autowired
	public CustomerService(CustomerRepositories customerRepositories, CouponRepositories couponRepositories) {
		super(customerRepositories, couponRepositories);
	}

	/**
	 * Authenticates login's email and password and returns value.
	 * 
	 * @param email
	 * @param password
	 * @return boolean
	 * 
	 */
	@Override
	public boolean login(String email, String password) {
		Optional<Customer> optional = customerRepositories.findByEmailAndPassword(email, password);
		if (optional.isPresent()) {
			this.customerID = optional.get().getId();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Purchasing a coupon for the calling customer instance.
	 * 
	 * This operation places a business logic limitations on what may be saved,
	 * refusing blanks and nulls for some of fields, not allowed actions or existed
	 * field's value.
	 * 
	 * @param coupon
	 * @throws CouponsSystemException
	 * @exception NotNullOrBlankException if coupon's id is null.
	 * @exception NotFoundExceptions      if coupon is not found.
	 * @exception NotAllowedException     if coupon's amount is zero or date is
	 *                                    past.
	 * @exception AlreadyExistsException  if coupon is already purchased by current
	 *                                    customer instance.
	 * 
	 */
	public void purchaseCoupon(int couponId) throws CouponsSystemException {
	
		Optional<Coupon> optional = couponRepositories.findById(couponId);
		if (!optional.isPresent()) {
			throw new NotFoundExceptions("Not Found: coupon's id=" + couponId);
		}
		Coupon dbCoupon = optional.get();
		if (couponRepositories.existsByIdAndCustomersId(couponId, customerID)) {
			throw new AlreadyExistsException("Already Exists: already purchased coupon with id=" + couponId);
		}
		if (dbCoupon.getAmount() == 0) {
			throw new NotAllowedException("Not Allowed: coupon is out of stock");
		}
		if (dbCoupon.getEndDate().isBefore(LocalDateTime.now())) {
			throw new NotAllowedException("Not Allowed: coupon's date is past");
		}
		dbCoupon.addCustomer(getCustomer());
		dbCoupon.setAmount(dbCoupon.getAmount() - 1);

	}

	/**
	 * Getting all customer's coupons.
	 * 
	 * @return List<coupon>
	 * 
	 */
	public List<Coupon> getCustomerCoupons() {
		return couponRepositories.getCouponsByCustomersId(this.customerID);
	}

	/**
	 * Getting all customer's coupons by category.
	 * 
	 * @param category
	 * @return List<coupon>
	 * 
	 */
	public List<Coupon> getCustomerCoupons(Category category) {
		return couponRepositories.getCouponsByCustomersIdAndCategory(this.customerID, category);
	}

	/**
	 * Getting all customer's coupons by limiting upper price.
	 * 
	 * @param maxPrice
	 * @return List<coupon>
	 * 
	 */
	public List<Coupon> getCustomerCoupons(double maxPrice) {
		return couponRepositories.getCouponsByCustomersIdAndPriceLessThanEqual(this.customerID, maxPrice);

	}

	public List<Coupon> getCustomerCoupons(Category category, double maxPrice) {
		return couponRepositories.findAllByCompanyIdAndCategoryAndPriceLessThanEqual(this.customerID, category, maxPrice);
		
	}
	
	public List<Coupon> getCustomerNotCoupons() throws CouponsSystemException {
		return couponRepositories.findAllByCustomersNotContaining(getCustomer());
	}
	
	/**
	 * Getting all customer's coupons by category.
	 * 
	 * @param category
	 * @return List<coupon>
	 * @throws CouponsSystemException 
	 * 
	 */
	public List<Coupon> getCustomerNotCoupons(Category category) throws CouponsSystemException {
		return couponRepositories.findAllByCustomersNotContainingAndCategory(getCustomer(), category);
	}
	
	/**
	 * Getting all customer's coupons by limiting upper price.
	 * 
	 * @param maxPrice
	 * @return List<coupon>
	 * @throws CouponsSystemException 
	 * 
	 */
	public List<Coupon> getCustomerNotCoupons(double maxPrice) throws CouponsSystemException {
		return couponRepositories.findAllByCustomersNotContainingAndPriceLessThanEqual(getCustomer(), maxPrice);
		
	}
	
	public List<Coupon> getCustomerNotCoupons(Category category, double maxPrice) throws CouponsSystemException {
		return couponRepositories.findAllByCustomersNotContainingAndCategoryAndPriceLessThanEqual(getCustomer(), category, maxPrice);
		
	}

	/**
	 * Getting customer's details.
	 *
	 * @return Customer
	 * @throws CouponsSystemException if coupon is not found.
	 * 
	 */
	public Customer getCustomer() throws CouponsSystemException {
		Optional<Customer> optional = customerRepositories.findById(this.customerID);
		if (optional.isPresent()) {
			Customer customer = optional.get();
			return customer;
		}
		throw new NotFoundExceptions("Not Found: customer");
	}

}
