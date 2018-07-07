package tju.MSchedule.moudles;

public class BlockEvent implements Event {

    private Name blockAfterEventName;

    private Name blockBeforeEventName;

    private final Name threadName;

    public BlockEvent(Name blockAfterEventName, Name blockBeforeEventName, Name threadName) {
        this.blockAfterEventName = blockAfterEventName;
        this.blockBeforeEventName = blockBeforeEventName;
        this.threadName = threadName;
    }

    public Name getBlockAfterEventName() {
        return blockAfterEventName;
    }

    public Name getBlockBeforeEventName() {
        return blockBeforeEventName;
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
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;


        BlockEvent objBlockEvent = (BlockEvent) obj;
        if (blockAfterEventName == null) {
            if (objBlockEvent.blockAfterEventName != null)
                return false;
        } else if (!blockAfterEventName.equals(objBlockEvent.blockAfterEventName))
            return false;
        if (blockBeforeEventName == null) {
            if (objBlockEvent.blockBeforeEventName != null)
                return false;
        } else if (!blockBeforeEventName.equals(objBlockEvent.blockBeforeEventName))
            return false;
        if (threadName == null) {
            if (objBlockEvent.threadName != null)
                return false;
        } else if (!threadName.equals(objBlockEvent.threadName))
            return false;
        return true;
    }

}
