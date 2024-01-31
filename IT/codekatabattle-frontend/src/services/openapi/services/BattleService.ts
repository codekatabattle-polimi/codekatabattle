/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Battle } from '../models/Battle';
import type { BattleDTO } from '../models/BattleDTO';
import type { BattleEntry } from '../models/BattleEntry';
import type { BattleEntryDTO } from '../models/BattleEntryDTO';
import type { PageBattle } from '../models/PageBattle';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class BattleService {
    /**
     * Find battle by id
     * Find a battle by id
     * @param id
     * @returns Battle OK
     * @throws ApiError
     */
    public static findById1(
        id: number,
    ): CancelablePromise<Battle> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/battles/{id}',
            path: {
                'id': id,
            },
            errors: {
                400: `Bad Request`,
                401: `Unauthorized`,
                404: `Not Found`,
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * Update battle
     * Update a battle by providing battle id and new battle data
     * @param id
     * @param requestBody
     * @returns Battle OK
     * @throws ApiError
     */
    public static updateById1(
        id: number,
        requestBody: BattleDTO,
    ): CancelablePromise<Battle> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/battles/{id}',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                400: `Bad Request`,
                401: `Unauthorized`,
                404: `Not Found`,
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * Delete battle
     * Delete a battle by providing battle id
     * @param id
     * @returns Battle OK
     * @throws ApiError
     */
    public static deleteById1(
        id: number,
    ): CancelablePromise<Battle> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/battles/{id}',
            path: {
                'id': id,
            },
            errors: {
                400: `Bad Request`,
                401: `Unauthorized`,
                404: `Not Found`,
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * Leave a battle
     * Leave a battle by providing battle id and GitHub access token
     * @param id
     * @returns Battle OK
     * @throws ApiError
     */
    public static leave1(
        id: number,
    ): CancelablePromise<Battle> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/battles/{id}/leave',
            path: {
                'id': id,
            },
            errors: {
                400: `Bad Request`,
                401: `Unauthorized`,
                404: `Not Found`,
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * Join a battle
     * Join a battle by providing battle id and GitHub access token
     * @param id
     * @returns Battle OK
     * @throws ApiError
     */
    public static join1(
        id: number,
    ): CancelablePromise<Battle> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/battles/{id}/join',
            path: {
                'id': id,
            },
            errors: {
                400: `Bad Request`,
                401: `Unauthorized`,
                404: `Not Found`,
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * Find paginated battles
     * Find paginated battles by specifying page number and page size
     * @param page
     * @param size
     * @returns PageBattle OK
     * @throws ApiError
     */
    public static findAll1(
        page?: number,
        size: number = 10,
    ): CancelablePromise<PageBattle> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/battles',
            query: {
                'page': page,
                'size': size,
            },
            errors: {
                400: `Bad Request`,
                401: `Unauthorized`,
                404: `Not Found`,
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * Create battle
     * Create a new battle
     * @param requestBody
     * @returns Battle OK
     * @throws ApiError
     */
    public static create1(
        requestBody: BattleDTO,
    ): CancelablePromise<Battle> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/battles',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                400: `Bad Request`,
                401: `Unauthorized`,
                404: `Not Found`,
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * Submit battle entry
     * Submit build artifact to be tested and scored by providing battle id and build artifact URL
     * @param id
     * @param authorization
     * @param requestBody
     * @returns BattleEntry OK
     * @throws ApiError
     */
    public static submit(
        id: number,
        authorization: string,
        requestBody: BattleEntryDTO,
    ): CancelablePromise<BattleEntry> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/battles/{id}/submit',
            path: {
                'id': id,
            },
            headers: {
                'Authorization': authorization,
            },
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                400: `Bad Request`,
                401: `Unauthorized`,
                404: `Not Found`,
                500: `Internal Server Error`,
            },
        });
    }
}
