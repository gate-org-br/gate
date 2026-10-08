import"./trigger.js";import d from"./dom.js";import p from"./data-url.js";import h from"./request-builder.js";import u from"./response-handler.js";window.addEventListener("@value",function(t){let o=t.composedPath(),{method:i,action:n,form:l,parameters:[r],signal:c}=t.detail,m=d.navigate(t,r).orElseThrow(`${r} is not a valid selector`);fetch(h.build(i,n,l),{signal:c}).then(e=>{const s=e.headers.get("content-type");return u.auto(e).then(a=>{m.value=a,t.success(o,new p(s,a).toString())})}).catch(e=>t.failure(o,e))});

//# sourceMappingURL=@value.js.map
