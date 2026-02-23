let l=document.createElement("template");l.innerHTML=`
	<g-slider id='h' size="5" value='0'></g-slider><g-slider id='m' size="5" value='0'></g-slider>
<style data-element="g-time-selector">:host(*){flex-grow:1;display:grid;grid-template-columns:1fr 1fr}
</style>`;import"./g-slider.js";customElements.define("g-time-selector",class extends HTMLElement{constructor(){super(),this.attachShadow({mode:"open"}),this.shadowRoot.appendChild(l.content.cloneNode(!0));let t=this.shadowRoot.getElementById("h");t.prev=e=>(e+23)%24,t.next=e=>(e+1)%24,t.format=e=>"00".concat(String(e)).slice(-2),t.addEventListener("update",()=>this.dispatchEvent(new CustomEvent("selected",{detail:this.selection})));let s=this.shadowRoot.getElementById("m");s.prev=e=>(e+50)%60,s.next=e=>(e+10)%60,s.format=e=>"00".concat(String(e)).slice(-2),s.addEventListener("update",()=>this.dispatchEvent(new CustomEvent("selected",{detail:this.selection})))}get selection(){let t=this.shadowRoot.getElementById("h"),s=this.shadowRoot.getElementById("m");return"00".concat(String(t.value)).slice(-2)+":"+"00".concat(String(s.value)).slice(-2)}});

//# sourceMappingURL=g-time-selector.js.map
