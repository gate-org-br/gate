/* global customElements */

class SideMenu extends Modal
{
	constructor(elements)
	{
		super();
		this.classList.add("g-side-menu");
		var menu = this.appendChild(document.createElement("div"));
		elements.forEach(e => menu.appendChild(e));
		this.addEventListener("click", e => e.stopPropagation() | this.hide());
	}

	showL()
	{
		setTimeout(() => this.classList.remove("R"), 100);
		setTimeout(() => this.classList.add("L"), 100);
	}

	showR()
	{
		setTimeout(() => this.classList.remove("L"), 100);
		setTimeout(() => this.classList.add("R"), 100);
	}
}

customElements.define('g-side-menu', SideMenu);