import { Duplex } from 'stream';
import { YError } from 'yerror';
const DEFAULT_BUFFER_STREAM_OPTIONS = {
    objectMode: false,
};
/**
 * Buffer the stream content and bring it into the provided callback
 */
class BufferStream extends Duplex {
    _options = DEFAULT_BUFFER_STREAM_OPTIONS;
    _bufferCallback;
    _finished = false;
    _buffer = [];
    /**
     * @param bufferCallback {Function} A function to handle the buffered content.
     * @param options {Object} inherits of Stream.Duplex, the options are passed to the parent constructor so you can use it's options too.
     * @param options.objectMode {boolean} Use if piped in streams are in object mode. In this case, an array of the buffered will be transmitted to the callback function.
     */
    constructor(bufferCallback, options) {
        super(options);
        if (!(bufferCallback instanceof Function)) {
            throw new YError('E_BAD_CALLBACK');
        }
        this._options = {
            ...DEFAULT_BUFFER_STREAM_OPTIONS,
            ...options,
        };
        this._bufferCallback =
            bufferCallback.length === 1
                ? ((err, payload, cb) => {
                    bufferCallback(payload)
                        .then((result) => {
                        cb(err, result);
                    })
                        .catch((err) => {
                        cb(err);
                    });
                })
                : bufferCallback;
        this.once('finish', this._bufferStreamCallbackWrapper);
        this.on('error', this._bufferStreamError);
    }
    _write(chunk, encoding, done) {
        this._buffer.push(chunk);
        done();
    }
    _read() {
        if (this._finished) {
            while (this._buffer.length) {
                if (!this.push(this._buffer.shift())) {
                    break;
                }
            }
            if (0 === this._buffer.length) {
                this.push(null);
            }
        }
    }
    _bufferStreamCallbackWrapper(err) {
        const buffer = (this._options.objectMode
            ? this._buffer
            : Buffer.concat(this._buffer));
        err = err || null;
        this._bufferCallback(err, buffer, (err2, buf) => {
            setImmediate(() => {
                this.removeListener('error', this._bufferStreamError);
                if (err2) {
                    this.emit('error', err2);
                }
                this._buffer = (buf == null ? [] : buf instanceof Buffer ? [buf] : buf);
                this._finished = true;
                this._read();
            });
        });
    }
    _bufferStreamError(err) {
        if (this._finished) {
            return;
        }
        this._bufferStreamCallbackWrapper(err);
    }
}
/**
 * Utility function if you prefer a functional way of using this lib
 * @param bufferCallback
 * @param options
 * @returns Stream
 */
export function bufferStream(bufferCallback, options = DEFAULT_BUFFER_STREAM_OPTIONS) {
    return new BufferStream(bufferCallback, options);
}
/**
 * Utility function to buffer objet mode streams
 * @param bufferCallback
 * @param options
 * @returns Stream
 */
export function bufferObjects(bufferCallback, options) {
    return new BufferStream(bufferCallback, {
        ...options,
        objectMode: true,
    });
}
export { BufferStream };
//# sourceMappingURL=index.js.map