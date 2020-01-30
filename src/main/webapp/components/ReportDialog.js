/* global customElements */

class ReportDialog extends Window
{
	constructor(options)
	{
		super(options);
		this.classList.add("g-report-dialog");
		var downloadStatus = new DownloadStatus();
		addEventListener("hide", () => downloadStatus.abort());

		this.head.innerHTML = (options && options.title) || "Imprimir";

		var selector = this.body.appendChild(new ReportSelector());
		selector.addEventListener("selected", event =>
		{
			this.body.removeChild(selector);
			this.body.appendChild(downloadStatus);
			var url = new URL(options.url).setParameter("type", event.detail).toString();
			downloadStatus.download(options.method, url, options.data);
			downloadStatus.addEventListener("done", () => this.hide());
		});

		let close = new Command();
		close.action = () => this.hide();
		close.innerHTML = "<i>&#x1011</i>";
		this.head.appendChild(close);
	}
}



customElements.define('g-report-dialog', ReportDialog);