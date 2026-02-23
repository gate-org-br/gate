let t=document.createElement("template");t.innerHTML=`
	<slot></slot>
<style data-element="g-grid-header">*{box-sizing:border-box}:host(*){height:32px;cursor:inherit;display:table-row}:host(:empty){display:none}::slotted(g-grid-cell){top:0;position:sticky;font-weight:700;color:var(--base1);border-color:var(--base5);background-color:var(--base6)}
</style>`;import"./g-grid-cell.js";customElements.define("g-grid-header",class extends HTMLElement{constructor(){super(),this._private={},this.attachShadow({mode:"open"}),this.shadowRoot.innerHTML=t.innerHTML}get index(){return[...this.parentNode.children].indexOf(this)}get length(){return this.children.length}cell(e=this.length){for(;this.length<=e;)this.appendChild(document.createElement("g-grid-cell"));return this.children[e]}});

//# sourceMappingURL=g-grid-header.js.map
