package app.core.sessions;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

//@Component
public class SessionsContext {

	@Autowired
	private ApplicationContext applicationContext;
	private Map<String, Session> sessions = new ConcurrentHashMap<String, Session>();
	private Timer timer = new Timer();
	@Value("${session.remove.expired.period:20}")
	private int removeExpiredSessionsPeriod;

	private boolean isSessionExpired(Session session) {
		return System.currentTimeMillis() - session.getLastAccesssed() > session.getMaxInactiveInterval();
	}

	@PostConstruct
	private void sessionRemovalTask() {
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				System.out.println(">>>> removing expired session");
				for (String token : sessions.keySet()) {
					Session session = sessions.get(token);
					if (isSessionExpired(session)) {
						System.out.println(">>>> expired session removed");
						invalidateSession(session);
					}
				}

			}
		};

		timer.schedule(task, 300, TimeUnit.SECONDS.toMillis(removeExpiredSessionsPeriod));
	}

	@PreDestroy
	private void stopSessionRemoval() {
		timer.cancel();
		System.out.println(">>>> session removal taks stopped");
	}

	public void invalidateSession(Session session) {
		sessions.remove(session.token);
	}

	public Session creatSession() {
		Session session = applicationContext.getBean(Session.class);
		sessions.put(session.token, session);
		return session;
	}

	public Session getSession(String token) {
		Session session = sessions.get(token);
		if (session != null) {
			if (!isSessionExpired(session)) {
				session.resetLastAccessed();
				return session;
			} else {
				invalidateSession(session);
				return null;
			}
		} else {
			return null;
		}
	}

}
