export class Log {
    _disabled;
    constructor(disabled) {
        this.disabled = disabled || false;
    }
    get disabled() {
        return this._disabled;
    }
    set disabled(val) {
        this._disabled = val;
    }
    log = (message, ...optionalParams) => {
        if (this.logger)
            this.logger(message);
        if (this.disabled)
            return () => { };
        return console.log(message, ...optionalParams);
    };
    error = (message, ...optionalParams) => {
        if (this.logger)
            this.logger(message);
        if (this.disabled)
            return () => { };
        return console.error(message, ...optionalParams);
    };
    logger = (message) => { };
}
export const log = new Log();
//# sourceMappingURL=log.js.map