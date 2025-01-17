package ca.cihi.cims.util.timer;

/**
 * This is a very basic timer that can be used as a low-rent alternative to
 * profiling. Wrap start() and end() invocations around the section you want to
 * time and the timer will keep track of how many invocations and the total
 * time.
 * 
 * @author MPrescott
 */
public class Timer {
	private String name;

	private long totalDuration = 0;
	private long invocationCount = 0;

	private Long startTime;

	public Timer(String name) {
		super();
		this.name = name;
	}

	public void start() {
		if (startTime != null) {
			throw new IllegalArgumentException(
					"This timer is very simple and can't currently handle multiple concurrent invocations. Sorry.");
		}

		unpause();
		invocationCount++;
	}

	public void unpause() {
		if (startTime != null) {
			throw new IllegalStateException("Can't unpause a running timer.");
		}
		this.startTime = System.currentTimeMillis();
	}

	public void stop() {
		pause();
	}

	public void pause() {
		if (startTime == null) {
			throw new IllegalStateException("Timer not running.");
		}
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		totalDuration += duration;
		startTime = null;
	}

	public long getTotalDuration() {
		return totalDuration;
	}

	public long getInvocationCount() {
		return invocationCount;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Timer \t" + name + "\tms:\t" + totalDuration
				+ "\tinvocations:\t" + invocationCount;
	}

	Timer combine(Timer timer) {

		if (!name.equals(timer.name)) {
			throw new IllegalArgumentException(
					"Can't combine timers with different names.");
		}

		this.invocationCount += timer.invocationCount;
		this.totalDuration += timer.totalDuration;

		return this;
	}
}