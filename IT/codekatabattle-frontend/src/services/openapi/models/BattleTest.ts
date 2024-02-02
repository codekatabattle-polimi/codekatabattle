/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
export type BattleTest = {
    name?: string;
    input?: string;
    expectedOutput?: string;
    givesScore?: number;
    privacy?: BattleTest.privacy;
};
export namespace BattleTest {
    export enum privacy {
        PUBLIC = 'PUBLIC',
        PRIVATE = 'PRIVATE',
    }
}

