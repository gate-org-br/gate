/* global Message, DataFormat, customElements */

class GDownloadStatus extends HTMLElement
{
	constructor()
	{
		super();
	}

	connectedCallback()
	{
		var title = this.appendChild(document.createElement("label"));
		title.style.fontSize = "20px";
		title.style.flexBasis = "40px";

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

		var counter = div.appendChild(document.createElement("label"));
		counter.style.flexGrow = "1";
		counter.style.fontSize = "12px";
		counter.style.display = "flex";
		counter.style.alignItems = "center";
		counter.style.justifyContent = "flex-end";
		counter.innerHTML = "...";

		this.abort = function ()
		{
			if (this.request)
				this.request.abort();
		};

		this.get = function (url)
		{
			this.download("GET", url, null);
		};

		this.post = function (url, data)
		{
			this.download("GET", url, data);
		};

		this.download = function (method, url, data)
		{
			var title = this.children[0];
			var progress = this.children[1];
			var clock = this.children[2].children[0];
			var counter = this.children[2].children[1];
			this.request = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");

			this.request.addEventListener("load", () =>
			{
				clock.setAttribute("paused", "paused");

				if (this.request.status === 200)
				{
					if (!progress.max && !progress.value)
						progress.max = progress.value = 100;
					title.style.color = "#006600";
					title.innerHTML = "Download efetuado com sucesso";

					var disposition = this.request.getResponseHeader('content-disposition');
					var matches = /"([^"]*)"/.exec(disposition);
					var filename = (matches !== null && matches[1] ? matches[1] : 'file');
					var blob = new Blob([this.request.response], {type: 'application/octet-stream'});
					var link = document.createElement('a');
					link.href = window.URL.createObjectURL(blob);
					link.download = filename;
					document.body.appendChild(link);
					link.click();
					document.body.removeChild(link);
					setTimeout(() => window.URL.revokeObjectURL(link.href), 60 * 1000);

					this.dispatchEvent(new CustomEvent('done', {cancelable: false}));
				} else
				{
					var reader = new FileReader();
					reader.addEventListener("loadend", function ()
					{
						title.style.color = "#660000";
						title.innerHTML = reader.result;
						this.dispatchEvent(new CustomEvent('error', {cancelable: false, 'detail': reader.result}));
					});
					reader.readAsText(new Blob([this.request.response], {type: 'application/octet-stream'}));
				}
			});

			this.request.addEventListener("loadend", () => this.request = null);

			this.request.addEventListener("progress", event =>
			{
				title.innerHTML = "Efetuando download";
				if (event.loaded)
				{
					progress.value = event.loaded;
					counter.innerHTML = DataFormat.format(event.loaded);

					if (event.total)
					{
						progress.max = event.total;
						counter.innerHTML = counter.innerHTML + " de " + DataFormat.format(event.total);
					}
				}
			});

			this.request.addEventListener("error", () =>
			{
				clock.setAttribute("paused", "paused");
				title.style.color = "#660000";
				title.innerHTML = "Erro ao efetuar download";
				this.dispatchEvent(new CustomEvent('error', {cancelable: false}));
			});

			title.innerHTML = "Conectando ao servidor";
			this.request.responseType = 'blob';
			this.request.open(method, resolve(url), true);
			this.request.send(data);

			return this;
		};
	}
}

customElements.define('g-download-status', GDownloadStatus);