import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.*;
import java.util.*;


// Imports availabilities from a CSV file and exports all availabilities to one.
// Columns must be in this fixed order: Name, Title, Specialty, Date, Start, End

public class CsvHandler {
    private static final String[] HEADER = {"Name", "Title", "Specialty", "Date", "Start", "End"};
    
    private record Row(HealthcareProfessional professional, TimeSlot slot) {}
    
    public static class ImportResult {
        public int rowsRead;
        public int slotsImported;
        public int duplicatesSkipped;
        public int professionalsAdded;
        public final List<String> errors = new ArrayList<>();
    }

    

    // -------------------------------------- import -------------------------------------- //    

    // Imports availabilities from a CSV file and returns an ImportResult object
    public static ImportResult importFrom(Path file, Map<String, Schedule> schedules) throws IOException {
        
        // Check for missing columns or empty file
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        if (lines.isEmpty()) throw new IllegalArgumentException("The file is empty.");
        if (!lines.get(0).trim().equalsIgnoreCase(String.join(",", HEADER))) {
            throw new IllegalArgumentException("Invalid columns in CSV header. Header must be: " + String.join(",", HEADER));
        }

        ImportResult result = new ImportResult();
        for (int i = 1; i < lines.size(); i++) { // skip header
            if (lines.get(i).isBlank()) continue;
            result.rowsRead++;
            try {
                importRow(lines.get(i).split(","), schedules, result);
            }
            catch (DateTimeException e) {
                result.errors.add("Line " + (i + 1) + ": invalid date or time (expected yyyy-MM-dd and HH:mm).");
            } 
            catch (IllegalArgumentException e) {
                result.errors.add("Line " + (i + 1) + ": " + e.getMessage());
            }
        }
        return result;
    }

    private static void importRow(String[] fields, Map<String, Schedule> schedules, ImportResult result) {
        if (fields.length != HEADER.length) {
            throw new IllegalArgumentException("expected " + HEADER.length + " columns but found " + fields.length + ".");
        }
        String name = fields[0].trim();
        String title = fields[1].trim();
        String specialties = fields[2].trim();
        if (name.isEmpty() || title.isEmpty()) {
            throw new IllegalArgumentException("name and title are required.");
        }
        LocalDate date = LocalDate.parse(fields[3].trim());
        LocalTime start = LocalTime.parse(fields[4].trim());
        LocalTime end = LocalTime.parse(fields[5].trim());

        Schedule schedule = findSchedule(schedules, name, title);
        boolean isNew = schedule == null;

        // If the professional is new, create a new schedule and ID
        if (isNew) {
            schedule = new Schedule(new HealthcareProfessional(nextProfessionalId(schedules), name, title));
        }
        if (schedule.hasTimeSlot(LocalDateTime.of(date, start), LocalDateTime.of(date, end))) {
            result.duplicatesSkipped++;
            return;
        }

        // Validate and add the new time slot to the professional's schedule
        result.slotsImported += schedule.makeAvailability(date, date, "MONTHLY", Set.of(), start, end);
        if (isNew) {
            schedules.put(schedule.getProfessional().getProfessionalId(), schedule);
            result.professionalsAdded++;
        }

        // Several specialties can share one cell, separated by ';'.
        for (String specialty : specialties.split(";")) {
            if (!specialty.isBlank()) {
                schedule.getProfessional().addSpecialty(findOrCreateSpecialty(schedules, specialty.trim()));
            }
        }
    }

    // Get a professional's current schedule
    private static Schedule findSchedule(Map<String, Schedule> schedules, String name, String profession) {
        for (Schedule s : schedules.values()) {
            HealthcareProfessional p = s.getProfessional();
            if (p.getName().equalsIgnoreCase(name) && p.getProfession().equalsIgnoreCase(profession)) return s;
        }
        return null;
    }
    
    // Generate a new professional ID not in use (e.g., HP-001, HP-002, etc.)
    private static String nextProfessionalId(Map<String, Schedule> schedules) {
        int n = schedules.size() + 1;
        while (schedules.containsKey(String.format("HP-%03d", n))) n++;
        return String.format("HP-%03d", n);
    }

    // Reuses a specialty if one is already in the system
    private static Specialty findOrCreateSpecialty(Map<String, Schedule> schedules, String name) {
        Set<Specialty> all = new LinkedHashSet<>();
        for (Schedule s : schedules.values()) all.addAll(s.getProfessional().getSpecialties());
        for (Specialty specialty : all) {
            if (specialty.getName().equalsIgnoreCase(name)) return specialty;
        }
        return new Specialty(String.format("SP-%03d", all.size() + 1), name);
    }
    
    // -------------------------------------- export -------------------------------------- //

    // Exports all schedules to a CSV file and returns the number of time slots exported
    public static int exportTo(Path file, Map<String, Schedule> schedules) throws IOException {
        List<Row> rows = new ArrayList<>();
        for (Schedule s : schedules.values()) {
            for (TimeSlot slot : s.getTimeSlots()) rows.add(new Row(s.getProfessional(), slot));
        }
        rows.sort(Comparator.comparing((Row r) -> r.slot().getStart())
                .thenComparing(r -> r.professional().getName()));

        List<String> lines = new ArrayList<>();
        lines.add(String.join(",", HEADER));
        for (Row r : rows) {
            HealthcareProfessional p = r.professional();
            List<String> specialties = new ArrayList<>();
            for (Specialty specialty : p.getSpecialties()) specialties.add(specialty.getName());

            lines.add(String.join(",", p.getName(), p.getProfession(), String.join(";", specialties),
                    r.slot().getStart().toLocalDate().toString(),
                    r.slot().getStart().toLocalTime().toString(),
                    r.slot().getEnd().toLocalTime().toString()));
        }
        Files.write(file, lines, StandardCharsets.UTF_8);
        return rows.size();
    }
}
