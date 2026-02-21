let o=document.createElement("template");o.innerHTML=`
	<dialog><header>
			Selecione um tipo de relat\xF3rio
			<a id='cancel' href="#"><g-icon>
					&#X1011;
				</g-icon></a></header><section><button id='PDF'><g-icon>&#x2218;</g-icon>PDF</button><button id='XLS'><g-icon>&#x2221;</g-icon>XLS</button><button id='DOC'><g-icon>&#x2220;</g-icon>DOC</button><button id='CSV'><g-icon>&#x2222;</g-icon>CSV</button></section></dialog>
<style data-element="g-report-picker">dialog{width:600px;height:fit-content}section{gap:8px;width:100%;height:auto;display:flex;flex-wrap:wrap;align-items:center;justify-content:flex-start}button{gap:8px;flex-grow:1;display:flex;height:128px;color:#006;cursor:pointer;font-size:24px;min-width:128px;border-radius:3px;align-items:center;text-decoration:none;flex-direction:column;justify-content:center;background-color:#fff}button:hover{background-color:var(--hovered)}section g-icon{font-size:50px}
</style>`;import"./g-icon.js";import d from"./g-window.js";export default class n extends d{constructor(){super(),this.addEventListener("cancel",()=>this.hide()),this.addEventListener("commit",()=>this.hide()),this.shadowRoot.appendChild(o.content.cloneNode(!0)),this.shadowRoot.getElementById("cancel").addEventListener("click",()=>this.dispatchEvent(new CustomEvent("cancel"))),Array.from(this.shadowRoot.querySelectorAll("button")).forEach(e=>e.addEventListener("click",()=>this.dispatchEvent(new CustomEvent("commit",{detail:e.id}))))}static pick(e){let t=window.top.document.createElement("g-report-picker");return t.caption=e,t.show(),new Promise((i,c)=>{t.addEventListener("cancel",()=>c(new Error("Cancel"))),t.addEventListener("commit",r=>i(r.detail))})}}customElements.define("g-report-picker",n);

//# sourceMappingURL=g-report-picker.js.map
