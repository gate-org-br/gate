let template = document.createElement("template");
template.innerHTML = `
	<label></label>
	<progress></progress>
	<div>
		<g-digital-clock>
		</g-digital-clock>
		<span>
			...
		</span>
	</div>
 <style>:host(*) {
	width: 100%;
	display: flex;
	padding: 10px;
	border-radius: 5px;
	align-items: stretch;
	flex-direction: column;
	justify-content: center;
	background-image: linear-gradient(to bottom, #FDFAE9 0%, #B3B0A4 100%);
}

label {
	font-size: 20px;
	flex-basis: 40px;
}

progress {
	width: 100%;
	flex-basis: 40px;
}

div {
	display: flex;
	flex-basis: 40px;
	align-items: center;
}

g-digital-clock
{
	flex-grow: 1;
	display:  flex;
	font-size:  12px;
	align-items:  center;
	justify-content: flex-start;
}


span
{
	flex-grow: 1;
	display:  flex;
	font-size:  12px;
	align-items:  center;
	justify-content: flex-end;
}
</style>`;

/* global customElements */

import resolve from './resolve.mjs';
import DataFormat from './data-format.mjs';

customElements.define('g-download-status', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	download(method, url, data)
	{
		let title = this.shadowRoot.querySelector("label");
		let progress = this.shadowRoot.querySelector("progress");
		let clock = this.shadowRoot.querySelector("g-digital-clock");
		let counter = this.shadowRoot.querySelector("span");
		
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
	}

	abort()
	{
		if (this.request)
			this.request.abort();
	}

	get(url)
	{
		this.download("GET", url, null);
	}

	post(url, data)
	{
		this.download("GET", url, data);
	}
});