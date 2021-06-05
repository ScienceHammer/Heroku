package app.core.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import app.core.entities.Category;
import app.core.entities.Coupon;
import app.core.entities.Customer;
import app.core.exceptions.AlreadyExistsException;
import app.core.exceptions.CouponsSystemException;
import app.core.exceptions.NotFoundExceptions;
import app.core.services.CustomerService;

@CrossOrigin
@RestController
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	private HttpServletRequest httpServletRequest;

	@PostMapping("/purchaseCoupon/{couponId}")
	public void purchaseCoupon(@RequestHeader String token, @PathVariable int couponId) {
		try {
			((CustomerService) httpServletRequest.getAttribute("service")).purchaseCoupon(couponId);
		} catch (CouponsSystemException e) {
			if (e instanceof AlreadyExistsException)
				throw new ResponseStatusException(HttpStatus.ALREADY_REPORTED, e.getMessage());
			else if (e instanceof NotFoundExceptions)
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
			else
				throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, e.getMessage());
		}
	}

	@GetMapping("/getCustomerInfo")
	public Customer getCustomerInfo(@RequestHeader String token) {
		try {
			return ((CustomerService) httpServletRequest.getAttribute("service")).getCustomer();
		} catch (CouponsSystemException e) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
		}
	}

	@GetMapping("/getCustomerCoupons")
	public List<Coupon> getCustomerCoupons(@RequestHeader String token,
		@RequestParam(required = false) Category category, @RequestParam(required = false) Double maxPrice) {
			if (category != null && maxPrice != null) {
				return ((CustomerService) httpServletRequest.getAttribute("service")).getCustomerCoupons(category, maxPrice);
			} else if (category != null) {
				return ((CustomerService) httpServletRequest.getAttribute("service")).getCustomerCoupons(category);
			} else if (maxPrice != null) {
				return ((CustomerService) httpServletRequest.getAttribute("service")).getCustomerCoupons(maxPrice);
			} else {
				return ((CustomerService) httpServletRequest.getAttribute("service")).getCustomerCoupons();
			}
	}
	
	@GetMapping("/getCustomerNotCoupons")
	public List<Coupon> getCustomerMotCoupons(@RequestHeader String token,
			@RequestParam(required = false) Category category, @RequestParam(required = false) Double maxPrice) {
		try {
			if (category != null && maxPrice != null) {
				return ((CustomerService) httpServletRequest.getAttribute("service")).getCustomerNotCoupons(category, maxPrice);
			} else if (category != null) {
				return ((CustomerService) httpServletRequest.getAttribute("service")).getCustomerNotCoupons(category);
			} else if (maxPrice != null) {
				return ((CustomerService) httpServletRequest.getAttribute("service")).getCustomerNotCoupons(maxPrice);
			} else {
					return ((CustomerService) httpServletRequest.getAttribute("service")).getCustomerNotCoupons();
			}
		} catch (CouponsSystemException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
			
		}
	}



}
