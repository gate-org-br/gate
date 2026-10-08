let template=document.createElement("template");template.innerHTML=`
	<slot></slot>
<style data-element="g-table-scroll">*{box-sizing:border-box}:host(*){gap:12px;height:100%;display:flex;overflow:auto;position:relative;align-items:stretch;flex-direction:column;justify-content:flex-start}
</style>`,customElements.define("g-table-scroll",class extends HTMLElement{constructor(){super(),this.attachShadow({mode:"open"}),this.shadowRoot.appendChild(template.content.cloneNode(!0))}connectedCallback(){Array.from(this.querySelectorAll("thead > tr > *")).forEach(t=>{t.style.top="0",t.style.position="sticky"}),Array.from(this.querySelectorAll("tfoot > tr > *")).forEach(t=>{t.style.bottom="0",t.style.position="sticky"})}});

//# sourceMappingURL=g-table-scroll.js.map
