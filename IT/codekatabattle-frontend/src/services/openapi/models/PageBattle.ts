/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Battle } from './Battle';
import type { PageableObject } from './PageableObject';
import type { SortObject } from './SortObject';
export type PageBattle = {
    totalPages?: number;
    totalElements?: number;
    size?: number;
    content?: Array<Battle>;
    number?: number;
    sort?: SortObject;
    first?: boolean;
    last?: boolean;
    numberOfElements?: number;
    pageable?: PageableObject;
    empty?: boolean;
};

