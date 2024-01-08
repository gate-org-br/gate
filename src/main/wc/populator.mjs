import './trigger.js';
import DOM from './dom.js';
import GBlock from './g-block.js';
import EventHandler from './event-handler.js';
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
	let {method, action, form} = event.detail;
	let trigger = event.composedPath()[0] || event.target;
	let [selector, value = "value", label = "label"] = event.detail.parameters;

	let element = DOM.navigate(trigger, selector)
		.orElseThrow("No populate target element specified");

	event.target.dispatchEvent(new TriggerStartupEvent(event));
	if (event.detail.action)
	{
		let path = event.composedPath();
		fetch(RequestBuilder.build(method, action, form))
			.then(ResponseHandler.json)
			.then(options => new Populator(options).populate(element, value, label))
			.then(() => EventHandler.dispatch(path, new TriggerSuccessEvent(event)))
			.catch(error => EventHandler.dispatch(path, new TriggerFailureEvent(event, error)))
			.finally(() => EventHandler.dispatch(path, new TriggerResolveEvent(event)));
	} else
	{
		element.innerHTML = "";
		event.target.dispatchEvent(new TriggerSuccessEvent(event));
		event.target.dispatchEvent(new TriggerResolveEvent(event));
	}
});
