/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
export type EntityNotFoundException = {
    cause?: {
        stackTrace?: Array<{
            classLoaderName?: string;
            moduleName?: string;
            moduleVersion?: string;
            methodName?: string;
            fileName?: string;
            lineNumber?: number;
            nativeMethod?: boolean;
            className?: string;
        }>;
        message?: string;
        localizedMessage?: string;
    };
    stackTrace?: Array<{
        classLoaderName?: string;
        moduleName?: string;
        moduleVersion?: string;
        methodName?: string;
        fileName?: string;
        lineNumber?: number;
        nativeMethod?: boolean;
        className?: string;
    }>;
    message?: string;
    suppressed?: Array<{
        stackTrace?: Array<{
            classLoaderName?: string;
            moduleName?: string;
            moduleVersion?: string;
            methodName?: string;
            fileName?: string;
            lineNumber?: number;
            nativeMethod?: boolean;
            className?: string;
        }>;
        message?: string;
        localizedMessage?: string;
    }>;
    localizedMessage?: string;
};

