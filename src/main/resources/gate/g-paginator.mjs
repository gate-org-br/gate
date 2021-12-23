let template = document.createElement("template");
template.innerHTML = `
 <style>:host {
	float: right;
	margin: 4px;
}</style>`;

/* global customElements */

customElements.define('g-paginator', class extends HTMLElement
{

});