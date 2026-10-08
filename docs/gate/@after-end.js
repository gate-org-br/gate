import"./trigger.js";import m from"./dom.js";import l from"./data-url.js";import c from"./request-builder.js";import f from"./response-handler.js";window.addEventListener("@after-end",function(e){let o=e.composedPath(),{method:n,action:s,parameters:[r],form:d,signal:a}=e.detail,i=m.navigate(e,r).orElseThrow(`${r} is not a valid selector`);fetch(c.build(n,s,d),{signal:a}).then(f.text).then(t=>{if(a?.aborted||!document.contains(i))return e.resolve(o);i.insertAdjacentHTML("afterend",t),e.success(o,l.ofHTML(t))}).catch(t=>e.failure(o,t))});

//# sourceMappingURL=@after-end.js.map
