import"./trigger.js";import d from"./dom.js";import l from"./data-url.js";import s from"./request-builder.js";import c from"./response-handler.js";window.addEventListener("@after-end",function(t){let o=t.composedPath(),{method:a,action:i,parameters:[r],form:m}=t.detail,n=d.navigate(t,r).orElseThrow(`${r} is not a valid selector`);fetch(s.build(a,i,m)).then(c.text).then(e=>{n.insertAdjacentHTML("afterend",e),t.success(o,new l("text/html",e).toString())}).catch(e=>t.failure(o,e))});

//# sourceMappingURL=@after-end.js.map
