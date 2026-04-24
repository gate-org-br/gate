import { Readable } from 'node:stream';
import { getMetadataService, FileMetadata, MetadataServiceOptions } from './metadata.js';
export type SVGIconsDirStreamOptions = {
    metadataProvider: ReturnType<typeof getMetadataService>;
};
export type SVGIconStream = Readable & {
    metadata: Pick<FileMetadata, 'name' | 'unicode'>;
};
declare class SVGIconsDirStream extends Readable {
    private _options;
    gotFilesInfos: boolean;
    fileInfos: FileMetadata[];
    dir: string;
    constructor(dir: string[], options: Partial<SVGIconsDirStreamOptions & MetadataServiceOptions>);
    _getFilesInfos(files: any): void;
    _pushSVGIcons(): void;
    _read(): void;
}
export { SVGIconsDirStream };
