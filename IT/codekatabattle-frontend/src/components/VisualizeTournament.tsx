import Placeholder from "../assets/Placeholder_Tournament_Image.jpg"
import Avatar from "../assets/avatar2.png"
import {useContext, useEffect, useState} from "react";
import {Tournament, TournamentService} from "../services/openapi";
import {useNavigate, useParams} from "react-router-dom";
import {NavBar} from "./NavBar.tsx";
import avatar2 from "../assets/avatar1.png";
import avatar3 from "../assets/avatar3.png";
import privacy = Tournament.privacy;
import {AuthContext} from "../context/AuthContext.ts";



export const VisualizeTournament= () => {
    const {user}=useContext(AuthContext);
    const {id} = useParams();
    const [tournament, setTournament] = useState<Tournament | null>(null);
    const [error, setError] = useState<Error | null>(null);
    const navigate= useNavigate();

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

    function joinOrLeaveButton() {
        if(tournament?.participants == undefined )
            return (<></>)
        if(tournament.creator == (user?.login ?? ""))
            return (<></>)
        if(tournament.participants.length == 0 || !tournament.participants.map((partcipant) => partcipant.username == user?.login).reduce((boola, boolb) => boola || boolb))
            return (joinButton())
        if(tournament.participants.map((partcipant) => partcipant.username == user?.login).reduce((boola, boolb) => boola || boolb))
            return (leaveButton())
        else return (<></>)
    }


    async function leaveTournament(){
        try{
            const t = await TournamentService.leave(tournament?.id ?? 0)
            setTournament(t)
        }catch(error) {
            setError(error as Error);
        }
    }

    function leaveButton() {
        const now = new Date()
        const endDate= new Date(tournament?.endsAt ?? now)
        if(compareDate(now,endDate) == 1)
            return (<></>)
        return (<button style={{width:"100%"}} className="btn btn-error" onClick={() => leaveTournament()}>Leave</button>)

    }

    async function joinTournament(){
        try{
            const t = await TournamentService.join(tournament?.id ?? 0)
            setTournament(t)
        }catch(error) {
            setError(error as Error);
        }
    }




    function joinButton() {
        const now = new Date()
        const startDate= new Date(tournament?.startsAt ?? now)
        if(compareDate(now,startDate) == 1 || compareDate(now,startDate) == 0)
            return (<></>)
        return (<button style={{width:"100%"}} className="btn btn-success" onClick={() => joinTournament()}>Join</button>)
    }


    const TournamentBattles = () => {
        return (
            <div style={{padding: "1%", width:"fit-content", height:"fit-content"}}>
                <div className="collapse collapse-arrow border border-base-300 bg-base-200">
                    <input type="checkbox"/>
                    <div className="collapse-title text-xl font-medium">
                       Battles
                    </div>

                    <div className="collapse-content">
                        {addBattleButton()}
                        <div className="overflow-x-auto">
                            <table className="table">
                                <div style={{height: "fit-content"}} className="boxx">
                                    <div className="boxx-inner">

                                        <thead>
                                        <tr>
                                            <th>Name</th>
                                            <th>Creator</th>
                                            <th>Participants</th>
                                            <th>Language</th>
                                        </tr>
                                        </thead>
                                        <tbody >
                                        {tournamentBattles()}
                                        </tbody>
                                    </div>
                                </div>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

    )
    }

    function openBattleEditor(){
        if (!tournament)
            return
        const path = "/tournaments/" + tournament.id?.toString() + "/battle/create";
        navigate(path);
    }

    function openBattle(battleId: number){
        if (!tournament)
            return
        const path = "/tournaments/" + tournament.id?.toString() + "/battles/" + battleId.toString();
        navigate(path);
    }

    function addBattleButton(){
        if(tournament?.coordinators && tournament.coordinators.length>0)
            if(tournament?.coordinators.map((coordinator) => coordinator.username == user?.login).reduce((boola, boolb) => boola || boolb))
            return (
                <button onClick={()=>{openBattleEditor()}} className="badge badge-info badge-outline">+</button>
            )
        if(user?.login == tournament?.creator)
            return (
                <button onClick={()=>{openBattleEditor()}} className="badge badge-info badge-outline">+</button>
            )
        return (<></>)
    }

    function seeMore(id : number){
        return (
            <button onClick={()=>{openBattle(id)}} style={{width:"100%"}} className="btn btn-outline btn-info"> See More →</button>
        )
    }

    function tournamentBattles() {
        if (!tournament?.battles) {
            return <></>;
        }

        return (
            tournament.battles?.map(((battle) => (
                <tr>
                    <td>
                        <div className="font-bold">{battle.title}</div>
                    </td>
                    <td>
                        <div className="font-bold">{battle.creator}</div>
                    </td>
                    <td>{(battle.participants?.length ?? 0)}</td>
                    <td>{(battle.language)}</td>
                    <td>{seeMore((battle.id ?? 0))}</td>
                </tr>
            )))
        );
    }

    const TournamentCoordinators = () => {
        return (
            <div style={{padding: "1%", width:"fit-content", height:"fit-content"}}>
                <div className="collapse collapse-arrow border border-base-300 bg-base-200">
                    <input type="checkbox"/>
                    <div className="collapse-title text-xl font-medium">
                        Coordinators
                    </div>
                    <div style={{height:"fit-content"}} className="collapse-content">
                        <div style={{height:"fit-content"}} className="overflow-x-auto">
                            <table style={{height:"fit-content"}} className="table">
                                <div style={{height:"fit-content"}} className="boxx">
                                    <div className="boxx-inner">
                                        <thead>
                                        <tr>
                                            <th>User</th>
                                            <th>N° of Battles</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {tournamentCoordinators()}
                                        </tbody>
                                    </div>
                                </div>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

        )
    }

    function tournamentCoordinators() {
        if (!tournament?.coordinators) {
            return <></>;
        }
        return (
            tournament?.coordinators.map(((coordinator) => (
                <tr className="bg-base-200">
                    <td>
                        <div className="flex items-center gap-3">
                            <div className="avatar">
                                <div className="mask mask-squircle w-12 h-12">
                                    <img src={avatar3}
                                         alt="Avatar Tailwind CSS Component"/>
                                </div>
                            </div>
                            <div>
                                <div className="font-bold">{coordinator.username}</div>
                            </div>
                        </div>
                    </td>
                    <td>10</td>
                </tr>
            )))
        );
    }

    const TournamentLeaderboard = () => {
        return (
            <div style={{padding: "1%", width:"fit-content", height:"fit-content"}} className="">
                <div className="collapse collapse-arrow border border-base-300 bg-base-200">
                    <input type="checkbox"/>
                    <div className="collapse-title text-xl font-medium">
                        Leaderboard
                    </div>
                    <div className="collapse-content">
                        <div className="overflow-x-auto ">
                            <table className="table ">
                                <tbody>
                                <div style={{height:"fit-content"}} className="boxx">
                                    <div className="boxx-inner">
                                        <thead>
                                        <tr>
                                            <th>Position</th>
                                            <th>User</th>
                                            <th>Score</th>
                                        </tr>
                                        </thead>
                                        {tournamentLeaderboard()}
                                    </div>
                                </div>

                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

        )
    }

    function colorOfWinner(x: number) {
        let r : string;
        if(x==0){
            r="bg-base-300";
        }
        else r="bg-base-200";

        return r;
    }

    function tournamentLeaderboard() {
        if (!tournament?.participants) {
            return <></>;
        }

        return (
            tournament.participants?.sort((a, b) => (b?.score ?? 0) - (a?.score ?? 0)).map(((participant, index) => (
                <tr className={colorOfWinner(index)}>
                    <th style={{ alignItems: "center" }}>{index + 1}</th>
                    <td>
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
                </tr>
            )))
        );
    }
    function tournamentPrivacy() {

        if(tournament?.privacy == undefined)
            return (<></>)
        let label : string;
        let message : string;
        let colorLable : string;
        let colorMessage : string;

        if(tournament.privacy == privacy.PUBLIC){
            colorLable=" font-bold badge badge-primary"
            label="public"
            colorMessage= "dropdown-content z-[1] menu p-2 shadow bg-base-300 rounded-box w-52 badge-primary badge-outline"
            message="Everyone can access to this tournament"
        }else{
            colorLable=" font-bold badge badge-secondary"
            label="private"
            colorMessage= "dropdown-content z-[1] menu p-2 shadow bg-base-300 rounded-box w-52 badge-secondary badge-outline"
            message="The creator must accept you to let you in"
        }

        return (<div style={{paddingLeft: "1%", paddingTop: "3%"}}>
            <div className="dropdown dropdown-end">
                <div tabIndex={0} role="button">
                    <div style={{color: "lightgray"}} className={colorLable}>{label}
                    </div>
                </div>
                <div style={{padding: "20%"}}>
                    <ul tabIndex={0}
                        className={colorMessage}>
                        <p> {message} </p>
                    </ul>
                </div>
            </div>
        </div>)
    }

    function tournamentStatus() {
        if (tournament?.startsAt == undefined && tournament?.endsAt == undefined)
            return (<></>)


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
                <div style={{paddingLeft: "1%", paddingTop: "2%"}}>
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

    function upperBar() {
        return (
            <div className="navbar bg-base-100">
                <div className="flex-1">
                    <div style={{padding: "1%"}} className="avatar">
                        <div className="w-16 h-16 rounded-full">
                            <img src={Placeholder}/>
                        </div>
                    </div>

                    <h2 className="text-3xl font-bold"
                        > {tournament?.title}</h2>


                    {tournamentPrivacy()}
                    {tournamentStatus()}
                </div>
                <div style={{width:"22%"}} className="flex-none">
                    <div style={{padding: "2%"}}>
                        {joinOrLeaveButton()}
                    </div>


                    <ul style={{borderSpacing:"", paddingTop: "1%", width: "100%"}}
                        className="menu-lg lg:menu-horizontal bg-base-200 rounded-box border border-base-300">
                        <div style={{paddingRight: "2%" , paddingLeft: "1%"}}>
                            <h2 className="text-l"
                                style={{padding: "1%", paddingTop: "2%"}}> Created by: </h2>
                            <h2 className="text-2xl font-bold"
                                style={{padding: "1%", paddingTop: "2%"}}> {tournament?.creator}</h2>
                        </div>


                        <div style={{padding:"1%"}} className="avatar">
                            <div className="w-16 h-16 rounded-box">
                                <img src={Avatar}/>
                            </div>
                        </div>
                    </ul>
                </div>
            </div>
        )
    }


    if (error) {
        return (
            <>{error.message}</>
        )
    }

    return (
        <>

            <div style={{alignSelf: "end", top: "8%", position: "fixed", width: "100%"}}>
                {upperBar()}
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
                <ul  style={{width:"100%"}} className="menu-lg lg:menu-horizontal bg-base-100 rounded-box">
                    <TournamentLeaderboard/>
                    <TournamentCoordinators/>
                    <TournamentBattles/>
                </ul>

            </div>
            <div style={{top: "0%", position:"fixed", width:"100%", height:"10%"}}><NavBar/></div>
        </>

    )
}
