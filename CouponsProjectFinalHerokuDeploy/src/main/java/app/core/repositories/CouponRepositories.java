package app.core.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import app.core.entities.Category;
import app.core.entities.Coupon;
import app.core.entities.Customer;

public interface CouponRepositories extends JpaRepository<Coupon, Integer> {

	boolean existsByTitleAndCompanyId(String title, int companyID);

	boolean existsByIdIsNotAndTitleAndCompanyId(int couponID, String title, int companyID);

	Long deleteByEndDateBefore(LocalDateTime endDate);

	List<Coupon> findAllByCompanyId(int companyID);

	List<Coupon> findAllByCompanyIdAndCategory(int companyID, Category category);

	List<Coupon> findAllByCompanyIdAndPriceLessThanEqual(int companyID, double price);

	List<Coupon> findAllByCompanyIdAndCategoryAndPriceLessThanEqual(int companyID, Category category, Double price);

	boolean existsByIdAndCustomersId(int id, int customerID);

	List<Coupon> getCouponsByCustomersId(int customerID);

	List<Coupon> getCouponsByCustomersIdAndCategory(int customerID, Category category);

	List<Coupon> getCouponsByCustomersIdAndPriceLessThanEqual(int customerID, double price);

	List<Coupon> findAllByCustomersIdAndCategoryAndPriceLessThanEqual(int customerID, Category category, Double price);
	
	List<Coupon> findAllByCustomersNotContaining(Customer customer);

	List<Coupon> findAllByCustomersNotContainingAndCategory(Customer customer, Category category);

	List<Coupon> findAllByCustomersNotContainingAndPriceLessThanEqual(Customer customer, double price);

	List<Coupon> findAllByCustomersNotContainingAndCategoryAndPriceLessThanEqual(Customer customer, Category category, Double price);
	
	List<Coupon> findAllByCategory(Category category);
	
	@Query(value = "SELECT DISTINCT category FROM  Coupon c WHERE c.company.id= ?1")
	List<Category> getAllCompanyCouponsCategory(Integer companyId);
	

	
}

