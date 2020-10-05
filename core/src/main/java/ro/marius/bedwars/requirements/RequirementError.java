package ro.marius.bedwars.requirements;

public class RequirementError extends Exception {

    private static final long serialVersionUID = 1L;

    public RequirementError(String description) {
        super(description);
    }

}
