package ro.marius.bedwars;

public class NPCSkin {

    private final String value;
    private final String signature;

    public NPCSkin(String value, String signature) {
        this.value = value;
        this.signature = signature;
    }

    public String getValue() {
        return value;
    }

    public String getSignature() {
        return signature;
    }
}
