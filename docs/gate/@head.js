import s from"./data-url.js";import c from"./request-builder.js";import n from"./response-handler.js";import"./trigger.js";window.addEventListener("@head",function(o){let e=o.composedPath(),{method:a,action:i,parameters:[d=1],form:m,signal:r}=o.detail;fetch(c.build(a,i,m),{signal:r}).then(n.json).then(t=>{if(r?.aborted)return o.resolve(e);t=t.slice(0,d),o.success(e,s.ofJSON(t))}).catch(t=>o.failure(e,t))});

//# sourceMappingURL=@head.js.map
