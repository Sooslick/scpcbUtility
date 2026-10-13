// API integration and frontend behavior checks, using Node's built-in test/assert/VM APIs.
// Run the web server first, then: node tools/verify-preview.cjs
const assert = require('node:assert/strict');
const fs = require('node:fs');
const vm = require('node:vm');
const { setTimeout: pause } = require('node:timers/promises');
const base = 'http://localhost:1499/';

async function request(query, status = 200) {
    // Respect the server's default six-request-per-second limit.
    await pause(200);
    const response = await fetch(base + 'api/map?' + query);
    assert.equal(response.status, status, query + ': ' + await response.clone().text());
    return status === 200 ? response.json() : response.text();
}

function frontend(search = '') {
    const elements = new Map();
    class Element {
        constructor() { this.value = ''; this.hidden = false; this.style = {}; this.childNodes = []; this.textContent = ''; this.classList = { add() {}, remove() {} }; }
        setAttribute(name, value) { if (name === 'id') elements.set(value, this); }
        appendChild(child) { this.childNodes.push(child); }
        addEventListener() {}
        set innerHTML(value) { this.html = value; this.childNodes = value ? [{}] : []; }
        get innerHTML() { return this.html || ''; }
    }
    for (const match of fs.readFileSync('frontend/index.html', 'utf8').matchAll(/id="([^"]+)"/g)) elements.set(match[1], new Element());
    elements.get('game').value = 'vanilla';
    elements.get('scp005').value = '1';
    const pending = [];
    let popstate;
    const location = { origin: 'http://localhost:1499', pathname: '/index.html', search };
    const context = vm.createContext({
        document: { getElementById: id => elements.get(id) || null, createElement: () => new Element() },
        window: { location, scroll() {} }, URLSearchParams,
        history: { pushState(state, title, url) { location.search = new URL(url).search; } },
        navigator: { clipboard: { writeText() {} } },
        setTimeout() {}, clearTimeout() {},
        addEventListener(type, handler) { if (type === 'popstate') popstate = handler; },
        XMLHttpRequest: class {
            open(method, url) { this.url = url; }
            addEventListener() {}
            send() { pending.push(this); }
        }
    });
    for (const file of ['svg.js', 'ui.js', 'form.js']) vm.runInContext(fs.readFileSync('frontend/' + file, 'utf8'), context, { filename: file });
    return { elements, pending, run: code => vm.runInContext(code, context), pop: state => popstate({ state }) };
}

async function main() {
    const ue = await request('game=ue-classic&scp005=1&prompt=446456054');
    assert.equal(ue.game, 'ue-classic');
    assert.equal(ue.seedString, '446456054');
    assert.equal(ue.seedValue, 9780);
    assert.equal(ue.scp005, 1);
    assert(ue.rooms.some(r => r.name === 'room173'));
    assert(ue.rooms.some(r => r.name === 'room914'));
    assert(ue.rooms.some(r => r.name === 'room009' && r.shape === 4));
    assert(ue.rooms.every(r => r.description && !r.en && !r.ek));
    assert(!('state106' in ue) && !('loadingScreen' in ue));
    const third = await request('game=ue-classic&scp005=3&prompt=446456054');
    assert.notDeepEqual(ue.rooms, third.rooms);
    assert.deepEqual((await request('game=ue-classic&scp005=1&prompt=446456054')).rooms, ue.rooms);
    const vanilla = await request('prompt=446456054');
    assert(vanilla.rooms.some(r => r.name === 'start'));
    assert(vanilla.rooms.some(r => r.name === '914'));
    assert('state106' in vanilla);
    const encoded = await request('game=ue-classic&prompt=' + encodeURIComponent('a=b&c+d "\\'));
    assert.equal(encoded.seedString, 'a=b&c+d "\\');
    const random = await request('random&game=ue-classic&scp005=2');
    assert.equal(random.game, 'ue-classic');
    assert.equal(random.scp005, 2);
    assert(random.seedString.length >= 1 && random.seedString.length <= 15);
    for (const query of ['game=nope&prompt=x', 'game=ue-classic&seed=123', 'game=ue-classic&scp005=4&prompt=x',
            'game=ue-classic&scp005&prompt=x', 'prompt=', 'prompt', 'prompt=abcdefghijklmnop', 'prompt=%01', 'seed=oops', 'seed=2147483648']) {
        await request(query, 400);
    }
    const page = frontend('?scp005=3&prompt=446456054&game=ue-classic');
    assert.equal(page.pending.length, 1);
    assert(page.pending[0].url.includes('game=ue-classic&scp005=3'));
    page.pending[0].status = 200;
    page.pending[0].responseText = JSON.stringify(third);
    page.pending[0].onload();
    assert.equal(page.elements.get('map').hidden, false);
    assert.equal(page.elements.get('seed-container').hidden, true);
    assert.equal(page.elements.get('ue-options').hidden, false);
    assert.equal(page.elements.get('room-event-container').hidden, true);
    assert.equal(page.elements.get('loadingScreen-container').hidden, true);
    assert.equal(page.elements.get('room-description-container').hidden, false);
    assert.equal(page.run('getRoomImage({name:"room009", shape:4})'), 'room3');
    assert.equal(page.run('getRoomImage({name:"room173", shape:1})'), 'room1START');
    const room = third.rooms.find(r => r.name === 'room914');
    page.run(`getRoomInfo(${room.x}, ${room.y})`);
    assert.equal(page.elements.get('room-description').textContent, room.description);
    const shared = new URL(page.run('link'));
    assert.equal(shared.searchParams.get('game'), 'ue-classic');
    assert.equal(shared.searchParams.get('scp005'), '3');
    assert.equal(shared.searchParams.get('prompt'), '446456054');
    page.pop(vanilla);
    assert.equal(page.elements.get('game').value, 'vanilla');
    assert.equal(page.elements.get('room-event-container').hidden, false);
    assert.equal(page.elements.get('ue-options').hidden, true);
    assert.equal(page.elements.get('room-description-container').hidden, true);
    page.pop(null);
    assert.equal(page.elements.get('map').hidden, true);
    console.log('API and frontend verification passed: game selection, UE rooms, SCP-005 variants, repeatability, encoded seeds, random seeds, invalid inputs, details, share links and browser history');
}
main().catch(error => { console.error(error); process.exitCode = 1; });
