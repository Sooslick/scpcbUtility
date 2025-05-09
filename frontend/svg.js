function svgCreate(roomtype, rotation, doorleft, doorbottom) {
	return svgWrap(
		svgWrapRoom(svgCreateRoom(roomtype), rotation) +
		svgDoorLeft(doorleft) +
		svgDoorBottom(doorbottom)
	);
}

function svgWrap(svg) {
	return "<svg width='34' height='34'>" + svg + "</svg>";
}

function svgWrapRoom(svg, rotation) {
	return "<g transform='rotate(" + rotation + " 18 16) translate(2,0)'>" + svg + "</g>";
}

function svgDoorLeft(state) {
	switch(state) {
		case 0: return "<rect width='2' height='16' x='0' y='8' fill='#dadada' />"
		case 1: return "<rect width='2' height='10' x='0' y='0' fill='#888888' /><rect width='2' height='10' x='0' y='22' fill='#888888' />"
		case 2: return "<rect width='2' height='10' x='0' y='0' fill='#ff4444' /><rect width='2' height='10' x='0' y='22' fill='#ff4444' />"
		default: return "";
	}
}

function svgDoorBottom(state) {
	switch(state) {
		case 0: return "<rect width='16' height='2' x='10' y='32' fill='#dadada' />"
		case 1: return "<rect width='10' height='2' x='2' y='32' fill='#888888' /><rect width='10' height='2' x='24' y='32' fill='#888888' />"
		case 2: return "<rect width='10' height='2' x='2' y='32' fill='#ff4444' /><rect width='10' height='2' x='24' y='32' fill='#ff4444' />"
		default: return "";
	}
}

function svgCreateRoom(roomtype) {
	switch(roomtype) {
    	case "room1": return svgRoom1();
    	case "room1K": return svgRoom1K();
    	case "room1PD": return svgRoom1PD();
    	case "room1START": return svgRoom1Start();
    	case "room2": return svgRoom2();
    	case "room2C": return svgRoom2C();
    	case "room2CLOSETS": return svgRoom2Closets();
    	case "room2CODE": return svgRoom2Code();
    	case "room2E": return svgRoom2E();
    	case "room2EXIT": return svgRoom2Exit();
    	case "room2L": return svgRoom2L();
    	case "room2MASK": return svgRoom2Mask();
    	case "room2SCP": return svgRoom2Scp();
    	case "room2SL": return svgRoom2Sl();
    	case "room2T": return svgRoom2T();
    	case "room2TRAP": return svgRoom2Trap();
    	case "room2WC": return svgRoom2Wc();
    	case "room3": return svgRoom3();
    	case "room3L": return svgRoom3L();
    	case "room3PD": return svgRoom3PD();
    	case "room3R": return svgRoom3R();
    	case "room4": return svgRoom4();
    	case "room4PD": return svgRoom4PD();
    }
}

function svgRoom1() {
	return `
<rect width='20' height='26' x='6' y='6' fill='black' />
<rect width='2' height='26' x='6' y='6' fill='white' />
<rect width='2' height='26' x='24' y='6' fill='white' />
<rect width='20' height='2' x='6' y='6' fill='white' />
<rect width='4' height='2' x='8' y='30' fill='white' />
<rect width='4' height='2' x='20' y='30' fill='white' />
	`;
}

function svgRoom1K() {
	return svgRoom1() +
		"<rect width='1' height='26' x='9' y='6' fill='white' />" +
		"<rect width='1' height='26' x='22' y='6' fill='white' />";
}

function svgRoom1PD() {
	return svgRoom1() + '<circle cx="16" cy="16" r="3" stroke="white" stroke-width="1.5" fill="none" />';
}

function svgRoom1Start() {
	return svgRoom1() +
		'<line x1="17" y1="9" x2="23" y2="15" stroke="white" />' +
		'<line x1="23" y1="9" x2="17" y2="15" stroke="white" />';
}

function svgRoom2() {
	return `
<rect width='20' height='32' x='6' y='0' fill='black' />
<rect width='2' height='32' x='6' y='0' fill='white' />
<rect width='2' height='32' x='24' y='0' fill='white' />
<rect width='4' height='2' x='8' y='0' fill='white' />
<rect width='4' height='2' x='20' y='0' fill='white' />
<rect width='4' height='2' x='8' y='30' fill='white' />
<rect width='4' height='2' x='20' y='30' fill='white' />
	`;
}

function svgRoom2C() {
	return `
<rect width='26' height='20' x='6' y='6' fill='black' />
<rect width='20' height='26' x='6' y='6' fill='black' />
<rect width='2' height='26' x='6' y='6' fill='white' />
<rect width='26' height='2' x='6' y='6' fill='white' />
<rect width='2' height='4' x='30' y='8' fill='white' />
<rect width='2' height='4' x='30' y='20' fill='white' />
<rect width='8' height='2' x='24' y='24' fill='white' />
<rect width='2' height='6' x='24' y='26' fill='white' />
<rect width='4' height='2' x='8' y='30' fill='white' />
<rect width='4' height='2' x='20' y='30' fill='white' />
	`;
}

function svgRoom2Closets() {
	return svgRoom2() + "<rect width='1' height='32' x='9' y='0' fill='white' />";
}

function svgRoom2Code() {
	return svgRoom2() +
		"<rect width='6' height='2' x='5' y='5' fill='white' />" +
		"<rect width='6' height='2' x='5' y='26' fill='white' />" +
		"<rect width='6' height='2' x='21' y='16' fill='white' />";
}

function svgRoom2E() {
	return svgRoom2L() +
    	"<rect width='6' height='2' x='22' y='5' fill='white' />" +
    	"<rect width='6' height='2' x='5' y='27' fill='white' />";
}

function svgRoom2Exit() {
	return svgRoom2() +
    	'<line x1="13" y1="13" x2="19" y2="19" stroke="white" />' +
    	'<line x1="19" y1="13" x2="13" y2="19" stroke="white" />';
}

function svgRoom2L() {
	return svgRoom2() + '<line x1="6" y1="16" x2="26" y2="16" stroke="white" stroke-width="2" />';
}

function svgRoom2Mask() {
	return svgRoom2() + "<rect width='6' height='2' x='5' y='27' fill='white' />";
}

function svgRoom2Scp() {
	return svgRoom2() +
		'<line x1="5" y1="16" x2="10" y2="16" stroke="white" stroke-width="2" />' +
		'<line x1="22" y1="16" x2="27" y2="16" stroke="white" stroke-width="2" />';
}

function svgRoom2Sl() {
	return svgRoom2() + "<rect width='6' height='2' x='22' y='26' fill='white' />";
}

function svgRoom2T() {
	return svgRoom2() +
		'<line x1="6" y1="16" x2="17" y2="13.5" stroke="white" stroke-width="1.5" />' +
		'<line x1="17" y1="13.5" x2="15" y2="18.5" stroke="white" stroke-width="1.5" />' +
		'<line x1="15" y1="18.5" x2="26" y2="16" stroke="white" stroke-width="1.5" />';
}

function svgRoom2Trap() {
	return `
<rect width='20' height='32' x='6' y='0' fill='black' />
<rect width='10' height='10' x='0' y='0' fill='black' />
<rect width='12' height='2' x='0' y='0' fill='white' />
<rect width='2' height='10' x='0' y='0' fill='white' />
<rect width='6' height='2' x='0' y='10' fill='white' />
<rect width='2' height='22' x='6' y='10' fill='white' />
<rect width='4' height='2' x='20' y='0' fill='white' />
<rect width='4' height='2' x='8' y='30' fill='white' />
<rect width='4' height='2' x='20' y='30' fill='white' />
<rect width='2' height='32' x='24' y='0' fill='white' />
	`;
}

function svgRoom2Wc() {
	return svgRoom2() + '<line x1="22" y1="16" x2="28" y2="16" stroke="white" stroke-width="2" />';
}

function svgRoom3() {
	return `
<rect width='32' height='20' x='0' y='6' fill='black' />
<rect width='20' height='26' x='6' y='6' fill='black' />
<rect width='32' height='2' x='0' y='6' fill='white' />
<rect width='2' height='4' x='0' y='8' fill='white' />
<rect width='2' height='4' x='30' y='8' fill='white' />
<rect width='2' height='4' x='0' y='20' fill='white' />
<rect width='2' height='4' x='30' y='20' fill='white' />
<rect width='8' height='2' x='0' y='24' fill='white' />
<rect width='8' height='2' x='24' y='24' fill='white' />
<rect width='2' height='6' x='6' y='26' fill='white' />
<rect width='2' height='6' x='24' y='26' fill='white' />
<rect width='4' height='2' x='8' y='30' fill='white' />
<rect width='4' height='2' x='20' y='30' fill='white' />
	`;
}

function svgRoom3L() {
	return svgRoom3() + '<line x1="8" y1="24" x2="24" y2="8" stroke="white" />';
}

function svgRoom3PD() {
	return svgRoom3() + '<circle cx="16" cy="16" r="4" stroke="white" stroke-width="1.5" fill="none" />';
}

function svgRoom3R() {
	return svgRoom3() + '<line x1="8" y1="8" x2="24" y2="24" stroke="white" />';
	return svgRoom3() + '<line x1="8" y1="8" x2="24" y2="24" stroke="white" />';
}

function svgRoom4() {
	return `
<rect width='32' height='20' x='0' y='6' fill='black' />
<rect width='20' height='32' x='6' y='0' fill='black' />
<rect width='2' height='4' x='0' y='8' fill='white' />
<rect width='2' height='4' x='30' y='8' fill='white' />
<rect width='8' height='2' x='0' y='6' fill='white' />
<rect width='8' height='2' x='24' y='6' fill='white' />
<rect width='2' height='6' x='6' y='0' fill='white' />
<rect width='2' height='6' x='24' y='0' fill='white' />
<rect width='4' height='2' x='8' y='0' fill='white' />
<rect width='4' height='2' x='20' y='0' fill='white' />
<rect width='2' height='4' x='0' y='20' fill='white' />
<rect width='2' height='4' x='30' y='20' fill='white' />
<rect width='8' height='2' x='0' y='24' fill='white' />
<rect width='8' height='2' x='24' y='24' fill='white' />
<rect width='2' height='6' x='6' y='26' fill='white' />
<rect width='2' height='6' x='24' y='26' fill='white' />
<rect width='4' height='2' x='8' y='30' fill='white' />
<rect width='4' height='2' x='20' y='30' fill='white' />
	`;
}

function svgRoom4PD() {
	return svgRoom4() + '<circle cx="16" cy="16" r="4" stroke="white" stroke-width="1.5" fill="none" />';
}