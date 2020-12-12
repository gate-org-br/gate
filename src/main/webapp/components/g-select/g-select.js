/* global customElements, GOverflow, GSelection */

class GSelect extends HTMLElement
{
	constructor()
	{
		super();

		this.attachShadow({mode: 'open'});
		let main = this.shadowRoot.appendChild(document.createElement("div"));
		main.style.display = "flex";
		main.style.flexDirection = "column";

		let select = main.appendChild(document.createElement("div"));
		select.style.display = "flex";
		select.style.justifyContent = "space-between";

		let label = select.appendChild(document.createElement("label"));
		label.style.flexGrow = "1";
		label.style.cursor = "pointer";
		label.innerText = "0 selecionados";

		let link = select.appendChild(document.createElement("a"));
		link.innerHTML = "&#X2276;";
		link.style.cursor = "pointer";
		link.style.flexBasis = "32px";
		link.style.fontFamily = "gate";

		let modal = main.appendChild(document.createElement("div"));
		modal.style.top = "0";
		modal.style.left = "0";
		modal.style.right = "0";
		modal.style.bottom = "0";
		modal.style.display = "none";
		modal.style.position = "fixed";
		modal.style.alignItems = "center";
		modal.style.justifyContent = "center";

		let dialog = modal.appendChild(document.createElement("div"));
		dialog.style = "display: flex; flex-direction: column";
		dialog.style.height = "400px";
		dialog.style.flexBasis = "50%";
		dialog.style.minWidth = "300px";
		dialog.style.maxWidth = "500px";
		dialog.style.alignItems = "stretch";
		dialog.style.boxShadow = "3px 10px 5px 0px rgba(0,0,0,0.75)";
		dialog.style.border = "1px solid var(--g-window-border-color)";

		let head = dialog.appendChild(document.createElement("div"));
		head.style.padding = "4px";
		head.style.display = "flex";
		head.style.flexBasis = "32px";
		head.style.alignItems = "center";
		head.style.justifyContent = "right";
		head.style.backgroundColor = "var(--g-window-border-color)";

		let close = head.appendChild(document.createElement("a"));
		close.style.width = "24px";
		close.style.height = "24px";
		close.style.display = "flex";
		close.innerHTML = "&#X1011;";
		close.style.fontSize = "20px";
		close.style.color = "#FFFFFF";
		close.style.cursor = "pointer";
		close.style.fontFamily = "gate";
		close.style.alignItems = "center";
		close.style.justifyContent = "center";

		let elements = dialog.appendChild(document.createElement("div"));
		elements.style.flexGrow = "1";
		elements.style.display = "flex";
		elements.style.padding = "4px";
		elements.style.overflow = "auto";
		elements.style.flexDirection = "column";
		elements.style.backgroundColor = "var(--g-window-section-background-color)";

		let slot = elements.appendChild(document.createElement("slot"));

		let changed = false;

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

}

customElements.define("g-select", GSelect);