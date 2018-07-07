package tju.MSchedule;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import tju.MSchedule.moudles.Orderings;
import tju.MSchedule.moudles.ParseException;
import tju.MSchedule.moudles.ScheduleParser;
import tju.MSchedule.moudles.TokenMgrError;

import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class MSchudeuleRunner extends BlockJUnit4ClassRunner{


    private static final String INVALID_SYNTAX_MESSAGE = "Ignoring schedule because of invalid syntax: name = %s value = %s .\nCaused by: %s";

    /**
     * Save current executing method notifier and schedules
     */

    private FrameworkMethod currentTestMethod;
    private RunNotifier currentTestNotifier;
    private String currentSchedules;


    public MSchudeuleRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {

        currentTestMethod = method;
        currentTestNotifier = notifier;
        Map<String,Orderings> schedules = collectSchedules();

        if(!schedules.isEmpty()){



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

}
