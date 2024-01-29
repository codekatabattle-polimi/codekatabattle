/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { GHUser } from '../models/GHUser';
import type { MultiValueMapStringString } from '../models/MultiValueMapStringString';
import type { OAuthAccessToken } from '../models/OAuthAccessToken';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class AuthService {
    /**
     * OAuth callback
     * Get access token by providing code from GitHub
     * @param formData
     * @returns OAuthAccessToken OK
     * @throws ApiError
     */
    public static callback(
        formData: MultiValueMapStringString,
    ): CancelablePromise<OAuthAccessToken> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/auth/callback',
            formData: formData,
            mediaType: 'application/x-www-form-urlencoded',
            errors: {
                400: `Bad Request`,
                401: `Unauthorized`,
                404: `Not Found`,
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * Get user info
     * Get user info by providing access token
     * @returns GHUser OK
     * @throws ApiError
     */
    public static me(): CancelablePromise<GHUser> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/auth/me',
            errors: {
                400: `Bad Request`,
                401: `Unauthorized`,
                404: `Not Found`,
                500: `Internal Server Error`,
            },
        });
    }
}
