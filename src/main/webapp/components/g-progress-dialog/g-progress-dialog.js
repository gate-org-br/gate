/* global customElements */

class GProgressDialog extends Picker
{
	constructor(process, options)
	{
		super(options);

		var status = "Pending";
		this.classList.add("g-progress-dialog");

		this.caption = options && options.title ? options.title : "Progresso";

		var progress = this.body.appendChild(document.createElement("g-progress-status"));
		progress.setAttribute("process", process);

		this.commit.innerText = "Processando";
		this.commit.style.color = getComputedStyle(document.documentElement).getPropertyValue('--b')

		this.addEventListener("hide", function (event)
		{
			if (status === "Pending"
				&& !confirm("Tem certeza de que deseja fechar o progresso?"))
				event.preventDefault();
		});

		this.commit.onclick = () => this.hide();

		progress.addEventListener("commited", () =>
		{
			status = "Commited";
			this.commit.innerHTML = "OK";
			this.commit.style.color = getComputedStyle(document.documentElement).getPropertyValue('--g');
		});

		progress.addEventListener("canceled", () =>
		{
			status = "Canceled";
			this.commit.innerHTML = "OK";
			this.commit.style.color = getComputedStyle(document.documentElement).getPropertyValue('--r');
		});

		progress.addEventListener("redirected", url =>
		{
			this.addEventListener("hide", event =>
			{
				this.hide();
				event.preventDefault();
				window.location.href = url.detail;
			});
		});
	}
}

customElements.define('g-progress-dialog', GProgressDialog);