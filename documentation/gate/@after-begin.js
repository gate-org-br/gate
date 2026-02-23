import"./trigger.js";import l from"./dom.js";import s from"./data-url.js";import c from"./request-builder.js";import d from"./response-handler.js";window.addEventListener("@after-begin",function(t){let o=t.composedPath(),{method:i,action:a,parameters:[r],form:m}=t.detail,n=l.navigate(t,r).orElseThrow(`${r} is not a valid selector`);fetch(c.build(i,a,m)).then(d.text).then(e=>{n.insertAdjacentHTML("afterbegin",e),t.success(o,new s("text/html",e).toString())}).catch(e=>t.failure(o,e))});

//# sourceMappingURL=@after-begin.js.map
