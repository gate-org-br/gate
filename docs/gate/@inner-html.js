import"./trigger.js";import s from"./dom.js";import c from"./data-url.js";import d from"./request-builder.js";import f from"./response-handler.js";window.addEventListener("@inner-html",function(t){let o=t.composedPath(),{method:n,action:m,parameters:[r],form:l,signal:i}=t.detail,a=s.navigate(t,r).orElseThrow(`${r} is not a valid selector`);fetch(d.build(n,m,l),{signal:i}).then(f.text).then(e=>{if(i?.aborted||!document.contains(a))return t.resolve(o);a.innerHTML=e,t.success(o,c.ofHTML(e))}).catch(e=>t.failure(o,e))});

//# sourceMappingURL=@inner-html.js.map
