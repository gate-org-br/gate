let template=document.createElement("template");template.innerHTML=`
<style data-element="g-logger">:host(*){padding:4px;cursor:pointer;overflow-y:auto;border-radius:5px;color:var(--text1);border:1px solid var(--main3);background-color:var(--main2)}span{height:16px;display:flex;align-items:center}
</style>`,customElements.define("g-logger",class extends HTMLElement{constructor(){super(),this.attachShadow({mode:"open"}),this.shadowRoot.appendChild(template.content.cloneNode(!0)),this.addEventListener("click",()=>this.#e())}append(t){this.shadowRoot.appendChild(document.createElement("span")).innerHTML=t,this.shadowRoot.lastElementChild.scrollIntoView()}#e(){const t=Array.from(this.shadowRoot.querySelectorAll("span")).map(o=>o.textContent).join(`
`),e=document.createElement("a");e.href=URL.createObjectURL(new Blob([t],{type:"text/plain"})),e.download="log.txt",e.click(),URL.revokeObjectURL(e.href)}});

//# sourceMappingURL=g-logger.js.map
