import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Appointment {
    String appointmentId;
    String status;
    LocalDateTime requestedStart;
    LocalDateTime requestedEnd;
    LocalDateTime bookedAt;
    LocalDateTime arrivedAt;

    List <Appointment> appointments = new ArrayList<>();

    public Appointment(String appointmentId, String status, LocalDateTime requestedStart, LocalDateTime requestedEnd, LocalDateTime bookedAt) {
        this.appointmentId = appointmentId;
        this.status = status;
        this.requestedStart = requestedStart;
        this.requestedEnd = requestedEnd;
        this.bookedAt = bookedAt;
        this.arrivedAt = null;
    }

    public Appointment bookAppointment(String appointmentId, String userId, String status, String serviceName, LocalDateTime requestedStart, LocalDateTime requestedEnd) {
        Appointment appointment = new Appointment(appointmentId, status, requestedStart, requestedEnd, bookedAt);
        appointments.add(appointment);
        return appointment;
    }

}
