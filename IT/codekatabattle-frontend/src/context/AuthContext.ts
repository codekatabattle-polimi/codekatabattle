import { createContext } from "react";
import { AuthService, GHUser, OpenAPI } from "../services/openapi";

export type TAuthContext = {
    isLoggedIn: boolean;
    token: string | null;
    user: GHUser | null;
};

export const AuthContext = createContext<TAuthContext>({
    isLoggedIn: false,
    token: null,
    user: null,
});

function generateRandom(len: number): string {
    const arr = new Uint8Array(len / 2)
    window.crypto.getRandomValues(arr)
    return Array.from(arr, (dec) => dec.toString(16).padStart(2, "0")).join('')
}

export async function requestGitHubIdentity() {
    const clientId: string = import.meta.env.VITE_GITHUB_CLIENT_ID;
    const redirectUri: string = import.meta.env.VITE_GITHUB_REDIRECT_URI;

    const params = new URLSearchParams();
    params.set("client_id", clientId);
    params.set("redirect_uri", redirectUri);
    params.set("scope", "user repo");
    params.set("state", generateRandom(32));

    window.open(`https://github.com/login/oauth/authorize?${params}`, "_self");
}

export async function requestGitHubToken(code: string) {
    const { access_token } = await AuthService.callback({ code: [code] });
    localStorage.setItem("token", access_token!);
    OpenAPI.TOKEN = access_token!;

    const user = await AuthService.me();
    localStorage.setItem("user", JSON.stringify(user));

    localStorage.setItem("isLoggedIn", "true");
}
