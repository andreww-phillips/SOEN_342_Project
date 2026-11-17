import java.time.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner kb = new Scanner(System.in);
        Map<String, Schedule> schedules = new LinkedHashMap<>();
        boolean running = true;

        while (running) {
            System.out.println("\n1. Add/select professional");
            System.out.println("2. Make availability");
            System.out.println("3. View all availability");
            System.out.println("0. Exit");
            System.out.print("Choice: ");
            String choice = kb.nextLine().trim();

            try {
                switch (choice) {
                    case "0":
                        running = false;
                        break;

                    case "1":
                        System.out.print("Professional ID: ");
                        String id = kb.nextLine().trim();
                        if (id.isEmpty()) throw new IllegalArgumentException("ID is required.");

                        if (schedules.containsKey(id)) {
                            System.out.println("Already available: " + schedules.get(id).getProfessional().getName());
                        } else {
                            System.out.print("Professional name: ");
                            String name = kb.nextLine().trim();
                            System.out.print("Profession/title: ");
                            String profession = kb.nextLine().trim();
                            if (name.isEmpty() || profession.isEmpty()) {
                                throw new IllegalArgumentException("Name and profession are required.");
                            }
                            schedules.put(id, new Schedule(new HealthcareProfessional(id, name, profession)));
                            System.out.println("Professional added in memory.");
                        }
                        break;

                    case "2":
                        if (schedules.isEmpty()) {
                            System.out.println("Add a professional first (CSV import will supply these later once the csv files are complete).");
                            break;
                        }
                        for (Schedule s : schedules.values()) {
                            System.out.println(s.getProfessional().getProfessionalId() + ": " + s.getProfessional().getName());
                        }
                        System.out.print("Select professional ID: ");
                        Schedule schedule = schedules.get(kb.nextLine().trim());
                        if (schedule == null) throw new IllegalArgumentException("Professional not found.");

                        System.out.print("From date (yyyy-MM-dd): ");
                        LocalDate from = LocalDate.parse(kb.nextLine().trim());
                        System.out.print("To date (yyyy-MM-dd): ");
                        LocalDate to = LocalDate.parse(kb.nextLine().trim());
                        System.out.print("Repeat WEEKLY or MONTHLY: ");
                        String repeat = kb.nextLine().trim().toUpperCase(Locale.ROOT);

                        Set<DayOfWeek> weekdays = EnumSet.noneOf(DayOfWeek.class);
                        if (repeat.equals("WEEKLY")) {
                            System.out.print("Days (e.g. MONDAY,WEDNESDAY): ");
                            for (String d : kb.nextLine().split(",")) {
                                weekdays.add(Enum.valueOf(DayOfWeek.class, d.trim().toUpperCase(Locale.ROOT)));
                            }
                        }
                        System.out.print("Start time (HH:mm): ");
                        LocalTime start = LocalTime.parse(kb.nextLine().trim());
                        System.out.print("End time (HH:mm): ");
                        LocalTime end = LocalTime.parse(kb.nextLine().trim());
                        int count = schedule.makeAvailability(from, to, repeat, weekdays, start, end);
                        System.out.println("Created " + count + " time slots for " + schedule.getProfessional().getName());
                        break;

                    case "3":
                        for (Schedule s : schedules.values()) {
                            System.out.println(s.getProfessional().getName() + ":");
                            for (TimeSlot slot : s.getTimeSlots()) {
                                System.out.println("  " + slot.getStart() + " to " + slot.getEnd());
                            }
                        }
                        break;

                    default:
                        System.out.println("Choose 0, 1, 2, or 3.");
                }
            } catch (DateTimeException e) {
                System.out.println("Invalid date or time. Please enter dates as yyyy-MM-dd and times as HH:mm.");
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
        }
        kb.close();
    }
}
