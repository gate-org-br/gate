import DOM from './dom.js';
import DataURL from './data-url.js';

window.addEventListener("@dataset", function pick(event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {action, parameters: [selector]} = event.detail;

	let table = DOM.navigate(trigger, selector || action)
		.orElseThrow(`${selector || action} is not a valid selector`);

	let dataset = Array.from(table.querySelectorAll("thead > tr, tbody > tr"))
		.map(tr => Array.from(tr.children).map(e => e.getAttribute("data-value") || e.innerText));

	event.success(path, new DataURL("application/json", JSON.stringify(dataset)));
});
