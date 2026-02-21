let template=document.createElement("template");template.innerHTML=`
	<g-date-selector id='date'></g-date-selector><g-time-selector id='time'></g-time-selector>
<style data-element="g-date-time-selector">:host(*){display:grid;grid-template-columns:1fr 1fr}
</style>`,customElements.define("g-date-time-selector",class extends HTMLElement{constructor(){super(),this.attachShadow({mode:"open"}),this.shadowRoot.innerHTML=template.innerHTML,this.shadowRoot.getElementById("date").addEventListener("selected",()=>this.dispatchEvent(new CustomEvent("selected",{detail:this.selection}))),this.shadowRoot.getElementById("time").addEventListener("selected",()=>this.dispatchEvent(new CustomEvent("selected",{detail:this.selection})))}get selection(){let e=this.shadowRoot.getElementById("date").selection,t=this.shadowRoot.getElementById("time").selection;return e&&t?e+" "+t:null}set date(e){this.shadowRoot.getElementById("date").date=e}});

//# sourceMappingURL=g-date-time-selector.js.map
