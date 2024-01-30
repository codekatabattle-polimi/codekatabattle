
import {useContext} from "react";
import {AuthContext} from "../context/AuthContext.ts";
import {GithubLoginButton} from "../components/GithubLoginButton.tsx";
import {NavBar} from "../components/NavBar.tsx";
export function HomePage() {
    const {user}=useContext(AuthContext);
    if(user){
        return(
            <>
                <><NavBar/></>

            </>
        )
    }
    return (
            <center><GithubLoginButton/></center>
        )
}