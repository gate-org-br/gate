export default class e{#t;constructor(t){this.#t=t}populate(t,r="value",u="label"){for(;t.firstChild;)t.removeChild(t.firstChild);t.tagName==="SELECT"&&(t.value=void 0,t.appendChild(document.createElement("option")).setAttribute("value",""));for(var a=0;a<this.#t.length;a++){var i=t.appendChild(document.createElement("option"));i.innerHTML=this.#t[a][u],i.setAttribute(t.tagName==="SELECT"?"value":"data-value",this.#t[a][r])}return this}}

//# sourceMappingURL=populator.js.map
