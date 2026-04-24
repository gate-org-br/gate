import { Readable } from 'node:stream';
import { createReadStream, readdir } from 'node:fs';
import { fileSorter } from './filesorter.js';
import { getMetadataService, } from './metadata.js';
import debug from 'debug';
const warn = debug('svgicons2svgfont');
class SVGIconsDirStream extends Readable {
    _options;
    gotFilesInfos = false;
    fileInfos = [];
    dir;
    constructor(dir, options) {
        super({ objectMode: true });
        this._options = {
            metadataProvider: options.metadataProvider || getMetadataService(options),
        };
        if (dir instanceof Array) {
            this.dir = '';
            this._getFilesInfos(dir);
        }
        else {
            this.dir = dir;
        }
    }
    _getFilesInfos(files) {
        let filesProcessed = 0;
        this.fileInfos = [];
        // Ensure prefixed files come first
        files = files.slice(0).sort(fileSorter);
        files.forEach((file) => {
            this._options.metadataProvider((this.dir ? this.dir + '/' : '') + file, (err, metadata) => {
                filesProcessed++;
                if (err) {
                    this.emit('error', err);
                }
                if (metadata) {
                    if (metadata.renamed) {
                        warn('➕ - Saved codepoint: ' +
                            'u' +
                            metadata.unicode[0]
                                .codePointAt(0)
                                ?.toString(16)
                                .toUpperCase() +
                            ' for the glyph "' +
                            metadata.name +
                            '"');
                    }
                    this.fileInfos.push(metadata);
                }
                if (files.length === filesProcessed) {
                    // Reorder files
                    this.fileInfos.sort((infosA, infosB) => infosA.unicode[0] > infosB.unicode[0] ? 1 : -1);
                    // Mark directory as processed
                    this.gotFilesInfos = true;
                    // Start processing
                    this._pushSVGIcons();
                }
            });
        });
    }
    _pushSVGIcons() {
        let fileInfo;
        let svgIconStream;
        while (this.fileInfos.length) {
            fileInfo = this.fileInfos.shift();
            svgIconStream = createReadStream(fileInfo.path);
            svgIconStream.metadata = {
                name: fileInfo.name,
                unicode: fileInfo.unicode,
            };
            if (!this.push(svgIconStream)) {
                return;
            }
        }
        this.push(null);
    }
    _read() {
        if (this.dir) {
            readdir(this.dir, (err, files) => {
                if (err) {
                    this.emit('error', err);
                }
                this._getFilesInfos(files);
            });
            return;
        }
        if (this.gotFilesInfos) {
            this._pushSVGIcons();
        }
    }
}
export { SVGIconsDirStream };
//# sourceMappingURL=iconsdir.js.map