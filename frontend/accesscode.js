function updatePj() {
	document.getElementById("pd").value = "";
    let promptInput = document.getElementById("pj");
    let prompt = promptInput.value.toString();
    for (let i = 0; i < prompt.length; i++) {
        let c = prompt.charCodeAt(i);
        if (c < 48 || c > 57) {
            prompt = prompt.slice(0, i) + prompt.slice(i+1);
            i--;
        }
    }
    if (prompt.length > 4)
        prompt = prompt.slice(0, 4);
    promptInput.value = prompt;
    if (prompt.length == 4)
        document.getElementById("pd").value = generateMaynard(prompt);
    else
        document.getElementById("pd").value = "";
}

function updatePd() {
    document.getElementById("pj").value = "";
	let promptInput = document.getElementById("pd");
    let prompt = promptInput.value.toString();
    for (let i = 0; i < prompt.length; i++) {
        let c = prompt.charCodeAt(i);
        if (c < 48 || c > 57) {
            prompt = prompt.slice(0, i) + prompt.slice(i+1);
            i--;
        }
    }
    if (prompt.length > 4)
        prompt = prompt.slice(0, 4);
    promptInput.value = prompt;
    if (prompt.length == 4)
        document.getElementById("pj").value = generatePj(prompt);
    else
        document.getElementById("pj").value = "";
}

function generatePj(maynardCode) {
	let tempval = parseInt(maynardCode) * 3 % 10000;
	if (tempval < 1000)
		return tempval + 1000;
	else
		return tempval;
}

function generateMaynard(pjcode) {
	let code = parseInt(pjcode);
	let remainder = code % 3;
	if (remainder > 0)
		remainder = 3 - remainder;
	let maynard1 = (code + remainder * 10000) / 3;
	let maynard2 = (maynard1 + 3000) % 10000;

	console.log("code: ", code);
	let maynard1ok = (generatePj(maynard1) == code) && !containsZero(maynard1) && maynard1 > 999;
	console.log("maynard1: ", maynard1, " pj: ", generatePj(maynard1), " equals: ", (generatePj(maynard1) == code), " contains 0: ", containsZero(maynard1), " ok: ", maynard1ok);
	let maynard2ok = (generatePj(maynard2) == code) && !containsZero(maynard2) && maynard2 > 999;
	console.log("maynard2: ", maynard2, " pj: ", generatePj(maynard2), " equals: ", (generatePj(maynard2) == code), " contains 0: ", containsZero(maynard2), " ok: ", maynard2ok);

	if (maynard1ok == maynard2ok)
		return pad(maynard1) + " or " + pad(maynard2);
	else if (maynard1ok)
		return pad(maynard1);
	else
		return pad(maynard2);
}

function containsZero(number) {
    if (number == 0)
        return true;
    while (number != 0) {
        if (number % 10 == 0)
            return true;
        number = Math.floor(number / 10);
    }
    return false;
}

function pad(num) {
    num = num.toString();
    while (num.length < 4)
    	num = "0" + num;
    return num;
}