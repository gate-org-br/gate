import"./trigger.js";import d from"./dom.js";import l from"./data-url.js";import s from"./request-builder.js";import c from"./response-handler.js";window.addEventListener("@before-end",function(e){let o=e.composedPath(),{method:i,action:a,parameters:[r],form:m}=e.detail,n=d.navigate(e,r).orElseThrow(`${r} is not a valid selector`);fetch(s.build(i,a,m)).then(c.text).then(t=>{n.insertAdjacentHTML("beforeend",t),e.success(o,new l("text/html",t).toString())}).catch(t=>e.failure(o,t))});

//# sourceMappingURL=@before-end.js.map
