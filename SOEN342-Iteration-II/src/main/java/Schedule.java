import java.time.*;
import java.util.*;

public class Schedule {
    private final HealthcareProfessional professional;
    private final ArrayList<TimeSlot> timeSlots = new ArrayList<>();
    private List <Appointment> appointments = new ArrayList<>();

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
            if (slot.getStart().equals(start) && slot.getEnd().equals(end)) {
                slot.setIsAvailable(false);
                return;
            }
        }
        throw new IllegalArgumentException("Time slot not found.");
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
    public boolean verifySchedule(String appointmentId, LocalDateTime start, LocalDateTime end) {
        for (TimeSlot slot : timeSlots) {
            if (slot.isAvailable && slot.getStart().equals(start) && slot.getEnd().equals(end)) {
                return true;
            }
        }
        return false;
    }

     public Appointment bookAppointment(String appointmentId, String userId, String status, String serviceName, LocalDateTime requestedStart, LocalDateTime requestedEnd) {
        Appointment appointment = new Appointment(appointmentId, userId, status, serviceName, requestedStart, requestedEnd);
        appointments.add(appointment);
        return appointment;
    }
}
