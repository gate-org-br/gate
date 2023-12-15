import './trigger.js';
import GBlock from './g-block.js';
import GMessageDialog from './g-message-dialog.js';
import ResponseHandler from './response-handler.js';

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
	let element = event.composedPath()[0];
	let parameters = event.detail.parameters;

	if (parameters[0] && parameters[0] !== 'this')
	{
		element = element.getRootNode().getElementById(parameters[0]);
		if (!element)
			throw new Error(`No element with id ${parameters[0]} found on page`);
	}

	let header = {};
	if (event.detail.method !== "get")
	{
		header.method = event.detail.method;
		header.body = new FormData(event.detail.element);
		header.headers = {'Content-Type': 'application/x-www-form-urlencoded'};
	}

	element.style.cursor = "wait";
	element.style.pointerEvents = "none";
	GBlock.show(event.detail.parameters[1] || "...");

	fetch(event.detail.action, header)
		.then(ResponseHandler.json)
		.then(options => new Populator(options).populate(element, parameters[1], parameters[2]))
		.catch(GMessageDialog.error)
		.finally(() =>
		{
			GBlock.hide();
			element.style.cursor = "";
			element.style.pointerEvents = "";
		});
});
