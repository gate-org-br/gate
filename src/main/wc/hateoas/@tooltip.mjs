import DataURL from './data-url.js';
import Formatter from './formatter.js';
import RequestBuilder from './request-builder.js';
import GTooltip, {POSITIONS} from './g-tooltip.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@tooltip", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {method, action, form, parameters, signal} = event.detail;

	let position = parameters.filter(e => POSITIONS.includes(e))[0];
	let width = parameters.filter(e => !POSITIONS.includes(e))[0];
	let height = parameters.filter(e => !POSITIONS.includes(e))[1] || width;

	return fetch(RequestBuilder.build(method, action, form), {signal})
		.then(ResponseHandler.dataURL)
		.then(result =>
		{
			let dataURL = DataURL.parse(result);
			if (dataURL.contentType.startsWith("application/json"))
				GTooltip.show(trigger, Formatter.JSONtoHTML(JSON.parse(dataURL.data)), position, width, height);
			else
				GTooltip.show(trigger, dataURL.data, position, width, height);
			event.success(path, result);
		})
		.catch(error => event.failure(path, error));
});
