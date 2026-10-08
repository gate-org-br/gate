import"./trigger.js";import"./trigger-event.js";let o=new WeakSet;window.addEventListener("@once",function(e){let t=e.composedPath(),r=t[0]||e.target;if(o.has(r))return e.resolve(t);o.add(r),e.success(t)});

//# sourceMappingURL=@once.js.map
