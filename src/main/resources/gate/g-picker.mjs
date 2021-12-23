/* global customElements */

import GWindow from './g-window.mjs';

export default class GPicker extends GWindow
{
	constructor()
	{
		super();
	}

	connectedCallback()
	{
		super.connectedCallback();
		this.classList.add("g-picker");
	}

	get commit()
	{
		if (!this._private.commit)
		{
			this._private.commit = this.foot.appendChild(document.createElement("a"));
			this._private.commit.innerText = "Concluir";
			this._private.commit.href = "#";
		}
		return this._private.commit;
	}
}

customElements.define('g-picker', GPicker);