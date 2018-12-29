class NavBar
{
	constructor(links, element)
	{
		var index = 0;

		if (!element)
			element = document.createElement("div");
		element.className = "NavBar";
		this.element = () => element;

		var frst = this.element.appendChild(document.createElement("a"));
		var prev = this.element.appendChild(document.createElement("a"));
		var text = this.element.appendChild(document.createElement("label"));
		var next = this.element.appendChild(document.createElement("a"));
		var last = this.element.appendChild(document.createElement("a"));

		frst.setAttribute("href", "#");
		prev.setAttribute("href", "#");
		next.setAttribute("href", "#");
		last.setAttribute("href", "#");

		frst.innerHTML = "&#x2213;";
		prev.innerHTML = "&#x2212;";
		next.innerHTML = "&#x2211;";
		last.innerHTML = "&#x2216;";

		frst.addEventListener("click", () => this.go(links[0]));
		prev.addEventListener("click", () => this.go(links[index - 1]));
		next.addEventListener("click", () => this.go(links[index + 1]));
		last.addEventListener("click", () => this.go(links[links.length - 1]));

		this.go = function (url)
		{
			if (this.element.dispatchEvent(new CustomEvent('go', {cancelable: true, detail: {navbar: this, target: url}})))
				index = Math.max(this._links.indexOf(url), 0);

			frst.setAttribute("navbar-disabled", String(index === 0));
			prev.setAttribute("navbar-disabled", String(index === 0));
			next.setAttribute("navbar-disabled", String(index === links.length - 1));
			last.setAttribute("navbar-disabled", String(index === links.length - 1));
			text.innerHTML = "Registro " + (index + 1) + " de " + links.length;
		};
	}
}