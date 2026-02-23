let r=document.createElement("template");r.innerHTML=`
	<dialog><header><label id='caption'>
				Selecione um \xEDtem
			</label><a id='close' href="#"><g-icon>
					&#X1011;
				</g-icon></a></header><section><input type="TEXT" placeholder="Pesquisar"/><g-grid>
				Nenhum registro encontrado
			</g-grid></section><footer><g-coolbar><button id='clear' class='primary'>
					Limpar <g-icon>&#X2018;</g-icon></button><hr><button id='cancel' class='tertiary'>
					Cancelar <g-icon>&#X2027;</g-icon></button></g-coolbar></footer></dialog>
<style data-element="g-select-picker">dialog{min-width:320px;max-width:800px;height:fit-content;width:calc(100% - 40px)}dialog>section{gap:4px;padding:4px;display:grid;align-items:stretch;justify-items:stretch;align-content:stretch;justify-content:stretch;grid-template-rows:40px 400px}
</style>`;import"./g-icon.js";import"./g-grid.js";import c from"./g-window.js";import d from"./object-filter.js";export default class o extends c{#t;constructor(){super(),this.addEventListener("cancel",()=>this.hide()),this.addEventListener("commit",()=>this.hide()),this.shadowRoot.innerHTML=this.shadowRoot.innerHTML+r.innerHTML,this.shadowRoot.getElementById("close").addEventListener("click",()=>this.dispatchEvent(new CustomEvent("cancel"))),this.shadowRoot.getElementById("cancel").addEventListener("click",()=>this.dispatchEvent(new CustomEvent("cancel"))),this.shadowRoot.getElementById("clear").addEventListener("click",()=>this.dispatchEvent(new CustomEvent("commit",{detail:{value:{}}})));let t=this.shadowRoot.querySelector("g-grid");t.addEventListener("select",e=>this.dispatchEvent(new CustomEvent("commit",{detail:{index:e.detail.index,value:e.detail.value}})));let n=this.shadowRoot.querySelector("input");n.addEventListener("input",()=>t.dataset=d.filter(this.options,n.value))}set caption(t){this.shadowRoot.getElementById("caption").innerHTML=t}get caption(){return this.shadowRoot.getElementById("caption").innerHTML}get options(){return this.#t||[]}set options(t){this.#t=t,this.shadowRoot.querySelector("g-grid").dataset=t}static pick(t,n){if(typeof t=="string")return fetch(t).then(i=>i.ok?i.json():i.text().then(s=>{throw new Error(s)})).then(i=>o.pick(i,n));let e=window.top.document.createElement("g-select-picker");return e.options=t,n&&(e.caption=n),e.show(),new Promise((i,s)=>{e.addEventListener("cancel",()=>s(new Error("Cancel"))),e.addEventListener("commit",a=>i(a.detail))})}}customElements.define("g-select-picker",o);

//# sourceMappingURL=g-select-picker.js.map
