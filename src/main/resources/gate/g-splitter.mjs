let template = document.createElement("template");
template.innerHTML = `
	<slot name="L"></slot>
	<div></div>
	<slot name="R"></slot>
 <style>* {
	box-sizing: border-box
}

:host(*) {
	height: 100%;
	display: flex;
	align-items: stretch;
}

div
{
	flex-basis: 4px;
	margin-left: 4px;
	margin-right: 4px;
	cursor: col-resize;
	background-color: #999999;
}

::slotted(*)
{
	flex-basis: 50%;
}

</style>`;

/* global customElements, template */


customElements.define('g-splitter', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		const panel1 = this.firstElementChild;
		const panel2 = this.lastElementChild;

		let currentX;
		let initialFlexBasis;
		let isDragging = false;

		this.shadowRoot.querySelector('div').addEventListener('mousedown', event =>
		{
			event.preventDefault();
			event.stopPropagation();

			if (event.detail === 2)
			{
				panel1.style.flexBasis = '50%';
				panel2.style.flexBasis = '50%';
			} else
			{
				isDragging = true;
				currentX = event.clientX;
				initialFlexBasis = panel1.offsetWidth / this.offsetWidth;
			}
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
				panel1.style.flexBasis = `${flexBasis}px`;
				panel2.style.flexBasis = `${this.offsetWidth - flexBasis}px`;
			}
		});
	}

	connectedCallback()
	{
		this.firstElementChild.setAttribute("slot", "L");
		this.lastElementChild.setAttribute("slot", "R");
	}
});