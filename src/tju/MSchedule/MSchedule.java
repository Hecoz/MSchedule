package tju.MSchedule;

import org.junit.Assert;
import tju.MSchedule.moudles.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MSchedule extends Assert {

    /**
     * Constants
     */
    private static final String AT = "@";
    private static final String CLEARED_SCHEDULE = "";
//    private static final String BLOCKING_EVENT_THREAD_DID_NOT_BLOCK_MSG = "Thread named: %s, executing blocking event: [%s], completed execution without blocking";


    /**
     * Current schedule state i.e. state describing the currently enforced schedule.
     */
    private static String currentSchedule = CLEARED_SCHEDULE;
    private static final Map<String,Set<Event>> currentOrderings = new HashMap<String,Set<Event>>();
    private static final Map<String,Thread> currentHappenedEvents = new HashMap<String,Thread>();
    private static final Map<Thread,String> currentWaitingThreads = new HashMap<Thread,String>();

    /**
     * Lock used to enforce schedules.
     */
    private static final Object lock = new Object();

    /**
     * Returns the current schedule's name.
     */
    public static String getCurrentSchedule() {
        return currentSchedule;
    }

    /**
     * Returns the orderings of the current schedule as a Map with beforeEvents keyed by their afterEvents.
     */
    public static Map<String, Set<Event>> getCurrentOrderings() {
        return currentOrderings;
    }

    /**
     * Returns the events of the current schedule that have already happened and also the threads in which they
     * happened.
     */
    public static Map<String, Thread> getCurrentHappenedEvents() {
        return currentHappenedEvents;
    }

    /**
     * Returns the threads that are currently waiting for events and the events they are waiting for.
     */
    public static Map<Thread, String> getCurrentWaitingThreads() {
        return currentWaitingThreads;
    }

    /**
     * Returns <code>true</code> if the given name is the name of the current schedule, <code>false</code> otherwise.
     */
    public static boolean isSchedule(String name) {
        return currentSchedule.equals(name);
    }

    /**
     * Sets the current schedule state. This method is called by {@link MSchudeuleRunner} for each test/schedule pair.
     *
     * @param name
     * @param orderings
     */
    public static void setSchedule(String name, Orderings orderings){

        /* Note the name of the schedule and the partial orders defined by it */
        currentSchedule = name;
        currentOrderings.clear();

        for (Ordering partialOrder : orderings.getOrderings()) {

            SimpleEvent afterEvent = partialOrder.getAfterEvent();
            String afterEventDesc = getEventDesc(afterEvent);
            Set<Event> beforeEvents = currentOrderings.get(afterEventDesc);
            if (beforeEvents == null) {

                beforeEvents = new HashSet<Event>();
                currentOrderings.put(afterEventDesc, beforeEvents);
            }
            beforeEvents.add(partialOrder.getBeforeEvent());
        }
        currentHappenedEvents.clear();
        currentWaitingThreads.clear();
    }

    /**
     * Helper method for constructing the description of a {@link SimpleEvent}.
     *
     * @param simpleEvent
     * @return description of simpleEvent
     */
    private static String getEventDesc(SimpleEvent simpleEvent) {
        String eventDesc = simpleEvent.getEventName().getName();
        Name eventThreadName = simpleEvent.getThreadName();
        if (eventThreadName.getName() != null) {
            eventDesc += AT + eventThreadName.getName();
        }
        return eventDesc;
    }

    /**
     * Clears the current schedule state.
     */
    public static void clearSchedule() {
        currentSchedule = CLEARED_SCHEDULE;
        currentOrderings.clear();
        currentHappenedEvents.clear();
        currentWaitingThreads.clear();
    }
}
