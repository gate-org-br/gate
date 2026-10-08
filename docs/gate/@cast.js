import"./trigger.js";import o from"./data-url.js";import l from"./request-builder.js";import c from"./response-handler.js";window.addEventListener("@cast",function(t){let e=t.composedPath(),p=e[0]||t.target,{method:r,action:i,parameters:[d],form:m,signal:n}=t.detail;fetch(l.build(r,i,m),{signal:n}).then(c.dataURL).then(a=>{let s=o.parse(a);t.success(e,new o(d,s.data).toString())}).catch(a=>t.failure(e,a))});

//# sourceMappingURL=@cast.js.map
