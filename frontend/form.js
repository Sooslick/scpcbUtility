function updatePrompt() {
    let promptInput = document.getElementById("prompt");
    let prompt = promptInput.value.toString();
    for (let i = 0; i < prompt.length; i++) {
        let c = prompt.charCodeAt(i);
        if (c < 32 || c > 127) {
            prompt = prompt.slice(0, i) + prompt.slice(i+1);
            i--;
        }
    }
    if (prompt.length > 15)
        prompt = prompt.slice(0, 15);
    promptInput.value = prompt;
    if (prompt.length > 0)
        document.getElementById("seed").value = generateSeedNumber(prompt);
    else
        document.getElementById("seed").value = "";
}

function updateSeed() {
    document.getElementById("prompt").value = "";
    let seedInput = document.getElementById("seed");
    let seedString = seedInput.value.toString();
    for (let i = 0; i < seedString.length; i++) {
        let c = seedString.charCodeAt(i);
        if (c < 48 || c > 57) {
            seedString = seedString.slice(0, i) + seedString.slice(i+1);
            i--;
        }
    }
    if (seedString.length > 0) {
        seedInput.value = seedString;
        let seed = parseInt(seedString);
        if (seed < 1)
            seedInput.value = 1;
        else if (seed > 2147483647)
            seedInput.value = 2147483647;
    } else
        seedInput.value = "";
}

function generateSeedNumber(seed) {
    let tmp = 0;
    let shift = 0;
    for (let i = 0; i < seed.length; i++) {
        let c = seed.charCodeAt(i);
        tmp = tmp ^ (c << shift);
        shift = (shift + 1) % 24;
    }
    return tmp;
}