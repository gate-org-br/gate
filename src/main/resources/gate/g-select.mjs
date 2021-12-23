let template = document.createElement("template");
template.innerHTML = `
	<div id='main'>
		<div id='select'>
			<label id='label'>
				0 selecionados
			</label>
			<a id='link' href="#">
				&#X2276;
			</a>
		</div>

		<div id='modal'>
			<div id='dialog'>
				<div id='head'>
					<a id='close' href="#">
						&#X1011;
					</a>
				</div>
				<div id='elements'>
					<slot>
					</slot>
				</div>
			</div>
		</div>
	</div>
 <style>#main
{
	display: flex;
	flex-direction: column;
}

#select
{
	display: flex;
	justify-content: space-between;
}

#label
{
	flex-grow: 1;
	cursor: pointer;
}

#link
{
	cursor:pointer;
	flex-basis:32px;
	font-family:gate;
}

#modal
{
	top:0;
	left:0;
	right:0;
	bottom:0;
	display:none;
	position:fixed;
	align-items:center;
	justify-content:center;
}

#dialog
{
	height:400px;
	display: flex;
	flex-basis:50%;
	min-width:300px;
	max-width:500px;
	align-items:stretch;
	flex-direction: column;
	box-shadow:3px 10px 5px 0px rgba(0,0,0,0.75);
	border:1px solid var(--g-window-border-color);
}


#head {

	padding:4px;
	display:flex;
	flex-basis:32px;
	align-items:center;
	justify-content:right;
	background-color:var(--g-window-border-color);
}

#close
{
	width:24px;
	height:24px;
	display:flex;
	color:#FFFFFF;
	cursor:pointer;
	font-size:20px;
	font-family:gate;
	align-items:center;
	justify-content:center;
}

#elements
{
	padding:4px;
	flex-grow:1;
	display:flex;
	overflow:auto;
	flex-direction:column;
	background-color: var(--g-window-section-background-color);
}</style>`;

/* global customElements, template */

customElements.define("g-select", class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		let changed = false;
		let select = this.shadowRoot.getElementById("select");
		let label = this.shadowRoot.getElementById("label");
		let modal = this.shadowRoot.getElementById("modal");
		let dialog = this.shadowRoot.getElementById("dialog");
		let close = this.shadowRoot.getElementById("close");
		let slot = this.shadowRoot.querySelector("slot");

		dialog.addEventListener("click", event =>
		{
			event.preventDefault();
			event.stopPropagation();
		});

		select.addEventListener("click", e =>
		{
			e.preventDefault();
			e.stopPropagation();
			modal.style.display = "flex";
		});

		slot.addEventListener("click", event =>
		{
			event.stopPropagation();
			if (modal.style.display === "none")
				event.preventDefault();
		});

		modal.addEventListener("click", event =>
		{
			close.click();
			event.preventDefault();
			event.stopPropagation();
			modal.style.display = "none";

			if (changed)
				this.dispatchEvent(new CustomEvent("change"));
		});

		close.addEventListener("click", () =>
		{
			let labels = this.labels;
			label.innerText = labels.length === 1 ?
				labels[0] : labels.length + " selecionados";

			modal.style.display = "none";
			if (changed)
				this.dispatchEvent(new CustomEvent("change"));
		});

		slot.addEventListener("slotchange", () =>
		{
			Array.from(this.querySelectorAll("input"))
				.forEach(e => e.addEventListener("change", () => changed = true));

			let labels = this.labels;
			label.innerText = labels.length === 1 ?
				labels[0] : labels.length + " selecionados";

		});
	}

	get values()
	{
		return Array.from(this.querySelectorAll("input"))
			.filter(e => e.checked).map(e => e.value);
	}

	get labels()
	{
		return Array.from(this.querySelectorAll("input"))
			.filter(e => e.checked).map(e => e.parentNode.innerText);
	}

});