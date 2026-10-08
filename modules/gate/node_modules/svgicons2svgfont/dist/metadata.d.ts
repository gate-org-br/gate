export type MetadataServiceOptions = {
    prependUnicode: boolean;
    startUnicode: number;
};
export type FileMetadata = {
    path: string;
    name: string;
    unicode: string[] | string;
    renamed: boolean;
};
declare function getMetadataService(options?: Partial<MetadataServiceOptions>): (file: string, cb: (error: Error | null, metadata?: FileMetadata) => void) => void;
export { getMetadataService };
