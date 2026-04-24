import trigger from './trigger-core.js';
import TriggerExtractor from './trigger-extractor.js';

window.addEventListener("load", event =>
{
	Array.from(document.querySelectorAll('*'))
		.filter(e => TriggerExtractor.trigger(e) === "load")
		.forEach(e => trigger(event, e, e, e.dataset.action));
});
