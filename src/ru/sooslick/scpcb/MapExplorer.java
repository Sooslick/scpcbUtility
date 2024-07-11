package ru.sooslick.scpcb;

import ru.sooslick.scpcb.map.Map;
import ru.sooslick.scpcb.map.ScpcbRoom;
import ru.sooslick.scpcb.pathfinder.PathFinder;
import ru.sooslick.scpcb.pathfinder.XY;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.Set;

import static ru.sooslick.scpcb.map.Map.MAP_HEIGHT;
import static ru.sooslick.scpcb.map.Map.MAP_WIDTH;

public class MapExplorer {

    public final Object seed;
    public final Set<ScpcbRoom> rooms;
    public final ScpcbRoom[][] grid;

    public MapExplorer(Object seed, Map map) {
        this.seed = seed;
        this.rooms = map.savedRooms;
        this.grid = new ScpcbRoom[MAP_WIDTH][MAP_HEIGHT];
        for (ScpcbRoom r : rooms) {
            int x = (int) (r.x / 8);
            int y = (int) (r.z / 8);
            this.grid[x][y] = r;
        }
    }

    public XY findRoom(String name) {
        ScpcbRoom room = rooms.stream()
                .filter(r -> name.equals(r.roomTemplate.name))
                .findFirst()
                .orElse(null);
        if (room == null)
            return null;
        return new XY((int) (room.x / 8), (int) (room.z / 8));
    }

    public int pathFind(XY start, XY end) {
        if (start == null || end == null)
            return 9999;

        int[][] paths = new int[MAP_WIDTH][MAP_HEIGHT];
        LinkedList<XY> queue = new LinkedList<>();
        start.steps = 1;
        queue.add(start);

        while (queue.size() > 0) {
            XY current = queue.removeFirst();
            if (current.equals(end))
                return current.steps - 1;
            if (paths[current.x][current.y] == 0) {
                paths[current.x][current.y] = current.steps;

                if (current.x < MAP_WIDTH - 1 && grid[current.x + 1][current.y] != null)
                    queue.add(current.getRelative(1, 0));
                if (current.x > 0 && grid[current.x - 1][current.y] != null)
                    queue.add(current.getRelative(-1, 0));
                if (current.y < MAP_HEIGHT - 1 && grid[current.x][current.y + 1] != null)
                    queue.add(current.getRelative(0, 1));
                if (current.y > 0 && grid[current.x][current.y - 1] != null)
                    queue.add(current.getRelative(0, -1));
            }
        }
        return 9999;
    }

    public int testRouteLength(PathFinder f) {
        return f.calcRouteLength(this);
    }

    public void printMaze() {
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = MAP_WIDTH - 1; x >= 0; x--) {
                if (grid[x][y] != null) {
                    String colorPrefix = "\u001B[37m";
                    String name = grid[x][y].roomTemplate.name;
                    if ("room079".equals(name) || "room049".equals(name) || "room3storage".equals(name) || "room2ccont".equals(name))
                        colorPrefix = "\u001B[31m";
                    else if ("008".equals(name) || "914".equals(name) || "room2sl".equals(name))
                        colorPrefix = "\u001B[32m";
                    else if ("room2tunnel".equals(name) || "roompj".equals(name) || "room1123".equals(name) || "room2storage".equals(name))
                        colorPrefix = "\u001B[33m";
                    else if ("room2closets".equals(name) || "room2testroom2".equals(name) || "room106".equals(name) || "room2servers".equals(name))
                        colorPrefix = "\u001B[34m";
                    System.out.print(colorPrefix + "█");
                } else
                    System.out.print(" ");
            }
            System.out.println();
        }
        System.out.printf("Seed: '%s'", seed);
        System.out.println();
    }

    public void drawMap() {
        BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 1000, 1000);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        rooms.forEach(r -> {
            int x = (MAP_WIDTH - 1 - (int) r.x / 8) * 50;
            int y = (int) r.z / 8 * 50;
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(x, y, 50, 50);
            g.setColor(Color.BLACK);
            String name = r.roomTemplate.name.replaceAll("room", "");
            g.drawString(name, x - 1, y + 20);
            if (r.adjDoorBottom != null) {
                int val = r.adjDoorBottom.open ? 4 : 24;
                g.fillRect(x, y + 47, val, 3);
                g.fillRect(x + 50 - val, y + 47, val, 3);
            }
            if (r.adjDoorRight != null) {
                int val = r.adjDoorRight.open ? 4 : 24;
                g.fillRect(x, y, 3, val);
                g.fillRect(x, y + 50 - val, 3, val);
            }
        });
        try {
            FileImageOutputStream fios = new FileImageOutputStream(new File(seed + ".jpg"));
            ImageIO.write(img, "jpg", fios);
        } catch (Exception ignored) {
        }
    }

    public void exportJson() {
        StringBuilder sb = new StringBuilder("{\"seed\":\"")
                .append(seed)
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
                    sb.append(",\"doorh\":").append(r.adjDoorRight.getJsonValue());
                if (r.adjDoorBottom != null)
                    sb.append(",\"doorv\":").append(r.adjDoorBottom.getJsonValue());
                if (r.rndInfo != null && r.rndInfo.size() > 0)
                    sb.append(",\"info\":\"").append(r.rndInfo).append("\"");
                sb.append("}");
                comma = true;
            }
        }
        sb.append("]}");
        System.out.println(sb);
    }
}
