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

function fits(element, target, gap, position)
{
	const viewport = {width: window.innerWidth, height: window.innerHeight};

	const location = calc(element, target, gap, position);

	const ok = location.x >= 0 &&
		location.y >= 0 &&
		(location.x + element.width) <= viewport.width &&
		(location.y + element.height) <= viewport.height;

	return ok ? location : null;
}

function intersects(element, location, exclusion)
{
	const area = {
		left: location.x,
		top: location.y,
		right: location.x + element.width,
		bottom: location.y + element.height
	};

	return exclusion.left <= area.right &&
		exclusion.right >= area.left &&
		exclusion.top <= area.bottom &&
		exclusion.bottom >= area.top;
}

function rect(reference)
{
	if (reference?.getBoundingClientRect)
		return reference.getBoundingClientRect();

	const x = reference?.x ?? 0;
	const y = reference?.y ?? 0;
	return {
		x,
		y,
		left: x,
		top: y,
		right: x,
		bottom: y,
		width: 0,
		height: 0
	};
}

function positions(value)
{
	if (value)
	{
		if (!Array.isArray(value))
			return [value];

		if (value.length > 0)
			return value;
	}
	return DEFAULT_POSITIONS;
}

export default function anchor(element, reference, options = {})
{
	const gap = options.gap ?? 0;
	const candidates = positions(options.positions);
	return new Promise((resolve, reject) =>
	{
		requestAnimationFrame(() =>
		{

			element = element.getBoundingClientRect();
			reference = rect(reference);
			const exclusion = options.exclusion ? rect(options.exclusion) : null;

			for (const position of candidates)
			{
				const location = fits(element, reference, gap, position);
				if (location && (!exclusion || !intersects(element, location, exclusion)))
					return resolve({position, location});
			}
			reject(new Error("No available space"));
		});
	});
}