import {AuthService, PageTournament, Tournament, TournamentService} from "../services/openapi";
import {useContext, useEffect, useState} from "react";
import {NavBar} from "./NavBar.tsx";
import five from "../assets/high-five.png"
import {useNavigate, useParams} from "react-router-dom";
import student from "../assets/reading.png";
import educator from "../assets/educator.png";
import {ImageCreator} from "./ImageCreator.tsx";
import {AuthContext} from "../context/AuthContext.ts";
export const VisualizeCoordinatedTournaments= () => {
    const params = useParams();
    const [tournaments, setTournaments] = useState<PageTournament | null>(null);
    const [error, setError] = useState<Error | null>(null);
    const navigate = useNavigate();
    const {user}=useContext(AuthContext);

    useEffect(() => {
        fetchTournaments();
    }, []);

    async function fetchTournaments() {
        try {
            let tuser;
            if(!params.username) tuser=user;
            else {
                 tuser = await AuthService.getUserInfo(params.username);
            }
            if(tuser?.login) {
                const tournaments = await TournamentService.findAllCoordinatedByUser(tuser.login, +params.page!, 5);
                setTournaments(tournaments);
            }

        } catch (error1) {
            setError(error1 as Error);
            if(error?.message)alert((error.message));
        }
    }

    function to(id: string | undefined) {
        if (id != undefined) {
            const s = "/coordinated/tournaments/view/"+params.username+"/"+id;
            navigate(s);
            location.reload();
        }
    }

    function buttomPage() {
        if (params.page == undefined) return (<></>)
        if (tournaments == undefined) return (<></>)
        if (tournaments.totalPages == undefined) return (<></>)
        if (tournaments.totalPages <= 1) return (
            <div className="join">

            </div>
        )
        if (tournaments.totalPages <= 2 && params.page == "0") {
            return (
                <div className="join">
                    <input className="join-item btn " type="radio" name="options" aria-label="1"
                           checked/>
                    <input className="join-item btn " type="radio" name="options" aria-label="2"
                           onClick={() => (to("1"))}/>
                </div>
            )
        }
        if (tournaments.totalPages <= 2 && params.page == "1") {
            return (
                <div className="join">
                    <input className="join-item btn " type="radio" name="options" aria-label="1"
                           onClick={() => (to("0"))}/>
                    <input className="join-item btn " type="radio" name="options" aria-label="2"
                           checked/>
                </div>
            )
        }
        if (params.page == "0") {
            return (
                <div className="join">
                    <input className="join-item btn " type="radio" name="options"
                           aria-label={"Page " + (+params.page! + 1).toString()}
                           checked/>
                    <input className="join-item btn " type="radio" name="options" aria-label="»"
                           onClick={() => (to((+params.page! + 1).toString()))}/>
                    <input className="join-item btn " type="radio" name="options"
                           aria-label={(tournaments.totalPages - 1).toString()}
                           onClick={() => (to(((tournaments?.totalPages ?? 2) - 2).toString()))}/>
                    <input className="join-item btn " type="radio" name="options"
                           aria-label={tournaments.totalPages.toString()}
                           onClick={() => (to(((tournaments?.totalPages ?? 1) - 1).toString()))}/>
                </div>
            )
        }
        if (params.page == (tournaments.totalPages - 1).toString()) {
            return (
                <div className="join">
                    <input className="join-item btn " type="radio" name="options" aria-label="1"
                           onClick={() => (to("0"))}
                    />
                    <input className="join-item btn " type="radio" name="options" aria-label="2"
                           onClick={() => (to("1"))}/>
                    <input className="join-item btn " type="radio" name="options" aria-label="«"
                           onClick={() => (to( (+params.page! - 1).toString()))}
                    />
                    <input className="join-item btn " type="radio" name="options"
                           aria-label={"Page " + (+params.page! + 1).toString()}
                           checked/>
                </div>
            )
        }
        return (<div className="join">
            <input className="join-item btn " type="radio" name="options" aria-label="1"
                   onClick={() => (to("0"))}
            />
            <input className="join-item btn " type="radio" name="options" aria-label="2"
                   onClick={() => (to("1"))}/>
            <input className="join-item btn " type="radio" name="options" aria-label="«"
                   onClick={() => (to( (+params.page! - 1).toString()))}
            />
            <input className="join-item btn " type="radio" name="options" aria-label={"Page " + (+params.page! + 1).toString()}
                   checked/>
            <input className="join-item btn " type="radio" name="options" aria-label="»"
                   onClick={() => (to( (+params.page! + 1).toString()))}
            />
            <input className="join-item btn " type="radio" name="options"
                   aria-label={(tournaments.totalPages - 1).toString()}
                   onClick={() => (to( ((tournaments?.totalPages ?? 2) - 2).toString()))}/>
            <input className="join-item btn " type="radio" name="options" aria-label={tournaments.totalPages.toString()}
                   onClick={() => (to( ((tournaments?.totalPages ?? 1) - 1).toString()))}/>
        </div>)
    }


    function tournamentRows() {
        if (tournaments == null) return (<></>);
        if (tournaments.content == null) return (<></>);
        return(
            tournaments.content.map((t: Tournament) => (
                <tr className=" shadow " style={{width: "100%"}} onClick={() => navigate("/tournaments/"+t.id?.toString())}>
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
                                        <ImageCreator username={t.creator}/>
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

    if (tournaments == null) return (<>no tournament</>);
    return (
        <>
            <div style={{alignSelf: "end", top: "8%",position:"fixed", width: "100%"}}>

                <ul className="menu menu-vertical lg:menu-horizontal " style={{width: "100%"}}>
                    <img src={five} style={{width: "4%", height: "4%", paddingLeft: "1%"}}/>
                    <h1 className="text-3xl font-bold"
                        style={{paddingLeft: "0.5%", paddingBottom: "0.5%"}}>Coordinated Tournament</h1>
                </ul>
                <div className="overflow-x-auto">
                    <table className="table " style={{width: "100%"}}>
                        <thead style={{width: "100%"}}>

                        </thead>
                        <tbody>
                        {tournamentRows()}
                        </tbody>
                    </table>
                </div>

                <div className="flex flex-col w-full">
                    <div className="divider">
                        {buttomPage()}
                    </div>
                </div>
            </div>
            <div style={{top: "0%", position: "fixed", width: "100%", height: "10%"}}><NavBar/></div>
        </>
    )
}