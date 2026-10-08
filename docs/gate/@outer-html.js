import"./trigger.js";import n from"./dom.js";import c from"./data-url.js";import d from"./request-builder.js";import f from"./response-handler.js";window.addEventListener("@outer-html",function(t){let e=t.composedPath(),{method:m,action:l,parameters:[r],form:s,signal:a}=t.detail,i=n.navigate(t,r).orElseThrow(`${r} is not a valid selector`);fetch(d.build(m,l,s),{signal:a}).then(f.text).then(o=>{if(a?.aborted||!document.contains(i))return t.resolve(e);i.outerHTML=o,t.success(e,c.ofHTML(o))}).catch(o=>t.failure(e,o))});

//# sourceMappingURL=@outer-html.js.map
