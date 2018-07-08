package examples;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import tju.MSchedule.MSchudeuleRunner;
import tju.MSchedule.Schedule;
import tju.MSchedule.Schedules;

import java.util.concurrent.ArrayBlockingQueue;

import static junit.framework.Assert.assertEquals;
import static tju.MSchedule.MSchedule.fireEvent;
import static tju.MSchedule.MSchedule.schAssertEquals;

@RunWith(MSchudeuleRunner.class)
public class MSchuduleDemoTest {

    private ArrayBlockingQueue<Integer> queue;

    //Before:每一个测试方法执行前自动调用一次
    @Before
    public void setup() {
        queue = new ArrayBlockingQueue<Integer>(1);
    }

    private void performParallelOfferssAndTake() throws InterruptedException {

        System.out.println("performParallelOfferssAndTake");

        Thread offerThread = new Thread(new Runnable() {
            @Override
            public void run() {

                fireEvent("startingOffer1");
                System.out.println("startingOffer1");
                queue.offer(43);
                fireEvent("finishOffer1");
                System.out.println("finishOffer1");


                fireEvent("startingOffer2");
                System.out.println("startingOffer2");
                queue.offer(47);
                fireEvent("finishOffer2");
                System.out.println("finishOffer2");
            }
        });

        offerThread.start();

        fireEvent("startingTake");
        System.out.println("startingTake:" + queue.size());
        assertEquals(43, (int) queue.take());

        fireEvent("finishTake");
        System.out.println("finishTake:" + queue.size());

        offerThread.join();
    }


    @Test
    @Schedule("finishOffer2->startingTake")
    public void testOfferOfferTake() throws InterruptedException {

        System.out.println("First Test Start...");
        System.out.println("01-testOfferOfferTake:");
        performParallelOfferssAndTake();
        System.out.println(queue.size());
        assertEquals(0, queue.size());
        System.out.println("----------------------------------------");
    }

    @Test
    @Schedules({
            @Schedule(name = "offer-offer-take", value = "finishOffer2->startingTake"),
            @Schedule(name = "offer-take-offer", value = "finishOffer1->startingTake,finishTake->startingOffer2"),
            @Schedule(name = "takeBlock-offer-takeFinish-offer", value = "[startingTake]->startingOffer1,finishTake->startingOffer2")
    })
    public void testAllThreeSchedules() throws InterruptedException {

        System.out.println("05-testAllThreeSchedules");
        performParallelOfferssAndTake();
        System.out.println(queue.size());
        schAssertEquals("offer-offer-take", 0, queue.size());
        schAssertEquals("offer-take-offer", 1, queue.size());
        schAssertEquals("takeBlock-offer-takeFinish-offer", 1, queue.size());
        System.out.println("----------------------------------------");
    }
}
