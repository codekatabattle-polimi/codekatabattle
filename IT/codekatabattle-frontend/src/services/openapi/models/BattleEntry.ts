/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Battle } from './Battle';
import type { BattleEntryProcessResult } from './BattleEntryProcessResult';
import type { BattleParticipant } from './BattleParticipant';
export type BattleEntry = {
    id?: number;
    createdAt?: string;
    updatedAt?: string;
    battle?: Battle;
    participant?: BattleParticipant;
    status?: BattleEntry.status;
    processResult?: BattleEntryProcessResult;
    score?: number;
};
export namespace BattleEntry {
    export enum status {
        QUEUED = 'QUEUED',
        TESTING = 'TESTING',
        ANALYZING = 'ANALYZING',
        COMPLETED = 'COMPLETED',
    }
}

