let h=document.createElement("template");h.innerHTML=`
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
</style>`;import v from"./stylesheets.js";customElements.define("g-field-editor",class extends HTMLElement{constructor(){super(),this.attachShadow({mode:"open"}),this.shadowRoot.appendChild(h.content.cloneNode(!0)),v("input.css","fieldset.css").forEach(e=>this.shadowRoot.appendChild(e))}set value(e){let a=this.shadowRoot.getElementById("name"),p=this.shadowRoot.getElementById("mask"),s=this.shadowRoot.getElementById("size"),o=this.shadowRoot.getElementById("multiple"),r=this.shadowRoot.getElementById("required"),m=this.shadowRoot.getElementById("maxlength"),n=this.shadowRoot.getElementById("pattern"),d=this.shadowRoot.getElementById("description"),l=this.shadowRoot.getElementById("options"),i=this.shadowRoot.getElementById("value");if(e){let t=e.schema;a.value=t.name||"",p.value=t.mask||"",s.value=t.size||"",o.value=t.multiple||!1,r.value=t.required||!1,m.value=t.maxlength||"",n.value=t.pattern||"",d.value=t.description||"",t.options&&t.options.forEach(u=>l.value=l.value?l.value+`
`+u:u),e.value&&e.value.forEach(u=>i.value=i.value?i.value+`
`+u:u)}else a.value=p.value=s.value=o.value=r.value=m.value=n.value=d.value=l.value=i.value=""}get value(){let e={schema:{}},a=e.schema,p=this.shadowRoot.getElementById("name").value;a.name=p||"";let s=this.shadowRoot.getElementById("mask").value;s&&(a.mask=s);let o=this.shadowRoot.getElementById("size").value;o&&(a.size=o),this.shadowRoot.getElementById("multiple").value==="true"&&(a.multiple=!0),this.shadowRoot.getElementById("required").value==="true"&&(a.required=!0);let n=this.shadowRoot.getElementById("maxlength").value;n&&(a.maxlength=n);let d=this.shadowRoot.getElementById("pattern").value;d&&(a.pattern=d);let l=this.shadowRoot.getElementById("description").value;l&&(a.description=l);let i=this.shadowRoot.getElementById("options").value;i&&(a.options=i.split(`
`));let t=this.shadowRoot.getElementById("value").value;return t&&(e.value=t.split(`
`)),e}validate(){let e=this.shadowRoot.getElementById("name");return!e.value||!e.value.trim().length?(e.reportValidity(),!1):!0}attributeChangedCallback(){this.value=JSON.parse(this.getAttribute("value"))}static get observedAttributes(){return["value"]}});

//# sourceMappingURL=g-field-editor.js.map
