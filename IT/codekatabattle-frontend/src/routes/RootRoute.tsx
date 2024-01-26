import { GithubLoginButton } from "../components/GithubLoginButton.tsx";

export function RootRoute() {
    return (
        <>
            <h1 className="text-3xl font-bold">Welcome to CodeKataBattle!</h1>
            <h2 className="text-2xl">Improve your programming skills online</h2>

            <GithubLoginButton/>
        </>
    )
}
