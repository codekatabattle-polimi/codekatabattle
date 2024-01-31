import {PageTournament, Tournament, TournamentService} from "../services/openapi";
import {useEffect, useState} from "react";
import {NavBar} from "./NavBar.tsx";
import earth from "../assets/earth.png"
import {useNavigate, useParams} from "react-router-dom";

export const VisualizeTournaments= () => {
    const {page} = useParams();
    const [tournaments, setTournaments] = useState<PageTournament | null>(null);
    const [error, setError] = useState<Error | null>(null);
    const navigate=useNavigate();

    useEffect(() => {
        fetchTournaments();
    }, [page]);

    async function fetchTournaments(){
        try {
            const tournaments = await TournamentService.findAll(+page!,13);
            setTournaments(tournaments);
        } catch (error) {
            setError(error as Error);
        }
    }
    function to(id:string|undefined){
        if (id!=undefined) {
            const s="/tournaments/"+id;
            navigate(s);
        }
    }
    function buttomPage(){
        if(page==undefined)return (<></>)
        if(tournaments==undefined)return (<></>)
        if(tournaments.totalPages==undefined)return (<></>)
        if(tournaments.totalPages<=1)return (
            <div className="join">

            </div>
        )
        if(tournaments.totalPages<=2 && page=="0"){
            return(
                <div className="join">
                    <input className="join-item btn " type="radio" name="options" aria-label="1"
                           checked/>
                    <input className="join-item btn " type="radio" name="options" aria-label="2"
                           onClick={()=>(to("view/1"))}/>
                </div>
            )
        }
        if(tournaments.totalPages<=2 && page=="1"){
            return(
                <div className="join">
                    <input className="join-item btn " type="radio" name="options" aria-label="1"
                           onClick={()=>(to("view/0"))}/>
                    <input className="join-item btn " type="radio" name="options" aria-label="2"
                           checked/>
                </div>
            )
        }
        if(page=="0"){
            return(
                <div className="join">
                    <input className="join-item btn " type="radio" name="options"
                           aria-label={"Page " + (+page! + 1).toString()}
                           checked/>
                    <input className="join-item btn " type="radio" name="options" aria-label="»"
                           onClick={() => (to("view/"+(+page!+1).toString()))}/>
                    <input className="join-item btn " type="radio" name="options"
                           aria-label={(tournaments.totalPages - 1).toString()}
                           onClick={() => (to("view/"+((tournaments?.totalPages ?? 2)-2).toString()))} />
                    <input className="join-item btn " type="radio" name="options" aria-label={tournaments.totalPages.toString()}
                           onClick={() => (to("view/"+((tournaments?.totalPages ?? 1)-1).toString()))} />
                </div>
            )
        }
        if (page == (tournaments.totalPages-1).toString()) {
            return (
                <div className="join">
                    <input className="join-item btn " type="radio" name="options" aria-label="1"
                           onClick={() => (to("view/0"))}
                    />
                    <input className="join-item btn " type="radio" name="options" aria-label="2"
                           onClick={() => (to("view/1"))}/>
                    <input className="join-item btn " type="radio" name="options" aria-label="«"
                           onClick={() => (to("view/"+(+page!-1).toString()))}
                    />
                    <input className="join-item btn " type="radio" name="options"
                           aria-label={"Page " + (+page! + 1).toString()}
                           checked/>
                </div>
            )
        }
        return (<div className="join">
            <input className="join-item btn " type="radio" name="options" aria-label="1"
                   onClick={() => (to("view/0"))}
            />
            <input className="join-item btn " type="radio" name="options" aria-label="2"
                   onClick={() => (to("view/1"))}/>
            <input className="join-item btn " type="radio" name="options" aria-label="«"
                   onClick={() => (to("view/" + (+page! - 1).toString()))}
            />
            <input className="join-item btn " type="radio" name="options" aria-label={"Page " + (+page! + 1).toString()}
                   checked/>
            <input className="join-item btn " type="radio" name="options" aria-label="»"
                   onClick={() => (to("view/" + (+page! + 1).toString()))}
            />
            <input className="join-item btn " type="radio" name="options"
                   aria-label={(tournaments.totalPages - 1).toString()}
                   onClick={() => (to("view/" + ((tournaments?.totalPages ?? 2) - 2).toString()))}/>
            <input className="join-item btn " type="radio" name="options" aria-label={tournaments.totalPages.toString()}
                   onClick={() => (to("view/" + ((tournaments?.totalPages ?? 1) - 1).toString()))}/>
        </div>)
    }

    function tournamentRows() {
        if (tournaments == null) return (<></>);
        if (tournaments.content == null) return (<></>);
        return (tournaments.content.map((t: Tournament) => (

            <tr className="hover" onClick={() => to(t.id?.toString())}>
                <td>{t.title}</td>
                <td>{(t.participants?.length ?? 0)}</td>
                <td>{t.creator}</td>
            </tr>
        )))
    }

    if (error) return (<>{error.message}</>);
    if (tournaments == null) return (<>Niente tornei</>);
    return (
        <>
            <div style={{alignSelf: "end", top: "8%",position:"fixed", width: "100%"}}>

                <ul className="menu menu-vertical lg:menu-horizontal " style={{width: "100%"}}>
                    <img src={earth} style={{width: "3%", height: "3%", paddingLeft: "1%", paddingTop: "0.1%"}}/>
                    <h1 className="text-3xl font-bold"
                        style={{paddingLeft: "0.5%", paddingBottom: "0.5%"}}>Tournament</h1>
                </ul>
                <div className="overflow-x-auto">
                    <table className="table">
                        <thead>
                        <tr>
                            <th>Title</th>
                            <th>Number of participants</th>
                            <th>Creator</th>
                        </tr>
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