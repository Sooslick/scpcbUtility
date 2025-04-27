let currentMap = null;
let loading = false;
let exitx = 0;
let exity = 0;
let savedOverlaps = null;
let link = null;

let mapWrapper = document.getElementById("map-table");
for (let i = 0; i <= 18; i++) {
	let tr = document.createElement("tr");
	for (let j = 0; j <= 18; j++) {
		let td = document.createElement("td");
		td.setAttribute("id", "c" + (18 - j) + "-" + i);
		td.setAttribute("onmouseover", "getRoomInfo(" + (18 - j) + "," + i + ")");
		tr.appendChild(td);
	}
	tr.style.background = getLineBackground(i);
	mapWrapper.appendChild(tr);
}
mapWrapper.addEventListener('contextmenu', function(evt) {
	evt.preventDefault();
}, false);

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

    if (prompt.length > 0)
        query+= "?prompt=" + encodeURIComponent(prompt);
    else if (seed != null)
        query+= "?seed=" + encodeURIComponent(seed);
    else
        query+= "?random";

    let xhr = new XMLHttpRequest();
	xhr.onload = buildMap;
	xhr.open('GET', '/map' + query, true);
	xhr.send();
}

function nullMap() {
    currentMap = null;
    exitx = 0;
    exity = 0;

    for (let i = 0; i <= 18; i++)
        for (let j = 0; j <= 18; j++) {
            document.getElementById("c" + (18 - i) + "-" + j).innerHTML = "";
            document.getElementById("t" + i + "-" + j).style.background = null;
        }

    for (let i = 0; i < 10; i++)
        for (let j = 0; j < 10; j++)
            document.getElementById("f" + i + "-" + j).style.background = null;

    document.getElementById("map").hidden = true;
    document.getElementById("map-share").hidden = true;
    document.getElementById("error-text").hidden = true;
    document.getElementById("copied-text").hidden = true;
    document.getElementById("map-pd").style.display = 'none';
    document.getElementById("map-forest").style.display = 'none';
    document.getElementById("map-info").hidden = true;
    document.getElementById("room-info").hidden = true;
}

function buildMap() {
    loading = false;
    document.getElementById("loading-gif").hidden = true;
    if (this.status != 200) {
        document.getElementById("error-text").hidden = false;
    }
	if (this.status == 200) {
		currentMap = JSON.parse(this.responseText);

		// show blocks
        document.getElementById("map").hidden = false;
        document.getElementById("map-share").hidden = false;
        document.getElementById("map-pd").style.display = 'inline-block';
        document.getElementById("map-forest").style.display = 'inline-block';
        document.getElementById("map-info").hidden = false;
        document.getElementById("room-info").hidden = false;

        // fill meta
		document.getElementById("seedString").innerHTML = currentMap.seedString
		document.getElementById("seedValue").innerHTML = currentMap.seedValue
		document.getElementById("state106").innerHTML = currentMap.state106
		document.getElementById("statePlayer").innerHTML = currentMap.angle
		document.getElementById("loadingScreen").innerHTML = currentMap.loadingScreen

		// create link
		if (currentMap.seedString)
		    link = "https://sooslick.art/scpcbmap/?prompt=" + encodeURIComponent(currentMap.seedString);
		else
		    link = "https://sooslick.art/scpcbmap/?seed=" + currentMap.seedValue;

		// fill main grid
		currentMap.rooms.forEach(r => {
		    let cellId = "c" + r.x + "-" + r.y;
			let img = document.createElement("img");
			img.setAttribute("src", getRoomImage(r.name, r.en, r.ek));
			img.style.transform = "rotate(" + getRoomRotation(r.name, r.angle) + "deg)";
			let td = document.getElementById(cellId);
			td.appendChild(img);

			if (r.dv != null)
			    td.classList.add("dv" + r.dv);

			if (r.dh != null)
			    td.classList.add("dh" + r.dh);

			if (r.name == "room2tunnel")
			    buildTunnel(r.info)
			else if (r.name == "room860")
			    buildForest(r.info)
			else if (r.name == "tunnel") {
			    if (r.y > exity || (r.y == exity && r.x <= exitx)) {
			        exitx = r.x;
			        exity = r.y;
			    }
			}
		});

		let tunnelRooms = document.getElementById("c" + exitx + "-" + exity).childNodes;
		if (tunnelRooms.length > 0)
		    tunnelRooms[0].setAttribute("src", "img/room2EXIT.png");

		// create annotations
		currentMap.rooms.forEach(r => {
			createAnnotation(r.name, r.x, r.y);
		});

		// scroll into viewport
		setTimeout(function() {
		    window.scroll({top: 185, left: 0, behavior: 'smooth'});
        }, 150);
	}
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
    updateOverlaps(null);   // I can optimize this call but I'm a bit lazy to do this
    currentMap.rooms.forEach(r => {
        if (r.x == x && r.y == y) {
            let en = r.en == null ? "-" : r.en;
            let ek = r.ek == null ? "-" : r.ek;
            if (en == ek) {
		        document.getElementById("room-event").innerHTML = en;
		        document.getElementById("event-hint").hidden = true;
		    } else {
		        document.getElementById("room-event").innerHTML = en + " / " + ek;
		        document.getElementById("event-hint").hidden = false;
		    }
		    document.getElementById("room").innerHTML = r.name;
		    if (r.info == null || r.info.includes("tunnel") || r.info.includes("forest"))
		        document.getElementById("rnd-info").innerHTML = "-"
		    else
		        document.getElementById("rnd-info").innerHTML = r.info;
            updateOverlaps(r.overlaps);
		}
    });
}

function getRoomImage(name, en, ek) {
    switch (name) {
        case "tunnel":
        case "tunnel2": return "img/room2.png";
        case "room2closets": return "img/room2CLOSETS.png";
        case "room012":
        case "room2shaft":
        case "room2scps2": return "img/room2MASK.png";
        case "room2poffices": return "img/room2CODE.png";
        case "room2nuke":
        case "room2elevator": return "img/room2E.png";
        case "room049":
        case "room2tunnel": return "img/room2ELEV.png";
        case "checkpoint1":
        case "checkpoint2":
        case "testroom":
        case "room860":
        case "room2doors":
        case "room2gw":
        case "room2servers": return "img/room2L.png";
        case "room2storage":
        case "room2scps": return "img/room2SCP.png";
        case "medibay":
        case "room1123":
        case "room2testroom2":
        case "room2cafeteria":
        case "room2sl": return "img/room2SL.png";
        case "room2tesla":
        case "room2tesla_hcz":
        case "room2tesla_lcz": return "img/room2T.png";
        case "room2sroom":
        case "room2toilets": return "img/room2WC.png";
        case "room2_4":
        case "room2pit": return "img/room2TRAP.png";
        case "lockroom":
        case "lockroom2":
        case "lockroom3":
        case "room1162": return "img/room2C.png";
        case "room3servers2":
        case "room513":
        case "room966": return "img/room3L.png";
        case "room3gw":
        case "room3storage": return "img/room3R.png";
        case "room205": return "img/room1.png";
        case "room1archive": return "img/room1K.png";
        case "start": return "img/room1START.png";
        case "coffin": return "img/room1PD.png";
    }

    if (en == null)
        en = "";
    if (ek == null)
        ek = "";
    if (name.startsWith("room3") && (en.startsWith("106s") || ek.startsWith("106s")))
        return "img/room3PD.png";
    if (name.startsWith("room4") && (en.startsWith("106s") || ek.startsWith("106s")))
        return "img/room4PD.png";
    if (name.startsWith("end") && (en.startsWith("endroom106") || ek.startsWith("endroom106")))
        return "img/room1PD.png";

    if (name.startsWith("room1"))
        return "img/room1.png";
    else if (name.startsWith("room2c"))
        return "img/room2C.png";
    else if (name.startsWith("room2"))
        return "img/room2.png";
    else if (name.startsWith("room3"))
        return "img/room3.png";
    else if (name.startsWith("room4"))
        return "img/room4.png";
    else
        return "img/room1.png";
}

function getRoomRotation(name, angle) {
    let a = angle % 180 == 0 ? angle - 180 : angle;
    switch (name) {
        case "medibay":
        case "room2cafeteria":
        case "room2scps2":
        case "room2testroom2":
        case "room2closets":
        case "room2shaft":
        case "room2poffices":
        case "room2pit":
        case "room2_4":
        case "room012": return a + 180;
        default: return a;
    }
}

function createAnnotation(name, x, y) {
    let text = null;

    switch (name) {
        case "room2closets": text = "K1"; break;
        case "room2testroom2": text = "K2"; break;
        case "914": text = "914"; break;
        case "room2sl": text = "CAMS"; break;
        case "roompj": text = "372"; break;
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
    document.getElementById("copied-text").hidden = false;
    navigator.clipboard.writeText(link);
}

function loadFromQuery() {
    let params = window.location.search.substr(1);
    if (!params)
        return;

    let parts = params.split("&");
    let kv = parts[0].split("=");
    if (kv.length != 2)
        return;

    if ("seed" == kv[0]) {
        document.getElementById("seed").value = decodeURIComponent(kv[1]);
        createMap();
        return;
    } else if ("prompt" == kv[0]) {
        document.getElementById("prompt").value = decodeURIComponent(kv[1]);
        createMap();
        return;
    }
}

loadFromQuery();