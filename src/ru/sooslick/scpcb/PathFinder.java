package ru.sooslick.scpcb;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.Set;

public class PathFinder {
    private final String seed;
    private final ScpcbRoom[][] map;

    private XY covid = null;
    private XY ai = null;
    private XY cont = null;
    private XY gateA = null;
    private XY gateB = null;

    public PathFinder(String seed, Set<ScpcbRoom> rooms) {
        this.seed = seed;
        this.map = new ScpcbRoom[20][20];
        for (ScpcbRoom r : rooms) {
            int x = (19 - r.x / 8);
            int y = (r.z / 8);
            map[x][y] = r;

            switch (r.roomTemplate.name) {
                case "008":
                    covid = new XY(x, y);
                    break;
                case "room079":
                    ai = new XY(x, y);
                    break;
                case "room2ccont":
                    cont = new XY(x, y);
                    break;
                case "exit1":
                    gateB = new XY(x, y);
                    break;
                case "gateaentrance":
                    gateA = new XY(x, y);
                    break;
            }
        }
    }

    public void printMaze() {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++)
                if (map[j][i] != null)
                    System.out.print("█");
                else
                    System.out.print(" ");
            System.out.println();
        }
        System.out.printf("Seed: '%s'", seed);
        System.out.println();
    }

    public int testRouteLength() {
        if (cont == null || covid == null)
            return 9999;

        return pathFind(covid, cont) + pathFind(cont, ai) + Math.min(pathFind(ai, gateA), pathFind(ai, gateB));
    }

    private int pathFind(XY start, XY end) {
        if (start == null || end == null)
            return 9999;

        int[][] paths = new int[20][20];
        LinkedList<XY> queue = new LinkedList<>();
        start.steps = 1;
        queue.add(start);

        while (queue.size() > 0) {
            XY current = queue.removeFirst();
            if (current.equals(end))
                return current.steps - 1;
            if (paths[current.x][current.y] == 0) {
                paths[current.x][current.y] = current.steps;

                if (current.x < 19 && map[current.x + 1][current.y] != null)
                    queue.add(current.getRelative(1, 0));
                if (current.x > 0 && map[current.x - 1][current.y] != null)
                    queue.add(current.getRelative(-1, 0));
                if (current.y < 19 && map[current.x][current.y + 1] != null)
                    queue.add(current.getRelative(0, 1));
                if (current.y > 0 && map[current.x][current.y - 1] != null)
                    queue.add(current.getRelative(0, -1));
            }
        }
        return 9999;
    }

//    private void drawMap() {
//        BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
//        Graphics g = img.createGraphics();
//        g.setColor(Color.BLACK);
//        g.fillRect(0, 0, 1000, 1000);
//        g.setFont(new Font("Arial", Font.PLAIN, 10));
//        int yOff = 1;
//        for (ScpcbRoom r : rooms) {
//            int x = (19 - r.x / 8) * 50;
//            int y = (r.z / 8) * 50;
//            g.setColor(Color.LIGHT_GRAY);
//            g.fillRect(x, y, 50, 50);
//            g.setColor(Color.BLACK);
//            String name = r.roomTemplate.name;
//            //name = name.substring(0, Math.min(name.length(), 8));
//            g.drawString(name, x - 1, y + yOff * 10);
//            if (++yOff > 5)
//                yOff = 1;
//        }
//        try {
//            FileImageOutputStream fios = new FileImageOutputStream(new File(seed + ".jpg"));
//            ImageIO.write(img, "jpg", fios);
//        } catch (Exception ignored) {
//        }
//    }

    private static class XY {
        int x;
        int y;
        int steps;

        private XY(int x, int y) {
            this(x, y, 0);
        }

        private XY(int x, int y, int steps) {
            this.x = x;
            this.y = y;
            this.steps = steps;
        }

        public boolean equals(XY other) {
            return x == other.x && y == other.y;
        }

        public XY getRelative(int addX, int addY) {
            return new XY(x + addX, y + addY, steps + 1);
        }
    }
}
