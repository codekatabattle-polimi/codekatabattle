import Placeholder from "../assets/Placeholder_Tournament_Image.jpg"
import {TournamentLeaderboard} from "./TournamentLeaderboard.tsx";
import {useEffect, useState} from "react";
import {Tournament, TournamentService} from "../services/openapi";
import {useParams} from "react-router-dom";



export const VisualizeTournament= () => {
    const {id} = useParams();
    const [tournament, setTournament] = useState<Tournament | null>(null);
    const [error, setError] = useState<Error | null>(null);

    useEffect(() => {
        fetchTournament();
    }, [id]);

    if (!id) {
        return (
            <>Id not found</>
        )
    }
    async function fetchTournament(){
        try {
            const tournament = await TournamentService.findById(+id!);
            setTournament(tournament);
        } catch (error) {
            setError(error as Error);
        }
    }
    if(error){
        return(
            <>{error.message}</>
        )
    }
    const tournamentTitle= tournament?.title;
    return(
        <>
            <ul style={{width: "50%"}} className="menu menu-vertical lg:menu-horizontal">
                <div style={{padding: "1%"}} className="avatar">
                    <div className="w-14 rounded-full">
                        <img src={Placeholder}/>
                    </div>
                </div>

                <h2 className="text-3xl font-bold" style={{padding: "1%", paddingTop: "2%"}}> {tournamentTitle}</h2>

                <div style={{paddingLeft: "1%", paddingTop: "3%"}}>
                    <div className="dropdown dropdown-end">
                        <div tabIndex={0} role="button">
                            <div style={{color: "lightgray"}} className=" font-bold badge badge-primary">public</div>
                        </div>
                        <div style={{padding: "20%"}}>
                            <ul tabIndex={0}
                                className="dropdown-content z-[1] menu p-2 shadow bg-base-300 rounded-box w-52 badge-primary badge-outline">
                                <p> Everyone can access to this tournament </p>
                            </ul>
                        </div>
                    </div>
                </div>

                <div style={{paddingLeft: "1%", paddingTop: "3%"}}>
                    <div className="dropdown dropdown-bottom">
                        <div tabIndex={0} role="button">
                            <div style={{color: "lightgray"}} className=" font-bold badge badge-success">enrollament</div>
                        </div>
                        <div style={{paddingTop: "20%"}}>
                            <ul tabIndex={0}
                                className="dropdown-content z-[1] menu p-2 shadow bg-base-300 rounded-box w-52 badge-success badge-outline">
                                <p> The tournament starts at 10/9/2008 </p>
                            </ul>
                        </div>
                    </div>
                </div>
            </ul>
            <div style={{padding: "1%"}}>
                <div className="collapse bg-base-200">
                    <input type="checkbox"/>
                    <div className="collapse-title text-xl font-medium">
                        Description
                    </div>
                    <div className="collapse-content">
                        <p>In the Age of Ancients the world was unformed, shrouded by fog. A land of gray crags,
                            Archtrees
                            and Everlasting Dragons. But then there was Fire and with fire came disparity. Heat and
                            cold,
                            life and death, and of course, light and dark. Then from the dark, They came, and found the
                            Souls of Lords within the flame. Nito, the First of the Dead, The Witch of Izalith and her
                            Daughters of Chaos, Gwyn, the Lord of Sunlight, and his faithful knights. And the Furtive Pygmy,
                            so easily forgotten.</p>
                    </div>
                </div>
            </div>
            <TournamentLeaderboard/>
        </>
    )
}
