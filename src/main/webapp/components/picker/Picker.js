/* global customElements */

class Picker extends Window
{
	constructor()
	{
		super();
		this.classList.add("g-picker");
		this.commands.add(document.createElement("g-command").action(() => this.hide())).innerHTML = 'Fechar janela<i>&#X1011;</i>';
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