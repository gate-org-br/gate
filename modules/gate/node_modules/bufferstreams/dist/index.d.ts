import { Duplex, type Writable } from 'stream';
export type BufferStreamOptions = {
    objectMode: boolean;
};
export type BufferStreamItem<O extends Partial<BufferStreamOptions>, T> = O extends {
    objectMode: true;
} ? T : Buffer;
export type BufferStreamPayload<O extends Partial<BufferStreamOptions>, T> = O extends {
    objectMode: true;
} ? T[] : Buffer;
export type BufferStreamHandler<O extends Partial<BufferStreamOptions>, T> = (payload: BufferStreamPayload<O, T>) => Promise<BufferStreamPayload<O, T>>;
export type BufferStreamCallback<O extends Partial<BufferStreamOptions>, T> = (err: Error | null, payload: BufferStreamPayload<O, T>, cb: (err: Error | null, payload?: null | BufferStreamPayload<O, T>) => void) => void;
/**
 * Buffer the stream content and bring it into the provided callback
 */
declare class BufferStream<T, O extends Partial<BufferStreamOptions>> extends Duplex {
    private _options;
    private _bufferCallback;
    private _finished;
    private _buffer;
    /**
     * @param bufferCallback {Function} A function to handle the buffered content.
     * @param options {Object} inherits of Stream.Duplex, the options are passed to the parent constructor so you can use it's options too.
     * @param options.objectMode {boolean} Use if piped in streams are in object mode. In this case, an array of the buffered will be transmitted to the callback function.
     */
    constructor(bufferCallback: BufferStreamCallback<O, T> | BufferStreamHandler<O, T>, options?: O);
    _write(chunk: BufferStreamItem<O, T>, encoding: Parameters<Writable['write']>[1], done: () => void): void;
    _read(): void;
    _bufferStreamCallbackWrapper(err: Error): void;
    _bufferStreamError(err: Error): void;
}
/**
 * Utility function if you prefer a functional way of using this lib
 * @param bufferCallback
 * @param options
 * @returns Stream
 */
export declare function bufferStream<T, O extends Partial<BufferStreamOptions>>(bufferCallback: BufferStreamCallback<O, T>, options?: O): BufferStream<T, O>;
/**
 * Utility function to buffer objet mode streams
 * @param bufferCallback
 * @param options
 * @returns Stream
 */
export declare function bufferObjects<T>(bufferCallback: BufferStreamCallback<{
    objectMode: true;
}, T>, options: Omit<BufferStreamOptions, 'objectMode'>): BufferStream<T, {
    objectMode: true;
}>;
export { BufferStream };
