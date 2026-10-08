let template=document.createElement("template");template.innerHTML=`
	<slot></slot>
<style data-element="g-card-pane">:host{gap:8px;display:flex;flex-wrap:wrap;align-items:flex-start;justify-content:flex-start}
</style>`,customElements.define("g-card-pane",class extends HTMLElement{constructor(){super(),this.attachShadow({mode:"open"}),this.shadowRoot.appendChild(template.content.cloneNode(!0))}});

//# sourceMappingURL=g-card-pane.js.map
