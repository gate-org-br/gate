import './trigger.js';
import DOM from './dom.js';
import GBlock from './g-block.js';
import RequestBuilder from './request-builder.js';
import GMessageDialog from './g-message-dialog.js';
import ResponseHandler from './response-handler.js';
import {TriggerStartupEvent, TriggerSuccessEvent, TriggerFailureEvent, TriggerResolveEvent} from './trigger-event.js';

export default function Populator(options)
{
	this.populate = function (element, value = 'value', label = 'label')
	{
		while (element.firstChild)
			element.removeChild(element.firstChild);

		switch (element.tagName.toLowerCase())
		{
			case "select":
				element.value = undefined;

				element.appendChild(document.createElement("option"))
					.setAttribute("value", "");

				for (var i = 0; i < options.length; i++)
				{
					var option = element.appendChild(document.createElement("option"));
					option.innerHTML = options[i][label];
					option.setAttribute('value', options[i][value]);
				}

				break;

			case "datalist":
				for (var i = 0; i < options.length; i++)
				{
					var option = element.appendChild(document.createElement("option"));
					option.innerHTML = options[i][label];
					option.setAttribute('data-value', options[i][value]);
				}

				break;

		}
		return this;
	};
}


window.addEventListener("@populate", function (event)
{
	event.preventDefault();
	let parameters = event.detail.parameters;
	let trigger = event.composedPath()[0] || event.target;

	let element = DOM.navigate(trigger, parameters[0])
		.orElseThrow("No populate target element specified");

	event.target.dispatchEvent(new TriggerStartupEvent(event));
	fetch(RequestBuilder.build(event.detail.method, event.detail.action, event.detail.form))
		.then(ResponseHandler.json)
		.then(options =>
		{
			let value = parameters[1] || "value";
			let label = parameters[2] || "label";
			new Populator(options).populate(element, value, label);
			setTimeout(() => event.target.dispatchEvent(new TriggerSuccessEvent(event)), 0);
		})
		.catch(error => setTimeout(event.target.dispatchEvent(new TriggerFailureEvent(event, error)), 0))
		.finally(setTimeout(() => event.target.dispatchEvent(new TriggerResolveEvent(event)), 0));
});
