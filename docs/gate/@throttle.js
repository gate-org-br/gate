import"./trigger.js";import"./trigger-event.js";let i=new WeakMap;window.addEventListener("@throttle",function(t){let e=t.composedPath(),o=e[0]||t.target,{parameters:[r="1000"]}=t.detail,a=Date.now();r=parseInt(r);let l=i.get(o)||0;a-l>r?(i.set(o,a),t.success(e)):t.resolve(e)});

//# sourceMappingURL=@throttle.js.map
