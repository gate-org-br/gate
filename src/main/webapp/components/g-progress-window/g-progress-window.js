/* global customElements */

class GProgressWindow extends GModal
{
	constructor()
	{
		super();
	}

	set target(target)
	{
		this.setAttribute("target", target);
	}

	get target()
	{
		return this.getAttribute("target") || "_self";
	}

	set process(process)
	{
		this.setAttribute("process", process);
	}

	get process()
	{
		return JSON.parse(this.getAttribute("process"));
	}

	connectedCallback()
	{
		super.connectedCallback();

		let body = this.appendChild(document.createElement("div"));

		let progress = new GProgressStatus();
		progress.process = this.process;
		body.appendChild(progress);

		let coolbar = body.appendChild(document.createElement("g-coolbar"));

		let action = coolbar.appendChild(document.createElement("a"));
		action.appendChild(document.createTextNode("Processando"));
		action.setAttribute("target", this.target);
		action.innerHTML = "Processando<i>&#X2017;</i>";
		action.href = "#";

		action.onclick = event =>
		{
			event.preventDefault();
			event.stopPropagation();
			if (confirm("Tem certeza de que deseja fechar o progresso?"))
				this.hide();
		};

		window.addEventListener("ProcessCommited", event =>
		{
			if (event.detail.process !== this.process)
				return;

			action.style.color = "#006600";
			action.innerHTML = "Ok<i>&#X1000;</i>";
			action.onclick = event => event.preventDefault() | event.stopPropagation() | this.hide();
		});

		window.addEventListener("ProcessCanceled", event =>
		{
			if (event.detail.process !== this.process)
				return;

			action.style.color = "#660000";
			action.innerHTML = "Ok<i>&#X1000;</i>";
			action.onclick = event => event.preventDefault() | event.stopPropagation() | this.hide();
		});

		window.addEventListener("ProcessRedirect", event =>
		{
			if (event.detail.process !== this.process)
				return;

			action.innerHTML = "Exibir<i>&#X1000;</i>";
			this.commit.onclick = (event) =>
			{
				this.hide();
				this.dispatchEvent(new CustomEvent('redirect', {detail: event.url}));
			};
		});
	}
}

customElements.define('g-progress-window', GProgressWindow);