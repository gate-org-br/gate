/* global fetch */

import URL from './url.js';
import GPopup from './g-popup.js';
import Process from './process.js';
import resolve from './resolve.js';
import GLoading from './g-loading.js';
import Extractor from './extractor.js';
import GSelectPicker from './g-select-picker.js';
import GSearchPicker from './g-search-picker.js';
import GGGMessageDialogDialogDialog from './g-message-dialog.js';


function processHide(link)
{
	GLoading.show();
	setTimeout(() =>
	{
		let type = link.getAttribute("data-on-hide");
		if (type === "reload")
			window.location.reload();
		else if (type === "submit")
			return link.closest("form").submit();
		else if (type.match(/submit\([^)]+\)/))
			document.getElementById(/submit\(([^)]+)\)/.exec(type)[1]).submit();
	}, 0);
}

window.addEventListener("click", function (event)
{
	if (event.button !== 0)
		return;

	let link = event.composed
		? event.composedPath()[0].closest("a")
		: event.target.closest("a");

	if (!link)
		return;

	if (link.hasAttribute("data-cancel"))
	{
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
		GGGMessageDialogDialogDialog.error(link.getAttribute("data-cancel"), 2000);
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
	if (link.getAttribute("target"))
	{
		switch (target)
		{
			case "_stack":
				event.preventDefault();
				event.stopPropagation();
				if (event.ctrlKey)
				{
					link.setAttribute("target", "_blank");
					link.click();
					link.setAttribute("target", "_dialog");
				} else
				{
					let dialog = window.top.document.createElement("g-stack-frame");
					dialog.target = link.getAttribute("href");
					dialog.addEventListener("show", () => link.dispatchEvent(new CustomEvent('show', {detail: {modal: dialog}})));
					dialog.addEventListener("hide", () => link.dispatchEvent(new CustomEvent('hide', {detail: {modal: dialog}})));
					if (link.getAttribute("data-on-hide"))
						dialog.addEventListener("hide", () => processHide(link));
					dialog.show();
				}
				break;
			case "_message":
				event.preventDefault();
				event.stopPropagation();
				link.style.pointerEvents = "none";
				new URL(link.href).get(function (status)
				{
					try
					{
						status = JSON.parse(status);
						GGGMessageDialogDialogDialog.show(status, 2000);
					} finally
					{
						link.style.pointerEvents = "";
					}
				});
				break;
			case "_none":
				event.preventDefault();
				event.stopPropagation();
				link.style.pointerEvents = "none";
				new URL(link.href).get(function (status)
				{
					try
					{
						status = JSON.parse(status);
						if (status.type !== "SUCCESS")
							GGGMessageDialogDialogDialog.show(status, 2000);
					} finally
					{
						link.style.pointerEvents = "";
					}
				});
				break;
			case "_this":
				event.preventDefault();
				event.stopPropagation();
				link.style.pointerEvents = "none";
				new URL(link.href).get(function (status)
				{
					try
					{
						status = JSON.parse(status);
						if (status.type === "SUCCESS")
							link.innerHTML = status.message;
						else
							GGGMessageDialogDialogDialog.show(status, 2000);
					} finally
					{
						link.style.pointerEvents = "";
					}
				});
				break;
			case "_select":
			{
				event.preventDefault();
				event.stopPropagation();
				let label = link.parentNode.querySelector("input[type=text]");
				let value = link.parentNode.querySelector("input[type=hidden]");
				if (label && value)
				{
					if (label.value || value.value)
					{
						label.value = '';
						value.value = '';
					} else
					{
						fetch(link.href)
							.then(options => options.json())
							.then(options =>
							{
								GSelectPicker.pick(options, link.title)
									.then(object =>
									{
										label.value = Extractor.label(object.value);
										value.value = Extractor.value(object.value);
									});
							}).catch(error => GGGMessageDialogDialogDialog.error(error.message));
					}
				} else
					console.log("label and value inputs not found");
				break;
			}
			case "_search":
			{
				event.preventDefault();
				event.stopPropagation();
				let label = link.parentNode.querySelector("input[type=text]");
				let value = link.parentNode.querySelector("input[type=hidden]");
				if (label && value)
				{
					if (label.value || value.value)
					{
						label.value = '';
						value.value = '';
					} else
					{
						GSearchPicker.pick(link.href, link.title)
							.then(object =>
							{
								label.value = Extractor.label(object.value);
								value.value = Extractor.value(object.value);
							});

					}
				} else
					console.log("label and value inputs not found");
				break;
			}
			case "_alert":
				event.preventDefault();
				event.stopPropagation();
				link.style.pointerEvents = "none";
				new URL(link.href).get(function (status)
				{
					alert(status);
					link.style.pointerEvents = "";
				});
				break;
			case "_hide":
				event.preventDefault();
				event.stopPropagation();
				if (window.frameElement
					&& window.frameElement.dialog
					&& window.frameElement.dialog.hide)
					window.frameElement.dialog.hide();
				else
					window.close();
				break;
			case "_popup":
				event.preventDefault();
				event.stopPropagation();
				Array.from(link.children)
					.filter(e => e.tagName.toLowerCase() === "div")
					.forEach(e => GPopup.show(e, link.getAttribute("title")));
				break;
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
				if (/^_dialog(\((.*)\))?$/g.test(target))
				{
					event.preventDefault();
					event.stopPropagation();
					if (event.ctrlKey)
					{
						link.setAttribute("target", "_blank");
						link.click();
						link.setAttribute("target", "_dialog");
					} else
					{
						let dialog = window.top.document.createElement("g-dialog");
						dialog.navigator = link.navigator;
						dialog.target = link.getAttribute("href");
						dialog.caption = link.getAttribute("title");
						dialog.blocked = Boolean(link.getAttribute("data-blocked"));
						let regex = /^_dialog(\((.*)\))?$/g.exec(target);
						if (regex[2])
							dialog.size = regex[2];
						dialog.addEventListener("show", () => link.dispatchEvent(new CustomEvent('show', {detail: {modal: dialog}})));
						dialog.addEventListener("hide", () => link.dispatchEvent(new CustomEvent('hide', {detail: {modal: dialog}})));
						if (link.getAttribute("data-on-hide"))
							dialog.addEventListener("hide", () => processHide(link));
						dialog.show();
					}
				} else if (/^_id\(([a-zA-Z0-9]+)\)$/g.test(target))
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
								GGGMessageDialogDialogDialog.show(status, 2000);
						} finally
						{
							link.style.pointerEvents = "";
						}
					});
				} else if (/^_popup\(([a-zA-Z0-9]+)\)$/g.test(target))
				{
					event.preventDefault();
					event.stopPropagation();
					GPopup.show(document.getElementById(/_popup\(([^)]+)\)/.exec(target)[1]),
						link.getAttribute("title"));
				}
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