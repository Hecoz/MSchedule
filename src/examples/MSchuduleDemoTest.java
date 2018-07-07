package examples;

import org.junit.Test;
import org.junit.runner.RunWith;
import tju.MSchedule.MSchudeuleRunner;
import tju.MSchedule.Schedule;
import tju.MSchedule.Schedules;

@RunWith(MSchudeuleRunner.class)
public class MSchuduleDemoTest {

    @Test
    @Schedule("finishOffer2->startingTake")
    public void testOfferOfferTake() throws InterruptedException {

        System.out.println("testSchedule");
    }

    @Test
    @Schedules({
            @Schedule(name = "offer-offer-take", value = "finishOffer2->startingTake"),
            @Schedule(name = "offer-take-offer", value = "finishOffer1->startingTake,finishTake->startingOffer2"),
            @Schedule(name = "takeBlock-offer-takeFinish-offer", value = "[startingTake]->startingOffer1,finishTake->startingOffer2")
    })
    public void testAllThreeSchedules() throws InterruptedException {

        System.out.println("testSchedules");
    }
}
