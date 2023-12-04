/* global fetch */

import './trigger.js';
import GBlock from './g-block.js';
import GMessageDialog from './g-message-dialog.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("trigger", function (event)
{
	if (event.detail.target === "@outerHTML"
		|| event.detail.target === "@innerHTML"
		|| event.detail.target === "@beforebegin"
		|| event.detail.target === "@afterbegin"
		|| event.detail.target === "@beforeend"
		|| event.detail.target === "@afterend")
	{
		event.preventDefault();

		let id = event.detail.parameters[0];
		if (!id)
			throw new Error(`Target id not specified`);

		let element = document.getElementById(id);
		if (!element)
			throw new Error(`No element with id ${id} found on page`);

		let header = {};
		if (event.detail.method !== "get")
		{
			header.method = event.detail.method;
			header.body = new FormData(event.detail.element);
			header.headers = {'Content-Type': 'application/x-www-form-urlencoded'};
		}

		let trigger = event.detail.cause.submitter || event.detail.element;

		trigger.style.cursor = "wait";
		trigger.onclick = e => e.preventDefault();
		if (event.detail.parameters[1])
			GBlock.show(event.detail.parameters[1]);

		fetch(event.detail.action, header)
			.then(ResponseHandler.text)
			.then(html =>
			{
				switch (event.detail.target)
				{
					case "@outerHTML":
						element.outerHTML = html;
						break;
					case "@innerHTML":
						element.innerHTML = html;
						break;
					case "@beforebegin":
						element.insertAdjacentHTML("beforebegin", html);
						break;
					case "@afterbegin":
						element.insertAdjacentHTML("afterbegin", html);
						break;
					case "@beforeend":
						element.insertAdjacentHTML("beforeend", html);
						break;
					case "@afterend":
						element.insertAdjacentHTML("afterend", html);
						break;
				}

			})
			.catch(GMessageDialog.error)
			.finally(() =>
			{
				GBlock.hide();
				trigger.onclick = null;
				trigger.style.cursor = "";
			});
	}
});
