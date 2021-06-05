package app.core.sessions;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

//@Component
//@Scope("prototype")
public class Session {
	
	public final String token; // session identifier
	private Map<String, Object> attributes = new HashMap<String, Object>();
	private long lastAccesssed;
	@Value("${session.max.inactive.interval:2}")
	private long maxInactiveInterval;
	private static final int TOKEN_MAX_LENGTH = 15;
	private LocalDateTime expirationDate;
	
	public Session() {
		this.token = UUID.randomUUID().toString().replace("-", "").substring(0, TOKEN_MAX_LENGTH);
		resetLastAccessed();
	}
	
	@PostConstruct
	private void init() {
		maxInactiveInterval = TimeUnit.MINUTES.toMillis(maxInactiveInterval);
		this.expirationDate =  LocalDateTime.ofInstant(Instant.ofEpochMilli(this.lastAccesssed + maxInactiveInterval), 
                ZoneOffset.UTC);
	}
	
	public void resetLastAccessed() {
		this.lastAccesssed = System.currentTimeMillis();
	}

	public long getLastAccesssed() {
		return lastAccesssed;
	}

	public long getMaxInactiveInterval() {
		return maxInactiveInterval;
	}
	
	public void setAttribute(String attrName, Object attrVal) {
		attributes.put(attrName, attrVal);
	}
	
	public Object getAttribute(String attrName) {
		return attributes.get(attrName);
	}

	public LocalDateTime getExpirationDate() {
		return expirationDate;
	}
	
	

}
