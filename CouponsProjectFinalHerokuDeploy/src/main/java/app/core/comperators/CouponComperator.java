package app.core.comperators;

import java.util.Comparator;

import app.core.entities.Coupon;

public class CouponComperator implements Comparator<Coupon>{

	@Override
	public int compare(Coupon o1, Coupon o2) {
		if(o1.getCustomers().size() < o2.getCustomers().size()) {
			return 1;
		}else if(o1.getCustomers().size() == o2.getCustomers().size()) {
			return 0;
		}else {
			return -1;
		}
	}
	

}
