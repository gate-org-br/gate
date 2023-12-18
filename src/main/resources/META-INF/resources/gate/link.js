/* global fetch */

import URL from './url.js';
import GBlock from './g-block.js';
import Process from './process.js';
import resolve from './resolve.js';
import GMessageDialog from './g-message-dialog.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("click", function (event)
{
	if (event.button !== 0)
		return;

	let link = event.target.closest("a");
	if (!link && event.composed)
		link = event.composedPath()[0].closest("a");
	if (!link)
		return;

	let target = link.getAttribute("target");
	target = /^(_[a-zA-Z]+)(\((.*)\))?$/g.exec(target);
	if (target)
		switch (target[1])
		{
			case "_progress-dialog":
				event.preventDefault();
				event.stopPropagation();
				new URL(link.href).get(process =>
				{
					link.setAttribute("data-process", process);
					process = new Process(JSON.parse(process));
					let dialog = window.top.document.createElement("g-progress-dialog");
					dialog.process = process.id;
					dialog.caption = link.getAttribute("title") || "Progresso";
					dialog.addEventListener("show", () => link.dispatchEvent(new CustomEvent('show', {detail: {modal: dialog}})));
					dialog.addEventListener("hide", () => link.dispatchEvent(new CustomEvent('hide', {detail: {modal: dialog}})));
					dialog.addEventListener("redirect", event => window.location.href = event.detail);
					dialog.show();
				});
				break;
		}
});

window.addEventListener("keydown", function (event)
{
	let link = event.target;
	if (link.tagName === 'A' && event.keyCode === 32)
	{
		event.target.click();
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
	}
});