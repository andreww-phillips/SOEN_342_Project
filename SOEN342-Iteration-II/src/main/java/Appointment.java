import java.time.LocalDateTime;

public class Appointment {
    String appointmentId;
    String userId;
    String status;
    String serviceName;
    LocalDateTime requestedStart;
    LocalDateTime requestedEnd;
    LocalDateTime bookedAt;
    LocalDateTime arrivedAt;
    private boolean scheduleUpdated;

    public Appointment(String appointmentId,String userId, String status, String serviceName, LocalDateTime requestedStart, LocalDateTime requestedEnd) {
        this.appointmentId = appointmentId;
        this.userId = userId;
        this.status = status;
        this.serviceName = serviceName;
        this.requestedStart = requestedStart;
        this.requestedEnd = requestedEnd;
        this.bookedAt = null;
        this.arrivedAt = null;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    void setBookedAt(LocalDateTime bookedAt) {
        this.bookedAt = bookedAt;
    }

    boolean isScheduleUpdated() {
        return scheduleUpdated;
    }

    void markScheduleUpdated() {
        this.scheduleUpdated = true;
    }

}
