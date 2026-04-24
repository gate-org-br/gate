import type { SVGCommand } from '../types.js';
/**
 * Reverses the order of path commands to go from end to start
 * IMPORTANT: This function expects absolute commands as input.
 * It doesn't convert relative to absolute - use SVGPathDataTransformer.TO_ABS() first if needed.
 * @param commands SVG path commands in absolute form to reverse
 * @param preserveSubpathOrder If true, keeps subpaths in their original order
 * @returns New SVG commands in reverse order with absolute coordinates
 */
export declare function REVERSE_PATH(commands: SVGCommand[], preserveSubpathOrder?: boolean): SVGCommand[];
