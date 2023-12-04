/* global fetch */

import URL from './url.js';
import GBlock from './g-block.js';
import Process from './process.js';
import resolve from './resolve.js';
import GLoading from './g-loading.js';
import GMessageDialog from './g-message-dialog.js';
import ResponseHandler from './response-handler.js';

function _message(link, timeout)
{
	link.style.pointerEvents = "none";
	fetch(link.href)
		.then(message => ResponseHandler.text(message))
		.then(message => GMessageDialog.success(message, timeout ? Number(timeout) : null))
		.catch(error => GMessageDialog.error(error.message))
		.finally(() => link.style.pointerEvents = "");
}

window.addEventListener("click", function (event)
{
	if (event.button !== 0)
		return;

	let link = event.target.closest("a");
	if (!link && event.composed)
		link = event.composedPath()[0].closest("a");
	if (!link)
		return;

	if (link.hasAttribute("data-cancel"))
	{
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
		GMessageDialog.error(link.getAttribute("data-cancel"), 2000);
		return;
	}

	if (link.hasAttribute("data-disabled"))
	{
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
		return;
	}

	if (link.hasAttribute("data-confirm")
		&& !confirm(link.getAttribute("data-confirm")))
	{
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
		return;
	}

	if (link.hasAttribute("data-alert"))
		alert(link.getAttribute("data-alert"));
	else if (link.href.match(/([@][{][^}]*[}])/g)
		|| link.href.match(/([!][{][^}]*[}])/g)
		|| link.href.match(/([?][{][^}]*[}])/g))
	{
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
		var resolved = resolve(link.href);
		if (resolved !== null)
		{
			var href = link.href;
			link.href = resolved;
			link.click();
			link.href = href;
		}
		return;
	}

	let target = link.getAttribute("target");
	target = /^(_[a-zA-Z]+)(\((.*)\))?$/g.exec(target);
	if (target)
		switch (target[1])
		{
			case "_message":
				event.preventDefault();
				event.stopPropagation();
				return _message(link, target[3]);

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
					if (link.getAttribute("data-on-hide"))
						dialog.addEventListener("hide", () => processHide(link));
					dialog.addEventListener("redirect", event => window.location.href = event.detail);
					dialog.show();
				});
				break;
			case "_report":
			case "_report-dialog":
				event.preventDefault();
				event.stopPropagation();
				let dialog = window.top.document.createElement("g-report-dialog");
				dialog.blocked = true;
				dialog.caption = link.getAttribute("title") || "Imprimir";
				dialog.get(link.href);
				break;
			default:
				if (/^_id\(([a-zA-Z0-9]+)\)$/g.test(target))
				{
					event.preventDefault();
					event.stopPropagation();
					link.style.pointerEvents = "none";
					new URL(link.href).get(function (status)
					{
						try
						{
							status = JSON.parse(status);
							if (status.type === "SUCCESS")
								document.getElementById(/^_id\(([a-zA-Z0-9]+)\)$/g.exec(target)[1])
									.innerHTML = status.message;
							else
								GMessageDialog.show(status, 2000);
						} finally
						{
							link.style.pointerEvents = "";
						}
					});
				}
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