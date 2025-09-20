let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style data-element="g-theme-selector">* {
	box-sizing: border-box;

}

:host(*) {
	gap: 12px;
	display: flex;
	color: inherit;
	flex-shrink: 0;
	font-size: 16px;
	cursor: pointer;
	align-items: center;
	justify-content: center;
}

:host::before
{
	content: '\\2203';
	font-family: 'gate';
}

:host([value='true'])::before
{
	content: '\\2164';
}

@media (prefers-color-scheme: dark)
{
	content: '\\2164';
	
	:host([value='true'])::before
	{
		content: '\\2203';
	}
}</style>`;
/* global customElements, template */
customElements.define('g-theme-selector', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this.addEventListener("click", () =>
			this.value = this.value === "true" ? "false" : 'true');
	}
	get value()
	{
		return this.getAttribute("value") || "false";
	}
	set value(value)
	{
		this.setAttribute("value", value);
	}
	connectedCallback()
	{
		this.setAttribute("title", "Change theme");
		this.value = localStorage.getItem('theme') || "false";
	}
});