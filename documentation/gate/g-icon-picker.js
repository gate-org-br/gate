let i=document.createElement("template");i.innerHTML=`
	<dialog><header>
			Selecione um \xEDcone
			<a id='cancel' href="#"><g-icon>
					&#X1011;
				</g-icon></a></header><section><g-icon-selector></g-icon-selector></section></dialog>
<style data-element="g-icon-picker">dialog{height:600px;min-width:320px;max-width:600px;width:calc(100% - 40px)}g-icon-selector{flex-grow:1}
</style>`;import"./g-icon.js";import"./g-icon-selector.js";import s from"./g-window.js";export default class n extends s{constructor(){super(),this.addEventListener("cancel",()=>this.hide()),this.addEventListener("commit",()=>this.hide()),this.shadowRoot.innerHTML=this.shadowRoot.innerHTML+i.innerHTML,this.shadowRoot.getElementById("cancel").addEventListener("click",()=>this.dispatchEvent(new CustomEvent("cancel"))),this.shadowRoot.querySelector("g-icon-selector").addEventListener("selected",t=>this.dispatchEvent(new CustomEvent("commit",{detail:t.detail.icon})))}static pick(){let e=window.top.document.createElement("g-icon-picker");return e.show(),new Promise((t,o)=>{e.addEventListener("cancel",()=>o(new Error("Cancel"))),e.addEventListener("commit",c=>t(c.detail))})}}customElements.define("g-icon-picker",n);

//# sourceMappingURL=g-icon-picker.js.map
