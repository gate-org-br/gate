import c from"./data-url.js";import d from"./request-builder.js";import n from"./response-handler.js";import"./trigger.js";window.addEventListener("@tail",function(o){let e=o.composedPath(),{method:r,action:a,parameters:[m=1],form:s,signal:i}=o.detail;fetch(d.build(r,a,s),{signal:i}).then(n.json).then(t=>{if(i?.aborted)return o.resolve(e);t=t.slice(-m),o.success(e,c.ofJSON(t))}).catch(t=>o.failure(e,t))});

//# sourceMappingURL=@tail.js.map
