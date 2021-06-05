package app.core.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import app.core.entities.Category;
import app.core.entities.Company;
import app.core.entities.Coupon;
import app.core.exceptions.AlreadyExistsException;
import app.core.exceptions.CouponsSystemException;
import app.core.exceptions.NotAllowedException;
import app.core.exceptions.NotFoundExceptions;
import app.core.exceptions.NotNullOrBlankException;
import app.core.repositories.CompanyRepositories;
import app.core.repositories.CouponRepositories;

@Service
@Transactional
@Scope("prototype")
public class CompanyService extends ClientService {

	private int companyID;
	


	@Autowired
	public CompanyService(CompanyRepositories companyRepositories, CouponRepositories couponRepositories) {
		super(companyRepositories, couponRepositories);
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
		Optional<Company> optional = companyRepositories.findByEmailAndPassword(email, password);
		if (optional.isPresent()) {
			this.companyID = optional.get().getId();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Saves a coupon entity to coupon's table.
	 * 
	 * This operation places a business logic limitations on what may be saved,
	 * refusing blanks and nulls for some of fields, not allowed actions or existed
	 * field's value.
	 * 
	 * @param coupon
	 * @return Coupon
	 * @throws CouponsSystemException
	 * @exception NotNullOrBlankException if amount, price, category, title, start
	 *                                    date and end date are null or blank.
	 * @exception NotAllowedException     if adding coupon's company or customer,
	 *                                    amount and price zero or negative or date
	 *                                    is past.
	 * @exception AlreadyExistsException  if id or title already exists.
	 * 
	 */
	public Coupon addCoupon(Coupon coupon) throws CouponsSystemException {
		if (coupon == null || StringUtils.isBlank(coupon.getTitle()) || coupon.getStartDate() == null
				|| coupon.getEndDate() == null || coupon.getCategory() == null) {
			throw new NotNullOrBlankException(
					"Not Null Or Blank: coupon's amount, price, category, title, start date or end date");
		}
		if (coupon.getCompany() != null || coupon.getCustomers() != null) {
			throw new NotAllowedException("Not Allowed: adding coupon's company or customers");
		}
		if (couponRepositories.existsById(coupon.getId())) {
			throw new AlreadyExistsException("Already Exists: coupon's id=" + coupon.getId());
		}
		if (couponRepositories.existsByTitleAndCompanyId(coupon.getTitle(), companyID)) {
			throw new AlreadyExistsException("Already Exists: coupon's title=" + coupon.getTitle());
		}
		if (coupon.getAmount() <= 0) {
			throw new NotAllowedException("Not Allowed: coupon's amount less or equals zero");
		}
		if (coupon.getPrice() <= 0) {
			throw new NotAllowedException("Not Allowed: coupon's price less or equals zero");
		}
		if (coupon.getStartDate().isBefore(LocalDate.now().atStartOfDay())) {
			throw new NotAllowedException("Not Allowed: coupon's start date is past today");
		}
		if (coupon.getEndDate().isBefore(coupon.getStartDate())) {
			throw new NotAllowedException("Not Allowed: coupon's end date is past start date");
		}
		coupon.setCompany(getCompany());
		couponRepositories.save(coupon);
		return coupon;

	}



	/**
	 * Updates a coupon entity in coupon's table.
	 * 
	 * This operation places a business logic limitations on what may be saved,
	 * refusing blanks and nulls for some of fields, not allowed actions or existed
	 * field's value.
	 * 
	 * @param coupon
	 * @return Coupon
	 * @throws CouponsSystemException
	 * @exception NotNullOrBlankException if coupon's id is null.
	 * @exception NotAllowedException     if updating coupon's company or customer,
	 *                                    amount and price zero or negative or date
	 *                                    is past.
	 * @exception AlreadyExistsException  if title already exists.
	 * @exception NotFoundExceptions      if coupon's id is not found.
	 * 
	 */
	public Coupon updateCoupon(Coupon coupon) throws CouponsSystemException {
		if (coupon == null) {
			throw new NotNullOrBlankException("Not Null Or Blank: coupon's id");
		}
		if (coupon.getCompany() != null || coupon.getCustomers() != null) {
			throw new NotAllowedException("Not Allowed: updating coupon's company or customers");
		}
		Optional<Coupon> optional = couponRepositories.findById(coupon.getId());
		if (!optional.isPresent()) {
			throw new NotFoundExceptions("Not found: coupon's id=" + coupon.getId());
		}
		Coupon couponDb = optional.get();
		// coupon's title validations
		if (!StringUtils.isBlank(coupon.getTitle())) {
			if (couponRepositories.existsByIdIsNotAndTitleAndCompanyId(coupon.getId(), coupon.getTitle(), companyID)) {
				throw new AlreadyExistsException("Already Exists: coupon's title=" + coupon.getTitle());
			}
			couponDb.setTitle(coupon.getTitle());
		}
		// coupon's startDate validations
		if (coupon.getStartDate() != null) {
			if (coupon.getStartDate().isBefore(LocalDate.now().atStartOfDay())
					|| coupon.getStartDate().isAfter(couponDb.getEndDate())) {
				throw new NotAllowedException(
						"Not Allowed: coupon's start date is past today or after existing end date");
			}
			couponDb.setStartDate(coupon.getStartDate());
		}
		// coupon's endDate validations
		if (coupon.getEndDate() != null) {
			if (coupon.getEndDate().isBefore(couponDb.getStartDate())) {
				throw new NotAllowedException("Not Allowed: coupon's end date is past start date");
			}
			couponDb.setEndDate(coupon.getEndDate());
		}
		// coupon's amount validations
		if(coupon.getAmount() != null) {			
			if (coupon.getAmount() <= 0) {
				throw new NotAllowedException("Not Allowed: coupon's amount less or equals zero");
			}
			couponDb.setAmount(coupon.getAmount());
		}
		// coupon's price validations
		if(coupon.getPrice() != null) {		
			
			if (coupon.getPrice() <= 0) {
				throw new NotAllowedException("Not Allowed: coupon's amount less or equals zero");
			}
			couponDb.setPrice(coupon.getPrice());
		}
		// coupon's category validations
		if (coupon.getCategory() != null) {
			couponDb.setCategory(coupon.getCategory());
		}
		// coupon's description validations
		if (!StringUtils.isBlank(coupon.getDescription())) {
			couponDb.setDescription(coupon.getDescription());
		}
		// coupon's image validations
		if (coupon.getImage() != null) {
			couponDb.setImage(coupon.getImage());
		}
		return couponDb;
	}

	/**
	 * Delete a coupon from company's coupons by supplying it's ID.
	 *
	 * @param couponID
	 * @return Coupon
	 * @throws CouponsSystemException if coupon is not be found.
	 * 
	 */
	public Coupon deleteCoupon(int couponID) throws CouponsSystemException {
		Optional<Coupon> optional = couponRepositories.findById(couponID);
		if (optional.isPresent()) {
			couponRepositories.delete(optional.get());
			return optional.get();
		} else {
			throw new NotFoundExceptions("Not Found: coupon's id=" + couponID);
		}
	}

	/**
	 * Getting all company's coupons.
	 * 
	 * @return List<coupon>
	 * 
	 */
	public List<Coupon> getCompanyCoupons() {
		return couponRepositories.findAllByCompanyId(this.companyID);
	}

	/**
	 * Getting all company's coupons by category.
	 * 
	 * @param category
	 * @return List<coupon>
	 * 
	 */
	public List<Coupon> getCompanyCoupons(Category category) {
		return couponRepositories.findAllByCompanyIdAndCategory(this.companyID, category);
	}

	/**
	 * Getting all company's coupons by limiting upper price.
	 * 
	 * @param maxPrice
	 * @return List<coupon>
	 * 
	 */
	public List<Coupon> getCompanyCoupons(double maxPrice) {
		return couponRepositories.findAllByCompanyIdAndPriceLessThanEqual(this.companyID, maxPrice);
	}

	public List<Coupon> getCompanyCoupons(Category category, Double maxPrice) {
		return couponRepositories.findAllByCompanyIdAndCategoryAndPriceLessThanEqual(this.companyID, category, maxPrice);
	}

	/**
	 * Getting company's details.
	 *
	 * 
	 * @return Company
	 * @throws CouponsSystemException if coupon is not found.
	 * 
	 */
	public Company getCompany() throws CouponsSystemException {
		Optional<Company> optional = companyRepositories.findById(this.companyID);
		if (optional.isPresent()) {
			Company company = optional.get();
			return company;
		}
		throw new NotFoundExceptions("Not Found: company");
	}
	
	public List<Category> getCompanyCategories() {
		return couponRepositories.getAllCompanyCouponsCategory(this.companyID);
	}

	

}
