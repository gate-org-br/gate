import DataURL from './data-url.js';
import Formatter from './formatter.js';
import RequestBuilder from './request-builder.js';
import GTooltip, {POSITIONS} from './g-tooltip.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@tooltip", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {method, action, form, parameters} = event.detail;

	let position = parameters.filter(e => POSITIONS.includes(e))[0];
	let width = parameters.filter(e => !POSITIONS.includes(e))[0];
	let height = parameters.filter(e => !POSITIONS.includes(e))[1] || width;

	return fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.dataURL)
		.then(response =>
		{
			let dataURL = DataURL.parse(response);
			if (dataURL.contentType === "application/json")
				GTooltip.show(event.target, Formatter
					.JSONtoHTML(JSON.parse(dataURL.data)),
					position, width, height);
			else
				GTooltip.show(event.target, dataURL.data,
					position, width, height);
			trigger.addEventListener("mouseleave", () => GTooltip.hide(), {"once": true});
			event.success(path, response);
		})
		.catch(error => event.failure(path, error));
});
