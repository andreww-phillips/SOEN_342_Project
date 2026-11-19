import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Appointment {
    String appointmentId;
    String userId;
    String status;
    String serviceName;
    LocalDateTime requestedStart;
    LocalDateTime requestedEnd;
    LocalDateTime bookedAt;
    LocalDateTime arrivedAt;

    public Appointment(String appointmentId,String userId, String status, String serviceName, LocalDateTime requestedStart, LocalDateTime requestedEnd) {
        this.appointmentId = appointmentId;
        this.userId = userId;
        this.status = status;
        this.serviceName = serviceName;
        this.requestedStart = requestedStart;
        this.requestedEnd = requestedEnd;
        this.bookedAt = LocalDateTime.now();
        this.arrivedAt = null;
    }

}
