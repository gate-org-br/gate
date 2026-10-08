import"./trigger.js";import l from"./dom.js";import c from"./data-url.js";import d from"./request-builder.js";import f from"./response-handler.js";window.addEventListener("@after-begin",function(e){let o=e.composedPath(),{method:n,action:s,parameters:[r],form:m,signal:a}=e.detail,i=l.navigate(e,r).orElseThrow(`${r} is not a valid selector`);fetch(d.build(n,s,m),{signal:a}).then(f.text).then(t=>{if(a?.aborted||!document.contains(i))return e.resolve(o);i.insertAdjacentHTML("afterbegin",t),e.success(o,c.ofHTML(t))}).catch(t=>e.failure(o,t))});

//# sourceMappingURL=@after-begin.js.map
