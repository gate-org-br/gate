import"./trigger.js";window.addEventListener("@log",function(o){const t=o.composedPath(),c=t[0]||o.target,{method:a,action:r,form:i,parameters:[s]=[],context:l}=o.detail;o.resolve(t),console.log(o.toLog(s)),o.success(t)});

//# sourceMappingURL=@log.js.map
