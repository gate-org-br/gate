import './trigger.js';
import DataURL from './data-url.js';
import Clipboard from './clipboard.js';

window.addEventListener("@clipboard", function (event)
{
	let path = event.composedPath();
	let {action} = event.detail;

	if (!action?.startsWith("data:"))
		return event.failure(path, new Error("@clipboard target only supports data URLs"));

	try
	{
		Clipboard.copy(DataURL.parse(action).data);
		event.success(path, action);
	} catch (error)
	{
		event.failure(path, error);
	}
});