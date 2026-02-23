let template=document.createElement("template");template.innerHTML=`
	<slot></slot>
<style data-element="g-paginator">:host{margin:4px;float:right;color:inherit}::slotted(a),::slotted(button),::slotted(.g-command){border:0;padding:0;color:inherit;font-size:12px;cursor:pointer;background:none;font-weight:400;text-decoration:none}
</style>`,customElements.define("g-paginator",class extends HTMLElement{constructor(){super(),this.attachShadow({mode:"open"}),this.shadowRoot.appendChild(template.content.cloneNode(!0))}});

//# sourceMappingURL=g-paginator.js.map
