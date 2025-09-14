let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style data-element="g-side-menu">* {
    box-sizing: border-box;
}

:host(*) {
    margin: 0;
    border: none;
    color: #222;
    padding: 10px;
    z-index: 1000;
    overflow-y: auto;
    overflow-x: hidden;
    font-size: 14px;
    min-width: 220px;
    width: fit-content;
    border-radius: 12px;
    transition: all 0.3s ease;
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
}

::slotted(hr) {
    max-height: 0;
    border: none;
    border-top: 1px solid #e0e0e0;
    margin: 8px 0;
}

::slotted(a),
::slotted(button),
::slotted(.g-command) {
    display: flex;
    align-items: center;
    width: 100%;
    max-height: 48px;
    flex-basis: 48px;

    font-size: 0;
    padding: 10px 16px;
    font-family: inherit;
    text-decoration: none;

    border: none;
    outline: none;
    cursor: pointer;

    overflow: hidden;
    border-radius: 8px;
    white-space: nowrap;
    text-overflow: ellipsis;

    color: var(--main9, #222);
    background-color: transparent;

    transition: all 0.25s ease;
}

::slotted(a:hover),
::slotted(button:hover),
::slotted(.g-command:hover) {
    color: var(--main9-hover, #000);
    background-color: var(--hovered, #f0f0f0);
    transform: translateX(2px);
}

:host([open]) ::slotted(*) {
    gap: 12px;
    font-size: inherit;
    transition: all 0.25s ease;
}

::slotted(a.active),
::slotted(button.active),
::slotted(.g-command.active) {
    background-color: #e6f0ff;
    color: #0044cc;
}

:host(*)::-webkit-scrollbar {
    width: 6px;
}

:host(*)::-webkit-scrollbar-thumb {
    background-color: rgba(0, 0, 0, 0.1);
    border-radius: 3px;
}

:host(*)::-webkit-scrollbar-track {
    background: transparent;
}

:host([open]) ::slotted(*) {
    gap: 12px;
    font-size: inherit;
}

@media (min-width: 1000px) {
    :host(*) ::slotted(*) {
        gap: 12px;
        font-size: inherit;
    }
}</style>`;
import WindowListenerHTMLElement from './window-listener-html-element.js';

const sheet = new CSSStyleSheet()
sheet.replaceSync(`g-side-menu g-icon { order: -1; font-size: 16px }`)
document.adoptedStyleSheets = [...document.adoptedStyleSheets, sheet]

customElements.define('g-side-menu', class extends WindowListenerHTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({ mode: "open" });
		this.shadowRoot.innerHTML = template.innerHTML;

		this.addEventListener("click", event =>
		{
			if (!this.hasAttribute("open"))
			{
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();
				this.toggleAttribute("open");
			}
		});

		this.addEventListener("mouseenter", event => this.setAttribute("open", ""));
		this.addEventListener("mouseleave", event => this.removeAttribute("open"));

		this.addWindowListener("click", () =>
		{
			if (this.hasAttribute("open"))
				this.removeAttribute("open")
		});
	}

	connectedCallback()
	{
		Array.from(this.children)
			.filter(e => e.tagName !== "HR")
			.filter(e => !e.querySelector('g-icon'))
			.forEach(e => e.appendChild(document.createElement("g-icon")).innerHTML = '&#x1003;')
	}
})
