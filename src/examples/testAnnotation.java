package examples;

import tju.MSchedule.Schedule;
import tju.MSchedule.Schedules;

import java.util.concurrent.ArrayBlockingQueue;

public class testAnnotation {


    @Schedule("finishOffer2->startingTake")
    public void testOfferOfferTake() throws InterruptedException {

        System.out.println("testSchedule");
    }

    @Schedules({
                 @Schedule(name = "offer-offer-take", value = "finishOffer2->startingTake"),
                 @Schedule(name = "offer-take-offer", value = "finishOffer1->startingTake,finishTake->startingOffer2"),
                 @Schedule(name = "takeBlock-offer-takeFinish-offer", value = "[startingTake]->startingOffer1,finishTake->startingOffer2")
               })
    public void testAllThreeSchedules() throws InterruptedException {

        System.out.println("testSchedules");
    }


}
