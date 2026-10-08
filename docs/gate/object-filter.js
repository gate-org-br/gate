export default class e{static filter(t,n){return t.filter(r=>e.contains(r,n))}static contains(t,n){return t==null?!1:typeof t=="string"?t.toLowerCase().includes(n):typeof t=="number"?t.toString().includes(n):Array.isArray(t)?t.some(r=>e.contains(r,n)):typeof t=="object"?Object.values(t).some(r=>e.contains(r,n)):!1}}

//# sourceMappingURL=object-filter.js.map
