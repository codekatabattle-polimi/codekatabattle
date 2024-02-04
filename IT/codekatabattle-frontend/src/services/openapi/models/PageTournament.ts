/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { PageableObject } from './PageableObject';
import type { SortObject } from './SortObject';
import type { Tournament } from './Tournament';
export type PageTournament = {
    totalPages?: number;
    totalElements?: number;
    first?: boolean;
    last?: boolean;
    size?: number;
    content?: Array<Tournament>;
    number?: number;
    sort?: SortObject;
    pageable?: PageableObject;
    numberOfElements?: number;
    empty?: boolean;
};

