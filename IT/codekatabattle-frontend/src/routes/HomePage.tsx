
import {useContext} from "react";
import {AuthContext} from "../context/AuthContext.ts";
import {GithubLoginButton} from "../components/GithubLoginButton.tsx";
import {NavBar} from "../components/NavBar.tsx";
import {Link} from "react-router-dom";
import dojo from "../assets/dojo.png";
export function HomePage() {
    const {user}=useContext(AuthContext);
    if(user){
        return(
            <>
                <label className="swap swap-flip " style={{alignSelf: "end", top: "4%", width: "100%"}}>
                    <h1 className="text-9xl max-w-full invisible">..........................................................</h1>

                    <input type="checkbox" className=""/>

                    <div className="hero w-full bg-base-300 swap-on "
                         style={{backgroundImage: 'url("https://i.imgur.com/9lQnWNW.jpeg")'}}>


                        <div className="hero-content text-center text-neutral-content bg-base-100/50 rounded-xl">
                            <div className="">
                                <ul className="menu menu-vertical lg:menu-horizontal">
                                    <div className="w-10 h-10 rounded-full"
                                         style={{paddingTop: "1.5%", paddingLeft: "0.5%"}}>
                                        <img src={dojo}/>
                                    </div>
                                    <h1 className="text-5xl font-bold">Welcome</h1>
                                    <div className="w-10 h-10 rounded-full"
                                         style={{paddingTop: "1.5%", paddingLeft: "0.5%"}}>
                                        <img src={dojo}/>
                                    </div>
                                </ul>

                                <h1 className="mb-5 text-5xl font-bold">{user.login}</h1>
                                    <p className="mb-5 font-black  ">This is
                                        CodekataBattle, your online dojo where you can train
                                        your students to became better programmers</p>
                                <button className="btn btn-info"><Link to="tournament/create">Create your
                                    Tournament</Link></button>
                            </div>
                        </div>
                    </div>
                    <div className="swap-off">
                        <div>
                            <div className="hero min-h-screen"
                                 style={{backgroundImage: 'url("https://i.imgur.com/B6WBZsX.jpeg")'}}>

                                <div className="hero-content text-center text-neutral-content bg-base-100/50 rounded-xl">
                                    <div className="">
                                        <ul className="menu menu-vertical lg:menu-horizontal">
                                            <div className="w-10 h-10 rounded-full"
                                                 style={{paddingTop: "1.5%", paddingLeft: "0.5%"}}>
                                                <img src={dojo}/>
                                            </div>
                                            <h1 className="text-5xl font-bold">Welcome</h1>
                                            <div className="w-10 h-10 rounded-full"
                                                 style={{paddingTop: "1.5%", paddingLeft: "0.5%"}}>
                                                <img src={dojo}/>
                                            </div>
                                        </ul>

                                        <h1 className="mb-5 text-5xl font-bold">{user.login}</h1>
                                        <p className="mb-5 font-bold text-blue-400">"click the background to see more"</p>
                                            <p className="mb-5 font-black">This is CodekataBattle, your online dojo
                                                where you can learn
                                                and
                                                improve
                                                the art of coding</p>
                                            <button className="btn btn-primary"><Link to={"/all/tournaments/view/0"}>Check
                                                tournaments and
                                                join
                                                them</Link></button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </label>

                <div style={{top: "0%", position: "fixed", width: "100%", height: "10%"}}><NavBar/></div>

            </>
        )
    }
    return (
        <center><GithubLoginButton/></center>
    )
}