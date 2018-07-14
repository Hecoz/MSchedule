package examples.SimpleTest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import tju.MSchedule.MSchudeuleRunner;
import tju.MSchedule.Schedule;

@RunWith(MSchudeuleRunner.class)
public class SimpleMainTest {

	@Test
	@Schedule("Thread1assigna->Thread1changex,Thread1changex->Thread1assignb,Thread1assignb->Thread2assignc,Thread2assignc->Thread2changey,Thread2changey->Thread1changey")
	public void test() {
		try {
			Main.main(null);
		} catch (Exception e) {
			System.out.println("error detected");
			fail();
		}
	}

}
