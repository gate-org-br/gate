/* global DateFormat, customElements */

class ActionPicker extends Picker
{
	constructor()
	{
		super();
		this.hideButtton;
	}

	connectedCallback()
	{
		super.connectedCallback();
		this.classList.add("g-picker");
	}

	get selector()
	{
		if (!this._private.selector)
		{
			this._private.selector = this.body.appendChild(new ActionSelector());
			this._private.selector.addEventListener("selected",
				e => this.dispatchEvent(new CustomEvent('picked', {detail: e.detail})));

		}
		return this._private.selector;
	}

	add(action)
	{
		this.selector.add(action);
	}
}

customElements.define('g-action-picker', ActionPicker);