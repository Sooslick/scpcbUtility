let currentMap = null;
let loading = false;
let savedOverlaps = null;
let link = null;
let lockedX = -1;
let lockedY = -1;

let mapWrapper = document.getElementById("map-table");
for (let i = 0; i <= 18; i++) {
	let tr = document.createElement("tr");
	for (let j = 0; j <= 18; j++) {
		let td = document.createElement("td");
		td.setAttribute("id", "c" + (18 - j) + "-" + i);
		td.setAttribute("onmouseover", "getRoomInfo(" + (18 - j) + "," + i + ")");
		td.setAttribute("onclick", "lockRoomInfo(" + (18 - j) + "," + i + ")");
		tr.appendChild(td);
	}
	tr.style.background = getLineBackground(i);
	mapWrapper.appendChild(tr);
}
mapWrapper.addEventListener('contextmenu', function(evt) {
	evt.preventDefault();
}, false);
addEventListener("popstate", (event) => {
	nullMap();
	if (event.state && typeof event.state.seedValue !== 'undefined') {
		buildMap(event.state);
		document.getElementById("prompt").value = currentMap.seedString
		document.getElementById("seed").value = currentMap.seedValue
		updateShareLink();
	}
});

mapWrapper = document.getElementById("table-pd");
for (let i = 0; i <= 18; i++) {
	let tr = document.createElement("tr");
	for (let j = 0; j <= 18; j++) {
		let td = document.createElement("td");
		td.setAttribute("id", "t" + i + "-" + j);
		tr.appendChild(td);
	}
	mapWrapper.appendChild(tr);
}
mapWrapper.addEventListener('contextmenu', function(evt) {
	evt.preventDefault();
}, false);

mapWrapper = document.getElementById("table-forest");
for (let i = 0; i < 10; i++) {
	let tr = document.createElement("tr");
	for (let j = 0; j < 10; j++) {
		let td = document.createElement("td");
		td.setAttribute("id", "f" + i + "-" + j);
		tr.appendChild(td);
	}
	mapWrapper.appendChild(tr);
}
mapWrapper.addEventListener('contextmenu', function(evt) {
	evt.preventDefault();
}, false);

document.getElementById("seed").addEventListener("keyup", e => {
    if (e.key == "Enter")
        createMap();
});
document.getElementById("prompt").addEventListener("keyup", e => {
    if (e.key == "Enter")
        createMap();
});

function getLineBackground(i) {
    if (i < 6) return "rgba(64, 128, 0, 0.3)";
    else if (i < 7) return "rgba(0, 0, 0, 0.1)";
    else if (i < 12) return "rgba(128, 0, 0, 0.3)";
    else if (i < 13) return "rgba(0, 0, 0, 0.1)";
    else return "rgba(128, 128, 0, 0.3)";
}

function createMap() {
    if (loading)
        return;

    nullMap();

    loading = true;
    document.getElementById("loading-gif").hidden = false;

    let query = "";
    let prompt = document.getElementById("prompt").value;
    if (prompt == null)
        prompt = "";
    let seed = document.getElementById("seed").value;
    if (seed != null)
        seed = seed.match("[0-9]{1,10}");

    const ultimate = document.getElementById("game").value === "ue-classic";
    if (prompt.length > 0)
        query+= "?prompt=" + encodeURIComponent(prompt);
    else if (seed != null && !ultimate)
        query+= "?seed=" + encodeURIComponent(seed);
    else
        query+= "?random";

    if (ultimate)
        query += "&game=ue-classic&scp005=" + document.getElementById("scp005").value;

    let xhr = new XMLHttpRequest();
	xhr.onload = readMapResponse;
	xhr.ontimeout = unableRequestMap;
	xhr.addEventListener("error", unableRequestMap);
	xhr.timeout = 6000;
	xhr.open('GET', 'api/map' + query, true);
	xhr.send();
}

function unableRequestMap() {
	let errortext = document.getElementById("error-text");
    errortext.hidden = false;
    errortext.innerHTML = "<span>Service seems to be unavailable. DM </span><span id=\"discord-id\" class=\"clickable\" onclick=\"copyContent('discord-id')\">@Sooslick</span><span> in Discord.</span>";
    loading = false;
}

function nullMap() {
    currentMap = null;
    savedOverlaps = null;

    for (let i = 0; i <= 18; i++)
        for (let j = 0; j <= 18; j++) {
            document.getElementById("c" + (18 - i) + "-" + j).innerHTML = "";
            document.getElementById("t" + i + "-" + j).style.background = null;
        }

    for (let i = 0; i < 10; i++)
        for (let j = 0; j < 10; j++)
            document.getElementById("f" + i + "-" + j).style.background = null;

	unlockCell();

    document.getElementById("map").hidden = true;
    document.getElementById("map-share").hidden = true;
    document.getElementById("error-text").hidden = true;
    document.getElementById("copied-text").hidden = true;
    document.getElementById("map-pd").style.display = 'none';
    document.getElementById("map-forest").style.display = 'none';
    document.getElementById("map-info").hidden = true;
    document.getElementById("room-info").hidden = true;

    document.getElementById("room").innerHTML = "";
    document.getElementById("room-event").innerHTML = "";
    document.getElementById("room-tele").innerHTML = "";
    document.getElementById("rnd-info").innerHTML = "";
}

function readMapResponse() {
	loading = false;
    document.getElementById("loading-gif").hidden = true;
    if (this.status != 200) {
    	let errtxt = document.getElementById("error-text");
        errtxt.hidden = false;
        if (this.status == 429)
        	errtxt.innerHTML = "Too many requests";
        else
        	errtxt.innerHTML = "<span>Something on server is broken, DM </span><span id=\"discord-id\" class=\"clickable\" onclick=\"copyContent('discord-id')\">@Sooslick</span><span> in Discord.</span>";
        return;
    }
    if (this.status == 200) {
    	try {
    		currentMap = JSON.parse(this.responseText);
    	} catch (jumpscare) {
    		let errtxt = document.getElementById("error-text");
            errtxt.hidden = false;
            errtxt.innerHTML = "<span>Unable to create seed, DM </span><span id=\"discord-id\" class=\"clickable\" onclick=\"copyContent('discord-id')\">@Sooslick</span><span> in Discord.</span>";
            return;
    	}

    	// create link
        updateShareLink();
        history.pushState(currentMap, "", link);
        buildMap(currentMap);
    }
}

function buildMap(map) {
	currentMap = map;
    const ultimate = map.game === "ue-classic";
    document.getElementById("game").value = ultimate ? "ue-classic" : "vanilla";
    document.getElementById("scp005").value = map.scp005 || 1;
    updateGame();

	// show blocks
    document.getElementById("map").hidden = false;
    document.getElementById("map-share").hidden = false;
    document.getElementById("map-pd").style.display = map.rooms.some(r => r.info && r.info.includes("tunnels=")) ? 'inline-block' : 'none';
    document.getElementById("map-forest").style.display = map.rooms.some(r => r.info && r.info.startsWith("forest=")) ? 'inline-block' : 'none';
    document.getElementById("map-info").hidden = false;
    document.getElementById("room-info").hidden = false;
    document.getElementById("room-event-container").hidden = ultimate;
    document.getElementById("room-description-container").hidden = !ultimate;
    ["loadingScreen", "state106", "statePlayer"].forEach(id => document.getElementById(id + "-container").hidden = ultimate);

    // fill meta
	document.getElementById("seedString").textContent = currentMap.seedString
	document.getElementById("seedValue").innerHTML = currentMap.seedValue
	document.getElementById("state106").innerHTML = currentMap.state106
	document.getElementById("statePlayer").innerHTML = currentMap.angle
	document.getElementById("loadingScreen").innerHTML = currentMap.loadingScreen

	// fill main grid
	currentMap.rooms.forEach(r => {
	    let cellId = "c" + r.x + "-" + r.y;
	    let svg = svgCreate(
	    	getRoomImage(r),
	    	getRoomRotation(r.name, r.angle),
	    	r.dh,
	    	r.dv
	    );

		let td = document.getElementById(cellId);
		td.innerHTML = svg;

		if (r.name == "room2tunnel" && r.info && r.info.includes("tunnels="))
		    buildTunnel(r.info)
		else if (r.name == "room860" && r.info && r.info.startsWith("forest="))
		    buildForest(r.info)
	});

	// create annotations
	currentMap.rooms.forEach(r => {
		createAnnotation(r.name, r.x, r.y);
	});

	// scroll into viewport
	setTimeout(function() {
		window.scroll({top: 225, left: 0, behavior: 'smooth'});
	}, 150);
}

function buildTunnel(info) {
    let map = info.replace("{tunnels=", "").replace("}", "");
    let parts = map.split("|");
    for (let i = 0; i <= 18; i++)
        for (let j = 0; j <= 18; j++) {
            let c = parts[i].charAt(j);
            if (c == 'X')
                document.getElementById("t" + i + "-" + (18 - j)).style.background = "#666";
            else if (c == 'H')
                document.getElementById("t" + i + "-" + (18 - j)).style.background = "#B66";
            else if (c == 'E')
                document.getElementById("t" + i + "-" + (18 - j)).style.background = "#996";
        }
}

function buildForest(info) {
    let map = info.replace("forest=", "");
    let parts = map.split("|");
    for (let i = 0; i < 10; i++)
        for (let j = 0; j < 10; j++) {
            let c = parts[i].charAt(j);
            if (c != '.')
                if (c == 'H')
                    document.getElementById("f" + i + "-" + j).style.background = "#B66";
                else
                    document.getElementById("f" + i + "-" + j).style.background = "#666";
        }
}

function getRoomInfo(x, y) {
    if (currentMap == null)
        return;
    if (lockedX >= 0 && lockedY >= 0)
        return;
    updateOverlaps(null);   // I can optimize this call but I'm a bit lazy to do this
    currentMap.rooms.forEach(r => {
        if (r.x == x && r.y == y) {
        	updateOverlaps(r.overlaps);
		    document.getElementById("room").innerHTML = r.name;
            document.getElementById("room-description").textContent = r.description || "";
		    document.getElementById("room-tele").innerHTML = "tele " + (r.x * 8) + " 1 " + (r.y * 8);
            let en = r.en == null ? "-" : r.en;
            let ek = r.ek == null ? "-" : r.ek;
            if (en == ek) {
		        document.getElementById("room-event").innerHTML = en;
		    } else {
		        document.getElementById("room-event").innerHTML = en + " / " + ek;
		        document.getElementById("rnd-info").innerHTML = "The game can create different events for this room, depending on Aggressive NPCs setting";
		        return;
		    }
		    if (r.info == null || r.info.includes("tunnel") || r.info.includes("forest"))
		        document.getElementById("rnd-info").innerHTML = "-"
		    else
		        document.getElementById("rnd-info").innerHTML = r.info;
		}
    });
}

function lockRoomInfo(x, y) {
	if (currentMap == null)
        return;
	if (lockedX == x && lockedY == y) {
		unlockCell();
		return;
	}
	if (lockedX >=0 && lockedY >= 0)
		unlockCell();
	getRoomInfo(x, y);
	lockCell(x, y);
}

function lockCell(x, y) {
	let probablyCell = document.getElementById("c" + x + "-" + y);
	if (probablyCell != null) {
		probablyCell.classList.add("locked")
    	lockedX = x;
    	lockedY = y;
    }
}

function unlockCell() {
	let probablyCell = document.getElementById("c" + lockedX + "-" + lockedY);
	if (probablyCell != null)
		probablyCell.classList.remove("locked")
	lockedX = -1;
    lockedY = -1;
}

function getRoomImage(room) {
	let info = room.info;
	if (info != null && info.startsWith("Pocket"))
		return "room2EXIT";

    const ue = currentMap.game === "ue-classic";
	let name = room.name;
    if (ue) {
        const aliases = {room173: "start", room372: "roompj", room914: "914", room008: "008", room895: "coffin", gateb: "exit1", room2testroom: "testroom"};
        name = aliases[name] || name;
        const special = {room005: "room1K", room066: "room2C", room650: "room2C", room409: "room2E", room457: "room2E", medibay2: "room2SL"};
        if (special[name]) return special[name];
    }
    switch (name) {
        case "tunnel":
        case "tunnel2": return "room2";
        case "room2offices3":
        case "room2closets": return "room2CLOSETS";
        case "room012":
        case "room2scps2":
        case "room2servers2":
        case "room2shaft": return "room2MASK";
        case "room2poffices": return "room2CODE";
        case "room049":
        case "room2tunnel": return "room2E";
        case "checkpoint1":
        case "checkpoint2":
        case "testroom":
        case "room2doors":
        case "room2gw":
        case "room2servers":
        case "room860": return "room2L";
        case "room2poffices2":
        case "room2scps":
        case "room2storage": return "room2SCP";
        case "medibay":
        case "room1123":
        case "room2cafeteria":
        case "room2sl":
        case "room2testroom2": return "room2SL";
        case "room2tesla":
        case "room2tesla_hcz":
        case "room2tesla_lcz": return "room2T";
        case "room2nuke":
        case "room2offices4":
        case "room2sroom":
        case "room2toilets": return "room2WC";
        case "room2_4":
        case "room2pit": return "room2TRAP";
        case "lockroom":
        case "lockroom2":
        case "lockroom3":
        case "room1162": return "room2C";
        case "room513":
        case "room966": return "room3L";
        case "room3gw":
        case "room3servers2":
        case "room3storage": return "room3R";
        case "room205": return "room1";
        case "room1archive": return "room1K";
        case "start": return "room1START";
        case "coffin": return "room1PD";
    }

	let en = room.en;
	let ek = room.ek;
    if (en == null)
        en = "";
    if (ek == null)
        ek = "";
    if (name.startsWith("room3") && (en.startsWith("106s") || ek.startsWith("106s")))
        return "room3PD";
    if (name.startsWith("room4") && (en.startsWith("106s") || ek.startsWith("106s")))
        return "room4PD";
    if (name.startsWith("end") && (en.startsWith("endroom106") || ek.startsWith("endroom106")))
        return "room1PD";

    // UE room names do not encode shape (e.g. room009 is a three-way junction).
    if (ue) return [null, "room1", "room2", "room2C", "room3", "room4"][room.shape] || "room1";
    if (name.startsWith("room1"))
        return "room1";
    else if (name.startsWith("room2c"))
        return "room2C";
    else if (name.startsWith("room2"))
        return "room2";
    else if (name.startsWith("room3"))
        return "room3";
    else if (name.startsWith("room4"))
        return "room4";
    else
        return "room1";
}

function getRoomRotation(name, angle) {
    let a = angle % 180 == 0 ? angle - 180 : angle;
    switch (name) {
        case "medibay":
        case "medibay2":
        case "room012":
        case "room2_4":
        case "room2cafeteria":
        case "room2closets":
        case "room2offices4":
        case "room2pit":
        case "room2poffices":
        case "room2scps2":
        case "room2servers2":
        case "room2shaft":
        case "room2testroom2": return a + 180;
        default: return a;
    }
}

function createAnnotation(name, x, y) {
    let text = null;
    const ueLabels = {room173: "173", room372: "372", room914: "914", room008: "008", room895: "895", gateb: "B", gateaentrance: "A", room005: "005", room009: "009", room066: "066", room096: "096", room409: "409", room457: "457", room447: "447", room650: "650", room3scps: "1079", room2scps3: "178", room079: "079", room106: "106", room035: "035", room049: "049", room966: "966", room1123: "1123", room1162: "1162", room012: "012", room2scps: "714", room2scps2: "1499", room860: "860", room2cafeteria: "294", room2closets: "K0", room2storage: "K0", room2posters: "K1", room2testroom2: "K2", room2shaft: "K3", room2sroom: "K5", room2sl: "CAMS", room2toilets: "WC", room2ccont: "EC"};
    if (currentMap.game === "ue-classic") text = ueLabels[name] || null;
    else switch (name) {
        case "room2closets": text = "K1"; break;
        case "room2testroom2": text = "K2"; break;
        case "914": text = "914"; break;
        case "room2sl": text = "CAMS"; break;
        case "roompj": text = "372"; break;
        case "room012": text = "012"; break;
        case "room2scps": text = "714"; break;
        case "room2scps2": text = "1499"; break;
        case "room2storage": text = "K1"; break;
        case "room1123": text = "1123"; break;
        case "room1162": text = "1162"; break;
        case "room079": text = "079"; break;
        case "room106": text = "106"; break;
        case "008": text = "008"; break;
        case "coffin": text = "895"; break;
        case "room035": text = "035"; break;
        case "room049": text = "049"; break;
        case "room966": text = "966"; break;
        case "room2servers": text = "096"; break;
        case "room2ccont": text = "EC"; break;
        case "exit1": text = "B"; break;
        case "gateaentrance": text = "A"; break;
        case "room2toilets": text = "WC"; break;
        case "room2sroom": text = "K5"; break;
        case "room2shaft": text = "K3"; break;
        case "room2cafeteria": text = "294"; break;
    }

    if (text == null)
        return;

    let a = document.createElement("div");
    a.innerHTML = text;

    let td = document.getElementById("c" + x + "-" + (y-1));
    if (td.childNodes.length == 0) {
        td.appendChild(a);
        td.style.verticalAlign = 'bottom';
        return;
    }
    td = document.getElementById("c" + x + "-" + (y+1));
    if (td.childNodes.length == 0) {
        td.appendChild(a);
        td.style.verticalAlign = 'top';
        return;
    }
    td = document.getElementById("c" + (x-1) + "-" + y);
    if (td.childNodes.length == 0) {
        a.style.textAlign = 'left';
        td.appendChild(a);
        td.style.verticalAlign = null;
        return;
    }
    td = document.getElementById("c" + (x+1) + "-" + y);
    if (td.childNodes.length == 0) {
        a.style.textAlign = 'right';
        td.appendChild(a);
        td.style.verticalAlign = null;
        return;
    }
}

function updateOverlaps(newOverlaps) {
    if (savedOverlaps != null) {
        savedOverlaps.split(",").forEach(pos => {
            document.getElementById("c" + pos).classList.remove("overlap");
        });
        savedOverlaps = null;
    }

    if (newOverlaps != null) {
        newOverlaps.split(",").forEach(pos => {
            document.getElementById("c" + pos).classList.add("overlap");
        });
        savedOverlaps = newOverlaps;
    }
}

function shareMap() {
    navigator.clipboard.writeText(link);
    showCopiedText();
}

function updateShareLink() {
	let searchParams = new URLSearchParams();
	if (currentMap.game === "ue-classic") {
        searchParams.set("game", "ue-classic");
        searchParams.set("scp005", currentMap.scp005);
    }
	if (currentMap.seedString)
       	searchParams.set("prompt", currentMap.seedString)
    else
        searchParams.set("seed", currentMap.seedValue);
    link = window.location.origin + window.location.pathname + "?" + searchParams.toString();
}

function loadFromQuery() {
    const params = new URLSearchParams(window.location.search);
    document.getElementById("game").value = params.get("game") === "ue-classic" ? "ue-classic" : "vanilla";
    document.getElementById("scp005").value = ["1", "2", "3"].includes(params.get("scp005")) ? params.get("scp005") : "1";
    updateGame();
    if (params.has("prompt")) {
        document.getElementById("prompt").value = params.get("prompt");
        createMap();
    } else if (params.has("seed")) {
        document.getElementById("seed").value = params.get("seed");
        createMap();
    }
}

function updateGame() {
    const ultimate = document.getElementById("game").value === "ue-classic";
    if (currentMap && (currentMap.game === "ue-classic") !== ultimate) nullMap();
    document.getElementById("ue-options").hidden = !ultimate;
    document.getElementById("seed-container").hidden = ultimate;
    document.getElementById("seed-hint").hidden = ultimate;
    document.getElementById("prompt-label").textContent = ultimate ? "UE Classic game seed" : "Vanilla game seed";
    document.getElementById("game-title").textContent = ultimate ? "SCP: CB UE Classic v5.5.4.1 map" : "SCP: Containment Breach v1.3.11 map";
}

loadFromQuery();
