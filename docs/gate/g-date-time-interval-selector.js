let template=document.createElement("template");template.innerHTML=`
	<g-date-time-selector id='min'></g-date-time-selector><g-date-time-selector id='max'></g-date-time-selector>
<style data-element="g-date-time-interval-selector">:host(*){display:grid;grid-template-rows:1fr 1fr}
</style>`,customElements.define("g-date-time-interval-selector",class extends HTMLElement{constructor(){super(),this.attachShadow({mode:"open"}),this.shadowRoot.innerHTML=template.innerHTML,this.shadowRoot.getElementById("min").addEventListener("selected",()=>this.dispatchEvent(new CustomEvent("selected",{detail:this.selection}))),this.shadowRoot.getElementById("max").addEventListener("selected",()=>this.dispatchEvent(new CustomEvent("selected",{detail:this.selection})))}get selection(){let e=this.shadowRoot.getElementById("min").selection,t=this.shadowRoot.getElementById("max").selection;return e&&t?e+" - "+t:null}set min(e){this.shadowRoot.getElementById("min").date=e}set max(e){this.shadowRoot.getElementById("max").date=e}});

//# sourceMappingURL=g-date-time-interval-selector.js.map
