import java.math.BigDecimal;

public class MedicalService {
    String name;
    BigDecimal fee;
    Boolean requiredReferral; 

    public MedicalService(String name, BigDecimal fee, Boolean requiredReferral) {
        this.name = name;
        this.fee = fee;
        this.requiredReferral = requiredReferral;
    }

    public String getName() {
        return name;
    }
}
