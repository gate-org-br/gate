let template=document.createElement("template");template.innerHTML=`
	<g-month-selector id='min'></g-month-selector><g-month-selector id='max'></g-month-selector>
<style data-element="g-month-interval-selector">:host(*){gap:4px;display:grid;grid-template-columns:1fr 1fr}
</style>`,customElements.define("g-month-interval-selector",class extends HTMLElement{constructor(){super(),this.attachShadow({mode:"open"}),this.shadowRoot.innerHTML=template.innerHTML,this.shadowRoot.getElementById("min").addEventListener("selected",()=>this.dispatchEvent(new CustomEvent("selected",{detail:this.selection}))),this.shadowRoot.getElementById("max").addEventListener("selected",()=>this.dispatchEvent(new CustomEvent("selected",{detail:this.selection})))}get selection(){let e=this.shadowRoot.getElementById("min");e=e.selection;let t=this.shadowRoot.getElementById("max");return t=t.selection,e&&t?e+" - "+t:null}});

//# sourceMappingURL=g-month-interval-selector.js.map
