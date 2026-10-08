import"./trigger.js";import"./dom.js";import m from"./data-url.js";import s from"./request-builder.js";import d from"./response-handler.js";const R="this.parent('.content, #content')";window.addEventListener("@content",function(t){let e=t.composedPath(),{method:i,action:c,form:a,signal:n}=t.detail,r=t.target.closest("#content, .content");fetch(s.build(i,c,a),{signal:n}).then(d.text).then(o=>{if(n?.aborted||!document.contains(r))return t.resolve(e);r.innerHTML=o,t.success(e,m.ofHTML(o))}).catch(o=>t.failure(e,o))});

//# sourceMappingURL=@content.js.map
