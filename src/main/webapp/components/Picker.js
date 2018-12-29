class Picker extends Modal
{
	constructor()
	{
		super();
		var main = this.element().appendChild(document.createElement("div"));
		main.className = "Picker";
		this.main = () => main;

		var head = main.appendChild(document.createElement("div"));
		this.head = () => head;

		var body = main.appendChild(document.createElement("div"));
		this.body = () => body;

		var foot = main.appendChild(document.createElement("div"));
		this.foot = () => foot;

		var cancel = foot.appendChild(document.createElement("a"));
		cancel.addEventListener("click", () => this.hide());
		cancel.appendChild(document.createTextNode("Cancelar"));
		cancel.href = "#";
		this.cancel = () => cancel;

		var commit = foot.appendChild(document.createElement("a"));
		commit.appendChild(document.createTextNode("Concluir"));
		commit.href = "#";
		this.commit = () => commit;
	}
}