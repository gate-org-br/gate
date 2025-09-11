const DEFAULT_POSITIONS = ["north", "south", "east", "west",
	"northeast", "southwest", "northwest", "southeast"];
function calc(element, target, gap, position)
{
	const targetRect = target.getBoundingClientRect();
	const elementRect = element.getBoundingClientRect();

	switch (position)
	{
		case "northeast":
			return {x: targetRect.right + gap, y: targetRect.top};
		case "southwest":
			return {x: targetRect.left - elementRect.width - gap, y: targetRect.bottom - elementRect.height};
		case "northwest":
			return {x: targetRect.left - elementRect.width - gap, y: targetRect.top};
		case "southeast":
			return {x: targetRect.right + gap, y: targetRect.bottom - elementRect.height};
		case "north":
			return {x: targetRect.left + (targetRect.width / 2) - (elementRect.width / 2), y: targetRect.top - elementRect.height - gap};
		case "east":
			return {x: targetRect.right + gap, y: targetRect.top + (targetRect.height / 2) - (elementRect.height / 2)};
		case "south":
			return {x: targetRect.left + (targetRect.width / 2) - (elementRect.width / 2), y: targetRect.bottom + gap};
		case "west":
			return {x: targetRect.left - elementRect.width - gap, y: targetRect.top + (targetRect.height / 2) - (elementRect.height / 2)};
		default:
			return {x: targetRect.right + gap, y: targetRect.bottom + gap};
	}
}

function fits(element, target, gap, position) {
	const targetRect = target.getBoundingClientRect();
	const elementRect = element.getBoundingClientRect();
	const viewport = {width: window.innerWidth, height: window.innerHeight};

	const location = calc(element, target, gap, position);

	const ok = location.x >= 0 &&
		location.y >= 0 &&
		(location.x + elementRect.width) <= viewport.width &&
		(location.y + elementRect.height) <= viewport.height;

	return ok ? location : null;
}

export default function anchor(element, target, gap = 0, ...positions) {
	if (!positions.length)
		positions = DEFAULT_POSITIONS;

	return new Promise((resolve, reject) => {
		requestAnimationFrame(() => {
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