import"./trigger.js";import l from"./dom.js";import c from"./data-url.js";import d from"./request-builder.js";import f from"./response-handler.js";window.addEventListener("@before-begin",function(e){let o=e.composedPath(),{method:n,action:s,parameters:[r],form:m,signal:i}=e.detail,a=l.navigate(e,r).orElseThrow(`${r} is not a valid selector`);fetch(d.build(n,s,m),{signal:i}).then(f.text).then(t=>{if(i?.aborted||!document.contains(a))return e.resolve(o);a.insertAdjacentHTML("beforebegin",t),e.success(o,c.ofHTML(t))}).catch(t=>e.failure(o,t))});

//# sourceMappingURL=@before-begin.js.map
