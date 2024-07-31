package ru.sooslick.scpcb;

import ru.sooslick.scpcb.map.Map;
import ru.sooslick.scpcb.map.ScpcbRoom;

import static ru.sooslick.scpcb.map.Map.MAP_HEIGHT;
import static ru.sooslick.scpcb.map.Map.MAP_WIDTH;

public class MapExplorer {

    public final Map map;
    public final String prompt;
    public final int seed;
    public final ScpcbRoom[][] grid;

    public MapExplorer(String prompt, int seed, Map map) {
        this.map = map;
        this.prompt = prompt == null ? "" : prompt;
        this.seed = seed;
        this.grid = new ScpcbRoom[MAP_WIDTH][MAP_HEIGHT];
        for (ScpcbRoom r : map.savedRooms) {
            int x = (int) (r.x / 8);
            int y = (int) (r.z / 8);
            this.grid[x][y] = r;
        }
    }

    public String exportJson() {
        String actualPrompt = prompt.equals(String.valueOf(seed)) ? "" : prompt;
        StringBuilder sb = new StringBuilder()
                .append("{\"seedString\":\"").append(actualPrompt.replace("\\", "\\\\").replace("\"", "\\\""))
                .append("\",\"seedValue\":").append(map.seed)
                .append(",\"state106\":").append(map.state106)
                .append(",\"angle\":").append(map.playerAngle)
                .append(",\"loadingScreen\":\"").append(map.loadingScreen)
                .append("\",\"rooms\":[");
        boolean comma = false;
        for (int i = 0; i < MAP_WIDTH; i++) {
            for (int j = 0; j < MAP_WIDTH; j++) {
                ScpcbRoom r = grid[i][j];
                if (r == null)
                    continue;
                if (comma)
                    sb.append(",");
                sb.append("{")
                        .append("\"name\":\"").append(r.roomTemplate.name).append("\",")
                        .append("\"x\":").append(i).append(",")
                        .append("\"y\":").append(j).append(",")
                        .append("\"angle\":").append(r.angle);
                if (r.adjDoorRight != null)
                    sb.append(",\"dh\":").append(r.adjDoorRight.getJsonValue());
                if (r.adjDoorBottom != null)
                    sb.append(",\"dv\":").append(r.adjDoorBottom.getJsonValue());
                if (r.linkedEventNormal != null)
                    sb.append(",\"en\":\"").append(r.linkedEventNormal.event).append("\"");
                if (r.linkedEventKeter != null)
                    sb.append(",\"ek\":\"").append(r.linkedEventKeter.event).append("\"");
                if (r.rndInfo != null)
                    sb.append(",\"info\":\"").append(r.rndInfo).append("\"");
                if (r.overlaps != null)
                    sb.append(",\"overlaps\":\"").append(r.overlaps).append("\"");
                sb.append("}");
                comma = true;
            }
        }
        sb.append("]}");
        return sb.toString();
    }
}
