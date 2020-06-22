/* global customElements */

class GReportDialog extends GWindow
{
	constructor()
	{
		super();
		this.classList.add("g-report-dialog");
		this._private.downloadStatus = new GDownloadStatus();
		this._private.selector = this.body.appendChild(new ReportSelector());

		addEventListener("hide", () => this._private.downloadStatus.abort());

		let close = document.createElement("a");
		close.href = "#";
		close.addEventListener("click", () => this.hide());
		close.innerHTML = "<i>&#x1011</i>";
		this.head.appendChild(close);
	}

	get(url)
	{
		this._private.selector.addEventListener("selected", event =>
		{
			this.body.removeChild(this._private.selector);
			this.body.appendChild(this._private.downloadStatus);
			url = new URL(url).setParameter("type", event.detail).toString();
			this._private.downloadStatus.download("get", url, null);
			this._private.downloadStatus.addEventListener("done", () => this.hide());
		});
		this.show();
	}

	post(url, data)
	{
		this._private.selector.addEventListener("selected", event =>
		{
			this.body.removeChild(this._private.selector);
			this.body.appendChild(this._private.downloadStatus);
			url = new URL(url).setParameter("type", event.detail).toString();
			this._private.downloadStatus.download("post", url, data);
			this._private.downloadStatus.addEventListener("done", () => this.hide());
		});
		this.show();
	}
}



customElements.define('g-report-dialog', GReportDialog);