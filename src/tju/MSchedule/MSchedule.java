package tju.MSchedule;

import org.junit.Assert;
import org.junit.internal.ArrayComparisonFailure;
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
    private static final String BLOCKING_EVENT_THREAD_DID_NOT_BLOCK_MSG = "Thread named: %s, executing blocking event: [%s], completed execution without blocking";


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
     * Helper method for constructing the description of a {@link BlockEvent}.
     *
     * @param blockEvent
     * @return description of blockEvent
     */
    private static String getEventDesc(BlockEvent blockEvent) {
        String eventDesc = blockEvent.getBlockAfterEventName().getName();
        Name eventThreadName = blockEvent.getThreadName();
        if (eventThreadName.getName() != null) {
            eventDesc += AT + eventThreadName.getName();
        }
        return eventDesc;
    }


    /**
     * Method called to notify the occurrence of an event. This method contains the logic for enforcing the current
     * schedule. If the fired event needs to occur after some events, the current thread is blocked until those events
     * have occurred.
     *
     * @param eventName
     */
    public static void fireEvent(String eventName){

        if(!currentOrderings.isEmpty()){

            //collect the event(s) this event needs to wait for
            Set<Event> beforeEvents = new HashSet<Event>();
            if(currentOrderings.containsKey(eventName)){
                beforeEvents.addAll(currentOrderings.get(eventName));
            }
            Thread currentThread = Thread.currentThread();
            String qualifiedName = eventName + AT + currentThread.getName();
            if (currentOrderings.containsKey(qualifiedName)) {
                beforeEvents.addAll(currentOrderings.get(qualifiedName));
            }

            //wait for collected events to be finished
            if(!beforeEvents.isEmpty()){

                for(Event beforeEvent : beforeEvents){

                    if(beforeEvent instanceof SimpleEvent){

                        SimpleEvent simpleEvent = (SimpleEvent)beforeEvent;
                        String simpleEventDesc = getEventDesc(simpleEvent);

                        synchronized (lock){

                            currentWaitingThreads.put(currentThread,simpleEventDesc);
                            while (!currentHappenedEvents.containsKey(simpleEventDesc)) {
                                try {
                                    lock.wait();
                                } catch (InterruptedException e) {
                                    System.err.println("Interrupted while enforcing schedule!");
                                    e.printStackTrace();
                                    System.exit(2);
                                }
                            }

                            currentWaitingThreads.remove(currentThread);
                        }
                    }else if(beforeEvent instanceof BlockEvent){

                        BlockEvent blockEvent = (BlockEvent) beforeEvent;
                        String blockEventDesc = getEventDesc(blockEvent);
                        synchronized (lock) {
                            currentWaitingThreads.put(currentThread, blockEventDesc);
                            while (!(currentHappenedEvents.containsKey(blockEventDesc))) {
                                try {
                                    lock.wait();
                                } catch (InterruptedException e) {
                                    System.err.println("Interrupted while enforcing schedule!");
                                    e.printStackTrace();
                                    System.exit(2);
                                }
                            }
                            Thread blockEventThread = currentHappenedEvents.get(blockEventDesc);
                            while (!isBlocked(blockEventThread)) {
                                if (!blockEventThread.isAlive()) {
                                    throw new ScheduleError(currentSchedule, String.format(BLOCKING_EVENT_THREAD_DID_NOT_BLOCK_MSG,
                                            blockEventThread.getName(), blockEventDesc));
                                }
                                Thread.yield();
                            }
                            currentWaitingThreads.remove(currentThread);
                        }
                    }
                }


            }

            synchronized (lock) {
                /* This event has now happened */
                currentHappenedEvents.put(eventName, currentThread);
                currentHappenedEvents.put(qualifiedName, currentThread);
                /* Notify other threads that might be waiting for this event to happen */
                lock.notifyAll();
            }
        }
    }

    /**
     * Helper method for checking whether the given {@link Thread} is blocked.
     *
     * @param thread
     * @return <code>true</code> if thread is blocked and <code>false</code> otherwise.
     */
    private static boolean isBlocked(Thread thread) {
        Thread.State state = thread.getState();
        return state.equals(Thread.State.BLOCKED) || state.equals(Thread.State.WAITING) || state.equals(Thread.State.TIMED_WAITING);
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

//
    // ASSERTS
    //

    static public void schAssertTrue(String scheduleName, String message, boolean condition) {
        if (currentSchedule.equals(scheduleName)) {
            assertTrue(message, condition);
        }
    }

    static public void schAssertTrue(String scheduleName, boolean condition) {
        schAssertTrue(scheduleName, null, condition);
    }

    static public void schAssertFalse(String scheduleName, String message, boolean condition) {
        schAssertTrue(scheduleName, message, !condition);
    }

    static public void schAssertFalse(String scheduleName, boolean condition) {
        schAssertFalse(scheduleName, null, condition);
    }

    static public void failSch(String scheduleName, String message) {
        if (currentSchedule.equals(scheduleName)) {
            fail(message);
        }
    }

    static public void failSch(String scheduleName) {
        failSch(scheduleName, null);
    }

    static public void schAssertEquals(String scheduleName, String message, Object expected, Object actual) {
        if (currentSchedule.equals(scheduleName)) {
            assertEquals(message, expected, actual);
        }
    }

    static public void schAssertEquals(String scheduleName, Object expected, Object actual) {
        schAssertEquals(scheduleName, null, expected, actual);
    }

    public static void schAssertArrayEquals(String scheduleName, String message, Object[] expecteds, Object[] actuals) throws ArrayComparisonFailure {
        if (currentSchedule.equals(scheduleName)) {
            assertArrayEquals(message, expecteds, actuals);
        }
    }

    public static void schAssertArrayEquals(String scheduleName, Object[] expecteds, Object[] actuals) {
        schAssertArrayEquals(scheduleName, null, expecteds, actuals);
    }

    public static void schAssertArrayEquals(String scheduleName, String message, byte[] expecteds, byte[] actuals) throws ArrayComparisonFailure {
        if (currentSchedule.equals(scheduleName)) {
            assertArrayEquals(message, expecteds, actuals);
        }
    }

    public static void schAssertArrayEquals(String scheduleName, byte[] expecteds, byte[] actuals) {
        schAssertArrayEquals(scheduleName, null, expecteds, actuals);
    }

    public static void schAssertArrayEquals(String scheduleName, String message, char[] expecteds, char[] actuals) throws ArrayComparisonFailure {
        if (currentSchedule.equals(scheduleName)) {
            assertArrayEquals(message, expecteds, actuals);
        }
    }

    public static void schAssertArrayEquals(String scheduleName, char[] expecteds, char[] actuals) {
        schAssertArrayEquals(scheduleName, null, expecteds, actuals);
    }

    public static void schAssertArrayEquals(String scheduleName, String message, short[] expecteds, short[] actuals) throws ArrayComparisonFailure {
        if (currentSchedule.equals(scheduleName)) {
            assertArrayEquals(message, expecteds, actuals);
        }
    }

    public static void schAssertArrayEquals(String scheduleName, short[] expecteds, short[] actuals) {
        schAssertArrayEquals(scheduleName, null, expecteds, actuals);
    }

    public static void schAssertArrayEquals(String scheduleName, String message, int[] expecteds, int[] actuals) throws ArrayComparisonFailure {
        if (currentSchedule.equals(scheduleName)) {
            assertArrayEquals(message, expecteds, actuals);
        }
    }

    public static void schAssertArrayEquals(String scheduleName, int[] expecteds, int[] actuals) {
        schAssertArrayEquals(scheduleName, null, expecteds, actuals);
    }

    public static void schAssertArrayEquals(String scheduleName, String message, long[] expecteds, long[] actuals) throws ArrayComparisonFailure {
        if (currentSchedule.equals(scheduleName)) {
            assertArrayEquals(message, expecteds, actuals);
        }
    }

    public static void schAssertArrayEquals(String scheduleName, long[] expecteds, long[] actuals) {
        schAssertArrayEquals(scheduleName, null, expecteds, actuals);
    }

    public static void schAssertArrayEquals(String scheduleName, String message, double[] expecteds, double[] actuals, double delta)
            throws ArrayComparisonFailure {
        if (currentSchedule.equals(scheduleName)) {
            assertArrayEquals(message, expecteds, actuals, delta);
        }
    }

    public static void schAssertArrayEquals(String scheduleName, double[] expecteds, double[] actuals, double delta) {
        schAssertArrayEquals(scheduleName, null, expecteds, actuals, delta);
    }

    public static void schAssertArrayEquals(String scheduleName, String message, float[] expecteds, float[] actuals, float delta)
            throws ArrayComparisonFailure {
        if (currentSchedule.equals(scheduleName)) {
            assertArrayEquals(message, expecteds, actuals, delta);
        }

    }

    public static void schAssertArrayEquals(String scheduleName, float[] expecteds, float[] actuals, float delta) {
        schAssertArrayEquals(scheduleName, null, expecteds, actuals, delta);
    }

    static public void schAssertEquals(String scheduleName, String message, double expected, double actual, double delta) {
        if (currentSchedule.equals(scheduleName)) {
            assertEquals(message, expected, actual, delta);
        }
    }

    static public void schAssertEquals(String scheduleName, long expected, long actual) {
        schAssertEquals(scheduleName, null, expected, actual);
    }

    static public void schAssertEquals(String scheduleName, String message, long expected, long actual) {
        schAssertEquals(scheduleName, message, (Long) expected, (Long) actual);
    }

    static public void schAssertEquals(String scheduleName, double expected, double actual, double delta) {
        schAssertEquals(scheduleName, null, expected, actual, delta);
    }

    static public void schAssertNotNull(String scheduleName, String message, Object object) {
        schAssertTrue(scheduleName, message, object != null);
    }

    static public void assertNotNull(String scheduleName, Object object) {
        schAssertNotNull(scheduleName, null, object);
    }

    static public void schAssertNull(String scheduleName, String message, Object object) {
        schAssertTrue(scheduleName, message, object == null);
    }

    static public void schAssertNull(String scheduleName, Object object) {
        schAssertNull(scheduleName, null, object);
    }

    static public void schAssertSame(String scheduleName, String message, Object expected, Object actual) {
        if (currentSchedule.equals(scheduleName)) {
            assertSame(message, expected, actual);
        }
    }

    static public void schAssertSame(String scheduleName, Object expected, Object actual) {
        schAssertSame(scheduleName, null, expected, actual);
    }

    static public void schAssertNotSame(String scheduleName, String message, Object unexpected, Object actual) {
        if (currentSchedule.equals(scheduleName)) {
            assertNotSame(message, unexpected, actual);
        }
    }

    static public void schAssertNotSame(String scheduleName, Object unexpected, Object actual) {
        schAssertNotSame(scheduleName, null, unexpected, actual);
    }
}
