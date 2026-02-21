import"./trigger.js";let r=new WeakMap;window.addEventListener("@debounce",function(e){let t=e.composedPath(),o=t[0]||e.target,{parameters:[a="1000"]}=e.detail;e.resolve(t);let s=r.get(o);s&&clearTimeout(s);const i=setTimeout(()=>{r.delete(o),e.success(t)},parseInt(a));r.set(o,i)});

//# sourceMappingURL=@debounce.js.map
