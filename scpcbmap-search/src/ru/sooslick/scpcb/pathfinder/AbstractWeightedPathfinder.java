package ru.sooslick.scpcb.pathfinder;

import ru.sooslick.scpcb.map.Map;
import ru.sooslick.scpcb.map.ScpcbRoom;

import java.util.LinkedList;

public abstract class AbstractWeightedPathfinder implements PathFinder {

    protected int pathFind(NodeWeights[][] weights, XY... points) {
        int length = 0;
        for (int i = 1; i < points.length; i++) {
            length += pathFind(points[i - 1], points[i], weights);
            if (length >= 9999)
                return 9999;
        }
        return length;
    }

    protected int pathFind(XY start, XY end, NodeWeights[][] weights) {
        if (start == null || end == null)
            return 9999;

        int[][] nodeScores = new int[Map.MAP_WIDTH][Map.MAP_HEIGHT];
        LinkedList<XY> queue = new LinkedList<>();
        queue.add(start);

        int mw = Map.MAP_WIDTH - 1;
        int mh = Map.MAP_HEIGHT - 1;
        while (queue.size() > 0) {
            XY current = queue.removeFirst();

            // check path to the left
            if (current.x < mw && weights[current.x + 1][current.y] != null) {
                int nextNodeScore = nodeScores[current.x][current.y] + weights[current.x][current.y].left + weights[current.x + 1][current.y].right;
                if (nodeScores[current.x + 1][current.y] == 0 || nextNodeScore < nodeScores[current.x + 1][current.y]) {
                    nodeScores[current.x + 1][current.y] = nextNodeScore;
                    queue.add(current.getRelative(1, 0));
                }
            }

            // check path to the right
            if (current.x > 0 && weights[current.x - 1][current.y] != null) {
                int nextNodeScore = nodeScores[current.x][current.y] + weights[current.x][current.y].right + weights[current.x - 1][current.y].left;
                if (nodeScores[current.x - 1][current.y] == 0 || nextNodeScore < nodeScores[current.x - 1][current.y]) {
                    nodeScores[current.x - 1][current.y] = nextNodeScore;
                    queue.add(current.getRelative(-1, 0));
                }
            }

            // check path to the bottom
            if (current.y < mh - 1 && weights[current.x][current.y + 1] != null) {
                int nextNodeScore = nodeScores[current.x][current.y] + weights[current.x][current.y].bottom + weights[current.x][current.y + 1].top;
                if (nodeScores[current.x][current.y + 1] == 0 || nextNodeScore < nodeScores[current.x][current.y + 1]) {
                    nodeScores[current.x][current.y + 1] = nextNodeScore;
                    queue.add(current.getRelative(0, 1));
                }
            }

            // check path to the top
            if (current.y > 0 && weights[current.x][current.y - 1] != null) {
                int nextNodeScore = nodeScores[current.x][current.y] + weights[current.x][current.y].top + weights[current.x][current.y - 1].bottom;
                if (nodeScores[current.x][current.y - 1] == 0 || nextNodeScore < nodeScores[current.x][current.y - 1]) {
                    nodeScores[current.x][current.y - 1] = nextNodeScore;
                    queue.add(current.getRelative(0, -1));
                }
            }
        }
        return nodeScores[end.x][end.y];
    }

    protected NodeWeights[][] calcWeights(ScpcbRoom[][] grid) {
        NodeWeights[][] weights = new NodeWeights[Map.MAP_WIDTH][Map.MAP_HEIGHT];
        for (int i = 0; i < Map.MAP_WIDTH; i++) {
            for (int j = 0; j < Map.MAP_HEIGHT; j++) {
                if (grid[i][j] == null)
                    continue;
                weights[i][j] = defineWeights(grid[i][j]);
            }
        }
        return weights;
    }

    protected NodeWeights defineWeights(ScpcbRoom r) {
        NodeWeights w = new NodeWeights();
        switch (r.roomTemplate.name) {
            case "lockroom" -> {
                w.top = 4;
                w.left = 4;
            }
            case "room3storage" -> w.left = 45;
            case "room2tesla_lcz", "room2doors", "checkpoint1", "room2tesla_hcz", "checkpoint2", "room2tesla" -> {
                w.top = 3;
                w.bottom = 3;
            }
            case "room860", "room2gw" -> {
                w.top = 5;
                w.bottom = 5;
            }
            case "room049" -> {
                w.top = 30;
                w.bottom = 30;
            }
            case "room513", "room966" -> w.right = 4;
            case "testroom" -> {
                w.top = 6;
                w.bottom = 6;
            }
            case "room2servers" -> {
                w.top = 40;
                w.bottom = 40;
            }
            case "lockroom2" -> {
                w.top = 3;
                w.left = 3;
            }
            case "room3servers" -> w.top = 5;
            case "room3servers2" -> w.left = 5;
            case "room3gw" -> {
                w.top = 2;
                w.left = 6;
                w.right = 6;
            }
        }
        return w.translate(r.angle % 360);
    }

    protected static class NodeWeights {
        protected int top = 2;
        protected int right = 2;
        protected int bottom = 2;
        protected int left = 2;

        public NodeWeights() {
        }

        public NodeWeights(int top, int right, int bottom, int left) {
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.left = left;
        }

        private NodeWeights translate(int angle) {
            switch (angle) {
                case 90:
                    return new NodeWeights(right, bottom, left, top);
                case 180:
                    return new NodeWeights(bottom, left, top, right);
                case 270:
                    return new NodeWeights(left, top, right, bottom);
                default:
                    return this;
            }
        }
    }
}
