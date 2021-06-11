/* global DateFormat, customElements */

class ActionSelector extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {actions: document.createElement("tbody")};
	}

	connectedCallback()
	{
		if (!this.firstChild)
			this.appendChild(document.createElement("div"))
				.appendChild(document.createElement("table"))
				.appendChild(this._private.actions);
	}

	add(action)
	{
		let link = this._private.actions.appendChild(document.createElement("tr"));
		link.appendChild(document.createElement("td")).innerHTML = "&#X" + action.icon + ";";
		link.appendChild(document.createElement("td")).innerHTML = action.text;
		link.addEventListener("click", () => this.dispatchEvent(new CustomEvent('selected', {detail: action})));
	}
}

customElements.define('g-action-selector', ActionSelector);