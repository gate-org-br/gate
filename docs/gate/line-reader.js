export default class l{#e;#r=[];#t="";#i=new TextDecoder;constructor(t){this.#e=t.body.getReader()}async next(){for(;;){if(this.#r.length>0)return this.#r.shift();let t,e=!1;const i=setTimeout(()=>{e=!0,this.#e.cancel().catch(()=>{})},6e4);try{t=await this.#e.read()}finally{clearTimeout(i)}if(e)throw new Error("Idle timeout");if(t.done){if(this.#t.trim().length===0)return null;const n=this.#t;return this.#t="",n}const s=this.#i.decode(t.value,{stream:!0}),r=(this.#t+s).split(/\r\n|\r|\n/g);this.#t=r.pop(),this.#r.push(...r)}}}

//# sourceMappingURL=line-reader.js.map
