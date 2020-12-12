/* global Message, customElements, CSV */

class GProgressStatus extends HTMLElement
{
	constructor()
	{
		super();
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
		var title = this.appendChild(document.createElement("label"));
		title.style.fontSize = "20px";
		title.style.flexBasis = "40px";
		title.innerHTML = "Conectando ao servidor";

		var progress = this.appendChild(document.createElement("progress"));
		progress.style.width = "100%";
		progress.style.flexBasis = "40px";

		var div = this.appendChild(document.createElement("div"));
		div.style.flexBasis = "40px";
		div.style.display = "flex";
		div.style.alignItems = "center";

		var clock = div.appendChild(document.createElement("digital-clock"));
		clock.style.flexGrow = "1";
		clock.style.fontSize = "12px";
		clock.style.display = "flex";
		clock.style.alignItems = "center";
		clock.style.justifyContent = "flex-start";
		clock.innerHTML = "00:00:00";

		var counter = div.appendChild(document.createElement("label"));
		counter.style.flexGrow = "1";
		counter.style.fontSize = "12px";
		counter.style.display = "flex";
		counter.style.alignItems = "center";
		counter.style.justifyContent = "flex-end";
		counter.innerHTML = "...";

		var div = this.appendChild(document.createElement("div"));
		div.style.padding = "4px";
		div.style.display = "flex";
		div.style.overflow = "auto";
		div.style.flexBasis = "120px";
		div.style.borderRadius = "5px";
		div.style.alignItems = "stretch";
		div.style.justifyContent = "center";
		div.style.backgroundColor = "white";

		var logger = div.appendChild(document.createElement("ul"));
		logger.setAttribute("title", "Clique para gerar CSV");
		logger.style.flexGrow = "1";
		logger.style.margin = "0";
		logger.style.padding = "0";
		logger.style.listStyleType = "none";
		logger.addEventListener("click", () => CSV.print(Array.from(logger.children).map(e => [e.innerText]), "log.txt"));

		let log = event =>
		{
			if (event.detail.progress)
				counter.innerHTML = event.detail.progress;

			if (event.detail.text !== title.innerHTML)
			{
				var log = logger.appendChild(document.createElement("li"));

				log.style.height = "16px";
				log.style.display = "flex";
				log.style.alignItems = "center";
				log.innerHTML = event.detail.text;

				title.innerHTML = event.detail.text;
			}

			if (event.todo !== -1)
			{
				progress.max = event.detail.todo;
				if (event.done !== -1)
					progress.value = event.detail.done;
			}
		}

		window.addEventListener("ProcessPending", event =>
		{
			if (event.detail.process !== this.process)
				return;

			log(event);
			title.style.color = '#000000';
			clock.style.color = '#000000';
			counter.style.color = '#000000';
		});


		window.addEventListener("ProcessCommited", event =>
		{
			if (event.detail.process !== this.process)
				return;

			log(event);

			if (!progress.max)
				progress.max = 100;
			if (!progress.value)
				progress.value = 100;

			title.style.color = '#006600';
			clock.style.color = '#006600';
			counter.style.color = '#006600';

			clock.setAttribute("paused", "paused");
		});

		window.addEventListener("ProcessCanceled", event =>
		{
			if (event.detail.process !== this.process)
				return;

			log(event);

			if (!progress.max)
				progress.max = 100;
			if (!progress.value)
				progress.value = 0;

			title.style.color = '#660000';
			clock.style.color = '#660000';
			counter.style.color = '#660000';

			clock.setAttribute("paused", "paused");
		});

		window.addEventListener("ProcessError", event =>
		{
			if (event.detail.process !== this.process)
				return;

			log(event);
			title.style.color = '#666666';
			clock.style.color = '#666666';
			counter.style.color = '#666666';
		});
	}
}

customElements.define('g-progress-status', GProgressStatus);


