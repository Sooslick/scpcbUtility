# Utility scripts for SCP: Containment Breach

Features:
- (Core) BlitzRandom: random numbers generator from Blitz3D engine
- (Core) SeedGenerator: generate seed and rooms' events
- (Search) SeedFinder: analyze and filter SCP:CB maps by speedrunning routes
- (Webmap) Server: host web seed previewer for yourself
- (Misc) SeedTester: find vanilla game seeds with same seed number and filter them by text pattern (mostly scrapped)
- (Misc) Tester914: simulate omni card rolls with blitz3d random (mostly scrapped)

### Complile & launch

Requirements:
- Maven 3
- Java 17

Launch main method from your favorite IDE or from compiled .jar. Make sure your working directory is project root
(map generator requires access to `scpcbFiles` directory to correct working)

### If you change map generator code

Launch tests via `MapJsonVerifier` main method to quick check everything is OK. Tests are located in `scpcbmap-core` module.

### Online seed previewer

Launch app with following command:
`mvn exec:java -pl scpcbmap-webmap`

Or build .jar using `mvn compile assembly:single -pl scpcbmap-webmap`, and then simply launch .jar-file

Or launch main method in `ru.sooslick.scpcb.server.ServerMain` from your IDE. Class is located in `scpcbmap-webmap` module.

Server will be available at port 1499 by default (unless other port is specified in `server.properties`).

Index page: http://localhost:1499/frontend/

### Seed finder

SeedFinder is bruteforce script that will generate every possible seed and analyze routes using various pathfinders.
Script supports following argline parameters:
- `--start` - seed number to start from (inclusive);
- `--end` - seed number to stop (inclusive);
- `--seed` - single seed to analyze (if present then --start and --end will be ignored);
- `--print-maze` - draw map to console like S-NAV;
- `--print-json` - print json output to console (like /map web endpoint do);
- `--path-finders` - PathFinder java class that will perform map analysis, and threshold route length, separated by colon.
  Example value: `fully.qualified.Classname:123`.
  If you want multiple pathfinders, separate them by comma;
- `--list-path-finders` - script will print all known pathfinder classes and then terminates.

Example command:  
`java -jar scpcbmap-search.jar --print-maze --start=10000 --end=20000 --path-finders=ru.sooslick.scpcb.pathfinder.SSPathFinder:42,ru.sooslick.scpcb.pathfinder.SSIPathFinder:66`  
This command will generate seeds from 10000 to 19999, printing each found seed.

Existing Pathfinder classes and recommended scores for WR-tier seeds:
- `SSPathFinder:30` - Set Seed Inbounds A1/B1 with PD route;
- `SSLegacyPathFinder:45` - Set Seed Inbounds A1/B1 with classic route;
- `SSIPathFinder:50` - Set Seed Intended A1/B1;
- `AnyPercent914PathFinder:30` - Any% route (Out of Bounds)

Other experimental pathfinders also may be available in `ru.sooslick.scpcb.pathfinder` package (or just run the script with `--list-path-finders`)