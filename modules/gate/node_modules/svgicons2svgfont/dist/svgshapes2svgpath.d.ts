declare const svgShapesToPath: {
    rectToPath: typeof svgShapesToPathRectToPath;
    polylineToPath: typeof svgShapesToPathPolylineToPath;
    lineToPath: typeof svgShapesToPathLineToPath;
    circleToPath: typeof svgShapesToPathCircleToPath;
    polygonToPath: typeof svgShapesToPathPolygonToPath;
};
export default svgShapesToPath;
declare function svgShapesToPathRectToPath(attributes: any): string;
declare function svgShapesToPathPolylineToPath(attributes: any): string;
declare function svgShapesToPathLineToPath(attributes: any): string;
declare function svgShapesToPathCircleToPath(attributes: any): string;
declare function svgShapesToPathPolygonToPath(attributes: any): string;
