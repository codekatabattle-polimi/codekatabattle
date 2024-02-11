import {useContext, useEffect, useState} from "react";
import {
    Battle,
    BattleService, BattleUpdateDTO,
} from "../services/openapi";
import {Link, useNavigate, useParams} from "react-router-dom";
import {NavBar} from "./NavBar.tsx";
import {AuthContext} from "../context/AuthContext.ts";
import language = Battle.language;
import fight from "../assets/judo.png";
import pencil from "../assets/pencil.png";
import {ImageCreator} from "./ImageCreator.tsx";



export const VisualizeBattle= () => {
    const {user}=useContext(AuthContext);
    const {bId} = useParams();
    const [battle, setBattle] = useState<Battle | null>(null);
    const [error, setError] = useState<Error | null>(null);

    const[newStartAt,setNewStartAt]=useState<string>();

    const[newEndAt,setNewEndAt]=useState<string>();

    const[newOME,setNewOME]=useState<boolean>(false);
    const navigate = useNavigate();

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
            setNewOME((battle.enableManualEvaluation ?? false));
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
        if(!battle?.endsAt) return (<></>);
        const endAt=new Date(battle.endsAt);
        const now= new Date();
        if(battle?.participants == undefined )
            return (<></>);
        if(!user)return (<></>)
        if(battle.creator == (user.login) && !battle.enableManualEvaluation && compareDate(now,endAt)==1)
            return (<></>);
        if(battle.creator == (user.login))
            return (editButton())
        if(battle.participants.length == 0 || !battle.participants.map((partcipant) => partcipant.username == user?.login).reduce((boola, boolb) => boola || boolb))
            return (joinButton())
        if(battle.participants.map((partcipant) => partcipant.username == user?.login).reduce((boola, boolb) => boola || boolb))
            return (leaveButton())
        else return (<></>)
    }

    function editButton() {

        return (<div  role="button" className="w-12 h-12 btn btn-ghost btn-circle" onClick={() => (document.getElementById('my_modal_1') as HTMLDialogElement).showModal()}>
            <img src={pencil}/>
        </div>)
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


    const handleOnChange = () => {
        setNewOME(!newOME);
    };

    function joinButton() {
        const now = new Date()
        const startDate= new Date(battle?.startsAt ?? now)
        const endDate= new Date(battle?.endsAt ?? now)
        if(compareDate(now,startDate) == 2 || compareDate(now,endDate)==1)
            return (<></>)
        return (<button style={{width:"100%"}} className="btn btn-success" onClick={() => joinBattle()}>Join</button>)
    }
    async function editBattle(){
        (document.getElementById('my_modal_2') as HTMLDialogElement).showModal();
        if(!battle)return;
        let s1,s2,s3;
        if(newStartAt!=undefined)s1=newStartAt;
        else s1=(battle?.startsAt ?? "");
        if(newEndAt!=undefined)s2=newEndAt;
        else s2=(battle?.endsAt ?? "");
        if(newOME)s3=true;
        else if(!newOME)s3=false;
        else s3=(battle?.enableManualEvaluation ?? true);
        const updateBattle:BattleUpdateDTO={startsAt: s1, endsAt: s2, enableManualEvaluation: s3};
        if(battle.id!=undefined){
            try{
                await BattleService.updateById1(battle.id,updateBattle);
                navigate(location);
                location.reload();
            } catch (e) {
                setError(e as Error);
                if(error?.message)alert(error.message);
            }
        }
    }
    function tournamentDate(){
        if(!battle)return(<>No information</>);
        const startDate = new Date((battle.startsAt ?? ""));
        const endDate = new Date((battle.endsAt ?? ""));
        let message;
        if(battle.enableManualEvaluation) message="Manual evaluation: true";
        else message="Manual evaluation: false";
        return(
            <>
                <ul className="menu menu-vertical lg:menu-horizontal" style={{width: "100%"}}>
                    <div style={{width: "50%"}}>
                        <label style={{width: "50%"}} className="label cursor-pointer  rounded-b-btn textarea "><h1
                            className="font-bold">Old</h1></label>
                    </div>

                    <div style={{width: "50%"}}>
                        <label style={{width: "50%"}} className="label cursor-pointer  rounded-b-btn textarea ">
                            <h1 className="font-bold">New</h1>
                        </label>
                    </div>
                </ul>
                    <ul className="menu menu-vertical lg:menu-horizontal" style={{width: "100%"}}>
                        <label style={{width: "50%"}} className="label cursor-pointer  rounded-b-btn textarea "><span
                            className=" font-bold">{"Enrollment deadline: " + startDate.getFullYear() + "/" + (startDate.getMonth() + 1) + "/" +
                            startDate.getDate()}</span></label>
                        <input style={{width: "50%"}} type="date" placeholder="Type here..."
                               className="bg-base-100 "
                               onChange={event => setNewStartAt(event.target.value + "T23:59:59.268Z")}/>
                    </ul>

                    <ul className="menu menu-vertical lg:menu-horizontal" style={{width: "100%"}}>

                        <label style={{width: "50%"}} className="label cursor-pointer  rounded-b-btn textarea "><span
                            className=" font-bold">{"Final deadline: " + endDate.getFullYear() + "/" + (endDate.getMonth() + 1) + "/" +
                            endDate.getDate()}</span></label>
                        <input style={{width: "50%"}} type="date" placeholder="Type here..."
                               className="bg-base-100 "
                               onChange={event => setNewEndAt(event.target.value + "T00:00:00.268Z")}/>

                    </ul>
                    <ul className="menu menu-vertical lg:menu-horizontal" style={{width: "100%"}}>
                        <label style={{width: "50%"}} className="label cursor-pointer  rounded-b-btn textarea "><span
                            className=" font-bold">{message}</span></label>

                        <label style={{width: "50%"}} className="label cursor-pointer  rounded-b-btn textarea ">
                            <span className=" font-bold">OME:</span>
                            <input
                                type="checkbox"
                                id="topping"
                                className="toggle"
                                value="true"
                                checked={newOME}
                                onChange={handleOnChange}
                            />

                        </label>

                    </ul>
                <label className="btn btn-primary" onClick={() => editBattle()}>Edit</label>
            </>
        )


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

    function seeEntries(username: string){
        return (
            <button onClick={()=>{navigate("/tournaments/"+ battle?.tournament?.id?.toString() + "/battles/" + battle?.id?.toString() + "/" + username)}} style={{width:"100%"}} className="btn btn-outline btn-info"> See Entries →</button>
        )
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
                    <th style={{alignItems: "center"}}>{index + 1}</th>
                    <td>
                        <div className="flex items-center gap-3">
                            <div className="avatar">
                                <div className="mask mask-squircle w-12 h-12">
                                    <ImageCreator username={participant.username ?? ""}/>
                                </div>
                            </div>
                            <div>
                                <div className="font-bold">{participant.username}</div>
                            </div>
                        </div>
                    </td>
                    <td>{participant.score}</td>
                    <td>{seeEntries((participant.username ?? " "))}</td>
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

        if (compareDate(now, endDate) == 1 && !battle.enableManualEvaluation) {
            colorBadge = " font-bold badge badge-error";
            colorText = "dropdown-content z-[1] menu p-2 shadow bg-base-300 rounded-box w-52 badge-error badge-outline";
            status = "terminated";
            message = "The battle has ended! :(";


        }
        else if (compareDate(now, endDate) == 1 && battle.enableManualEvaluation) {
            colorBadge = " font-bold badge badge-secondary";
            colorText = "dropdown-content z-[1] menu p-2 shadow bg-base-300 rounded-box w-52 badge-secondary badge-outline";
            status = "Consolidation";
            message = "The creator is performing manual evaluation";


        }else if (compareDate(now, startDate) == 1 && compareDate(now, endDate) != 1) {
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
        if(!battle?.creator)return(<></>);
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
                    <Link to={"/profile/"+battle.creator}>
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
                                    <img src={`https://github.com/${battle.creator}.png`}/>
                                </div>
                            </div>
                        </a>


                    </ul>
                    </Link>

                </div>
            </div>
        )
    }


    if (error) {
        return (
            <>{error.message}</>
        )
    }

    function buttonOME(){
        if ( battle?.endsAt == undefined) return (<></>)
        const endDate = new Date((battle.endsAt ?? ""));
        const now = new Date();
        if(compareDate(now,endDate)==1 && battle.enableManualEvaluation){
            return(
                <>
                    <div style={{paddingLeft: "1%"}}>
                        <div className="stats shadow bg-base-200 border border-base-300">
                            <Link to={location+"/perform/OME"}>
                                <div className="stat bg-error">
                                    <div className="stat-title text-black">Click</div>
                                    <div className="stat-value text-black">
                                        <p className="font-bold">Perform OME</p>
                                    </div>
                                    <div className="stat-desc text-black">for performing the manual evaluation and terminate the battle</div>
                                </div>
                            </Link>

                        </div>
                    </div>
                </>
            )
        }
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
                    <BattleLeaderboard/>
                    <BattleTest/>

                </ul>
                <ul style={{width: "100%"}} className="menu-lg lg:menu-horizontal bg-base-100 rounded-box">

                    <div style={{paddingLeft: "1%"}}>
                        <div className="stats shadow bg-base-200 border border-base-300">

                            <div className="stat">
                                <div className="stat-title">Gain up to</div>
                                <div className="stat-value">{battle?.timelinessBaseScore} extra point</div>
                                <div className="stat-desc">for submitting your final version as soon as possible!</div>
                            </div>

                        </div>
                    </div>
                    <div style={{paddingLeft: "1%"}}>
                        <div className="stats shadow bg-base-200 border border-base-300">
                            <a href={battle?.repositoryUrl}>
                                <div className="stat bg-info">
                                    <div className="stat-title text-black">Click</div>
                                    <div className="stat-value text-black">
                                        <p className="font-bold">Repository Link ↘</p>
                                    </div>
                                <div className="stat-desc text-black">for accessing in the Github repository</div>
                                </div>
                            </a>

                        </div>
                    </div>
                    {buttonOME()}
                </ul>
                <dialog id="my_modal_1" className="modal">
                    <div className="modal-box">
                        <h3 className="font-bold text-lg">Hello! You are the creator so you can be edit
                            this: </h3>
                        {tournamentDate()}
                        <div className="modal-action">
                            <form method="dialog">
                                <button className="btn">Close</button>
                            </form>
                        </div>
                    </div>
                        <form method="dialog" className="modal-backdrop">
                            <button>close</button>
                        </form>
                    </dialog>
            </div>
            <div style={{top: "0%", position: "fixed", width: "100%", height: "10%"}}><NavBar/></div>
        </>

    )
}
