package app.core.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.core.entities.Category;
import app.core.entities.Coupon;
import app.core.services.GuestService;

@CrossOrigin
@RestController
@RequestMapping("/guest")
public class GuestController {
	
	@Autowired
	private GuestService guestService;
	
	@GetMapping("/getTop10Coupons")
	public List<Coupon> getCoupons(@RequestParam(required = false) Category category) {
		if (category != null) {
			return guestService.getTop10ByCustomers(category); 
		} else {	
			return guestService.getTop10ByCustomers(); 
		}
	}

}
