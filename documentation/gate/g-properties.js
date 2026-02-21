let template=document.createElement("template");template.innerHTML=`
<style data-element="g-properties">:host(*){display:inline-grid;grid-template-columns:auto auto}label{padding:4px;display:flex;font-weight:700;align-items:center;justify-content:flex-end}span{padding:4px;display:flex;align-items:center;justify-content:flex-start}
</style>`,customElements.define("g-properties",class extends HTMLElement{constructor(){super(),this.attachShadow({mode:"open"}),this.shadowRoot.appendChild(template.content.cloneNode(!0))}set value(t){Array.from(this.shadowRoot.querySelectorAll("label, span")).forEach(e=>e.remove()),Object.getOwnPropertyNames(t).forEach(e=>{this.shadowRoot.appendChild(document.createElement("label")).innerHTML=e,this.shadowRoot.appendChild(document.createElement("span")).innerHTML=t[e]})}attributeChangedCallback(){this.value=JSON.parse(this.getAttribute("value"))}static get observedAttributes(){return["value"]}});

//# sourceMappingURL=g-properties.js.map
