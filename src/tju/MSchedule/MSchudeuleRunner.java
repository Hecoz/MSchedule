package tju.MSchedule;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class MSchudeuleRunner extends BlockJUnit4ClassRunner{


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

        super.runChild(method, notifier);
    }

    /**
     *  Helper for collect Schedules(include Schedule and Schedules)
     *
     * @return
     */
    private Map<String,String> collectSchedules(){

        //save all the schedules
        Map<String,String> schedules = new HashMap<String,String>();
        //obtain schedules
        Schedules schdulesAnnotations = currentTestMethod.getAnnotation(Schedules.class);
        if(schdulesAnnotations != null){

            for(Schedule schedule : schdulesAnnotations.value()){

                //collectSchedule
            }
        }
        return schedules;
    }

    /**
     *  Helper for collect single schedule
     */
    private void collectSchedule(Schedule schedule,Map<String,String> schedules){


    }

}
