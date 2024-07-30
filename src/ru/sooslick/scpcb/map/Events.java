package ru.sooslick.scpcb.map;

import java.util.LinkedHashSet;
import java.util.Set;

import static ru.sooslick.scpcb.BlitzRandom.bbRand;
import static ru.sooslick.scpcb.BlitzRandom.bbRnd;

public class Events {

    private final Set<ScpcbRoom> rooms;
    private final boolean keterMode;

    private final LinkedHashSet<ScpcbEvent> savedEvents = new LinkedHashSet<>();

    public Events(Set<ScpcbRoom> rooms, boolean keterMode) {
        this.rooms = rooms;
        this.keterMode = keterMode;
    }

    LinkedHashSet<ScpcbEvent> createEvents() {
        createEvent("173", "173", 0);
        createEvent("alarm", "start", 0);
        createEvent("pocketdimension", "pocketdimension", 0);
        createEvent("tunnel106", "tunnel", 0, 0.07f, 0.1f);

        // the chance for 173 appearing in the first lockroom is about 66%
        // there's a 30% chance that it appears in the later lockrooms
        if (bbRand(1, 3) < 3)
            createEvent("lockroom173", "lockroom", 0);
        createEvent("lockroom173", "lockroom", 0, 0.3f, 0.5f);

        createEvent("room2trick", "room2", 0, 0.15f);
        createEvent("1048a", "room2", 0, 1.0f);
        createEvent("room2storage", "room2storage", 0);
        createEvent("lockroom096", "lockroom2", 0);
        createEvent("endroom106", "endroom", bbRand(0, 1));
        createEvent("room2poffices2", "room2poffices2", 0);
        createEvent("room2fan", "room2_2", 0, 1.0f);
        createEvent("room2elevator2", "room2elevator", 0);
        createEvent("room2elevator", "room2elevator", bbRand(1, 2));
        createEvent("room3storage", "room3storage", 0, 0);
        createEvent("tunnel2smoke", "tunnel2", 0, 0.2f);

        createEvent("tunnel2", "tunnel2", bbRand(0, 2), 0);
        createEvent("tunnel2", "tunnel2", 0, 0, 0.2f);      // todo douuble check

        // 173 appears in half of the "room2doors" -rooms
        createEvent("room2doors173", "room2doors", 0, 0.5f, 0.4f);

        createEvent("room2offices2", "room2offices2", 0, 0.7f);
        createEvent("room2closets", "room2closets", 0);
        createEvent("room2cafeteria", "room2cafeteria", 0);
        createEvent("room3pitduck", "room3pit", 0);
        createEvent("room3pit1048", "room3pit", 1);
        createEvent("room2offices3", "room2offices3", 0, 1.0f);
        createEvent("room2servers", "room2servers", 0);
        createEvent("room3servers", "room3servers", 0);
        createEvent("room3servers", "room3servers2", 0);
        createEvent("room3tunnel", "room3tunnel", 0, 0.08f);
        createEvent("room4", "room4", 0);

        if (bbRand(1, 5) < 5) {
            switch (bbRand(1, 3)) {
                case 1:
                    createEvent("682roar", "tunnel", bbRand(0, 2), 0);
                    break;
                case 2:
                    createEvent("682roar", "room3pit", bbRand(0, 2), 0);
                    break;
                case 3:
                    createEvent("682roar", "room2z3", 0, 0);
                    break;
            }
        }

        createEvent("testroom173", "room2testroom2", 0, 1.0f);
        createEvent("room2tesla", "room2tesla", 0, 0.9f);
        createEvent("room2nuke", "room2nuke", 0, 0);

        if (bbRand(1, 5) < 5)
            createEvent("coffin106", "coffin", 0, 0);
        else
            createEvent("coffin", "coffin", 0, 0);

        createEvent("checkpoint", "checkpoint1", 0, 1.0f);
        createEvent("checkpoint", "checkpoint2", 0, 1.0f);

        createEvent("room3door", "room3", 0, 0.1f);
        createEvent("room3door", "room3tunnel", 0, 0.1f);

        if (bbRand(1, 2) == 1) {
            createEvent("106victim", "room3", bbRand(1, 2));
            createEvent("106sinkhole", "room3_2", bbRand(2, 3));
        } else {
            createEvent("106victim", "room3_2", bbRand(1, 2));
            createEvent("106sinkhole", "room3", bbRand(2, 3));
        }
        createEvent("106sinkhole", "room4", bbRand(1, 2));

        createEvent("room079", "room079", 0, 0);
        createEvent("room049", "room049", 0, 0);
        createEvent("room012", "room012", 0, 0);
        createEvent("room035", "room035", 0, 0);
        createEvent("008", "008", 0, 0);
        createEvent("room106", "room106", 0, 0);
        createEvent("pj", "roompj", 0, 0);
        createEvent("914", "914", 0, 0);

        createEvent("buttghost", "room2toilets", 0, 0);
        createEvent("toiletguard", "room2toilets", 1, 0);

        createEvent("room2pipes106", "room2pipes", bbRand(0, 3));
        createEvent("room2pit", "room2pit", 0, 0.4f, 0.4f);
        createEvent("testroom", "testroom", 0);
        createEvent("room2tunnel", "room2tunnel", 0);
        createEvent("room2ccont", "room2ccont", 0);

        createEvent("gateaentrance", "gateaentrance", 0);
        //createEvent("gatea", "gatea", 0);
        createEvent("exit1", "exit1", 0);

        createEvent("room205", "room205", 0);
        createEvent("room860", "room860", 0);
        createEvent("room966", "room966", 0);
        createEvent("room1123", "room1123", 0, 0);

        createEvent("room2tesla", "room2tesla_lcz", 0, 0.9f);
        createEvent("room2tesla", "room2tesla_hcz", 0, 0.9f);

        // New Events in SCP:CB Version 1.3 - ENDSHN
        createEvent("room4tunnels", "room4tunnels", 0);
        createEvent("room_gw", "room2gw", 0, 1.0f);
        //createEvent("dimension1499", "dimension1499", 0);
        createEvent("room1162", "room1162", 0);
        createEvent("room2scps2", "room2scps2", 0);
        createEvent("room_gw", "room3gw", 0, 1.0f);
        createEvent("room2sl", "room2sl", 0);
        createEvent("medibay", "medibay", 0);
        createEvent("room2shaft", "room2shaft", 0);
        createEvent("room1lifts", "room1lifts", 0);

        createEvent("room2gw_b", "room2gw_b", bbRand(0, 1));

        createEvent("096spawn", "room4pit", 0, 0.6f, 0.2f);
        createEvent("096spawn", "room3pit", 0, 0.6f, 0.2f);
        createEvent("096spawn", "room2pipes", 0, 0.4f, 0.2f);
        createEvent("096spawn", "room2pit", 0, 0.5f, 0.2f);
        createEvent("096spawn", "room3tunnel", 0, 0.6f, 0.2f);
        createEvent("096spawn", "room4tunnels", 0, 0.7f, 0.2f);
        createEvent("096spawn", "tunnel", 0, 0.6f, 0.2f);
        createEvent("096spawn", "tunnel2", 0, 0.4f, 0.2f);
        createEvent("096spawn", "room3z2", 0, 0.7f, 0.2f);

        createEvent("room2pit", "room2_4", 0, 0.4f, 0.4f);
        createEvent("room2offices035", "room2offices", 0);
        createEvent("room2pit106", "room2pit", 0, 0.07f, 0.1f);
        createEvent("room1archive", "room1archive", 0, 1.0f);
        return savedEvents;
    }

    private void createEvent(String event, String room, int id) {
        createEvent(event, room, id, 0, 0);
    }

    private void createEvent(String event, String room, int id, float prob) {
        createEvent(event, room, id, prob, 0);
    }

    private void createEvent(String event, String room, int id, float prob, float keter) {
        float realProb = keterMode ? prob + keter : prob;

        int i = 0;
        if (realProb == 0) {
            for (ScpcbRoom r : rooms) {
                if (r.roomTemplate.name.equals(room)) {
                    boolean temp = false;
                    for (ScpcbEvent e : savedEvents) {
                        if (e.room == r) {
                            temp = true;
                            break;
                        }
                    }

                    if (++i >= id && !temp) {
//                        System.out.println("Created event " + event);
                        savedEvents.add(new ScpcbEvent(event, r));
                        return;
                    }
                }
            }
        } else {
            for (ScpcbRoom r : rooms) {
                if (r.roomTemplate.name.equals(room)) {
                    boolean temp = false;
                    for (ScpcbEvent e : savedEvents) {
                        if (e.room == r) {
                            temp = true;
                            break;
                        }
                    }

                    double rndValue = bbRnd(0.0f, 1.0f);
//                    System.out.println("event prob test: " + rndValue + " < " + prob);
                    if (rndValue < realProb && !temp) {
//                        System.out.println("Created event " + event);
                        savedEvents.add(new ScpcbEvent(event, r));
                    }
                }
            }
        }
    }
}
