import { AuthService, OpenAPI } from "../services/openapi";
import { useContext, useEffect } from "react";
import { AuthContext } from "../context/AuthContext.ts";
import signIn from "../assets/signin.jpg";
import imageToAdd from "../assets/ckb-logo.jpg";

function generateRandom(len: number): string {
    const arr = new Uint8Array(len / 2)
    window.crypto.getRandomValues(arr)
    return Array.from(arr, (dec) => dec.toString(16).padStart(2, "0")).join('')
}

export const GithubLoginButton = () => {
    const { user, setUser, setToken } = useContext(AuthContext);

    useEffect(() => {
        fetchGitHubToken();
    }, []);


    async function requestGitHubIdentity() {
        const clientId: string = import.meta.env.VITE_GITHUB_CLIENT_ID;
        const redirectUri: string = import.meta.env.VITE_GITHUB_REDIRECT_URI;

        const params = new URLSearchParams();
        params.set("client_id", clientId);
        params.set("redirect_uri", redirectUri);
        params.set("scope", "user");
        params.set("state", generateRandom(32));

        window.open(`https://github.com/login/oauth/authorize?${params}`, "_self");
        localStorage.setItem("state", params.get("state")!);
    }

    async function requestGitHubToken(code: string) {
        const res = await fetch(`${import.meta.env.VITE_API_URL}/auth/callback`, {
            method: "POST",
            body: new URLSearchParams({ code }),
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
        });
        if (res.status !== 200) {
            throw new Error("Auth callback failed");
        }

        const { access_token } = await res.json();
        OpenAPI.TOKEN = access_token!;
        localStorage.setItem("token", access_token);
        setToken(access_token!);

        const user = await AuthService.me();
        setUser(user);
    }

    async function fetchGitHubToken() {
        const urlSearchParams = new URLSearchParams(window.location.search);
        const code = urlSearchParams.get("code");
        if (!code) {
            return;
        }

        try {
            await requestGitHubToken(code);
            window.history.pushState({}, document.title, window.location.pathname);
        } catch (err) {
            console.error(err);
        }
    }

    function logout() {
        setUser(null);
        localStorage.removeItem("user");

        setToken(null);
        localStorage.removeItem("token");
    }


    if (user) {
        return (

                    <div id="box2" style={{
                        alignItems: 'center',
                        justifyContent: 'center'
                    }}>
                        <center><img src={imageToAdd} className="mask mask-squircle" alt="helo"
                                     style={{width: "30%"}}></img></center>
                        <center>
                            <div><h1 className="text-3xl font-bold" style={{color: "#000000"}}>Welcome to
                                CodeKataBattle!</h1></div>
                            <h2 className="text-2xl" style={{color: "#696969"}}>Improve your programming skills
                                online</h2></center>

                        <h1 className="text-2xl">
                            Hello, {user.login}!
                        </h1>

                        <a className="text-2xl cursor-pointer text-blue-500 underline" onClick={() => logout()}>
                            Logout
                        </a>
                    </div>

        );
    }

    return (


            <div id="box2" style={{
                alignItems: 'center',
                justifyContent: 'center'
            }}>
                <center><img src={imageToAdd} className="mask mask-squircle" alt="helo" style={{width: "30%"}}></img>
                </center>
                <center>
                    <div><h1 className="text-3xl font-bold" style={{color: "#000000"}}>Welcome to CodeKataBattle!</h1>
                    </div>
                    <h2 className="text-2xl" style={{color: "#696969"}}>Improve your programming skills online</h2>
                </center>
                <a className="text-2xl cursor-pointer text-blue-500 underline" onClick={() => requestGitHubIdentity()}>
                    <img src={signIn} style={{width: "50%"}}></img>
                </a>

            </div>

    )
}
