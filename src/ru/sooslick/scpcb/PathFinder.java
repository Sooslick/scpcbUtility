package ru.sooslick.scpcb;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class PathFinder {
    public static final Function<PathFinder, Integer> NO_SCP914_FINDER = PathFinder::calcNoScp914;
    public static final Function<PathFinder, Integer> ANY_PERCENT_ENDGAME = PathFinder::calcOmniEndgameLength;

    private final String seed;
    private final ScpcbRoom[][] map;

    private XY covid = null;
    private XY ai = null;
    private XY cont = null;
    private XY gateA = null;
    private XY gateB = null;

    private XY shaft = null;
    private XY tunnel = null;
    private XY room106 = null;
    private XY room049 = null;

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
                case "room2shaft":
                    shaft = new XY(x, y);
                    break;
                case "tunnel":
                    tunnel = new XY(x, y);
                    break;
                case "room106":
                    room106 = new XY(x, y);
                    break;
                case "room049":
                    room049 = new XY(x, y);
                    break;
            }
        }
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

    public int testRouteLength(Function<PathFinder, Integer> method) {
        return method.apply(this);
    }

    private int calcOmniEndgameLength() {
        if (cont == null || covid == null)
            return 9999;

        return pathFind(covid, cont) + pathFind(cont, ai) * 2 + Math.min(pathFind(cont, gateA), pathFind(cont, gateB));
    }

    private int calcNoScp914() {
        List<XY> startLocations = Arrays.asList(shaft, tunnel, room106);
        int toContLength = startLocations.stream().mapToInt((start) -> {
                            int initValue = 0;
                            if (start.equals(shaft))
                                System.out.println("Start from shaft");
                            else if (start.equals(tunnel))
                                System.out.println("Start from tunnel");
                            else {
                                System.out.println("Start from 106");
                                initValue = 5;
                            }

                            int covidFirstLength = pathFind(start, covid) +
                                    pathFind(covid, room049) +
                                    pathFind(room049, room106) +
                                    pathFind(room106, cont);

                            int covidSecondLength = pathFind(start, room049) +
                                    pathFind(room049, covid) +
                                    pathFind(covid, room106) +
                                    pathFind(room106, cont);

                            int covidLastLength = pathFind(start, room049) +
                                    pathFind(room049, room106) +
                                    pathFind(room106, covid) +
                                    pathFind(covid, cont);

                            if (covidFirstLength < covidSecondLength && covidFirstLength < covidLastLength) {
                                System.out.println("Covid first: " + covidFirstLength);
                                return initValue + covidFirstLength;
                            } else if (covidSecondLength < covidFirstLength && covidSecondLength < covidLastLength) {
                                System.out.println("Covid after 049: " + covidSecondLength);
                                return initValue + covidSecondLength;
                            } else {
                                System.out.println("Covid after 106: " + covidLastLength);
                                return initValue + covidLastLength;
                            }
                        }
                ).min()
                .orElse(9999);
        return toContLength + pathFind(cont, ai) * 2 + Math.min(pathFind(ai, gateA), pathFind(ai, gateB));
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

    public void drawMap() {
        BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 1000, 1000);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        int yOff = 1;
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                ScpcbRoom r = map[i][j];
                if (r == null)
                    continue;
                int x = (19 - r.x / 8) * 50;
                int y = (r.z / 8) * 50;
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(x, y, 50, 50);
                g.setColor(Color.BLACK);
                String name = r.roomTemplate.name;
                //name = name.substring(0, Math.min(name.length(), 8));
                g.drawString(name, x - 1, y + yOff * 10);
                if (++yOff > 5)
                    yOff = 1;
            }
        }
        try {
            FileImageOutputStream fios = new FileImageOutputStream(new File(seed + ".jpg"));
            ImageIO.write(img, "jpg", fios);
        } catch (Exception ignored) {
        }
    }

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
