import"./trigger.js";import c from"./dom.js";import s from"./data-url.js";import d from"./request-builder.js";import h from"./response-handler.js";window.addEventListener("@outer-html",function(t){let o=t.composedPath(),{method:a,action:m,parameters:[r],form:i}=t.detail,l=c.navigate(t,r).orElseThrow(`${r} is not a valid selector`);fetch(d.build(a,m,i)).then(h.text).then(e=>{let n=document.createRange().createContextualFragment(e);l.replaceWith(n),t.success(o,new s("text/html",e).toString())}).catch(e=>t.failure(o,e))});

//# sourceMappingURL=@outer-html.js.map
