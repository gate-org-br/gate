/* global customElements, GOverflow, GSelection */

class GSelect extends HTMLElement
{
	constructor()
	{
		super();

		this.attachShadow({mode: 'open'});
		var main = this.shadowRoot.appendChild(document.createElement("div"));
		main.style = "display: flex; flex-direction: column";

		var select = main.appendChild(document.createElement("div"));
		select.style = "display: flex; justify-content: space-between";

		var label = select.appendChild(document.createElement("label"));
		label.style = "flex-grow: 1; cursor: pointer";
		label.innerText = "0 selecionados";

		var link = select.appendChild(document.createElement("a"));
		link.style = "font-family: gate; flex-basis: 32px; cursor: pointer";
		link.innerHTML = "&#X2276;";

		var popup = main.appendChild(document.createElement("div"));
		popup.style = "display: none; flex-direction: column; position: fixed; margin-top: 32px; background-color: var(--main-tinted50); z-index: 10000";
		popup.appendChild(document.createElement("slot"));

		select.addEventListener("click", e =>
		{
			e.preventDefault();
			e.stopPropagation();
			popup.style.width = select.clientWidth + "px";
			popup.style.display = popup.style.display === "flex" ? "none" : "flex";
			link.innerHTML = popup.style.display === "flex" ? "&#X2278;" : "&#X2276;";
		});

		this.addEventListener("change", () =>
		{
			let checked = Array.from(this.querySelectorAll("input")).filter(e => e.checked);
			label.innerText = checked.length + " selecionados";
		});

		window.addEventListener("load", () =>
		{
			let checked = Array.from(this.querySelectorAll("input")).filter(e => e.checked);
			label.innerText = checked.length + " selecionados";
			Array.from(this.querySelectorAll("input")).forEach(e => e.addEventListener("change", () => this.dispatchEvent(new CustomEvent("change"))));
		});

		window.addEventListener("resize", () =>
		{
			link.innerHTML = "&#X2276;";
			popup.style.display = "none";
		});
	}
}

customElements.define("g-select", GSelect);