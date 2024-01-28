import {GithubLoginButton} from "./GithubLoginButton.tsx";
import {useContext} from "react";
import {AuthContext} from "../context/AuthContext.ts";
import {CreateTournament} from "./CreateTournament.tsx";

export const PageCore= () => {
    const {user}=useContext(AuthContext);
    if(user){
        return(<CreateTournament/>)
    }
    return (
        <center><GithubLoginButton/></center>
    )
}