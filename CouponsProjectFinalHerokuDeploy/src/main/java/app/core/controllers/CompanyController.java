package app.core.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import app.core.entities.Category;
import app.core.entities.Company;
import app.core.entities.Coupon;
import app.core.exceptions.CouponsSystemException;
import app.core.services.CompanyService;

@CrossOrigin
@RestController
@RequestMapping("/company")
public class CompanyController {

	@Autowired
	private HttpServletRequest httpServletRequest;

	@PostMapping("/addCoupon")
	public Coupon addCoupon(@RequestHeader String token, @ModelAttribute Coupon coupon) {
		try {
			return ((CompanyService) httpServletRequest.getAttribute("service")).addCoupon(coupon);
		} catch (CouponsSystemException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PutMapping("/updateCoupon")
	public Coupon updateCoupon(@RequestHeader String token, @ModelAttribute Coupon coupon) {
		try {
			return ((CompanyService) httpServletRequest.getAttribute("service")).updateCoupon(coupon);
		} catch (CouponsSystemException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@DeleteMapping("/deleteCoupon")
	public String deleteCoupon(@RequestHeader String token, @RequestParam int couponId) {
		try {
			((CompanyService) httpServletRequest.getAttribute("service")).deleteCoupon(couponId);
			return "Coupon with id " + couponId + ", is deleted";
		} catch (CouponsSystemException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping("/getCompanyInfo")
	public Company getCompanyInfo(@RequestHeader String token) {
		try {
			return ((CompanyService) httpServletRequest.getAttribute("service")).getCompany();
		} catch (CouponsSystemException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping("/getCompanyCategories")
	public List<Category> getCompanyCategories(@RequestHeader String token) {
		return ((CompanyService) httpServletRequest.getAttribute("service")).getCompanyCategories();

	}

	@GetMapping("/getAllCompanyCoupons")
	public List<Coupon> getAllCompanyCoupons(@RequestHeader String token,
			@RequestParam(required = false) Category category, @RequestParam(required = false) Double maxPrice) {
		if (category != null && maxPrice != null) {
			return ((CompanyService) httpServletRequest.getAttribute("service")).getCompanyCoupons(category, maxPrice);
		} else if (category != null) {
			return ((CompanyService) httpServletRequest.getAttribute("service")).getCompanyCoupons(category);
		} else if (maxPrice != null) {
			return ((CompanyService) httpServletRequest.getAttribute("service")).getCompanyCoupons(maxPrice);
		} else {
			return ((CompanyService) httpServletRequest.getAttribute("service")).getCompanyCoupons();
		}
	}
}
