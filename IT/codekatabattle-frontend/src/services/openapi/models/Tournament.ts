/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { TournamentParticipant } from './TournamentParticipant';
export type Tournament = {
    id?: number;
    createdAt?: string;
    updatedAt?: string;
    creator: string;
    title: string;
    description?: string;
    startsAt: string;
    endsAt: string;
    privacy: Tournament.privacy;
    maxParticipants?: number;
    participants?: Array<TournamentParticipant>;
};
export namespace Tournament {
    export enum privacy {
        PUBLIC = 'PUBLIC',
        PRIVATE = 'PRIVATE',
    }
}

