import"./trigger.js";import i from"./data-url.js";import p from"./clipboard.js";window.addEventListener("@clipboard",function(r){let a=r.composedPath(),{action:t}=r.detail;if(!t?.startsWith("data:"))return r.failure(a,new Error("@clipboard target only supports data URLs"));try{p.copy(i.parse(t).data),r.success(a,t)}catch(o){r.failure(a,o)}});

//# sourceMappingURL=@clipboard.js.map
