/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Battle } from './Battle';
import type { PageableObject } from './PageableObject';
import type { SortObject } from './SortObject';
export type PageBattle = {
    totalElements?: number;
    totalPages?: number;
    size?: number;
    content?: Array<Battle>;
    number?: number;
    sort?: SortObject;
    numberOfElements?: number;
    pageable?: PageableObject;
    first?: boolean;
    last?: boolean;
    empty?: boolean;
};

