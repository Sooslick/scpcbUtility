package ru.sooslick.scpcb.pathfinder;

import ru.sooslick.scpcb.MapExplorer;

public class RankedPathFinder extends AbstractWeightedPathfinder {
    @Override
    public int calcRouteLength(MapExplorer map) {
        NodeWeights[][] weights = calcWeights(map.grid);

        XY cont = map.findRoom("room2ccont");
        if (cont == null)
            return 999999;

        int lcz = calcBeforePD(weights, map);
        int hcz = calcAfterPD(weights, map, cont);
        return lcz*2 + hcz + 120;

        // extra time:
        // start: +15
        // closets+testroom: +5 (+20)
        // 914: +20 (+40)
        // pocket dimension warp: +5 (+45)
        // 008: +5 (+50)
        // cont-1: +10 (+60)
        // 079: +10 (+70)
        // cont-2: +10 (+80)
        // ending: +30 (+110)
        // various timeloss: +10 (+120)
    }

    @Override
    public String getName() {
        return "Ranked game";
    }

    private int calcBeforePD(NodeWeights[][] weights, MapExplorer map) {
        XY start = map.findRoom("start");
        XY closets = map.findRoom("room2closets");
        XY room970 = map.findRoom("room2storage");
        XY testroom2 = map.findRoom("room2testroom2");
        XY room914 = map.findRoom("914");
        int defaultRoute = pathFind(weights, start, closets, testroom2, room914);
        int altRoute = pathFind(weights, start, room970, testroom2, closets, room914);
        return Math.min(defaultRoute, altRoute);
    }

    private int calcAfterPD(NodeWeights[][] weights, MapExplorer map, XY cont) {
        XY gateA = map.findRoom("gateaentrance");
        XY gateB = map.findRoom("exit1");
        XY room008 = map.findRoom("008");
        XY room079 = map.findRoom("room079");
        int beforeCont = calcBestHcz(weights, map, room008, cont);
        int afterCont = pathFind(cont, room079, weights) * 2 +
                Math.min(pathFind(cont, gateA, weights), pathFind(cont, gateB, weights));
        return beforeCont + afterCont;
    }

    private int calcBestHcz(NodeWeights[][] weights, MapExplorer map, XY room008, XY cont) {
        XY room106 = map.findRoom("room106");
        XY shaft = map.findRoom("shaft");
        XY tunnel = map.findPDExit();

        int route106 = calcHcz(weights, room106, room008, cont) + 15;
        int routeShaft = calcHcz(weights, shaft, room008, cont) + 15;
        int routeTunnel = calcHcz(weights, tunnel, room008, cont);
        return Math.min(Math.min(route106, routeShaft), routeTunnel);
    }

    private int calcHcz(NodeWeights[][] weights, XY startPoint, XY room008, XY cont) {
        if (startPoint == null)
            return 9999;
        if (room008 == null) {
            return pathFind(startPoint, cont, weights);
        } else {
            return pathFind(weights, startPoint, room008, cont);
        }
    }
}
