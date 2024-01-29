/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { TournamentParticipant } from './TournamentParticipant';
export type Tournament = {
    id?: number;
    createdAt?: string;
    updatedAt?: string;
    creator?: string;
    title?: string;
    description?: string;
    startsAt?: string;
    endsAt?: string;
    participants?: Array<TournamentParticipant>;
};

