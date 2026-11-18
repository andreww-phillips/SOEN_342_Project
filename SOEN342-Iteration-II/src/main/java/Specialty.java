public class Specialty {
    private final String specialtyId;
    private final String name;

    public Specialty(String specialtyId, String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Specialty name is required.");
        }
        this.specialtyId = specialtyId;
        this.name = name.trim();
    }

    public String getSpecialtyId() {
        return specialtyId;
    }

    public String getName() {
        return name;
    }
}
