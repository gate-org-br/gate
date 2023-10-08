let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style>:host(*)
{
	display: flex;
	flex-direction: column;
	box-shadow: 1px 1px 2px 0px var(--main6);
}
</style>`;

/* global customElements */

customElements.define('g-accordion', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this.addEventListener("expanded", event =>
		{
			if (!this.multiple)
				Array.from(this.children)
					.filter(e => e !== event.target)
					.forEach(e => e.expanded = false);
		});
	}

	get multiple()
	{
		return this.hasAttribute("multiple");
	}

	set multiple(value)
	{
		if (value)
			this.setAttribute("multiple", "");
		else
			this.removeAttribute("multiple", "");
	}
});