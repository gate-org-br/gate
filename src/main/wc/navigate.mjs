import Optional from './optional.js';
export default function navigate(source, path)
{
	if (source instanceof Event)
		source = source.composedPath()[0] || source.target;

	path = (path || "").trim();

	if (!path)
		return new Optional(source);

	if (!path.startsWith("this"))
		return new Optional(source.getRootNode().querySelector(path)
			|| document.querySelector(path));

	for (let step of path.split(/\.(?=(?:[^"']*["'][^"']*["'])*[^"']*$)/g))
	{
		if (!source)
			return new Optional(source);
		else if (step === "parentNode")
			source = source.parentNode;
		else if (step === "nextElementSibling")
			source = source.nextElementSibling;
		else if (step === "previousElementSibling")
			source = source.previousElementSibling;
		else if (step === "firstElementChild")
			source = source.firstElementChild;
		else if (step === "lastElementChild")
			source = source.lastElementChild;
		else if (step.startsWith("closest(")
			&& step.endsWith(")"))
			source = source.closest(step.slice(8, -1));
		else if (step.startsWith("querySelector(")
			&& step.endsWith(")"))
			source = source.querySelector(step.slice(14, -1));
		else if (step.startsWith("children[")
			&& step.endsWith("]"))
			source = source.children[Number(step.slice(9, -1))];
		else if (step !== "this")
			throw new Error(`${path} is not a valid path`);
	}

	return new Optional(source);

}