function Picker()
{
	var modal = new Modal();
	modal.addEventListener("click", function (event)
	{
		if (event.target === modal || event.srcElement === modal)
			modal.hide();
	});

	var main = modal.appendChild(document.createElement("div"));
	main.className = "Picker";

	var head = main.appendChild(document.createElement("div"));

	var body = main.appendChild(document.createElement("div"));

	var foot = main.appendChild(document.createElement("div"));

	var cancel = foot.appendChild(document.createElement("a"));
	cancel.addEventListener("click", () => modal.hide());
	cancel.appendChild(document.createTextNode("Cancelar"));
	cancel.href = "#";

	var commit = foot.appendChild(document.createElement("a"));
	commit.appendChild(document.createTextNode("Concluir"));
	commit.href = "#";

	this.modal = () => modal;
	this.main = () => main;
	this.head = () => head;
	this.body = () => body;
	this.foot = () => foot;
	this.commit = () => commit;
	this.cancel = () => cancel;
}