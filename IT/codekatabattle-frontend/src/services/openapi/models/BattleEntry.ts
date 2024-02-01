/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Battle } from './Battle';
import type { BattleParticipant } from './BattleParticipant';
import type { BattleTestResult } from './BattleTestResult';
export type BattleEntry = {
    id?: number;
    createdAt?: string;
    updatedAt?: string;
    battle?: Battle;
    participant?: BattleParticipant;
    status?: BattleEntry.status;
    testResults?: Array<BattleTestResult>;
    score?: number;
};
export namespace BattleEntry {
    export enum status {
        QUEUED = 'QUEUED',
        RUNNING = 'RUNNING',
        ANALYZING = 'ANALYZING',
        COMPLETED = 'COMPLETED',
    }
}

