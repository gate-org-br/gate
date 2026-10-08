import trigger from './trigger-core.js';
import TriggerExtractor from './trigger-extractor.js';

window.addEventListener("change", function (event)
{
	let element = event.target || event.composedPath()[0];
	if (TriggerExtractor.trigger(element) === "change")
		trigger(event, element, element);
});

window.addEventListener("input", function (event)
{
	let element = event.target || event.composedPath()[0];
	if (TriggerExtractor.trigger(element) === "input")
		trigger(event, element, element);
});
