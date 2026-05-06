let template = document.createElement("template");
template.innerHTML = `
<style data-element="g-logger">:host(*)
{
	padding: 4px;
	cursor: pointer;
	overflow-y: auto;
	border-radius: 5px;
	color: var(--text1);
	border: 1px solid var(--main3);
	background-color: var(--main2);
}

span
{
	height: 16px;
	display: flex;
	align-items: center;
}
</style>`;
/* global customElements */


customElements.define('g-logger', class extends HTMLElement
{
    constructor()
    {
        super();
        this.attachShadow({mode: "open"});
        this.shadowRoot.appendChild(template.content.cloneNode(true));
        this.addEventListener('click', () => this.#download());
    }

    append(text)
    {
        this.shadowRoot.appendChild(document.createElement("span")).innerHTML = text;
        this.shadowRoot.lastElementChild.scrollIntoView();
    }

    #download()
    {
        const content = Array.from(this.shadowRoot.querySelectorAll('span'))
            .map(s => s.textContent)
            .join('\n');

        const a = document.createElement('a');
        a.href = URL.createObjectURL(new Blob([content], {type: 'text/plain'}));
        a.download = 'log.txt';
        a.click();
        URL.revokeObjectURL(a.href);
    }
});