import Avatar from "../assets/avatar2.png"
import {useContext, useEffect, useState} from "react";
import {Battle, BattleService} from "../services/openapi";
import {useParams} from "react-router-dom";
import {NavBar} from "./NavBar.tsx";
import avatar2 from "../assets/avatar1.png";
import {AuthContext} from "../context/AuthContext.ts";
import language = Battle.language;
import fight from "../assets/judo.png";


export const VisualizeBattle= () => {
    const {user}=useContext(AuthContext);
    const {bId} = useParams();
    const [battle, setBattle] = useState<Battle | null>(null);
    const [error, setError] = useState<Error | null>(null);

    useEffect(() => {
        fetchBattle();
    }, [bId]);

    if (!bId) {
        return (
            <>Id not found</>
        )
    }
    async function fetchBattle(){
        try {
            const battle = await BattleService.findById1(+bId!);
            setBattle(battle);
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
        if(battle?.participants == undefined )
            return (<></>)
        if(battle.creator == (user?.login ?? ""))
            return (<></>)
        if(battle.participants.length == 0 || !battle.participants.map((partcipant) => partcipant.username == user?.login).reduce((boola, boolb) => boola || boolb))
            return (joinButton())
        if(battle.participants.map((partcipant) => partcipant.username == user?.login).reduce((boola, boolb) => boola || boolb))
            return (leaveButton())
        else return (<></>)
    }


    async function leaveBattle(){
        try{
            const b = await BattleService.leave1(battle?.id ?? 0)
            setBattle(b)
        }catch(error) {
            setError(error as Error);
        }
    }

    function leaveButton() {
        const now = new Date()
        const endDate= new Date(battle?.endsAt ?? now)
        if(compareDate(now,endDate) == 1)
            return (<></>)
        return (<button style={{width:"100%"}} className="btn btn-error" onClick={() => leaveBattle()}>Leave</button>)

    }

    async function joinBattle(){
        try{
            const b = await BattleService.join1(battle?.id ?? 0)
            setBattle(b)
        }catch(error) {
            setError(error as Error);
        }
    }




    function joinButton() {
        const now = new Date()
        const startDate= new Date(battle?.startsAt ?? now)
        if(compareDate(now,startDate) == 1 || compareDate(now,startDate) == 0)
            return (<></>)
        return (<button style={{width:"100%"}} className="btn btn-success" onClick={() => joinBattle()}>Join</button>)
    }
    const BattleTest = () => {
        return (
            <div style={{padding: "1%", width:"fit-content", height:"fit-content"}} className="">
                <div className="collapse collapse-arrow border border-base-300 bg-base-200">
                    <input type="checkbox"/>
                    <div className="collapse-title text-xl font-medium">
                        Tests
                    </div>
                    <div className="collapse-content">
                        <div className="overflow-x-auto ">
                            <table className="table ">
                                <tbody>
                                <div style={{height: "fit-content"}} className="boxx">
                                    <div className="boxx-inner">
                                        <thead>
                                        <tr>
                                            <th>Name</th>
                                            <th>Input</th>
                                            <th>Expected output</th>
                                            <th>Score given</th>
                                        </tr>
                                        </thead>
                                        {battleTest()}
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

    function battleTest() {
        if (!battle?.tests) {
            return <></>;
        }

        return (
            battle.tests.map(((test) => (
                <tr>
                    <th className="font-bold" style={{alignItems: "center"}}>
                        {test.name}
                    </th>

                    <th>
                        <div>{test.input}</div>
                    </th>

                    <th>
                        {test.expectedOutput}
                    </th>


                    <th>
                        {test.givesScore}
                    </th>
                </tr>
            )))
        );
    }

    const BattleLeaderboard = () => {
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
                                        {battleLeaderboard()}
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

    function battleLeaderboard() {
        if (!battle?.participants) {
            return <></>;
        }

        return (
            battle.participants?.sort((a, b) => (b?.score ?? 0) - (a?.score ?? 0)).map(((participant, index) => (
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

    function battleLanguage() {

        if(battle?.language == undefined)
            return (<></>)
        let label : string;
        let colorLable : string;

        if(battle.language == language.PYTHON){
            colorLable=" font-bold badge badge-primary"
            label="Python"
        }else{
            colorLable=" font-bold badge badge-secondary"
            label="Golang"
        }

        return (<div style={{paddingLeft: "1%", paddingTop: "1.2%"}}>
                    <div tabIndex={0} role="button">
                        <div style={{color: "lightgray"}} className={colorLable}>{label}</div>
                    </div>
                </div>)
    }

    function battleStatus() {

        if (battle?.startsAt == undefined && battle?.endsAt == undefined)
            return (<></>)

        const startDate = new Date((battle.startsAt ?? ""));
        const endDate = new Date((battle.endsAt ?? ""));
        const now = new Date();

        let colorBadge: string;
        let colorText: string;
        let status: string;
        let message: string;

        if (compareDate(now, endDate) == 1) {
            colorBadge = " font-bold badge badge-error";
            colorText = "dropdown-content z-[1] menu p-2 shadow bg-base-300 rounded-box w-52 badge-error badge-outline";
            status = "terminated";
            message = "The battle has ended! :(";


        } else if (compareDate(now, startDate) == 1 && compareDate(now, endDate) != 1) {
            colorBadge = " font-bold badge badge-warning";
            colorText = "dropdown-content z-[1] menu p-2 shadow bg-base-300 rounded-box w-52 badge-warning badge-outline";
            status = "ongoing";
            message = "The battle ends at " + endDate.getFullYear() + "/" + (endDate.getMonth()+1) + "/" + endDate.getDate();
         }
         else {
            colorBadge = " font-bold badge badge-success";
            colorText = "dropdown-content z-[1] menu p-2 shadow bg-base-300 rounded-box w-52 badge-success badge-outline";
            status = "enrollment";
            message = "The battle starts at " + startDate.getFullYear() + "/" + (startDate.getMonth()+1) + "/" + startDate.getDate();
        }
         return (
                <div style={{paddingLeft: "1%", paddingTop: "2.5%"}}>
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
                <div className="flex-1 navbar-start">

                    <div style={{padding: "1%"}}>
                        <div className="w-10 h-10 rounded-full">
                            <img src={fight}/>
                        </div>
                    </div>

                    <h2 className="text-3xl font-bold"
                    > {battle?.title}</h2>
                    {battleLanguage()}
                    {battleStatus()}
                </div>
                <div style={{width: "50%"}} className="flex-none navbar-end">
                    <div style={{padding: "2%"}}>
                        {joinOrLeaveButton()}
                    </div>
                    <ul className="menu menu-vertical lg:menu-horizontal bg-base-200 rounded-box">
                        <a>
                            <div style={{paddingRight: "10%", paddingLeft: "1%"}}>
                                <h2 className="text-l w-full"
                                    style={{padding: "1%", paddingTop: "2%"}}> CreatedBy: </h2>
                                <h2 className="text-2xl font-bold w-full"
                                    style={{padding: "1%", paddingTop: "2%"}}> {battle?.creator}</h2>
                            </div>
                        </a>
                        <li><a></a></li>
                        <a>
                            <div style={{padding: "1%"}} className="avatar">
                                <div className="w-16 h-16 rounded-box" style={{position: "relative", right: "0%"}}>
                                    <img src={Avatar}/>
                                </div>
                            </div>
                        </a>


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
                            <p>{battle?.description}</p>
                        </div>
                    </div>
                </div>


                <ul style={{width: "100%"}} className="menu-lg lg:menu-horizontal bg-base-100 rounded-box">
                    <a href={battle?.repositoryUrl}
                       style={{paddingTop: "1%", paddingLeft: "1%", width: "12%", height:"99%"}}>
                        <div className="bg-info rounded-xl border-base-300 " style={{height: "78%", width: "100%"}}><p
                            className="font-bold text-base-300"
                            style={{paddingLeft: "8%", paddingTop: "10%",paddingBottom: "10%"}}>Repository Link ↘</p></div>
                    </a>
                    <BattleLeaderboard/>
                    <BattleTest/>

                </ul>
                <div style={{paddingLeft: "1%"}}>
                    <div className="stats shadow bg-base-200 border border-base-300">

                        <div className="stat">
                            <div className="stat-title">Gain up to</div>
                            <div className="stat-value">{battle?.timelinessBaseScore} extra point</div>
                            <div className="stat-desc">for submitting your final version as soon as possible!</div>
                        </div>

                    </div>
                </div>

            </div>
            <div style={{top: "0%", position: "fixed", width: "100%", height: "10%"}}><NavBar/></div>
        </>

    )
}
