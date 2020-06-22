/* global customElements */

class Picker extends GWindow
{
	constructor()
	{
		super();
		this.classList.add("g-picker");
		let close = document.createElement("a");
		close.href = "#";
		close.onclick = () => this.hide();
		close.innerHTML = "<i>&#x1011</i>";
		this.head.appendChild(close);
	}

	connectedCallback()
	{
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

customElements.define('g-picker', Picker);