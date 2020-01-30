/* global customElements */

class Picker extends Window
{
	constructor()
	{
		super();
		this.classList.add("g-picker");
		let close = new Command();
		close.action = () => this.hide();
		close.innerHTML = "<i>&#x1011</i>";
		this.head.appendChild(close);
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