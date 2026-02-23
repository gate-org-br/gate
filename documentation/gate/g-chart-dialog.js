let a=document.createElement("template");a.innerHTML=`
	<dialog part='dialog'><header part='header' tabindex='1'><label id='caption'></label><g-chart-toolbar chart='chart'></g-chart-toolbar><a id='hide' href='#'><g-icon>
					&#x1011;
				</g-icon></a></header><section><g-chart id='chart'></g-chart></section></dialog>
<style data-element="g-chart-dialog">*{box-sizing:border-box}dialog{width:100%;height:100%;border-radius:0}@media only screen and (min-width:640px){dialog{border-radius:5px;width:calc(100% - 80px);height:calc(100% - 80px)}}section{display:flex;align-items:center;justify-content:center;background-color:#fff}g-chart{height:100%;flex-basis:100%;max-width:100%;max-height:100%}
</style>`;import"./g-icon.js";import c from"./g-window.js";export default class o extends c{constructor(){super(),this.shadowRoot.innerHTML+=a.innerHTML,this.shadowRoot.querySelector("#hide").addEventListener("click",()=>this.hide())}set type(e){this.shadowRoot.querySelector("g-chart").type=e}set caption(e){this.shadowRoot.querySelector("#caption").innerText=e}set value(e){this.shadowRoot.querySelector("g-chart").value=e}static show(e,i,r){let t=document.createElement("g-chart-dialog");t.show(),t.type=e,t.title=i,t.value=r}}customElements.define("g-chart-dialog",o);

//# sourceMappingURL=g-chart-dialog.js.map
