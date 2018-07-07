package examples;

import org.junit.Test;
import org.junit.runner.RunWith;
import tju.MSchedule.MSchudeuleRunner;
import tju.MSchedule.Schedule;

@RunWith(MSchudeuleRunner.class)
public class MSchuduleDemoTest {

    @Test
    @Schedule("finishOffer2->startingTake")
    public void testOfferOfferTake() throws InterruptedException {

        System.out.println("testSchedule");
    }
}
