import GFilePicker from './g-file-picker.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@save", function (event)
{
	let path = event.composedPath();
	let {method, action, form, signal} = event.detail;

	fetch(RequestBuilder.build(method, action, form), {signal})
		.then(ResponseHandler.dataURL)
		.then(GFilePicker.saveDataURL)
		.then(response => event.success(path, response))
		.catch(error => event.failure(path, error));
});
