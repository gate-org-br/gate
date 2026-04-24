let template = document.createElement("template");
template.innerHTML = `
	<div id='panel1'><slot name="L"></slot></div><button id='handle' 
		title='Mostrar ambos os paineis'></button><div id='panel2'><slot name="R"></slot></div>
<style data-element="g-splitter">* {
	box-sizing: border-box
}

:host(*) {
	height: 100%;
	display: flex;
	min-height: 0;
	align-items: stretch;
}

div {
	flex: 1 1 50%;
	min-height: 0;
	align-self: stretch;
}

div[hidden] {
	flex-basis: 0;
	display: none;
}

button {
	padding: 0;
	width: 10px;
	border: none;

	margin: 0;
	margin-left: 4px;
	margin-right: 4px;

	cursor: col-resize;
	background-color: var(--main2, #F0F0F0);
}</style>`;
/* global customElements, template */


customElements.define('g-splitter', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		const panel1 = this.shadowRoot.getElementById("panel1");
		const panel2 = this.shadowRoot.getElementById("panel2");
		const handle = this.shadowRoot.getElementById('handle');

		let currentX;
		let initialFlexBasis;
		let isDragging = false;

		handle.addEventListener('mousedown', event =>
		{
			event.preventDefault();
			event.stopPropagation();
			isDragging = true;
			currentX = event.clientX;
			initialFlexBasis = panel1.offsetWidth / this.offsetWidth;
		});

		window.addEventListener('mouseup', () => isDragging = false);

		window.addEventListener('mousemove', event =>
		{
			if (isDragging)
			{
				event.preventDefault();
				event.stopPropagation();
				const deltaX = event.clientX - currentX;
				const flexBasis = initialFlexBasis * this.offsetWidth + deltaX;
				const containerWidth = this.offsetWidth;
				const collapseThreshold = 50;

				if (flexBasis < collapseThreshold)
				{
					panel1.setAttribute("hidden", "");
					panel2.style.flexBasis = "100%";
					panel2.removeAttribute("hidden");
				} else if ((containerWidth - flexBasis) < collapseThreshold)
				{
					panel2.setAttribute("hidden", "");
					panel1.style.flexBasis = "100%";
					panel1.removeAttribute("hidden");
				} else
				{
					panel1.style.flexBasis = `${flexBasis}px`;
					panel2.style.flexBasis = `${containerWidth - flexBasis}px`;
					panel1.removeAttribute("hidden");
					panel2.removeAttribute("hidden");
				}
			}
		});

		handle.addEventListener("dblclick", () =>
		{
			panel1.style.flexBasis = "50%";
			panel2.style.flexBasis = "50%";
			panel1.removeAttribute("hidden");
			panel2.removeAttribute("hidden");
		});
	}

	connectedCallback()
	{
		this.firstElementChild.setAttribute("slot", "L");
		this.lastElementChild.setAttribute("slot", "R");
	}
});