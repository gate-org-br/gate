class ProgressDialog extends Modal
{
	constructor(process, options)
	{
		super();

		var status = "Pending";

		var main = this.element().appendChild(document.createElement("div"));
		main.className = "ProgressDialog";
		this.main = () => main;

		var head = main.appendChild(document.createElement("div"));
		if (options && options.title)
			head.innerHTML = options.title;

		var body = main.appendChild(document.createElement("div"));

		var progress = body.appendChild(document.createElement("progress-status"));
		progress.setAttribute("process", process);

		var foot = main.appendChild(document.createElement("div"));

		var action = foot.appendChild(document.createElement("a"));
		action.appendChild(document.createTextNode("Processando"));
		action.href = "#";

		this.creator().addEventListener("hide", function (event)
		{
			if (status === "Pending"
				&& !confirm("Tem certeza de que deseja fechar o progresso?"))
				event.preventDefault();
		});

		action.onclick = () => this.hide();

		progress.addEventListener("commited", () =>
		{
			status = "Canceled";
			action.innerHTML = "OK";
			action.style.color = "#006600";
		});

		progress.addEventListener("canceled", () =>
		{
			status = "Commited";
			action.innerHTML = "OK";
			action.style.color = "#660000";
		});

		progress.addEventListener("redirected",
			event => window.location.href = event.detail);
	}
}