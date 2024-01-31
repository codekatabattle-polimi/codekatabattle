/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
export type BattleDTO = {
    tournamentId: number;
    title: string;
    description: string;
    startsAt?: string;
    endsAt?: string;
    language: BattleDTO.language;
    enableSAT: boolean;
};
export namespace BattleDTO {
    export enum language {
        GOLANG = 'GOLANG',
    }
}

