import { createContext } from "react";
import { GHUser } from "../services/openapi";

export type TAuthContext = {
    user: GHUser | null;
    setUser: (user: GHUser | null) => void;
    token: string | null;
    setToken: (token: string | null) => void;
};

export const AuthContext = createContext<TAuthContext>({
    user: null,
    setUser: () => {},
    token: null,
    setToken: () => {},
});
