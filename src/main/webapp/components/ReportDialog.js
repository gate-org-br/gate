class ReportDialog extends Modal
{
	constructor(options)
	{
		super(options);
		var download = new DownloadStatus();
		addEventListener("hide", () => download.abort());

		var main = this.element().appendChild(document.createElement("div"));
		main.className = "ReportDialog";

		var head = main.appendChild(document.createElement("div"));
		if (options && options.title)
			head.innerHTML = options.title || "Imprimir";

		var body = main.appendChild(document.createElement("div"));

		var selector = body.appendChild(new ReportSelector());
		selector.addEventListener("selected", event =>
		{
			body.removeChild(selector);
			body.appendChild(download);
			var url = new URL(options.url).setParameter("type", event.detail).toString();
			download.download(options.method, url, options.data);
			download.addEventListener("done", () => this.hide());
		});

		var foot = main.appendChild(document.createElement("div"));

		var action = foot.appendChild(document.createElement("a"));
		action.appendChild(document.createTextNode("Cancelar"));
		action.addEventListener("click", () => this.hide());
		action.style.color = "#660000";
		action.href = "#";
	}
}