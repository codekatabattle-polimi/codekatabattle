/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
export type TournamentDTO = {
    title: string;
    description: string;
    startsAt?: string;
    endsAt?: string;
    privacy: TournamentDTO.privacy;
    maxParticipants?: number;
    coordinators?: Array<string>;
};
export namespace TournamentDTO {
    export enum privacy {
        PUBLIC = 'PUBLIC',
        PRIVATE = 'PRIVATE',
    }
}

