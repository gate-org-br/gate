/* global fetch */

let INSTANCE;

export default class Return
{

	static bind (picker)
	{
		if (picker.iframe)
			picker.iframe._GReturnTrigger = picker;
		else
			INSTANCE = picker;
	}

	static free (picker)
	{
		if (picker.iframe)
			delete picker.iframe._GReturnTrigger;
		else
			INSTANCE = null;
	}

	static update (parameters, values)
	{
		let size = parameters.length;
		for (var i = 0; i < size; i++)
			if (parameters[i])
				parameters[i].value = values[i] || "";

		for (var i = 0; i < size; i++)
			if (parameters[i])
				parameters[i].dispatchEvent(new CustomEvent('changed', { bubbles: true }));
	}
}

window.addEventListener("@return", function (event)
{
	let target = window?.frameElement?._GReturnTrigger || INSTANCE;
	target.dispatchEvent(new CustomEvent('commit', { detail: event.detail.parameters }));
});