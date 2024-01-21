import DOM from './dom.js';
import Return from './@return.js';
import GFetchPicker from './g-fetch-picker.js';

window.addEventListener("@pick", function pick(event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {cause, action, parameters} = event.detail;
	parameters = parameters.map(e => e ? DOM.navigate(trigger, e).orElseThrow(`${e} is not a valid selector`) : e);

	if (cause.type === "change")
	{
		GFetchPicker.pick(action, trigger.title)
			.then(values => Return.update(parameters, values))
			.catch(() => parameters.forEach(e => e.value = ""))
			.finally(() => event.success(path));
	} else
	{
		GFetchPicker.pick(action, trigger.title)
			.then(values => Return.update(parameters, values))
			.catch(() => undefined)
			.finally(() => event.success(path));
	}
});
