import trigger from './trigger-core.js';
import TriggerExtractor from './trigger-extractor.js';

window.addEventListener("load", function (event)
{
	let selector = window.location.hash;
	if (!selector)
		return;

	let element = document.querySelector(selector);
	if (!element)
		return;

	if (TriggerExtractor.isNative(element))
		return element.click();

	let target = TriggerExtractor.target(element);
	if (target && target.startsWith("@"))
		trigger(event, element, element);
});