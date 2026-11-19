import java.time.*;
import java.util.*;

public class Schedule {
    private final HealthcareProfessional professional;
    private final ArrayList<TimeSlot> timeSlots = new ArrayList<>();
    private final List<Appointment> appointments = new ArrayList<>();

    public Schedule(HealthcareProfessional professional) {
        this.professional = professional;
    }

    public HealthcareProfessional getProfessional() {
        return professional;
    }

    public List<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public void addTimeSlot(TimeSlot slot) {
        timeSlots.add(slot);
    }

    public boolean hasTimeSlot(LocalDateTime start, LocalDateTime end) {
        for (TimeSlot slot : timeSlots) {
            if (slot.getStart().equals(start) && slot.getEnd().equals(end)) return true;
        }
        return false;
    }

    public void selectTimeSlot(LocalDateTime start, LocalDateTime end) {
        for (TimeSlot slot : timeSlots) {
            if (slot.isAvailable && slot.getStart().equals(start) && slot.getEnd().equals(end)) {
                slot.setIsAvailable(false);
                return;
            }
        }
        throw new IllegalArgumentException("Time slot is not available.");
    }

    public int makeAvailability(LocalDate from, LocalDate to, String repetitionPattern, Set<DayOfWeek> weekdays, LocalTime start, LocalTime end) {
        if (from == null || to == null || to.isBefore(from) || start == null || end == null || !start.isBefore(end)) {
            throw new IllegalArgumentException("Invalid date range or times.");
        }
        if (!repetitionPattern.equals("WEEKLY") && !repetitionPattern.equals("MONTHLY")) {
            throw new IllegalArgumentException("Repeat must be WEEKLY or MONTHLY.");
        }
        if (repetitionPattern.equals("WEEKLY") && weekdays.isEmpty()) {
            throw new IllegalArgumentException("Choose at least one weekday.");
        }

        int count = 0;
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            boolean matches = repetitionPattern.equals("WEEKLY")
                    ? weekdays.contains(date.getDayOfWeek())
                    : date.getDayOfMonth() == from.getDayOfMonth();

            if (matches) {
                addTimeSlot(new TimeSlot(LocalDateTime.of(date, start), LocalDateTime.of(date, end)));
                count++;
            }
        }
        return count;
    }
    public boolean verifySchedule(LocalDateTime start, LocalDateTime end) {
        for (TimeSlot slot : timeSlots) {
            if (start != null && end != null && slot.isAvailable
                    && slot.getStart().equals(start) && slot.getEnd().equals(end)) {
                return true;
            }
        }
        return false;
    }

    public Appointment requestAppointment(String appointmentId, String userId, String serviceName,
            LocalDateTime requestedStart, LocalDateTime requestedEnd) {
        if (appointmentId == null || appointmentId.trim().isEmpty()
                || userId == null || userId.trim().isEmpty()
                || serviceName == null || serviceName.trim().isEmpty()) {
            throw new IllegalArgumentException("Appointment ID, user ID, and service name are required.");
        }
        if (requestedStart == null || requestedEnd == null || !requestedStart.isBefore(requestedEnd)) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }
        if (findAppointment(appointmentId) != null) {
            throw new IllegalArgumentException("Appointment ID already exists.");
        }
        if (!verifySchedule(requestedStart, requestedEnd)) {
            throw new IllegalArgumentException("Requested time slot is not available.");
        }

        Appointment appointment = new Appointment(
                appointmentId, userId, "requested", serviceName, requestedStart, requestedEnd);
        appointments.add(appointment);
        return appointment;
    }

    public void updateSchedule(String appointmentId, LocalDateTime newStart, LocalDateTime newEnd) {
        Appointment appointment = findAppointment(appointmentId);
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment not found.");
        }
        if (!"requested".equals(appointment.status) || appointment.isScheduleUpdated()) {
            throw new IllegalArgumentException("Only a pending appointment can have its schedule updated.");
        }
        if (!verifySchedule(newStart, newEnd)) {
            throw new IllegalArgumentException("New time slot is not available.");
        }

        selectTimeSlot(newStart, newEnd);
        appointment.requestedStart = newStart;
        appointment.requestedEnd = newEnd;
        appointment.markScheduleUpdated();
    }

    public Appointment bookAppointment(String appointmentId, String userId, String status, String serviceName,
            LocalDateTime requestedStart, LocalDateTime requestedEnd) {
        Appointment appointment = findAppointment(appointmentId);
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment not found.");
        }
        if (!Objects.equals(appointment.userId, userId)) {
            throw new IllegalArgumentException("Appointment does not belong to this user.");
        }
        if (!"requested".equals(status) || !Objects.equals(appointment.status, status)) {
            throw new IllegalArgumentException("Only requested appointments can be confirmed.");
        }
        if (!appointment.isScheduleUpdated()) {
            throw new IllegalArgumentException("Update the appointment schedule before booking.");
        }
        if (!Objects.equals(appointment.serviceName, serviceName)
                || !Objects.equals(appointment.requestedStart, requestedStart)
                || !Objects.equals(appointment.requestedEnd, requestedEnd)) {
            throw new IllegalArgumentException("Booking details do not match the appointment request.");
        }

        appointment.setStatus("confirmed");
        appointment.setBookedAt(LocalDateTime.now());
        return appointment;
    }

    private Appointment findAppointment(String appointmentId) {
        for (Appointment appointment : appointments) {
            if (Objects.equals(appointment.appointmentId, appointmentId)) {
                return appointment;
            }
        }
        return null;
    }
}
