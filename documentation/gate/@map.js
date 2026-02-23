import"./trigger.js";import i from"./data-url.js";import m from"./request-builder.js";import l from"./response-handler.js";window.addEventListener("@map",function(e){let n=e.composedPath(),o=n[0]||e.target,{method:p,action:c,parameters:[s],form:d}=e.detail;fetch(m.build(p,c,d)).then(l.dataURL).then(t=>{let a=i.parse(t),r=new Function("result",`return ${s}`).bind(o)();a.contentType.startsWith("application/json")?t=JSON.stringify(r(JSON.parse(a.data))):t=r(a.data),e.success(n,new i(a.contentType,t).toString())}).catch(t=>e.failure(n,t))});

//# sourceMappingURL=@map.js.map
