package tju.MSchedule.moudles;

public class SimpleEvent implements Event{

    private Name eventName;

    private Name threadName;

    public SimpleEvent(Name eventName, Name threadName) {
        this.eventName = eventName;
        this.threadName = threadName;
    }

    public Name getEventName() {
        return eventName;
    }

    public Name getThreadName() {
        return threadName;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if(this == obj)
            return true;
        if(obj == null || getClass() != obj.getClass())
            return false;

        SimpleEvent objSimpleEvent = (SimpleEvent)obj;

        if(eventName == null){
            if(objSimpleEvent.eventName != null)
                return false;
        }else if(!eventName.equals(objSimpleEvent.eventName)){
            return false;
        }

        if(threadName == null){
            if(objSimpleEvent.threadName != null)
                return false;
        }else if(!threadName.equals(objSimpleEvent.threadName))
            return false;

        return super.equals(obj);
    }
}
