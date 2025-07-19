# Utility scripts for SCP: Containment Breach

Features:
- BlitzRandom: random numbers generator from Blitz3D engine
- SeedGenerator: generate seed and rooms' events
- SeedFinder: analyze and filter SCP:CB maps by speedrunning routes
- SeedTester: find vanilla game seeds with same seed number and filter them by text pattern (work in progress)
- Tester914: simulate omni card rolls with blitz3d random

### Complile & launch

All scripts use plain java 8. No project building tools like Maven or Gradle are used.

Launch main method from your favorite IDE or from compiled .jar. Make sure your working directory is project root
(map generator requires access to `scpcbFiles` directory to correct working)

### Online seed previewer

Check `server` branch for more details

### If you change map generator code

Launch tests via `MapJsonVerifier` main method to quick check everything is OK

### Seed finder

SeedFinder is bruteforce script that will generate every possible seed and analyze routes using various pathfinders.
Script supports following argline parameters:
- `--start` - seed number to start from (inclusive);
- `--end` - seed number to stop (exclusive);
- `--print-maze` - draw map to console like S-NAV
- `--path-finders` - PathFinder java class that will perform map analysis, and threshold route length, separated by colon.
  Example value: `fully.qualified.Classname:123`.
  If you want multiple pathfinders, separate them by comma.

Example command:  
`java -cp scpcbmap.jar ru.sooslick.scpcb.SeedFinder --print-maze --start=10000 --end=20000 --path-finders=ru.sooslick.scpcb.pathfinder.SSPathFinder:42,ru.sooslick.scpcb.pathfinder.SSIPathFinder:66`  
This command will generate seeds from 10000 to 19999, printing each found seed.

Existing Pathfinder classes and recommended scores:
- `ru.sooslick.scpcb.pathfinder.SSPathFinder:30` - Set Seed Inbounds A1/B1 with PD route;
- `ru.sooslick.scpcb.pathfinder.SSLegacyPathFinder:45` - Set Seed Inbounds A1/B1 with classic route;
- `ru.sooslick.scpcb.pathfinder.SSA2PathFinder:35` - Set Seed Inbounds A2 with PD route;
- `ru.sooslick.scpcb.pathfinder.SSB2PathFinder:35` - Set Seed Inbounds B2 with PD route;
- `ru.sooslick.scpcb.pathfinder.SSIPathFinder:50` - Set Seed Intended A1/B1;
- `ru.sooslick.scpcb.pathfinder.SSIA2PathFinder:52` - Set Seed Intended A2;
- `ru.sooslick.scpcb.pathfinder.SSIB2PathFinder:55` - Set Seed Intended B2;
- `ru.sooslick.scpcb.pathfinder.AnyPercent914PathFinder:30` - Any% route (Out of Bounds)

Other experimental pathfinders also may be available in `ru.sooslick.scpcb.pathfinder` package