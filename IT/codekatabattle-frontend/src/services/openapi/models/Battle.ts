/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BattleEntry } from './BattleEntry';
import type { BattleParticipant } from './BattleParticipant';
import type { BattleTest } from './BattleTest';
import type { Tournament } from './Tournament';
export type Battle = {
    id?: number;
    createdAt?: string;
    updatedAt?: string;
    creator?: string;
    title?: string;
    description?: string;
    startsAt?: string;
    endsAt?: string;
    repositoryUrl?: string;
    repositoryId?: number;
    language: Battle.language;
    enableSAT?: boolean;
    tests?: Array<BattleTest>;
    timelinessBaseScore?: number;
    participants?: Array<BattleParticipant>;
    entries?: Array<BattleEntry>;
    tournament?: Tournament;
};
export namespace Battle {
    export enum language {
        GOLANG = 'GOLANG',
        PYTHON = 'PYTHON',
    }
}

