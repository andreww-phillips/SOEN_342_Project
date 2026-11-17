public class HealthcareProfessional extends User {
    private final String professionalId;
    private final String name;
    private final String profession;

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
}
