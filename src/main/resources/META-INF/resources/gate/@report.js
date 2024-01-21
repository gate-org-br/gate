import GReportPicker from './g-report-picker.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@report", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {method, action, form} = event.detail;

	GReportPicker.pick(trigger.title)
		.then(type =>
		{
			fetch(RequestBuilder.build(method, `${action}${action.indexOf("?") !== -1 ? "&" : "?"}type=${type}`, form))
				.then(ResponseHandler.dataURL)
				.then(response => event.success(path, response))
				.catch(error => event.failure(path, error));
		}).catch(() => event.resolve(path));
});
