/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Battle } from './Battle';
import type { BattleParticipant } from './BattleParticipant';
export type BattleEntry = {
    id?: number;
    createdAt?: string;
    updatedAt?: string;
    battle?: Battle;
    participant?: BattleParticipant;
    score?: number;
};

