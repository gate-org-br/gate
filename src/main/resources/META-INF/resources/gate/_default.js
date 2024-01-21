import './trigger.js';
import DOM from './dom.js';
import submit from './submit.js';
import DataURL from './data-url.js';
import GFilePicker from './g-file-picker.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("_self", event => proccess(event, window));
window.addEventListener("_top", event => proccess(event, window.top));
window.addEventListener("_parent", event => proccess(event, window.parent));
window.addEventListener("_blank", event => proccess(event, window.open()));
window.addEventListener("@frame", event => proccess(event, DOM.navigate(event,
		`[name='${event.detail.parameters[0]}'], #${event.detail.parameters[0]}`)
		.orElseThrow("Invalid target element")
		.contentWindow));

function proccess(event, target)
{
	let path = event.composedPath();
	let {method, action, form} = event.detail;

	if (method === "get")
	{
		target.addEventListener("load", () => event.success(path), {once: true});
		target.location = action;
	} else if (method === "post" && form)
	{
		target.addEventListener("load", () => event.success(path), {once: true});
		submit(form, method, action, target);
	} else
	{
		let path = event.composedPath();
		fetch(RequestBuilder.build(method, action, form))
			.then(ResponseHandler.dataURL)
			.then(response =>
			{
				dataURL = DataURL.parse(response);
				if (dataURL.contentType === "text/html")
				{
					let document = new DOMParser().parseFromString(dataURL.data, 'text/html');
					let nodes = document.documentElement.childNodes;
					target.document.documentElement.replaceChildren(...nodes);
				} else
					return GFilePicker.saveDataURL(response);
				event.success(path);
			})
			.catch(error => event.failure(path, error));
	}
}