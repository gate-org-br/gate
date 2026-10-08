import { Transform } from 'stream';
import { SVGPathData } from 'svg-pathdata';
export { fileSorter } from './filesorter.js';
export * from './iconsdir.js';
export * from './metadata.js';
export type SVGIcons2SVGFontStreamOptions = {
    fontName: string;
    fontId: string;
    fixedWidth: boolean;
    descent: number;
    ascent?: number;
    round: number;
    metadata: string;
    usePathBounds: boolean;
    normalize?: boolean;
    preserveAspectRatio?: boolean;
    centerHorizontally?: boolean;
    centerVertically?: boolean;
    fontWeight?: number;
    fontHeight?: number;
    fontStyle?: string;
    callback?: (glyphs: Glyph[]) => void;
};
export type Glyph = {
    name: string;
    width: number;
    height: number;
    defaultHeight?: number;
    defaultWidth?: number;
    unicode: string[];
    paths?: SVGPathData[];
};
export declare class SVGIcons2SVGFontStream extends Transform {
    private _options;
    glyphs: Glyph[];
    constructor(options: Partial<SVGIcons2SVGFontStreamOptions>);
    _transform(svgIconStream: any, _unused: any, svgIconStreamCallback: any): void;
    _flush(svgFontFlushCallback: any): void;
}
