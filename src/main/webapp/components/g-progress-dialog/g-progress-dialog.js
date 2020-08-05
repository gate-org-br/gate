/* global customElements */

class GProgressDialog extends Picker
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

		this.classList.add("g-progress-dialog");

		this.caption = this.caption || "Progresso";

		let progress = document.createElement("g-progress-status");
		progress.process = this.process;
		this.body.appendChild(progress);

		this.commit.innerText = "Processando";
		this.commit.setAttribute("target", this.target);
		this.commit.style.color = getComputedStyle(document.documentElement).getPropertyValue('--b');

		this.commit.onclick = event =>
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


			this.commit.innerHTML = "Ok";
			this.commit.style.color = getComputedStyle(document.documentElement).getPropertyValue('--g');
			this.commit.onclick = event => event.preventDefault() | event.stopPropagation() | this.hide();
		});

		window.addEventListener("ProcessCanceled", event =>
		{
			if (event.detail.process !== this.process)
				return;

			this.commit.innerHTML = "OK";
			this.commit.style.color = "#660000";
			this.commit.style.color = getComputedStyle(document.documentElement).getPropertyValue('--r');
			this.commit.onclick = event => event.preventDefault() | event.stopPropagation() | this.hide();
		});

		window.addEventListener("ProcessRedirect", event =>
		{
			if (event.detail.process !== this.process)
				return;

			this.commit.innerHTML = "Exibir";
			this.commit.onclick = (event) =>
			{
				this.hide();
				this.dispatchEvent(new CustomEvent('redirect', {detail: event.url}));
			};
		});
	}
}

customElements.define('g-progress-dialog', GProgressDialog);