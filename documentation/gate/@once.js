import"./trigger.js";let a=new WeakSet;window.addEventListener("@once",function(e){let t=e.composedPath(),r=t[0]||e.target;if(a.has(r))return e.resolve(t);a.add(r),e.success(t)});

//# sourceMappingURL=@once.js.map
