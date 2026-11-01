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

Index page: http://localhost:1499/index.html

Choose **SCP: CB UE Classic v5.5.4.1** in the game selector to preview Ultimate Edition Classic
seed strings, facility rooms, room descriptions, door states, overlaps, and the SCP-860 forest.
Shared links retain the game and SCP-005 location. Vanilla remains the default.

UE Classic support targets the supplied v5.5.4.1 source and its default 18×18 map, with the intro skipped.
UE picks SCP-005's location before seeding map generation, so a seed alone does not determine every room.
Select its chamber, Maynard's office, or SCP-409's chamber to match the run; the chamber is the preview default.
Custom map sizes, UE events, maintenance tunnels, loading screens and initial NPC/player state are not previewed.
The speedrun route tools and ranked seed picker continue to target vanilla.

API example: `/api/map?game=ue-classic&prompt=n790&scp005=1`. `scp005` accepts 1, 2 or 3;
UE accepts the same 1–15 ASCII seed characters as the game. Omit `game` for existing vanilla API behavior.

UE room metadata and forest heightmaps are bundled in `scpcbFiles/ue-classic`; no desktop source folder
is needed at runtime. To regenerate that data from the game source, run
`node tools/export-ue-rooms.cjs "C:/path/to/CBUE-Classic-main"` (Node.js required only for this developer tool).

Verification from the repository root:
```
mvn test-compile
java -cp "scpcbmap-core/target/classes;scpcbmap-core/target/test-classes" ru.sooslick.scpcb.MapJsonVerifier
java -ea -cp "scpcbmap-core/target/classes;scpcbmap-core/target/test-classes" ru.sooslick.scpcb.UeClassicVerifier
java -cp "scpcbmap-core/target/classes;scpcbmap-core/target/test-classes" ru.sooslick.scpcb.map.ExtentsModelVerifier
node tools/verify-preview.cjs
```
Use `:` instead of `;` in the Java classpath on Linux/macOS. The last command requires the web server
to be running at localhost:1499 and checks the API and frontend behavior. UE verification covers
repeatability and source-specific room rules.

Room overlap prevention depends on the last bit of Blitz3D's 32-bit float math (see `BlitzRoomExtents`).
`ExtentsModelVerifier` checks that float model against the room bounds dumped from the real 1.3.11 game
(`scpcbFiles/roomdb.dat`, 48960 values, no mismatches). UE's `CalculateRoomExtents` and `PreventRoomOverlap`
([source](https://github.com/Ultimate-Edition-Team/CBUE-Classic)) have the same steps, so UE reuses the model,
assuming the same engine math, but UE maps have not yet been compared with live-game dumps of UE itself.

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
