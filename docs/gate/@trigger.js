import f from"./dom.js";import l from"./trigger.js";import n from"./request-builder.js";import p from"./response-handler.js";window.addEventListener("@trigger",function(t){let r=t.composedPath(),{cause:i,method:o,action:a,parameters:[s],form:m,signal:c}=t.detail;fetch(n.build(o,a,m),{signal:c}).then(p.dataURL).then(e=>{t.success(r,e),f.navigate(t,s).ifPresent(d=>l(i,d))}).catch(e=>t.failure(r,e))});

//# sourceMappingURL=@trigger.js.map
