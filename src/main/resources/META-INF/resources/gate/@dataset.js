import DOM from './dom.js';
import DataURL from './data-url.js';

window.addEventListener("@dataset", function pick(event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {parameters: [selector, ...columns]} = event.detail;

	let table = DOM.navigate(trigger, selector)
		.orElseThrow(`${selector} is not a valid selector`);

	columns = new Set(columns.map(e => parseInt(e, 10))
		.filter(e => Number.isInteger(e) && e >= 0));

	let dataset = Array.from(table.querySelectorAll("thead > tr, tbody > tr"))
		.map(tr => Array.from(tr.children)
				.filter((e, index) => !columns.size
						|| columns.has(index))
				.map(e => e.getAttribute("data-value") ?? e.textContent));

	event.success(path, DataURL.ofJSON(dataset));
});
