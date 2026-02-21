import"./trigger.js";let o=new WeakMap;window.addEventListener("@throttle",function(t){let e=t.composedPath(),r=e[0]||t.target,{parameters:[a="1000"]}=t.detail,l=Date.now();a=parseInt(a);let s=o.get(r)||0;l-s>a?(o.set(r,l),t.success(e)):t.resolve(e)});

//# sourceMappingURL=@throttle.js.map
