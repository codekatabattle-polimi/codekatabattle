import {GithubLoginButton} from "./GithubLoginButton.tsx";
import {useContext} from "react";
import {AuthContext} from "../context/AuthContext.ts";
import {VisualizeTournament} from "./VisualizeTournament.tsx";

export const PageCore= () => {
    const {user}=useContext(AuthContext);
    if(user){
        return(<VisualizeTournament/>)
    }
    return (
        <center><GithubLoginButton/></center>
    )
}