/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { PageTournament } from '../models/PageTournament';
import type { Tournament } from '../models/Tournament';
import type { TournamentDTO } from '../models/TournamentDTO';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class TournamentService {
    /**
     * Find tournament by id
     * Find a tournament by id
     * @param id
     * @returns Tournament OK
     * @throws ApiError
     */
    public static findById(
        id: number,
    ): CancelablePromise<Tournament> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/tournaments/{id}',
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
     * Update tournament
     * Update a tournament by providing tournament id and new tournament data
     * @param id
     * @param requestBody
     * @returns Tournament OK
     * @throws ApiError
     */
    public static updateById(
        id: number,
        requestBody: TournamentDTO,
    ): CancelablePromise<Tournament> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/tournaments/{id}',
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
     * Delete tournament
     * Delete a tournament by providing tournament id
     * @param id
     * @returns Tournament OK
     * @throws ApiError
     */
    public static deleteById(
        id: number,
    ): CancelablePromise<Tournament> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/tournaments/{id}',
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
     * Leave a tournament
     * Leave a tournament by providing tournament id and GitHub access token
     * @param id
     * @returns Tournament OK
     * @throws ApiError
     */
    public static leave(
        id: number,
    ): CancelablePromise<Tournament> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/tournaments/{id}/leave',
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
     * Join a tournament
     * Join a tournament by providing tournament id and GitHub access token
     * @param id
     * @returns Tournament OK
     * @throws ApiError
     */
    public static join(
        id: number,
    ): CancelablePromise<Tournament> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/tournaments/{id}/join',
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
     * Find paginated tournaments
     * Find paginated tournaments by specifying page number and page size
     * @param page
     * @param size
     * @returns PageTournament OK
     * @throws ApiError
     */
    public static findAll(
        page?: number,
        size: number = 10,
    ): CancelablePromise<PageTournament> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/tournaments',
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
     * Create tournament
     * Create a new tournament
     * @param requestBody
     * @returns Tournament OK
     * @throws ApiError
     */
    public static create(
        requestBody: TournamentDTO,
    ): CancelablePromise<Tournament> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/tournaments',
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
     * Find paginated tournaments joined by user
     * Find paginated tournaments joined by user by specifying page number and page size
     * @param participant
     * @param page
     * @param size
     * @returns PageTournament OK
     * @throws ApiError
     */
    public static findAllJoinedByUser(
        participant: string,
        page?: number,
        size: number = 10,
    ): CancelablePromise<PageTournament> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/tournaments/joined',
            query: {
                'page': page,
                'size': size,
                'participant': participant,
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
     * Find paginated tournaments created by user
     * Find paginated tournaments created by user by specifying page number and page size
     * @param creator
     * @param page
     * @param size
     * @returns PageTournament OK
     * @throws ApiError
     */
    public static findAllCreatedByUser(
        creator: string,
        page?: number,
        size: number = 10,
    ): CancelablePromise<PageTournament> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/tournaments/created',
            query: {
                'page': page,
                'size': size,
                'creator': creator,
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
     * Find paginated tournaments coordinated by user
     * Find paginated tournaments coordinated by user by specifying page number and page size
     * @param coordinator
     * @param page
     * @param size
     * @returns PageTournament OK
     * @throws ApiError
     */
    public static findAllCoordinatedByUser(
        coordinator: string,
        page?: number,
        size: number = 10,
    ): CancelablePromise<PageTournament> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/tournaments/coordinated',
            query: {
                'page': page,
                'size': size,
                'coordinator': coordinator,
            },
            errors: {
                400: `Bad Request`,
                401: `Unauthorized`,
                404: `Not Found`,
                500: `Internal Server Error`,
            },
        });
    }
}
