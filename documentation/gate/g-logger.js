let template=document.createElement("template");template.innerHTML=`
<style data-element="g-logger">:host(*){padding:4px;cursor:pointer;overflow-y:auto;border-radius:5px;background-color:#fff}span{height:16px;display:flex;align-items:center}
</style>`,customElements.define("g-logger",class extends HTMLElement{constructor(){super(),this.attachShadow({mode:"open"}),this.shadowRoot.appendChild(template.content.cloneNode(!0))}append(e){this.shadowRoot.appendChild(document.createElement("span")).innerHTML=e,this.shadowRoot.lastElementChild.scrollIntoView()}});

//# sourceMappingURL=g-logger.js.map
