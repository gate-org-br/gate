import n from"./event-reader.js";export default class a extends EventTarget{#t=null;#e=null;get uuid(){return this.#t}constructor(t,e){super(),this.#t=t,this.#e=e}async start(){try{let t;for(;(t=await this.#e.next())!==null;)t?.data&&this.dispatchEvent(new CustomEvent(t.event||"message",{detail:t.data}))}catch(t){throw t.uuid=this.#t,t}}static async from(t){const e=new n(t),s=await e.next();if(!s||s.event!=="UUID")throw new Error("Expected UUID as first message");return new a(s.data.uuid,e)}}

//# sourceMappingURL=job.js.map
