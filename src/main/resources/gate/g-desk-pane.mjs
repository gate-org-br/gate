let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style>* {
	box-sizing: border-box
}

:host(*)
{
	margin: 0;
	padding: 0;
	width: 100%;
	color: black;
	display: flex;
	flex-wrap: wrap;
	align-items: flex-start;
	justify-content: flex-start;
	background-color: transparent;
}

::slotted(hr:last-child),
::slotted(hr:first-child),
:host([child]) > ::slotted(a),
:host([child]) > ::slotted(hr),
:host([child]) > ::slotted(button),
:host([child]) > ::slotted(g-desk-pane)
{
	display: none;
}

::slotted(hr)
{
	border: solid 4px #BBBBBB;
	flex-basis: calc(100% - 8px);
}

::slotted(a),
::slotted(button),
::slotted(g-desk-pane)
{
	margin: 4px;
	padding: 8px;
	display: flex;
	color: inherit;
	cursor: pointer;
	border-radius: 5px;
	align-items: center;
	font-weight: inherit;
	border: 2px solid white;
	background-color: #E8E8E8;
	border: 1px solid #E0E0E0;
	justify-content: flex-start;
	flex-basis: calc(100% - 8px);
}

::slotted(a:hover),
::slotted(button:hover),
::slotted(g-desk-pane:hover)
{
	background-color:  #FFFACD;
}

::slotted(a[x8]),
::slotted(button[x8]),
::slotted(g-desk-pane[x8]),
::slotted(a[data-size='8']),
::slotted(button[data-size='8']),
::slotted(g-desk-pane[data-size='8'])
{
	flex-basis: 100%;
}

@media only screen and (min-width: 768px) {

	::slotted(a[x1]),
	::slotted(button[x1]),
	::slotted(g-desk-pane[x1]),
	::slotted(a[data-size='1']),
	::slotted(button[data-size='1']),
	::slotted(g-desk-pane[data-size='1'])
	{
		flex-basis: calc(50% - 8px);
	}
}

@media only screen and (min-width: 992px) {
	::slotted(a),
	::slotted(button),
	::slotted(g-desk-pane)
	{
		flex-basis: calc(50% - 8px);
	}

	::slotted(a[x1]),
	::slotted(button[x1]),
	::slotted(g-desk-pane[x1]),
	::slotted(a[data-size='1']),
	::slotted(button[data-size='1']),
	::slotted(g-desk-pane[data-size='1'])
	{
		flex-basis: calc(25% - 8px);
	}

	::slotted(a[x2]),
	::slotted(button[x2]),
	::slotted(g-desk-pane[x2]),
	::slotted(a[data-size='2']),
	::slotted(button[data-size='2']),
	::slotted(g-desk-pane[data-size='2'])
	{
		flex-basis: calc(50% - 8px);
	}
}

@media only screen and (min-width: 1200px) {
	::slotted(a),
	::slotted(button),
	::slotted(g-desk-pane)
	{
		flex-basis: calc(25% - 8px);
	}

	::slotted(a[x1]),
	::slotted(button[x1]),
	::slotted(g-desk-pane[x1]),
	::slotted(a[data-size='1']),
	::slotted(button[data-size='1']),
	::slotted(g-desk-pane[data-size='1'])
	{
		flex-basis: calc(12.5% - 8px);
	}

	::slotted(a[x2]),
	::slotted(button[x2]),
	::slotted(g-desk-pane[x2]),
	::slotted(a[data-size='2']),
	::slotted(button[data-size='2']),
	::slotted(g-desk-pane[data-size='2'])
	{
		flex-basis: calc(25% - 8px);
	}

	::slotted(a[x4]),
	::slotted(button[x4]),
	::slotted(g-desk-pane[x4]),
	::slotted(a[data-size='4']),
	::slotted(button[data-size='4']),
	::slotted(g-desk-pane[data-size='4'])
	{
		flex-basis: calc(50% - 8px);
	}
}</style>`;

/* global customElements */

customElements.define('g-desk-pane', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this.addEventListener("click", function (e)
		{
			let target = e.target.closest('g-desk-pane');
			if (target !== this)
			{
				let backup = Array.from(this.children);
				let elements = Array.from(target.children)
					.filter(e => e.tagName !== 'I'
							&& e.tagName !== 'IMG');
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

	connectedCallback()
	{
		Array.from(this.querySelectorAll("hr + hr"))
			.forEach(e => e.remove());
		Array.from(this.querySelectorAll("g-desk-pane"))
			.forEach(e => e.setAttribute("child", true));
	}
});