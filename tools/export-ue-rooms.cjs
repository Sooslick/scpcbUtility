// Usage: node tools/export-ue-rooms.cjs <CBUE-Classic source root>
// Export the room catalog, drawn-mesh bounds and light counts used by v5.5.4.1.
const fs = require('node:fs');
const path = require('node:path');
const root = process.argv[2];
if (!root) throw new Error('Pass the CBUE-Classic source root');
let section;
const sections = [];
for (const raw of fs.readFileSync(path.join(root, 'Data/rooms.ini'), 'utf8').split(/\r?\n/)) {
    const line = raw.split(';')[0].trim();
    if (line.startsWith('[')) { section = { name: line.slice(1, -1).toLowerCase() }; sections.push(section); }
    else if (line.includes('=') && section) {
        const i = line.indexOf('='); section[line.slice(0, i).trim().toLowerCase()] = line.slice(i + 1).trim();
    }
}
function meshInfo(filename) {
    const b = fs.readFileSync(filename); let p = 0;
    const int = () => { const v = b.readInt32LE(p); p += 4; return v; };
    const float = () => { const v = b.readFloatLE(p); p += 4; return v; };
    const str = () => { const n = int(); const v = b.toString('utf8', p, p + n); p += n; return v; };
    const bounds = [Infinity, Infinity, Infinity, -Infinity, -Infinity, -Infinity];
    const vertex = save => { for (let k = 0; k < 3; k++) { const v = float(); if (save) { bounds[k] = Math.min(bounds[k], v); bounds[k + 3] = Math.max(bounds[k + 3], v); } } };
    const header = str();
    if (!['RoomMesh', 'RoomMesh.HasTriggerBox'].includes(header)) throw new Error('Unexpected mesh header ' + filename);
    let count = int();
    for (let i = 0; i < count; i++) {
        for (let k = 0; k < 2; k++) { if (b[p++] !== 0) str(); }
        const n = int(); for (let j = 0; j < n; j++) { vertex(true); p += 19; }
        const triangles = int(); p += triangles * 12;
    }
    const skipCollision = () => { const count = int(); for (let i = 0; i < count; i++) { const vertices = int(); p += vertices * 12; const triangles = int(); p += triangles * 12; } };
    skipCollision();
    if (header.endsWith('HasTriggerBox')) { const n = int(); for (let i = 0; i < n; i++) { skipCollision(); str(); } }
    let lights = 0;
    count = int();
    for (let i = 0; i < count; i++) {
        const type = str();
        switch (type) {
            case 'screen': case 'playerstart': p += 12; str(); break;
            case 'waypoint': p += 12; break;
            case 'light': case 'spotlight': {
                const coords = [float(), float(), float()];
                if (coords.some(v => v !== 0)) lights++;
                p += 4; str(); p += 4;
                if (type === 'spotlight') { str(); p += 8; }
                break;
            }
            case 'soundemitter': p += 20; break;
            case 'model': { const model = str(); p += model ? 36 : 12; break; }
            default: throw new Error('Unknown point entity ' + type + ' in ' + filename);
        }
    }
    if (p < b.length && str() !== 'EOF') throw new Error('Unexpected mesh trailer in ' + filename);
    if (p !== b.length) throw new Error('Unconsumed mesh data in ' + filename);
    return { bounds: bounds.map(v => Number.isFinite(v) ? v : 0), lights };
}
const shapes = { room1: 1, '1': 1, room2: 2, '2': 2, room2c: 3, '2c': 3, room3: 4, '3': 4, room4: 5, '4': 5 };
const output = ['# UE Classic v5.5.4.1: name, shape, zones, commonness, disableOverlap, lights, minXYZ/maxXYZ, description (tab separated)'];
for (const room of sections.filter(r => r['mesh path'])) {
    const info = meshInfo(path.join(root, room['mesh path'].replaceAll('\\', '/')));
    output.push([room.name, shapes[room.shape.toLowerCase()], [1,2,3,4,5].map(z => room['zone' + z] || 0).join(','),
        Math.max(0, Math.min(100, Number(room.commonness || 0))), room.disableoverlapcheck === 'true' || room.disableoverlapcheck === '1',
        info.lights, ...info.bounds, (room.descr || '').replaceAll('\t', ' ')].join('\t'));
}
fs.mkdirSync('scpcbFiles/ue-classic', { recursive: true });
fs.writeFileSync('scpcbFiles/ue-classic/rooms.tsv', output.join('\n') + '\n');
for (const name of ['forest1h.png', 'forest2h.png', 'forest2Ch.png', 'forest3h.png', 'forest4h.png']) {
    fs.copyFileSync(path.join(root, 'GFX/map/forest', name), path.join('scpcbFiles/ue-classic', name));
}
console.log('Exported ' + (output.length - 1) + ' UE room templates and forest heightmaps');
