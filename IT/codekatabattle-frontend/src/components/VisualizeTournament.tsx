import Placeholder from "../assets/Placeholder_Tournament_Image.jpg"
import {useEffect, useState} from "react";
import {Tournament, TournamentService} from "../services/openapi";
import {useParams} from "react-router-dom";
import {NavBar} from "./NavBar.tsx";
import avatar2 from "../assets/avatar1.png";




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

    function compareDate(date1:Date, date2:Date){
        if(date1.getFullYear() > date2.getFullYear())
            return 1;
        if(date1.getFullYear() < date2.getFullYear())
            return 2;
        if(date1.getMonth() > date2.getMonth())
            return 1;
        if(date1.getMonth() < date2.getMonth())
            return 2;
        if(date1.getDate() > date2.getDate())
            return 1;
        if(date1.getDate() < date2.getDate())
            return 2;
        return 0;
    }

    const TournamentLeaderboard = () => {
        return (
            <div style={{padding: "1%"}}>
                <div className="collapse collapse-arrow border border-base-300 bg-base-200">
                    <input type="checkbox"/>
                    <div className="collapse-title text-xl font-medium">
                        Leaderboard
                    </div>
                    <div className="collapse-content">
                        <div className="overflow-x-auto">
                            <table className="table">
                                {/* head */}
                                <thead>
                                <tr>
                                    <th>Position</th>
                                    <th>User</th>
                                    <th>Score</th>
                                </tr>
                                </thead>
                                <tbody>
                                {tournamentLeaderboard()}

                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

        )
    }

    function tournamentLeaderboard() {
        if(tournament?.participants) {
            const participants = tournament.participants, partList: JSX.Element[] = [];
            participants.forEach((participant, index) => {
                partList.push(
                    <tr className="bg-base-200">
                    <th style={{width: "30%", alignItems: "center"}}>{index+1}</th>
                    <td style={{width: "30%"}}>
                        <div className="flex items-center gap-3">
                            <div className="avatar">
                                <div className="mask mask-squircle w-12 h-12">
                                    <img src={avatar2}
                                         alt="Avatar Tailwind CSS Component"/>
                                </div>
                            </div>
                            <div>
                                <div className="font-bold">{participant.username}</div>
                            </div>
                        </div>
                    </td>
                    <td>{participant.score}</td>
                </tr>);


            })

            return partList;
        }
    }

        function tournamentStatus() {


            if (tournament?.startsAt != undefined && tournament?.endsAt != undefined) {
                const startDate = new Date(tournament.startsAt);
                const endDate = new Date(tournament.endsAt);
                const now = new Date();

                let colorBadge: string;
                let colorText: string;
                let status: string;
                let message: string;

                if (compareDate(now, endDate) == 1) {
                    colorBadge = " font-bold badge badge-error";
                    colorText = "dropdown-content z-[1] menu p-2 shadow bg-base-300 rounded-box w-52 badge-error badge-outline";
                    status = "terminated";
                    message = "The tournament has ended! :(";


                } else if (compareDate(now, startDate) == 1 && compareDate(now, endDate) != 1) {
                    colorBadge = " font-bold badge badge-warning";
                    colorText = "dropdown-content z-[1] menu p-2 shadow bg-base-300 rounded-box w-52 badge-warning badge-outline";
                    status = "ongoing";
                    message = "The tournament ends at " + endDate.getFullYear() + "/" + (endDate.getMonth()+1) + "/" + endDate.getDate();
         }
         else {
            colorBadge = " font-bold badge badge-success";
            colorText = "dropdown-content z-[1] menu p-2 shadow bg-base-300 rounded-box w-52 badge-success badge-outline";
            status = "enrollment";
            message = "The tournament starts at " + startDate.getFullYear() + "/" + (startDate.getMonth()+1) + "/" + startDate.getDate();
        }

         return (
                <div style={{paddingLeft: "1%", paddingTop: "3%"}}>
                    <div className="dropdown dropdown-bottom">
                        <div tabIndex={0} role="button">
                            <div style={{color: "lightgray"}} className={colorBadge}> {status}
                            </div>
                        </div>
                        <div style={{paddingTop: "10%"}}>
                            <ul tabIndex={0} className={colorText}>
                                <p> {message} </p>
                            </ul>
                        </div>
                    </div>
                </div>
            )


    }
    }

    if (error) {
        return (
            <>{error.message}</>
        )
    }

    return (
        <>

            <div style={{alignSelf: "end", top:"8%", position:"fixed", width:"100%"}}>
                <ul style={{width: "50%"}} className="menu menu-vertical lg:menu-horizontal">
                    <div style={{padding: "1%"}} className="avatar">
                        <div className="w-14 rounded-full">
                            <img src={Placeholder}/>
                        </div>
                    </div>

                    <h2 className="text-3xl font-bold"
                        style={{padding: "1%", paddingTop: "2%"}}> {tournament?.title}</h2>

                    <div style={{paddingLeft: "1%", paddingTop: "3%"}}>
                        <div className="dropdown dropdown-end">
                            <div tabIndex={0} role="button">
                                <div style={{color: "lightgray"}} className=" font-bold badge badge-primary">public
                                </div>
                            </div>
                            <div style={{padding: "20%"}}>
                                <ul tabIndex={0}
                                    className="dropdown-content z-[1] menu p-2 shadow bg-base-300 rounded-box w-52 badge-primary badge-outline">
                                    <p> Everyone can access to this tournament </p>
                                </ul>
                            </div>
                        </div>
                    </div>
                    {tournamentStatus()}
                </ul>
                <div style={{padding: "1%"}}>
                    <div className="collapse collapse-arrow border border-base-300 bg-base-200">
                        <input type="checkbox"/>
                        <div className="collapse-title text-xl font-medium">
                            Description
                        </div>
                        <div className="collapse-content">
                            <p>{tournament?.description}</p>
                        </div>
                    </div>
                </div>
                <TournamentLeaderboard/>
            </div>
            <div style={{top: "0%", position:"fixed", width:"100%", height:"10%"}}><NavBar/></div>
        </>

    )
}
