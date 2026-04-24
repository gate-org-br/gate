import trigger from './trigger-core.js';

window.addEventListener("connected", function (event)
{
	let element = event.composedPath()[0] || event.target;

	let type = element.getAttribute("data-trigger") || "";

	if (type === "connected")
		trigger(event, element);
	else if (type.match(/^every\([0-9]+\)$/))
		setInterval(() => trigger(event, element), Number(type.slice(6, -1)) * 1000);
	else if (type === "drag-and-drop")
	{
		Array.from(element.children).forEach(e =>
		{
			if (e.draggable)
				e.addEventListener("dragstart", dragstart =>
				{
					const value = e.value
						|| e.getAttribute("value")
						|| e.getAttribute("data-value")
						|| "";
					dragstart.dataTransfer.setData("text/plain", value);
				});

			e.addEventListener("drop", function (drop)
			{
				drop.stopPropagation();
				let source = drop.dataTransfer.getData("text/plain");
				let target = e.value
					|| e.getAttribute("value")
					|| e.getAttribute("data-value")
					|| element.value
					|| element.getAttribute("value")
					|| element.getAttribute("data-value")
					|| "";
				trigger(drop, element, {source, target});
			});

			e.addEventListener("dragover", dragover => dragover.preventDefault());
		});

		element.addEventListener("dragover", dragover => dragover.preventDefault());
		element.addEventListener("drop", function (drop)
		{
			drop.stopPropagation();
			let source = drop.dataTransfer.getData("text/plain");
			let target = element.value
				|| element.getAttribute("value")
				|| element.getAttribute("data-value")
				|| "";
			trigger(drop, element, {source, target});
		});
	}
});
