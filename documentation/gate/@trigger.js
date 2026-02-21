import d from"./dom.js";import f from"./trigger.js";import l from"./request-builder.js";import n from"./response-handler.js";window.addEventListener("@trigger",function(t){let r=t.composedPath(),{cause:o,method:i,action:a,parameters:[m],form:s}=t.detail;fetch(l.build(i,a,s)).then(n.dataURL).then(e=>{t.success(r,e),d.navigate(t,m).ifPresent(c=>f(o,c))}).catch(e=>t.failure(r,e))});

//# sourceMappingURL=@trigger.js.map
