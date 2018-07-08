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
        final int prime = 31;
        int result = 1;
        result = prime * result + ((eventName == null) ? 0 : eventName.hashCode());
        result = prime * result + ((threadName == null) ? 0 : threadName.hashCode());
        return result;
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
