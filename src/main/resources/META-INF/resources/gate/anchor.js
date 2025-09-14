const DEFAULT_POSITIONS = ["north", "south", "east", "west",
	"northeast", "southwest", "northwest", "southeast"];
function calc(element, target, gap, position)
{
	switch (position)
	{
		case "northeast":
			return {x: target.right + gap, y: target.top};
		case "southwest":
			return {x: target.left - element.width - gap, y: target.bottom - element.height};
		case "northwest":
			return {x: target.left - element.width - gap, y: target.top};
		case "southeast":
			return {x: target.right + gap, y: target.bottom - element.height};
		case "north":
			return {x: target.left + (target.width / 2) - (element.width / 2), y: target.top - element.height - gap};
		case "east":
			return {x: target.right + gap, y: target.top + (target.height / 2) - (element.height / 2)};
		case "south":
			return {x: target.left + (target.width / 2) - (element.width / 2), y: target.bottom + gap};
		case "west":
			return {x: target.left - element.width - gap, y: target.top + (target.height / 2) - (element.height / 2)};
		default:
			return {x: target.right + gap, y: target.bottom + gap};
	}
}

function fits(element, target, gap, position) {
	const viewport = {width: window.innerWidth, height: window.innerHeight};

	const location = calc(element, target, gap, position);

	const ok = location.x >= 0 &&
		location.y >= 0 &&
		(location.x + element.width) <= viewport.width &&
		(location.y + element.height) <= viewport.height;

	return ok ? location : null;
}

export default function anchor(element, target, gap = 0, ...positions) {
	if (!positions.length)
		positions = DEFAULT_POSITIONS;
	return new Promise((resolve, reject) => {
		requestAnimationFrame(() => {

			element = element.getBoundingClientRect();
			target = target?.getBoundingClientRect?.() ?? {
				x: target?.x || 0,
				y: target?.y || 0,
				left: target?.x || 0,
				top: target?.y || 0,
				right: target?.x || 0,
				bottom: target?.y || 0,
				width: 0,
				height: 0
			};

			for (const position of positions)
			{
				const location = fits(element, target, gap, position);
				if (location)
					return resolve({position, location});
			}
			reject(new Error("No available space"));
		});
	});
}