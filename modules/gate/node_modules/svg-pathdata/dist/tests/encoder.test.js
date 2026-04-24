import { describe, test, expect } from '@jest/globals';
import { encodeSVGPath } from '../index.js';
describe('SVGPathDataEncoder', () => {
    test('should not work when the command is forgotten', () => {
        expect(() => encodeSVGPath(undefined)).toThrow(new TypeError("Cannot read properties of undefined (reading 'type')"));
    });
    test('should fail when a bad command is given', () => {
        expect(() => encodeSVGPath({
            type: 'plop',
            x: 0,
            y: 0,
        })).toThrow(new Error('Unexpected command type "plop" at index 0.'));
    });
});
//# sourceMappingURL=encoder.test.js.map