import URL from './url.mjs';
import GPopup from './g-popup.mjs';
import Process from './process.mjs';
import resolve from './resolve.mjs';
import Message from './g-message.mjs';
import GLoading from './g-loading.mjs';

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
	let link = event.target
		.closest("a");
	if (!link)
		return;
	if (link.hasAttribute("data-cancel"))
	{
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
		Message.error(link.getAttribute("data-cancel"), 2000);
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
						Message.show(status, 2000);
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
							Message.show(status, 2000);
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
							Message.show(status, 2000);
					} finally
					{
						link.style.pointerEvents = "";
					}
				});
				break;
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
					dialog.target = link.getAttribute("data-redirect") || "_self";
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
								Message.show(status, 2000);
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