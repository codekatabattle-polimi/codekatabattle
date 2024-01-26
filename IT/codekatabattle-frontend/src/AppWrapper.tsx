import { ReactNode, useEffect, useState } from "react";
import { AuthService, GHUser, OpenAPI } from "./services/openapi";
import { AuthContext } from "./context/AuthContext";

export function AppWrapper({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<GHUser | null>(null);
    const [token, setToken] = useState<string | null>(null);

    useEffect(() => {
        fetchUser();
    }, []);

    async function fetchUser() {
        const _token = localStorage.getItem("token");
        setToken(_token);
        OpenAPI.TOKEN = _token!;

        const _user = localStorage.getItem("user");
        if (_token && _token !== "null" && (!_user || _user === "null")) {
            const user = await AuthService.me();
            setUser(user);
            localStorage.setItem("user", JSON.stringify(user));
        }

        if (_user && _user !== "null") {
            setUser(JSON.parse(_user!));
        }
    }

    return (
        <AuthContext.Provider value={{ user, setUser, token, setToken }}>
            {children}
        </AuthContext.Provider>
    )
}
