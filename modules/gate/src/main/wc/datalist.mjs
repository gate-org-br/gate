import resolve from './resolve.js';
import Populator from './populator.js';
import ResponseHandler from './response-handler.js';

const MIN = 3;

const REGISTRY = new WeakSet();
window.addEventListener("connected", function (event)
{
	let element = event.target || event.composedPath()[0];
	if (!REGISTRY.has(element)
		&& element.tagName === "INPUT"
		&& element.hasAttribute("list"))
	{
		REGISTRY.add(element);
		element.setAttribute("autocomplete", "off");
		element.addEventListener("input", () =>
		{
			var datalist = document.getElementById(element.getAttribute("list"));

			if (element.value && datalist.hasAttribute("data-options"))
			{
				let len = datalist.hasAttribute("data-options-len")
					? parseInt(datalist.getAttribute("data-options-len"))
					: MIN;

				if (element.value.length < len)
				{
					element.filter = null;
					new Populator([]).populate(datalist);
				} else if (!element.filter || !element.value.toLowerCase().includes(element.filter.toLowerCase()))
				{
					element.blur();
					element.disabled = true;
					fetch(resolve(element, event, datalist.getAttribute("data-options")))
						.then(ResponseHandler.json)
						.then(options =>
						{
							new Populator(JSON.parse(options)).populate(datalist);
							element.disabled = false;
							element.focus();
							element.click();
							element.filter = element.value;
						});
				}
			}

			let value = Array.from(element.parentNode.children)
				.filter(e => e.tagName === "INPUT")
				.filter(e => (e.getAttribute("type") || "").toLowerCase() === "hidden")[0];

			if (value)
			{
				value.value = "";
				value.dispatchEvent(new Event('change', {bubbles: true}));
			}


			if (element.value && element.value.length)
			{
				let option = Array.from(datalist.children)
					.find(option => option.innerHTML === element.value
							|| option.innerHTML.toLowerCase() === element.value.toLowerCase());

				if (option)
				{
					element.setCustomValidity("");
					element.value = option.innerText;

					if (value)
					{
						value.value = option.getAttribute("data-value");
						value.dispatchEvent(new Event('change', {bubbles: true}));
					}

				} else
					element.setCustomValidity("Entre com um dos valores da lista");
			} else
				element.setCustomValidity("");
		});
	}
});