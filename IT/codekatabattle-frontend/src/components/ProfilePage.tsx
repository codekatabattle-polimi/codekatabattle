//import {useContext} from "react";
//import {AuthContext} from "../context/AuthContext.ts";

import {useNavigate, useParams} from "react-router-dom";
import {AuthService, GHUser, PageTournament, Tournament, TournamentService} from "../services/openapi";
import {NavBar} from "./NavBar.tsx";
import {useEffect, useState} from "react";
import educator from "../assets/educator.png";
import student from "../assets/reading.png";
import avatar from "../assets/avatar2.png";



export const ProfilePage= () => {
    const params = useParams();
    const [error, setError] = useState<Error | null>(null);
    const [user, setUser]=useState<GHUser|null>(null)
    const [createdTournaments, setCreatedTournaments]=useState<PageTournament|null>(null)
    const [coordinatedTournaments, setCoordinatedTournaments]=useState<PageTournament|null>(null)
    const [joinedTournaments, setJoinedTournaments]=useState<PageTournament|null>(null)
    const navigate = useNavigate();

    useEffect(() => {
        fetchAll();

    }, [params]);
    async function fetchAll(){
        try {
            if(!params.username)
                return;
            const user =await AuthService.getUserInfo(params.username);
            setUser(user);
            if(!user.login)
                return;
            const createdTournaments =await TournamentService.findAllCreatedByUser(user.login.toString(),0, 4);
            setCreatedTournaments(createdTournaments);
            const coordinatedTournaments =await TournamentService.findAllCoordinatedByUser(user.login.toString(),0, 4);
            setCoordinatedTournaments(coordinatedTournaments);
            const joinedTournaments =await TournamentService.findAllJoinedByUser(user.login.toString(),0, 4);
            setJoinedTournaments(joinedTournaments);
        }catch (error1) {
            setError(error1 as Error);
            if (error?.message) alert(error?.message);
        }

    }



    function to(id: string | undefined) {
        if (id != undefined) {
            const s = "/tournaments/" + id;
            navigate(s);
        }
    }

    function tournamentRows( type: string) {
        let tournaments : PageTournament|null;
        if (type=="created")
            tournaments = createdTournaments;
        else if (type=="coordinated")
            tournaments = coordinatedTournaments;
        else if (type=="joined")
            tournaments = joinedTournaments;
        else  return (<>Error</>);

        if (tournaments == null)
            return (<>No tournament found</>);
        if (tournaments.content == null) return (<>No tournament found 1</>);
        if (tournaments.content.length == 0) return (<>No tournament found 2</>);
        return(
            tournaments.content.map((t: Tournament) => (
                <tr className=" bg-base-200 shadow rounded-box" style={{width: "100%"}} onClick={() => to(t.id?.toString())}>
                    <th style={{width: "10%", overflow: "auto"}}>
                        <div className=" ">
                            <div className="stat-title text-xs">Title</div>
                            <div className="stat-value text-2xl">{t.title}
                            </div>
                        </div>
                    </th>
                    <th>
                        <div className="" style={{overflow: "auto"}}>
                            <div className="stat-title text-xs">Total Coordinators</div>
                            <div className="navbar " style={{}}>
                                <div className=" stat-value text-secondary text-2xl"
                                     style={{width: "20%"}}>{t.coordinators?.length ?? 0}</div>
                                <img className="" src={educator}
                                     style={{position: "relative", width: "15%", left: "65%"}}/>
                            </div>
                            <div className="stat-desc"></div>
                        </div>
                    </th>
                    <th>
                        <div className="" style={{overflow: "auto"}}>
                            <div className="stat-title text-xs">Total Participants</div>
                            <div className="navbar " style={{}}>
                                <div className=" stat-value text-primary text-2xl"
                                     style={{width: "20%"}}>{(t.participants?.length ?? 0)}</div>
                                <img className="" src={student}
                                     style={{position: "relative", width: "15%", left: "65%"}}/>
                            </div>
                            <div className="stat-desc"></div>
                        </div>
                    </th>

                    <th>
                        <div className="stat" style={{overflow: "auto"}}>
                            <div className="stat-figure" style={{paddingTop: "25%"}}>
                                <div
                                    className="stat-desc">{(t.maxParticipants ?? 1000) - (t.participants?.length ?? 0)} available
                                    spot
                                </div>
                            </div>
                            <div className="stat-title text-xs" style={{paddingBottom: "4%"}}>Occupation</div>
                            <div className="stat-value text-secondary">
                                <div className="radial-progress text-secondary"
                                     style={{
                                         "--value": (t.participants?.length ?? 0) / (t.maxParticipants ?? 1000),
                                         "--size": "3rem"
                                     } as React.CSSProperties}
                                     role="progressbar"><p
                                    className="text-xs">{Math.min(Math.ceil((t.participants?.length ?? 0) / (t.maxParticipants ?? 1000)), 100)}</p>
                                </div>
                            </div>
                        </div>
                    </th>

                    <th>
                        <div className="stat" style={{overflow: "auto"}}>
                            <div className="stat-figure text-secondary">
                                <div className="avatar online">
                                    <div className="w-16 rounded-full">
                                        <img src={avatar}/>
                                    </div>
                                </div>
                            </div>
                            <div className="stat-title" style={{paddingTop: "10%"}}>created by</div>
                            <div className="stat-desc text-primary">{t.creator}</div>
                        </div>
                    </th>


                </tr>
            ))
        )

    }

    const CreatedTournaments= () => {
        return(
            <table className="table bg-base-100 rounded-box" style={{width: "100%"}}>
            <thead style={{width: "100%"}}>

            </thead>
            <tbody className="bg-base-100 rounded-box">
            {tournamentRows("created")}
            </tbody>
        </table>)
    }
    const CoordinatedTournaments = () => {
        return (<table className="table " style={{width: "100%"}}>
            <thead style={{width: "100%"}}>

            </thead>
            <tbody>
            {tournamentRows("coordinated")}
            </tbody>
        </table>)
    }
    const JoinedTournaments = () => {
        return (<table className="table " style={{width: "100%"}}>
            <thead style={{width: "100%"}}>

            </thead>
            <tbody>
            {tournamentRows("joined")}
            </tbody>
        </table>)
    }

    return (
        <>
            <div className="page bg-base-300">
                <div className="bg-base-300" style={{alignSelf: "end", top: "8%", position: "fixed", width: "100%"}}>
                    <ul className="menu menu-vertical lg:menu-horizontal bg-base-300 rounded-box" style={{width: "100%"}}>
                    <div className="avatar">
                        <div className="w-28 rounded-full">
                            <img src={user?.avatar_url}/>
                        </div>
                    </div>
                    <h2 className="text-5xl font-bold" style={{padding: "2%"}}>{user?.login}</h2>
                </ul>
                <div style={{height:"100%"}} role="tablist" className="tabs tabs-lifted">
                    <input type="radio" name="my_tabs_2" role="tab" className="tab" aria-label="Created tournaments" checked/>
                    <div role="tabpanel" className="tab-content bg-base-100 border-base-300 rounded-box p-6">
                        <CreatedTournaments/>
                        <div onClick={() => navigate("/created/tournaments/view/0")} className="badge badge-info gap-2 font-bold">
                            {"See more >>"}
                        </div>
                    </div>

                    <input type="radio" name="my_tabs_2" role="tab" className="tab"
                           aria-label="Coordinated tournaments"/>
                    <div role="tabpanel" className="tab-content bg-base-100 border-base-300 rounded-box p-6">
                        <CoordinatedTournaments/>
                        <div  onClick={() => navigate("/coordinated/tournaments/view/0")} className="badge badge-info gap-2 font-bold">
                            {"See more >>"}
                        </div>
                    </div>

                    <input type="radio" name="my_tabs_2" role="tab" className="tab" aria-label="Joined tournaments"/>
                    <div role="tabpanel" className="tab-content bg-base-100 border-base-300 rounded-box p-6">
                        <JoinedTournaments/>
                        <div  onClick={() => navigate("/joined/tournaments/view/0")} className="badge badge-info gap-2 font-bold">
                            {"See more >>"}
                        </div>

                    </div>
                </div>

                </div>


                <div style={{top: "0%", position: "fixed", width: "100%", height: "10%"}}><NavBar/></div>
        </div>
        </>
    )

}