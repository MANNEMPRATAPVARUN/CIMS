package ca.cihi.cims.util.timer;

import org.junit.Ignore;
import org.junit.Test;

public class PerfTest {
	@Ignore
	@Test
	public void testPerf() throws Exception {
		Perf.start("go");
		Perf.start("wendy");

		Thread.sleep(500);

		Perf.stop("wendy");
		Perf.stop("go");
		Perf.displayAll();
	}

	@Ignore
	@Test
	public void testAccumulatingTime() throws Exception {

		Perf.start("a");
		Thread.sleep(500);
		Perf.stop("a");

		Perf.start("a");
		Thread.sleep(500);
		Perf.stop("a");

		Perf.displayAll();
	}
}
