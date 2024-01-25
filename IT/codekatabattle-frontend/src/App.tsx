import { requestGitHubIdentity } from "./context/AuthContext.ts";

function App() {
    return (
        <>
            <h1 className="text-3xl font-bold">Welcome to CodeKataBattle!</h1>
            <h2 className="text-2xl">Improve your programming skills online</h2>

            <a className="text-2xl cursor-pointer text-blue-500 underline" onClick={() => requestGitHubIdentity()}>Login with GitHub</a>
        </>
    )
}

export default App
