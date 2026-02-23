import DOM from './dom.js';
const BLOCK = new Set(["data-trigger", "data-action", "data-target"]);

window.addEventListener("@execute-scripts", function (event)
{
	let path = event.composedPath();
	const parameters = event.detail.parameters?.length ? event.detail.parameters : ["this"];

	parameters.map(selector => DOM.navigate(event, selector)
			.orElseThrow(`${selector} is not a valid selector`))
		.flatMap(element => element.tagName === "SCRIPT" ? [element] :
				Array.from(element.querySelectorAll("script")))
		.forEach(e =>
		{
			const script = document.createElement("script");

			for (const attr of e.attributes)
				if (!BLOCK.has(attr.name.toLowerCase()))
					script.setAttribute(attr.name, attr.value);
			script.textContent = e.textContent;
			e.replaceWith(script);
		});

	event.success(path);
});