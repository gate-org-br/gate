import"./trigger.js";import l from"./dom.js";import s from"./data-url.js";import c from"./request-builder.js";import d from"./response-handler.js";window.addEventListener("@before-begin",function(e){let o=e.composedPath(),{method:i,action:a,parameters:[r],form:m}=e.detail,n=l.navigate(e,r).orElseThrow(`${r} is not a valid selector`);fetch(c.build(i,a,m)).then(d.text).then(t=>{n.insertAdjacentHTML("beforebegin",t),e.success(o,new s("text/html",t).toString())}).catch(t=>e.failure(o,t))});

//# sourceMappingURL=@before-begin.js.map
