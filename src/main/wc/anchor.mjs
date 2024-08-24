const DEFAULT_POSITIONS = ["north", "south", "east", "west",
	"northeast", "southwest", "northwest", "southeast"];

function isVisible(element)
{
	let rect = element.getBoundingClientRect();
	return rect.top >= 0 && rect.left >= 0
		&& rect.bottom <= window.innerHeight && rect.right <= window.innerWidth;
}

function calc(element, target, gap, position)
{
	target = target.getBoundingClientRect();
	element = element.getBoundingClientRect();

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

export default function anchor(element, target, gap = 0, ...positions)
{
	if (!positions.length)
		positions = DEFAULT_POSITIONS;

	return new Promise(resolve =>
	{
		for (let i = 0; i < positions.length; i++)
		{
			const position = positions[i];
			let point = calc(element, target, gap, position);
			element.style.top = `${point.y}px`;
			element.style.left = `${point.x}px`;
			if (isVisible(element))
				return resolve(position);
		}

		const position = positions[0] || "north";
		let point = calc(element, target, gap, position);
		element.style.top = `${point.y}px`;
		element.style.left = `${point.x}px`;
		return resolve(position);
	});
}