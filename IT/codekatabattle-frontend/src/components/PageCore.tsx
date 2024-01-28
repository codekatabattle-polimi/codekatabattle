import {GithubLoginButton} from "./GithubLoginButton.tsx";
import {useContext} from "react";
import {AuthContext} from "../context/AuthContext.ts";

export const PageCore= () => {
    const {user}=useContext(AuthContext);
    if(user){
        return(<></>)
    }
    return (
        <center><GithubLoginButton/></center>
    )
}