package app.core.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.core.comperators.CouponComperator;
import app.core.entities.Category;
import app.core.entities.Coupon;
import app.core.repositories.CouponRepositories;

@Service
@Transactional
public class GuestService {
	
	@Autowired
	private CouponRepositories couponRepositories;

	public List<Coupon> getTop10ByCustomers() {
		CouponComperator couponComperator = new CouponComperator();
		List<Coupon> coupons = couponRepositories.findAll();
		coupons.sort(couponComperator);
		if (coupons.size() > 10) {		
			return coupons.subList(0, 10);
		}else {
			return coupons.subList(0, coupons.size());
		}
	}
	
	public List<Coupon> getTop10ByCustomers(Category category) {
		CouponComperator couponComperator = new CouponComperator();
		List<Coupon> coupons = couponRepositories.findAllByCategory(category);
		coupons.sort(couponComperator);
		if (coupons.size() > 10) {		
			return coupons.subList(0, 10);
		}else {
			return coupons.subList(0, coupons.size());
		}
	}
	

}
