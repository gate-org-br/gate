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

	return new Promise((resolve, reject) =>
	{
		const top = element.style.top;
		const left = element.style.left;

		for (let i = 0; i < positions.length; i++)
		{
			const position = positions[i];
			let location = calc(element, target, gap, position);
			element.style.top = `${location.y}px`;
			element.style.left = `${location.x}px`;
			if (isVisible(element))
			{
				element.style.top = top;
				element.style.left = left;
				return resolve({position, location});
			}
		}

		element.style.top = top;
		element.style.left = left;
		reject(new Error("No position"));
	});
}