/* global customElements */

class Picker extends GWindow
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

	get close()
	{
		if (!this._private.close)
		{
			this._private.close = this.head.appendChild(document.createElement("a"));
			this._private.close.href = "#";
			this._private.close.onclick = () => this.hide();
			this._private.close.innerHTML = "<i>&#x1011</i>";
		}
		return this._private.close;
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

customElements.define('g-picker', Picker);