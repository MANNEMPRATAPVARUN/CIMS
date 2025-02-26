package ca.cihi.cims.util.timer;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Perf {
	private static Deque<Timer> timers = new LinkedList<Timer>();

	// private static Collection<Timer> allTimers = new ArrayList<Timer>();

	private static Map<String, Timer> consolidated = new HashMap<String, Timer>();

	private static final Logger LOGGER = LogManager.getLogger(Perf.class);

	private Perf() {
		// Don't instantiate Perf, it will get angry.
	}

	public static synchronized void start(String timerName) {

		if (!LOGGER.isDebugEnabled()) {
			return;
		}

		Timer peek = timers.peek();
		if (peek != null) {
			peek.pause();
		}
		Timer timer = new Timer(timerName);
		timer.start();
		timers.addFirst(timer);

//		consolidate(timer);
	}

	public static synchronized void stop(String timerName) {
		if (!LOGGER.isDebugEnabled()) {
			return;
		}

		Timer timer = timers.poll();

		if (timer == null) {
			throw new IllegalStateException("Can't stop timer '" + timerName
					+ "': no current timer.");
		}

		if (!timer.getName().equals(timerName)) {
			throw new IllegalStateException("Can't stop timer '" + timerName
					+ "': current timer: " + timer.getName());
		}

		timer.stop();
		consolidate(timer);

		Timer peek = timers.peek();
		if (peek != null) {
			peek.unpause();
		}
	};

	public static synchronized void displayAll() {

		for (Timer timer : consolidated.values()) {
			LOGGER.debug(timer);
		}
	}

	private static void consolidate(Timer timer) {
		if (consolidated.containsKey(timer.getName())) {
			consolidated.get(timer.getName()).combine(timer);
		} else {
			consolidated.put(timer.getName(), timer);
		}
	}
}
