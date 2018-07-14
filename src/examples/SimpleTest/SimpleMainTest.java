package examples.SimpleTest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import tju.MSchedule.MSchudeuleRunner;

@RunWith(MSchudeuleRunner.class)
public class SimpleMainTest {

	@Test

	public void test() {
		try {
			Main.main(null);
		} catch (Exception e) {
			System.out.println("error detected");
			fail();
		}
	}

}
