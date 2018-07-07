package tju.MSchedule;

/**
 * {@link Error} used to report problems encountered during parsing or enforcement of {@link Schedule}s.
 * 
 * @author Vilas Jagannath (vbangal2@illinois.edu)
 * 
 */
public class ScheduleError extends Error {

    private static final long serialVersionUID = 1L;

    private static final String EXCEPTION_MSG = "Problem with schedule: %s !\n%s";

    public ScheduleError(String scheduleName, String message, Throwable cause) {
        super(String.format(EXCEPTION_MSG, scheduleName, message), cause);
    }

    public ScheduleError(String scheduleName, String message) {
        this(scheduleName, message, null);
    }

}
