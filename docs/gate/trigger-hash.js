import trigger from './trigger-core.js';

window.addEventListener("load", function (event)
{
	let selector = window.location.hash;
	if (selector)
	{
		let element = document.querySelector(selector);
		if (element)
		{
			let target = element.target
				|| element.getAttribute("formtarget")
				|| element.getAttribute("data-target");
			if (target && target.startsWith("@"))
				trigger(event, element);
		}
	}
});
