package examples;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import tju.MSchedule.MSchudeuleRunner;
import tju.MSchedule.Schedule;
import tju.MSchedule.Schedules;

import java.util.concurrent.ArrayBlockingQueue;

@RunWith(MSchudeuleRunner.class)
public class MSchuduleDemoTest {

    private ArrayBlockingQueue<Integer> queue;

    //Before:每一个测试方法执行前自动调用一次
    @Before
    public void setup() {
        queue = new ArrayBlockingQueue<Integer>(1);
    }

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
