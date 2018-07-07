package examples;

import tju.MSchedule.Schedule;
import tju.MSchedule.Schedules;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class testMSchedule {

    public static void main(String[] args) {

        testAnnotation testObj = new testAnnotation();
        Class reflectClass = testObj.getClass();

        Method[] methods = reflectClass.getDeclaredMethods();

        for(Method method : methods){

            if(method.isAnnotationPresent(Schedule.class)){

                try {

                    System.out.println("InvokeMethod:" + method.getName());

                    method.setAccessible(true);
                    method.invoke(testObj,null);

                    Schedule annotation = method.getAnnotation(Schedule.class);

                    if(annotation != null){

                        System.out.println("Method Annotations:" + annotation.value());
                    }
                    System.out.println(" - - - - - - - - - - - - - - - - ");
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            if(method.isAnnotationPresent(Schedules.class)){

                try {

                    System.out.println("InvokeMethod:" + method.getName());

                    method.setAccessible(true);
                    method.invoke(testObj,null);

                    Schedules schedules = method.getAnnotation(Schedules.class);

                    if(schedules != null){

                        for (Schedule schedule : schedules.value()){

                            System.out.println("schedule:" + schedule.value());
                        }
                    }
                    System.out.println(" - - - - - - - - - - - - - - - - ");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }
    }
}
