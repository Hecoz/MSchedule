package tju.MSchedule.moudles;

import java.util.ArrayList;
import java.util.List;

public class Orderings {

    private List<Ordering> orderings;

    public Orderings() {

        orderings = new ArrayList<Ordering>();
    }

    public void add(Ordering ordering){

        orderings.add(ordering);
    }

    public List<Ordering> getOrderings() {
        return orderings;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((orderings == null) ? 0 : orderings.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        Orderings otherOrderings = (Orderings) obj;
        if (orderings == null) {
            if (otherOrderings.orderings != null)
                return false;
        }
        else if (!orderings.equals(otherOrderings.orderings))
            return false;
        return true;
    }
}
