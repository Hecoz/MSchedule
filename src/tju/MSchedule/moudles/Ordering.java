package tju.MSchedule.moudles;

public class Ordering {

    private Event beforeEvent;
    private SimpleEvent afterEvent;

    public Ordering(Event beforeEvent, SimpleEvent afterEvent) {
        this.beforeEvent = beforeEvent;
        this.afterEvent = afterEvent;
    }

    public Event getBeforeEvent() {
        return beforeEvent;
    }

    public SimpleEvent getAfterEvent() {
        return afterEvent;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        Ordering objOrdering = (Ordering) obj;
        if (afterEvent == null) {
            if (objOrdering.afterEvent != null)
                return false;
        } else if (!afterEvent.equals(objOrdering.afterEvent))
            return false;
        if (beforeEvent == null) {
            if (objOrdering.beforeEvent != null)
                return false;
        } else if (!beforeEvent.equals(objOrdering.beforeEvent))
            return false;
        return true;
    }
}
