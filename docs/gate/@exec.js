import"./trigger.js";import p from"./data-url.js";import m from"./request-builder.js";import u from"./response-handler.js";window.addEventListener("@exec",function(t){let a=t.composedPath(),o=a[0]||t.target,{method:i,action:n,parameters:[s],form:c,signal:d}=t.detail;fetch(m.build(i,n,c),{signal:d}).then(u.dataURL).then(e=>{let l=new Function("result",`return ${s}`).bind(o)(),r=p.parse(e);l(r.contentType.startsWith("application/json")?JSON.parse(r.data):r.data),t.success(a,e)}).catch(e=>t.failure(a,e))});

//# sourceMappingURL=@exec.js.map
