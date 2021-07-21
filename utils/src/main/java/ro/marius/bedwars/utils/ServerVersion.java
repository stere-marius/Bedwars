package ro.marius.bedwars.utils;

public enum ServerVersion {

    v1_7_10(0),
    v1_8_R3(1),
    v1_9_R1(2),
    v1_9_R2(3),
    v1_10_R1(4),
    v1_11_R1(5),
    v1_12_R1(6),
    v1_13_R2(7),
    v1_14_R1(8),
    v1_15_R1(9),
    v1_16_R1(10),
    v1_16_R2(11),
    v1_16_R3(12);

    private final int id;

    ServerVersion(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public boolean isGreaterOrEqualThan(ServerVersion serverVersion) {
        return getID() >= serverVersion.getID();
    }

    public boolean isGreaterThan(ServerVersion serverVersion) {
        return getID() > serverVersion.getID();
    }

}
