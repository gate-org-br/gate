import"./trigger.js";import p from"./data-url.js";import l from"./request-builder.js";import m from"./response-handler.js";window.addEventListener("@exec",function(t){let a=t.composedPath(),o=a[0]||t.target,{method:i,action:n,parameters:[c],form:s}=t.detail;fetch(l.build(i,n,s)).then(m.dataURL).then(e=>{let d=new Function("result",`return ${c}`).bind(o)(),r=p.parse(e);d(r.contentType.startsWith("application/json")?JSON.parse(r.data):r.data),t.success(a,e)}).catch(e=>t.failure(a,e))});

//# sourceMappingURL=@exec.js.map
