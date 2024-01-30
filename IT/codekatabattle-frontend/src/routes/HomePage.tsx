
import {useContext} from "react";
import {AuthContext} from "../context/AuthContext.ts";
import {VisualizeTournament} from "../components/VisualizeTournament.tsx";
import {GithubLoginButton} from "../components/GithubLoginButton.tsx";
export function HomePage() {
    const {user}=useContext(AuthContext);
    if(user){
        return(
            <>
                <><VisualizeTournament/></>

            </>
        )
    }
    return (
            <center><GithubLoginButton/></center>
        )
}