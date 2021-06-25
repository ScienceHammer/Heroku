package app.core.dailyJob;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import app.core.services.DailyJobService;

@Component
public class ExpiredCouponsCleaner {

	private final Timer timer;
	private DailyJobService service;
	@Value("${dail.jop.expired.coupons.period:2}")
	private long period;

	/**
	 * initializing the cleaner and it's scheduled timer
	 * 
	 */
	@Autowired
	public ExpiredCouponsCleaner(DailyJobService service) {
		this.service = service;
		timer = new Timer();
	}
	
	@PostConstruct
	public void init() {
		timer.scheduleAtFixedRate(new ExpiredCouponsTask(), 3000, TimeUnit.MINUTES.toMillis(period));
	}

	// timer task class for implementing run method deleting expired coupons
	private class ExpiredCouponsTask extends TimerTask {
		@Override
		public void run() {
			service.deleteByEndDateBefore(LocalDateTime.now());
		}

	}
	
	/*
	 * stops the cleaner timer
	 */
	@PreDestroy
	public void stop() {
		timer.cancel();
	}
	
}
