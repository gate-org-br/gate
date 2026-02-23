let m=document.createElement("template");m.innerHTML=`
	<fieldset><label data-size="4">
			Nome:
			<span><input required id='name' type='text' tabindex="1"/></span></label><label data-size="4">
			M\xE1scara:
			<span><input id='mask' type='text' tabindex="1"/></span></label><label data-size="2">
			Colunas:
			<span><select id='size' tabindex="1"><option value=""></option><option value="0">1 Coluna</option><option value="1">2 Colunas</option><option value="2">4 Colunas</option><option value="3">8 Colunas</option></select></span></label><label data-size="2">
			Multiplo:
			<span><select id='multiple' tabindex="1"><option value=""></option><option value="true">Sim</option><option value="false">N\xE3o</option></select></span></label><label data-size="2">
			Requerido:
			<span><select id='required' tabindex="1"><option value=""></option><option value="true">Sim</option><option value="false">N\xE3o</option></select></span></label><label data-size="2">
			Tamanho Max:
			<span><input id='maxlength' min='1' type='number' tabindex="1"/></span></label><label data-size="8">
			Express\xE3o Regular:
			<span style='flex-basis: 120px;'><textarea id="pattern" tabindex="1"></textarea></span></label><label data-size="8">
			Descri\xE7\xE3o:
			<span style='flex-basis: 120px;'><textarea id="description" tabindex="1"></textarea></span></label><label data-size="8">
			Op\xE7\xF5es:
			<span style='flex-basis: 120px;'><textarea id="options" tabindex="1"></textarea></span></label><label data-size="8">
			Valor:
			<span style='flex-basis: 120px;'><textarea id="value" tabindex="1"></textarea></span></label></fieldset>
<style data-element="g-field-editor">*{box-sizing:border-box}:host(*){display:flex;align-items:stretch;flex-direction:column;justify-content:stretch}fieldset{padding:0;border:none}
</style>`;import h from"./stylesheets.js";customElements.define("g-field-editor",class extends HTMLElement{constructor(){super(),this.attachShadow({mode:"open"}),this.shadowRoot.appendChild(m.content.cloneNode(!0)),h("input.css","fieldset.css").forEach(e=>this.shadowRoot.appendChild(e))}set value(e){let p=this.shadowRoot.getElementById("name"),l=this.shadowRoot.getElementById("mask"),i=this.shadowRoot.getElementById("size"),u=this.shadowRoot.getElementById("multiple"),r=this.shadowRoot.getElementById("required"),o=this.shadowRoot.getElementById("maxlength"),s=this.shadowRoot.getElementById("pattern"),n=this.shadowRoot.getElementById("description"),t=this.shadowRoot.getElementById("options"),a=this.shadowRoot.getElementById("value");e?(p.value=e.name||"",l.value=e.mask||"",i.value=e.size||"",u.value=e.multiple||!1,r.value=e.required||!1,o.value=e.maxlength||"",s.value=e.pattern||"",n.value=e.description||"",e.options&&e.options.forEach(d=>t.value=t.value?t.value+`
`+d:d),e.value&&e.value.forEach(d=>a.value=a.value?a.value+`
`+d:d)):p.value=l.value=i.value=u.value=r.value=o.value=s.value=n.value=t.value=a.value=""}get value(){let e={},p=this.shadowRoot.getElementById("name").value;e.name=p||"";let l=this.shadowRoot.getElementById("mask").value;l&&(e.mask=l);let i=this.shadowRoot.getElementById("size").value;i&&(e.size=i),this.shadowRoot.getElementById("multiple").value==="true"&&(e.multiple=!0),this.shadowRoot.getElementById("required").value==="true"&&(e.required=!0);let o=this.shadowRoot.getElementById("maxlength").value;o&&(e.maxlength=o);let s=this.shadowRoot.getElementById("pattern").value;s&&(e.pattern=s);let n=this.shadowRoot.getElementById("description").value;n&&(e.description=n);let t=this.shadowRoot.getElementById("options").value;t&&(e.options=t.split(`
`));let a=this.shadowRoot.getElementById("value").value;return a&&(e.value=a.split(`
`)),e}validate(){let e=this.shadowRoot.getElementById("name");return!e.value||!e.value.trim().length?(e.reportValidity(),!1):!0}attributeChangedCallback(){this.value=JSON.parse(this.getAttribute("value"))}static get observedAttributes(){return["value"]}});

//# sourceMappingURL=g-field-editor.js.map
