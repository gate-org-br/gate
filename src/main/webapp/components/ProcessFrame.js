class ProcessFrame extends HTMLElement
{
	constructor(process)
	{
		super();
		if (process)
			this.setAttribute("process", process);
	}

	connectedCallback()
	{
		var body = this.appendChild(document.createElement("div"));

		var progress = body.appendChild(new ProgressStatus(this.getAttribute("process")));

		var coolbar = body.appendChild(document.createElement("div"));
		coolbar.className = "Coolbar";

		var action = coolbar.appendChild(document.createElement("a"));
		action.appendChild(document.createTextNode("Processando"));
		action.innerHTML = "Processando<i>&#X2017;</i>";
		action.href = "#";

		action.onclick = () => Message
				.error("Aguarde o processamento", 1000);

		progress.addEventListener("commited", () =>
		{
			action.innerHTML = "Ok<i>&#X1000;</i>";
			action.style.color = "#006600";
		});

		progress.addEventListener("canceled", () =>
		{
			action.innerHTML = "OK";
			action.style.color = "#660000";
		});

		progress.addEventListener("redirected", url =>
			action.onclick = () => window.location.href = url.detail);
	}
}

window.addEventListener("load", () =>
	customElements.define('process-frame', ProcessFrame));