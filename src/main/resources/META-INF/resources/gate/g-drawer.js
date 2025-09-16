let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style data-element="g-drawer">* {
    box-sizing: border-box;
}

:host {
    top: 0;
    right: 0;
    margin: 0;
    opacity: 0;
    border: none;
    display: flex;
    height: 100vh;
    z-index: 1000;
    color: black;
    font-size: 14px;
    border-radius: 0;
    pointer-events: none;
    flex-direction: column;
    width: min(320px, 80vw);
    background-color: #FFFFFF;
    transform: translateX(-100%);
    transition: transform 0.3s ease-in-out;
    box-shadow: -4px 0 12px rgba(0, 0, 0, 0.15);
}

:host(:popover-open) {
    opacity: 1;
    pointer-events: auto;
    transform: translateX(0);
}

header {
    padding: 16px;
    font-weight: bold;
    font-size: 16px;
    border-bottom: 1px solid #eee;
}

main {
    flex: 1;
    overflow-y: auto;
    padding: 8px 0;
}

footer {
    padding: 12px 16px;
    border-top: 1px solid #eee;
    background: #fafafa;
}

a,
label,
button,
::slotted(a),
::slotted(label),
::slotted(button),
::slotted(.g-command) {
    gap: 12px;
    border: none;
    display: flex;
    color: inherit;
    cursor: pointer;
    border-radius: 6px;
    font-size: inherit;
    padding: 12px 18px;
    align-items: center;
    white-space: nowrap;
    text-decoration: none;
    transition: background-color 0.2s ease, padding 0.2s ease;
    background-color: transparent;
    justify-content: flex-start;
}

a:hover,
label:hover,
button:hover,
::slotted(a:hover),
::slotted(button:hover),
::slotted(.g-command:hover),
::slotted(label:hover) {
    background-color: var(--hovered, #f5f5f5);
    padding-left: 24px;
}

a:focus,
label:focus,
button:focus,
::slotted(a:focus),
::slotted(label:focus),
::slotted(button:focus),
::slotted(.g-command:focus) {
    outline: none;
    box-shadow: inset 0 0 0 2px #0078d4;
}

label {
    flex-grow: 1;
    font-weight: 600;
}

a[data-icon]::before,
button[data-icon]::before,
label[data-icon]::before,
::slotted(a[data-icon])::before,
::slotted(label[data-icon])::before,
::slotted(button[data-icon])::before,
::slotted(.g-command[data-icon])::before {
    font-family: gate;
    content: attr(data-icon);
}

label::after,
::slotted(label)::after {
    color: #888;
    font-size: 12px;
    content: '\\3017';
    font-family: gate;
    margin-left: auto;
}</style>`;
import WindowListenerHTMLElement from './window-listener-html-element.js';

const sheet = new CSSStyleSheet()
sheet.replaceSync(`g-drawer g-icon { order: -1; font-size: 20px }`)
document.adoptedStyleSheets = [...document.adoptedStyleSheets, sheet]

customElements.define('g-drawer', class extends WindowListenerHTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({ mode: "open" });
		this.shadowRoot.innerHTML = template.innerHTML;

		const click = event => 
		{
			if (!event.composedPath().includes(this))
			{
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();
			}
			this.hide();
		};

		this.addEventListener("toggle", e =>
		{
			if (e.newState === "open")
				window.addEventListener("click",
					click, { once: true, capture: true });
			else
				window.removeEventListener("click", click, { capture: true });
		});
	}

	show()
	{
		this.showPopover();
	}

	hide()
	{
		this.hidePopover();
	}

	connectedCallback()
	{
		this.setAttribute("popover", "manual");
	}
})

window.addEventListener("click", function (event)
{
	const path = event.composedPath();
	if (path.some(e => e instanceof HTMLAnchorElement
		|| e instanceof HTMLButtonElement))
		return;

	const trigger = path.find(e => e.matches && e.matches("label:has(g-drawer)"));
	if (!trigger)
		return;

	event.preventDefault();
	event.stopPropagation();
	event.stopImmediatePropagation();

	const drawer = trigger.querySelector("g-drawer");
	drawer.show();
}, { capture: true });