window.addEventListener("load", function ()
{
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
				link.onclick = function ()
				{
					links.forEach(e => e.parentNode.style.display = "none");
					this.icons.forEach(e => e.style.display = "");
					reset.style.display = "";
					reset.firstChild.onclick = () => (this.parentNode.link || root).onclick();
					component.dispatchEvent(new CustomEvent("selected", {detail: this}));
				};
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
});