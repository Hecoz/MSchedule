package tju.MSchedule.moudles;

import org.junit.Test;

import java.io.StringReader;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestParser {

    @Test
    public void testSimpleEvent() throws ParseException {
        ScheduleParser parser = new ScheduleParser(new StringReader("afterPut->get@getThread"));
        Orderings partialOrders = parser.Orderings();
        List<Ordering> pos = partialOrders.getOrderings();
        assertEquals(1, pos.size());

        Ordering partialOrder = pos.get(0);

        SimpleEvent beforeEvent = (SimpleEvent) partialOrder.getBeforeEvent();
        Name beforeEventName = beforeEvent.getEventName();
        assertEquals("afterPut", beforeEventName.getName());
        assertEquals(null, beforeEvent.getThreadName().getName());

        SimpleEvent afterEvent = partialOrder.getAfterEvent();
        Name afterEventName = afterEvent.getEventName();
        assertEquals("get", afterEventName.getName());
        assertEquals("getThread", afterEvent.getThreadName().getName());
    }

    @Test
    public void testBlockEvent() throws ParseException {
        ScheduleParser parser = new ScheduleParser(new StringReader("afterPut@putThread->get,[beforeget]@getThread->beforeput@putThread"));
        Orderings partialOrders = parser.Orderings();
        List<Ordering> pos = partialOrders.getOrderings();
        assertEquals(2, pos.size());

        Ordering partialOrder = pos.get(1);

        BlockEvent beforeEvent = (BlockEvent) partialOrder.getBeforeEvent();
        Name blockAfterEventName = beforeEvent.getBlockAfterEventName();
        assertEquals("beforeget", blockAfterEventName.getName());
        Name blockBeforeEventName = beforeEvent.getBlockBeforeEventName();
        assertEquals(null, blockBeforeEventName.getName());
        assertEquals("getThread", beforeEvent.getThreadName().getName());

        SimpleEvent afterEvent = partialOrder.getAfterEvent();
        Name afterEventName = (Name) afterEvent.getEventName();
        assertEquals("beforeput", afterEventName.getName());
        assertEquals("putThread", afterEvent.getThreadName().getName());
    }

}
