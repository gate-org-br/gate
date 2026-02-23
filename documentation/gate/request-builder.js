export default class u{static build(e,s,r,l){(!s||s==="#")&&(s="data:text/plain,");let t=new Headers;if(l&&t.append("Content-Type",l),r instanceof HTMLFormElement?r=new FormData(r):r||(r=""),e==="get")return new Request(s);if(e==="delete"||e==="head"||e==="options")return new Request(s,{method:e});if(e==="post"||e==="put"||e==="patch")return new Request(s,{method:e,headers:t,body:r})}}

//# sourceMappingURL=request-builder.js.map
