/* global customElements */

customElements.define('g-desk-pane', class extends HTMLElement
{
	constructor()
	{
		super();

		this.addEventListener("click", function (e)
		{
			let target = e.target.closest('g-desk-pane');
			if (target !== this)
			{
				let backup = Array.from(this.children);
				let elements = Array.from(target.children)
					.filter(e => e.tagName !== 'I');
				backup.forEach(e => e.parentNode.removeChild(e));
				elements.forEach(e => this.appendChild(e));

				let reset = this.appendChild(document.createElement("a"));
				reset.href = "#";
				reset.style.color = "#660000";
				reset.innerText = "Retornar";
				reset.appendChild(document.createElement("i")).innerHTML = "&#X2023";

				reset.addEventListener("click", () =>
				{
					reset.parentNode.removeChild(reset);
					elements.forEach(e => target.appendChild(e));
					backup.forEach(e => this.appendChild(e));
					e.preventDefault();
					e.stopPropagation();
				});

				e.preventDefault();
				e.stopPropagation();
			}

		}, true);
	}
});

Array.from(document.querySelectorAll("ul.DeskPane")).forEach(component =>
	{
		var root = document.createElement("a");
		root.icons = Array.from(component.children);
		root.onclick = function ()
		{
			reset.style.display = "none";
			links.forEach(e => e.parentNode.style.display = "none");
			this.icons.forEach(e => e.style.display = "");
			component.dispatchEvent(new CustomEvent("selected", {detail: this}));
		};

		let links = Array.from(component.getElementsByTagName("a"));
		links.forEach(link =>
		{
			link.icons = Array.from(link.parentNode.children)
				.filter(e => e.tagName === "UL")
				.flatMap(e => Array.from(e.children));
			link.icons.forEach(e => e.link = link);
			link.icons.forEach(e => e.style.display = "none");

			if (link.icons.length)
				link.addEventListener("click", function (event)
				{
					links.forEach(e => e.parentNode.style.display = "none");
					this.icons.forEach(e => e.style.display = "");
					reset.style.display = "";
					reset.firstChild.onclick = () => {
						(this.parentNode.link || root).click();
					};
					component.dispatchEvent(new CustomEvent("selected", {detail: this}));
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();
				});
		});

		links.map(e => e.parentNode).forEach(e => component.appendChild(e));


		let reset = component.appendChild(document.createElement("li"));
		reset.className = "Reset";
		reset.style.display = "none";
		reset.appendChild(document.createElement("a"));
		reset.firstChild.innerHTML = "Retornar";
		reset.firstChild.setAttribute("href", "#");
		reset.firstChild.appendChild(document.createElement("i")).innerHTML = "&#X2023";
	});