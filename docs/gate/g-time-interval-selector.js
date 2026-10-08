let template=document.createElement("template");template.innerHTML=`
	<g-time-selector id='min'></g-time-selector><g-time-selector id='max'></g-time-selector>
<style data-element="g-time-interval-selector">:host(*){gap:4px;display:grid;grid-template-columns:1fr 1fr}
</style>`,customElements.define("g-time-interval-selector",class extends HTMLElement{constructor(){super(),this.attachShadow({mode:"open"}),this.shadowRoot.innerHTML=template.innerHTML,this.shadowRoot.getElementById("min").addEventListener("selected",()=>this.dispatchEvent(new CustomEvent("selected",{detail:this.selection}))),this.shadowRoot.getElementById("max").addEventListener("selected",()=>this.dispatchEvent(new CustomEvent("selected",{detail:this.selection})))}set min(e){this.shadowRoot.getElementById("min").time=e}get min(){return this.shadowRoot.getElementById("min").time}set max(e){this.shadowRoot.getElementById("max").time=e}get max(){return this.shadowRoot.getElementById("max").time}get selection(){let e=this.shadowRoot.getElementById("min").selection,t=this.shadowRoot.getElementById("max").selection;return e&&t?e+" - "+t:null}});

//# sourceMappingURL=g-time-interval-selector.js.map
