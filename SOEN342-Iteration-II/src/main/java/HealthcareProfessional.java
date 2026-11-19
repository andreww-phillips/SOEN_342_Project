import java.util.*;

public class HealthcareProfessional extends User {
    private final String professionalId;
    private final String name;
    private final String profession;
    private final List<Specialty> specialties = new ArrayList<>();

    public HealthcareProfessional(String id, String name, String profession) {
        super(id); 
        this.professionalId = id;
        this.name = name;
        this.profession = profession;
    }

    public String getProfessionalId() {
        return professionalId;
    }

    public String getName() {
        return name;
    }

    public String getProfession() {
        return profession;
    }

    public List<Specialty> getSpecialties() {
        return specialties;
    }

    public void addSpecialty(Specialty specialty) {
        if (!specialties.contains(specialty)) {
            specialties.add(specialty);
        }
    }
}
