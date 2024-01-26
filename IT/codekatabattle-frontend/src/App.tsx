import { GithubLoginButton } from "./components/GithubLoginButton.tsx";
import { AuthService, GHUser, OpenAPI } from "./services/openapi";
import { useEffect, useState } from "react";
import { AuthContext } from "./context/AuthContext.ts";

function App() {
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
            <h1 className="text-3xl font-bold">Welcome to CodeKataBattle!</h1>
            <h2 className="text-2xl">Improve your programming skills online</h2>

            <GithubLoginButton/>
        </AuthContext.Provider>
    )
}

export default App
