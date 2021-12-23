/* global customElements */

import URL from './url.mjs';
import GWindow from './g-window.mjs';

customElements.define('g-report-dialog', class extends GWindow
{
	constructor()
	{
		super();
		this._private.selector = document.createElement('g-report-selector');
		this._private.downloadStatus = document.createElement('g-download-status');
		addEventListener("hide", () => this._private.downloadStatus.abort());

	}

	connectedCallback()
	{
		super.connectedCallback();
		this.classList.add("g-report-dialog");

		this.body.appendChild(this._private.selector);

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
});





