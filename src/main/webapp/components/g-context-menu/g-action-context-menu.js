/* global CopyTextMenuItem, CopyLinkMenuItem, OpenLinkMenuItem, Clipboard, customElements */

class GActionContextMenu extends GContextMenu
{
	connectedCallback()
	{
		super.connectedCallback();

		let copyText = this.appendChild(document.createElement("a"));
		copyText.innerHTML = "Copiar texto <i>&#X2217;</i>";
		copyText.addEventListener("click", () => Clipboard.copy(this.target.innerText, true));

		let copyLink = this.appendChild(document.createElement("a"));
		copyLink.innerHTML = "Copiar endereço <i>&#X2159;</i>";
		copyLink.addEventListener("click", () => Clipboard.copy(this.context.getAttribute("data-action"), true));

		let openLink = this.appendChild(document.createElement("a"));
		openLink.innerHTML = "Abrir em nova aba <i>&#X2256;</i>";
		openLink.addEventListener("click", () =>
		{
			let context = this.context;
			let target = this.context.getAttribute("data-target");
			this.context.setAttribute("data-target", "_blank");
			this.context.click();
			this.context.setAttribute("data-target", target);
		});
	}
}

customElements.define('g-action-context-menu', GActionContextMenu);


window.addEventListener("contextmenu", event => {
	event = event || window.event;
	let action = event.target || event.srcElement;
	action = action.closest("[data-action]");
	if (!action)
		return;

	let menu = document.querySelector("g-action-context-menu")
		|| document.body.appendChild(new GActionContextMenu());
	menu.show(action, event.target, event.clientX, event.clientY);

	event.preventDefault();
	event.stopPropagation();
	event.stopImmediatePropagation();
});
