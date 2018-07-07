package tju.MSchedule;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import tju.MSchedule.moudles.*;

import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MSchudeuleRunner extends BlockJUnit4ClassRunner{

    /**
     * Constants
     */
    private static final String GROUP_NAME = "imunit-thread-group";
    private static final String MAIN_THREAD_NAME = "main";
    private static final String INVALID_SYNTAX_MESSAGE = "Ignoring schedule because of invalid syntax: name = %s value = %s .\nCaused by: %s";
    private static final String TERMINATION_DETECTION_THREAD_INTERRUPTED_MSG = "Termination detection thread was interrupted!";
    private static final String DEADLOCK_DETECTED_MSG = "Deadlock detected!%s\nFollowing threads were unable to make progress: %s";
    private static final String UNENCOUNTERED_AFTER_EVENTS_MSG = "The following events were not encountered: %s";
    private static final String COMMA_SEP = ", ";
    private static final String WAITING_THREAD_MSG = "\nThread named: %s, was waiting for event: %s";

    /**
     * Save current executing method notifier and schedules
     */

    private FrameworkMethod currentTestMethod;
    private RunNotifier currentTestNotifier;
    private String currentSchedule;


    public MSchudeuleRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {

        currentTestMethod = method;
        currentTestNotifier = notifier;
        Map<String,Orderings> schedules = collectSchedules();

        if(!schedules.isEmpty()){

            for(Map.Entry<String,Orderings> schedule : schedules.entrySet()){

                currentSchedule = schedule.getKey();
                MSchedule.setSchedule(currentSchedule,schedule.getValue());
                //System.out.println(currentSchedule);

                /*
                 * Create a separate thread group and thread within that group to run the imunit test
                 */
                ThreadGroup MScheduleGroup = new ThreadGroup(GROUP_NAME);
                Runnable MScheduleRunnable = new Runnable() {
                    @Override
                    public void run() {
                        MSchudeuleRunner.super.runChild(method, notifier);
                    }
                };

                Thread MScheduleThread = new Thread(MScheduleGroup, MScheduleRunnable, MAIN_THREAD_NAME);

                /* Start the MSchedule thread and the termination detection thread */
                MScheduleThread.start();

                Thread terminationDetectionThread = new Thread(new TerminationDetector(MScheduleGroup, MScheduleThread));
                terminationDetectionThread.setDaemon(true);
                terminationDetectionThread.start();

                /* Wait for the termination detection thread to terminate */
                try {
                    terminationDetectionThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(2);
                }
            }
            MSchedule.clearSchedule();
        }else{

            super.runChild(method, notifier);
        }
    }

    /**
     *  Helper for collect Schedules(include Schedule and Schedules)
     *
     * @return
     */
    private Map<String,Orderings> collectSchedules(){

        //save all the schedules
        Map<String,Orderings> schedules = new HashMap<String,Orderings>();
        //obtain schedules
        Schedules schdulesAnnotations = currentTestMethod.getAnnotation(Schedules.class);
        if(schdulesAnnotations != null){

            for(Schedule schedule : schdulesAnnotations.value()){

                //collectSchedule
                collectSchedule(schedule,schedules);
            }
        }

        Schedule schedule = currentTestMethod.getAnnotation(Schedule.class);
        if(schedule != null){

            collectSchedule(schedule,schedules);
        }
        return schedules;
    }

    /**
     *  Helper for collect single schedule
     */
    private void collectSchedule(Schedule schedule,Map<String,Orderings> schedules){

        String schName = schedule.name();
        schName = schName != null && schName.length() > 0 ? schName : schedule.value();

        try {
            schedules.put(schName, new ScheduleParser(new StringReader(schedule.value())).Orderings());

        } catch (ParseException e) {
            this.currentTestNotifier.fireTestFailure(new Failure(describeChild(this.currentTestMethod), new ScheduleError(schName, String.format(
                    INVALID_SYNTAX_MESSAGE, schName, schedule.value(), e))));
        } catch (TokenMgrError e) {
            this.currentTestNotifier.fireTestFailure(new Failure(describeChild(this.currentTestMethod), new ScheduleError(schName, String.format(
                    INVALID_SYNTAX_MESSAGE, schName, schedule.value(), e))));
        }

        //String[] Orderings = schName.split("->");
        //schedules.put(Orderings[0],Orderings[1]);
    }


    final class TerminationDetector implements Runnable{


        private final Thread main;
        private final ThreadGroup group;

        public TerminationDetector(ThreadGroup group, Thread main) {
            this.main = main;
            this.group = group;
        }

        @Override
        public void run() {

            /* Run while the main thread has not terminated */
            while (!main.getState().equals(Thread.State.TERMINATED)){


                /* Sleep for a while */
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    System.err.println(TERMINATION_DETECTION_THREAD_INTERRUPTED_MSG);
                    e.printStackTrace();
                }

                /* If all active threads are blocked/waiting its a deadlock */
                Thread[] activeThreads = new Thread[group.activeCount()];

                int numInArray = group.enumerate(activeThreads);
                if (numInArray == activeThreads.length && numInArray > 0) {
                    boolean deadlock = true;
                    for (int i = 0; i < activeThreads.length; i++) {

                        Thread.State activeState = activeThreads[i].getState();
                        if (activeState != Thread.State.BLOCKED && activeState != Thread.State.WAITING) {
                            deadlock = false;
                            break;
                        }
                    }
                    if (deadlock) {
                        String deadlockMessage = String.format(DEADLOCK_DETECTED_MSG, getWaitingThreadsList(MSchedule.getCurrentWaitingThreads()),
                                getThreadsList(activeThreads));
                        ScheduleError scheduleError = new ScheduleError(currentSchedule, deadlockMessage);
                        currentTestNotifier.fireTestFailure(new Failure(describeChild(currentTestMethod), scheduleError));
                        break;
                    }
                }


            }
            /* Terminated normally. Report a failure if any after-events were not encountered */
            Set<String> unEncouteredAfterEvents = MSchedule.getCurrentOrderings().keySet();
            unEncouteredAfterEvents.removeAll(MSchedule.getCurrentHappenedEvents().keySet());
            if(!unEncouteredAfterEvents.isEmpty()){

                String unencounteredEventsMessage = String.format(UNENCOUNTERED_AFTER_EVENTS_MSG, getEventsList(unEncouteredAfterEvents));
                ScheduleError scheduleError = new ScheduleError(currentSchedule, unencounteredEventsMessage);
                currentTestNotifier.fireTestFailure(new Failure(describeChild(currentTestMethod), scheduleError));
            }
        }

        private String getWaitingThreadsList(Map<Thread, String> waitingThreads) {
            StringBuffer list = new StringBuffer();
            for (Map.Entry<Thread, String> waitingThread : waitingThreads.entrySet()) {
                list.append(String.format(WAITING_THREAD_MSG, waitingThread.getKey().getName(), waitingThread.getValue()));
            }
            return list.toString();
        }

        private String getThreadsList(Thread[] threads) {
            StringBuffer list = new StringBuffer();
            for (Thread thread : threads) {
                list.append(thread.getName());
                list.append(COMMA_SEP);
            }
            return list.substring(0, list.lastIndexOf(COMMA_SEP));
        }

        private String getEventsList(Set<String> events) {

            StringBuffer list = new StringBuffer();
            for (String event : events) {
                list.append(event);
                list.append(COMMA_SEP);
            }
            return list.substring(0, list.lastIndexOf(COMMA_SEP));
        }
    }
}
