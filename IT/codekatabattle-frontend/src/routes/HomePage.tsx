
import {useContext} from "react";
import {AuthContext} from "../context/AuthContext.ts";
import {GithubLoginButton} from "../components/GithubLoginButton.tsx";
import {NavBar} from "../components/NavBar.tsx";
export function HomePage() {
    const {user}=useContext(AuthContext);
    if(user){
        return(
            <><div  style={{alignSelf: "end", top: "8%", position: "fixed", width: "100%"}}>
                <div className="hero min-h-screen"
                     style={{backgroundImage: 'url("src/assets/homepageBackground.jpg")'}}>
                    <div></div>
                    <div className="hero-content text-center text-neutral-content">
                        <div className="max-w-md">
                            <h1 className="mb-5 text-5xl font-bold">Welcome {user.login}</h1>
                            <p className="mb-5">This is CodekataBattle, your online dojo where you can learn and improve
                                the art pf coding</p>
                            <button className="btn btn-primary">Get Started</button>
                        </div>
                    </div>
                </div>
            </div>
                <div style={{top: "0%", position: "fixed", width: "100%", height: "10%"}}><NavBar/></div>

            </>
        )
    }
    return (
        <center><GithubLoginButton/></center>
    )
}