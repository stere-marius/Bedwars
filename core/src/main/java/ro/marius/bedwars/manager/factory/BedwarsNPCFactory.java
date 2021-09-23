package ro.marius.bedwars.manager.factory;

import ro.marius.bedwars.*;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.utils.ServerVersion;

public class BedwarsNPCFactory {

    public static NPCPlayer getNpc() {

        NPCPlayer npcPlayer = null;

        switch (ManagerHandler.getVersionManager().getServerVersion()) {
            case v1_16_R3:
                npcPlayer = new NPC_V_1_16_R3();
                break;
            case v1_16_R2:
                npcPlayer = new NPC_V_1_16_R2();
                break;
            case v1_16_R1:
                npcPlayer = new NPC_V_1_16_R1();
                break;
            case v1_15_R1:
                npcPlayer = new NPC_V_1_15_R1();
                break;
            case v1_14_R1:
                npcPlayer = new NPC_V_1_14_R1();
                break;
            case v1_13_R2:
                npcPlayer = new NPC_V_1_13_R2();
                break;
            case v1_12_R1:
                npcPlayer = new NPC_V_1_12_R1();
                break;
            case v1_11_R1:
                npcPlayer = new NPC_V_1_11_R1();
                break;
            case v1_10_R1:
                npcPlayer = new NPC_V_1_10_R1();
                break;
            case v1_9_R2:
                npcPlayer = new NPC_V1_9_R2();
                break;
            case v1_9_R1:
                npcPlayer = new NPC_V1_9_R1();
                break;
            case v1_8_R3:
                npcPlayer = new NPC_V1_8_R3();
                break;
        }

        return npcPlayer;
    }

}
