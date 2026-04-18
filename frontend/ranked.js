let loading = false;

document.getElementById("seed").addEventListener("keyup", e => {
    if (e.key == "Enter")
        findMap();
});
document.getElementById("prompt").addEventListener("keyup", e => {
    if (e.key == "Enter")
        findMap();
});

function findMap() {
    if (loading)
        return;

    loading = true;
    document.getElementById("loading-gif").hidden = false;

    document.getElementById("map-info").hidden = true;
    document.getElementById("ranked-info").hidden = true;

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
	xhr.onload = readMapResponse;
	xhr.ontimeout = unableRequestMap;
	xhr.addEventListener("error", unableRequestMap);
	xhr.timeout = 6000;
	xhr.open('GET', '/rank-seed' + query, true);
	xhr.send();
}

function unableRequestMap() {
	let errortext = document.getElementById("error-text");
    errortext.hidden = false;
    errortext.innerHTML = "<span>Service seems to be unavailable. DM </span><span id=\"discord-id\" class=\"clickable\" onclick=\"copyContent('discord-id')\">@Sooslick</span><span> in Discord.</span>";
    loading = false;
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
    		let response = JSON.parse(this.responseText);
    		updateFields(response);
    	} catch (jumpscare) {
    		let errtxt = document.getElementById("error-text");
            errtxt.hidden = false;
            errtxt.innerHTML = "<span>Something on this page is broken, DM </span><span id=\"discord-id\" class=\"clickable\" onclick=\"copyContent('discord-id')\">@Sooslick</span><span>in Discord.</span>";
            return;
    	}
    }
}

function updateFields(response) {
    document.getElementById("map-info").hidden = false;
    document.getElementById("ranked-info").hidden = false;

    // fill meta
	document.getElementById("seedString").innerHTML = response.seedString
	document.getElementById("seedValue").innerHTML = response.seedValue
	document.getElementById("loadingScreen").innerHTML = response.loadingScreen

	document.getElementById("estimate").innerHTML = roundEstimate(response.estimate);
	document.getElementById("inbounds").innerHTML = response.routeLength + " rooms";
	document.getElementById("lcz").innerHTML = response.lcz + " rooms";
	document.getElementById("map-link").setAttribute("href", "index.html?seed=" + response.seedValue);
}

function roundEstimate(seconds) {
	let round = Math.ceil(seconds / 15) * 15;
	return Math.floor(round / 60) + "m " + pad(round % 60) + "s";
}

function pad(num) {
    if (num < 10)
    	return "0" + num.toString();
    else
    	return num.toString();
}