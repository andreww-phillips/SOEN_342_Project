import java.time.LocalDateTime;

public class TimeSlot {
    private final LocalDateTime start;
    private final LocalDateTime end;
    boolean isAvailable = true;

    public TimeSlot(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !start.isBefore(end)) {
            throw new IllegalArgumentException("the start time must be before the end time.");
        }
        this.start = start;
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setIsAvailable(boolean available) {
        isAvailable = available;
    }
}
