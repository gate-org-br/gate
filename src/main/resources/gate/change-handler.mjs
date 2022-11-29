import Extractor from './extractor.mjs';
import GSearchPicker from './g-search-picker.mjs';

window.addEventListener("change", event =>
	{
		let source = event.target || event.srcElement;

		if ((source.tagName === "INPUT" || source.tagName === "SELECT")
			&& (source.hasAttribute("data-method") || source.hasAttribute("data-action") || source.hasAttribute("data-target")))
		{

			source.blur();

			switch (source.getAttribute("data-method") ? source.getAttribute("data-method").toLowerCase() : "get")
			{
				case "get":

					let link = document.createElement("a");

					if (source.hasAttribute("data-target"))
						link.setAttribute("target", source.getAttribute("data-target"));

					if (source.hasAttribute("data-action"))
						link.setAttribute("href", source.getAttribute("data-action"));

					if (source.hasAttribute("data-on-hide"))
						link.setAttribute("data-on-hide", source.getAttribute("data-on-hide"));

					if (source.hasAttribute("title"))
						link.setAttribute("title", source.getAttribute("title"));

					if (source.hasAttribute("data-block"))
						link.setAttribute("data-block", source.getAttribute("data-block"));

					if (source.hasAttribute("data-alert"))
						link.setAttribute("data-alert", source.getAttribute("data-alert"));

					if (source.hasAttribute("data-confirm"))
						link.setAttribute("data-confirm", source.getAttribute("data-confirm"));

					document.body.appendChild(link);
					link.click();
					document.body.removeChild(link);
					break;
				case "post":
					let button = document.createElement("button");

					if (source.hasAttribute("data-target"))
						button.setAttribute("target", source.getAttribute("data-target"));

					if (source.hasAttribute("data-action"))
						button.setAttribute("formaction", source.getAttribute("data-action"));

					if (source.hasAttribute("data-on-hide"))
						button.setAttribute("data-on-hide", source.getAttribute("data-on-hide"));
					if (source.hasAttribute("data-on-hide"))
						button.setAttribute("data-on-hide", source.getAttribute("data-on-hide"));

					if (source.hasAttribute("title"))
						button.setAttribute("title", source.getAttribute("title"));

					if (source.hasAttribute("data-block"))
						button.setAttribute("data-block", source.getAttribute("data-block"));

					if (source.hasAttribute("data-alert"))
						button.setAttribute("data-alert", source.getAttribute("data-alert"));

					if (source.hasAttribute("data-confirm"))
						button.setAttribute("data-confirm", source.getAttribute("data-confirm"));


					var form = source.closest("form");
					form.appendChild(button);
					button.click();
					form.removeChild(button);
					break;
			}
		}

		if (source.tagName === "INPUT"
			&& source.getAttribute("type").toUpperCase() === "TEXT")
		{
			let action = source.parentNode.querySelector("a[target='_search']");
			let hidden = source.parentNode.querySelector("input[type='hidden']");
			if (action && hidden)
				if (source.value)
				{
					GSearchPicker.pick(action.href, action.title, source.value).then(object =>
					{
						source.value = Extractor.label(object);
						hidden.value = Extractor.value(object);
					});
				} else
					hidden.value = '';
		}
	});