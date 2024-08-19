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

import static ru.sooslick.scpcb.map.Map.MAP_HEIGHT;
import static ru.sooslick.scpcb.map.Map.MAP_WIDTH;

public class MapExplorer {

    public final Map map;
    public final Object seedPrompt;
    public final ScpcbRoom[][] grid;

    public MapExplorer(Object seed, Map map) {
        this.map = map;
        this.seedPrompt = seed;
        this.grid = new ScpcbRoom[MAP_WIDTH][MAP_HEIGHT];
        for (ScpcbRoom r : map.savedRooms) {
            int x = (int) (r.x / 8);
            int y = (int) (r.z / 8);
            this.grid[x][y] = r;
        }
    }

    public XY findRoom(String name) {
        ScpcbRoom room = map.savedRooms.stream()
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
        System.out.printf("Seed: '%s'", seedPrompt);
        System.out.println();
    }

    public void printForest() {
        map.savedRooms.stream()
                .filter(r -> r.roomTemplate.name.contains("860"))
                .findFirst()
                .ifPresent(r -> System.out.println(r.rndInfo.replace("|", "\n")));
    }

    public void printTunnels() {
        map.savedRooms.stream()
                .filter(r -> r.roomTemplate.name.contains("room2tunnel"))
                .findFirst()
                .ifPresent(r -> System.out.println(r.rndInfo.replace("|", "\n")));
    }

    public void drawMap() {
        BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.createGraphics();
        g.setColor(new Color(60, 0, 0));
        g.fillRect(0, 0, 1000, 300);
        g.setColor(new Color(40, 20, 0));
        g.fillRect(0, 300, 1000, 350);
        g.setColor(new Color(40, 40, 0));
        g.fillRect(0, 350, 1000, 600);
        g.setColor(new Color(20, 40, 0));
        g.fillRect(0, 600, 1000, 650);
        g.setColor(new Color(0, 40, 0));
        g.fillRect(0, 650, 1000, 1000);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        map.savedRooms.forEach(r -> {
            int x = (MAP_WIDTH - 1 - (int) r.x / 8) * 50;
            int y = (int) r.z / 8 * 50;
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(x, y, 50, 50);
            g.setColor(Color.GRAY);
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
            g.setColor(Color.BLACK);
            g.drawString(r.roomTemplate.name.replaceAll("room", ""), x - 1, y + 20);
            if (r.linkedEventNormal != null)
                g.drawString(r.linkedEventNormal.event.replaceAll("room", ""), x - 1, y + 30);
            if (r.linkedEventKeter != null)
                g.drawString(r.linkedEventKeter.event.replaceAll("room", ""), x - 1, y + 40);
        });
        try {
            FileImageOutputStream fios = new FileImageOutputStream(new File(seedPrompt + ".jpg"));
            ImageIO.write(img, "jpg", fios);
        } catch (Exception ignored) {
        }
    }

    public String exportJson() {
        // todo: I have a request for overlap check
        StringBuilder sb = new StringBuilder()
                .append("{\"seedString\":\"").append(seedPrompt.toString().replace("\\", "\\\\").replace("\"", "\\\""))
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
                sb.append("}");
                comma = true;
            }
        }
        sb.append("]}");
        return sb.toString();
    }
}
