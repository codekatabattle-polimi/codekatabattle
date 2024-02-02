/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BattleTest } from './BattleTest';
export type BattleDTO = {
    tournamentId: number;
    title: string;
    description: string;
    startsAt?: string;
    endsAt?: string;
    language: BattleDTO.language;
    tests?: Array<BattleTest>;
    enableSAT?: boolean;
    enableManualEvaluation?: boolean;
    timelinessBaseScore?: number;
};
export namespace BattleDTO {
    export enum language {
        GOLANG = 'GOLANG',
        PYTHON = 'PYTHON',
    }
}

