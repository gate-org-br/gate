let a=document.createElement("template");a.innerHTML=`
	<slot></slot>
<style data-element="g-grid-cell">*{box-sizing:border-box}:host(*){padding:4px;cursor:inherit;display:table-cell;vertical-align:middle;background-color:transparent}
</style>`;import"./g-properties.js";function t(e){switch(typeof e){case"string":return document.createTextNode(e);case"number":return document.createTextNode(e);case"undefined":return document.createTextNode("");case"boolean":return document.createTextNode(e?"Sim":"N\xE3o");case"function":return t(e());case"object":if(!e)return document.createTextNode("");if(e instanceof HTMLElement)return e;if(Array.isArray(e)){let i=document.createElement("ul");return e.forEach(l=>i.appendChild(document.createElement("li")).appendChild(t(l))),i}let r=document.createElement("g-properties");return r.value=e,r}}customElements.define("g-grid-cell",class extends HTMLElement{constructor(){super(),this.attachShadow({mode:"open"}),this.shadowRoot.innerHTML=a.innerHTML}set value(e){this.firstChild&&this.firstChild.remove(),this.appendChild(t(e))}});

//# sourceMappingURL=g-grid-cell.js.map
