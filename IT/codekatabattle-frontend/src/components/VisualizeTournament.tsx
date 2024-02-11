import tournamentimg from "../assets/tournament.png"
import {useContext, useEffect, useState} from "react";
import {Tournament, TournamentCoordinator, TournamentDTO, TournamentService} from "../services/openapi";
import {Link, useNavigate, useParams} from "react-router-dom";
import {NavBar} from "./NavBar.tsx";
import privacy = Tournament.privacy;
import {AuthContext} from "../context/AuthContext.ts";
import pencil from "../assets/pencil.png";
import {ImageCreator} from "./ImageCreator.tsx";



export const VisualizeTournament= () => {
    const {user}=useContext(AuthContext);
    const {id} = useParams();
    const [tournament, setTournament] = useState<Tournament | null>(null);
    const [error, setError] = useState<Error | null>(null);
    const navigate= useNavigate();
    const[newTitle,setNewTitle]=useState<string>();

    const[newStartAt,setNewStartAt]=useState<string>();

    const[newEndAt,setNewEndAt]=useState<string>();

    const[newDescription,setNewDescription]=useState<string>();

    const[newPublic,setNewPublic]=useState<boolean>(false);

    const[newMaxParticipants,setnewMaxParticipants]=useState<number>();

    useEffect(() => {
        fetchTournament();
    }, [id]);

    if (!id) {
        return (
            <>Id not found</>
        )
    }
    const handleOnChange = () => {
        setNewPublic(!newPublic);
    };
    async function fetchTournament(){
        try {
            const tournament = await TournamentService.findById(+id!);
            setTournament(tournament);
            setNewPublic((tournament?.privacy==Tournament.privacy.PUBLIC));
        } catch (error) {
            setError(error as Error);
        }
    }
    function editDescription() {
        const now = new Date();
        const startDate= new Date(tournament?.startsAt ?? now);
        if(compareDate(now,startDate)==1)return (<></>);
        if(tournament==null) return (<></>);
        if(!user) return (<></>);
        if(tournament.creator == (user.login)) {
            return(
            <div role="button" className="w-12 h-12 btn btn-ghost btn-circle"
                 onClick={() => (document.getElementById('my_modal_4') as HTMLDialogElement).showModal()}>
                <img src={pencil}/>
            </div>
        )
        }


    }

    function compareDate(date1: Date, date2: Date) {
        if (date1.getFullYear() > date2.getFullYear())
            return 1;
        if (date1.getFullYear() < date2.getFullYear())
            return 2;
        if (date1.getMonth() > date2.getMonth())
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
        const now = new Date();
        const startDate= new Date(tournament?.startsAt ?? now);
        if(compareDate(now,startDate)==1)return (<></>);
        if(tournament?.participants == undefined )
            return (<></>)
        if(!user)return (<></>)
        if(tournament.creator == (user.login))
            return (editButton())
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
    function editButton() {

        return (<div  role="button" className="w-12 h-12 btn btn-ghost btn-circle" onClick={() => (document.getElementById('my_modal_1') as HTMLDialogElement).showModal()}>
            <img src={pencil}/>
        </div>)
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
                                    <ImageCreator username={coordinator.username ?? ""}/>
                                </div>
                            </div>
                            <div>
                                <div className="font-bold">{coordinator.username}</div>
                            </div>
                        </div>
                    </td>
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
                                    <ImageCreator username={participant.username ?? ""}/>
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
                <div style={{paddingLeft: "1%", paddingTop: "1%"}}>
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
        if(!tournament?.creator){
            return(<></>)
        }
        return (
            <div className="navbar bg-base-100">
                <div className="flex-1 navbar-start">

                    <div style={{padding: "1%"}} >
                        <div className="w-12 h-12 rounded-full">
                            <img src={tournamentimg}/>
                        </div>
                    </div>

                    <h2 className="text-3xl font-bold"
                        > {tournament?.title}</h2>
                    {tournamentPrivacy()}
                    {tournamentStatus()}
                </div>
                <div style={{width:"50%"}} className="flex-none navbar-end">
                    <div style={{padding: "2%"}}>
                        {joinOrLeaveButton()}
                    </div>
                    <Link to={"/profile/"+tournament.creator}>
                    <ul className="menu menu-vertical lg:menu-horizontal bg-base-200 rounded-box" >
                        <a>
                            <div style={{paddingRight: "10%", paddingLeft: "1%"}}>
                                <h2 className="text-l"
                                    style={{padding: "1%", paddingTop: "2%"}}> CreatedBy: </h2>
                                <h2 className="text-2xl font-bold"
                                    style={{padding: "1%", paddingTop: "2%"}}> {tournament?.creator}</h2>
                            </div>
                        </a>
                        <li><a></a></li>
                        <a>
                            <div style={{padding: "1%"}} className="avatar">
                                <div className="w-16 h-16 rounded-box" style={{position: "relative", right: "0%"}}>
                                    <ImageCreator username={tournament.creator}/>
                                </div>
                            </div>
                        </a>


                    </ul>
                        </Link>

                </div>
            </div>
        )
    }
    async function editTournamentDescription(){
        (document.getElementById('my_modal_2') as HTMLDialogElement).showModal();
        if(!tournament)return;
        let desc;
        if(newDescription!=undefined)desc=newDescription;
        else desc=(tournament?.description ?? "");
        const coordinators =(tournament.coordinators?.map(((coordinator: TournamentCoordinator):string=>{return coordinator.username ?? ""})));
        const updateTournament:TournamentDTO={startsAt: tournament.startsAt, endsAt: tournament.endsAt, privacy: tournament.privacy, title:tournament.title,maxParticipants: tournament.maxParticipants,description: desc, coordinators: coordinators};
        if(tournament.id!=undefined){
            try{
                await TournamentService.updateById(tournament.id,updateTournament);
                navigate(location);
                location.reload();
            } catch (e) {
                setError(e as Error);
                if(error?.message)alert(error.message);
            }
        }
    }
    async function editTournament(){
        (document.getElementById('my_modal_2') as HTMLDialogElement).showModal();
        if(!tournament)return;
        let s1,s2,s3,s4,s5;
        if(newStartAt!=undefined)s1=newStartAt;
        else s1=(tournament?.startsAt ?? "");
        if(newEndAt!=undefined)s2=newEndAt;
        else s2=(tournament?.endsAt ?? "");
        if(newPublic)s3=Tournament.privacy.PUBLIC;
        else if(!newPublic)s3=Tournament.privacy.PRIVATE;
        else s3=(tournament?.privacy ?? true);
        if(newTitle!=undefined)s4=newTitle;
        else s4=(tournament?.title ?? "");
        if(newMaxParticipants!=undefined)s5=newMaxParticipants;
        else s5=(tournament?.maxParticipants ?? 1000);
        const coordinators =(tournament.coordinators?.map(((coordinator: TournamentCoordinator):string=>{return coordinator.username ?? ""})));
        const updateTournament:TournamentDTO={startsAt: s1, endsAt: s2, privacy: s3, title:s4,maxParticipants: s5,description: (tournament.description ?? ""), coordinators: coordinators};
        if(tournament.id!=undefined){
            try{
                await TournamentService.updateById(tournament.id,updateTournament);
                navigate(location);
                location.reload();
            } catch (e) {
                setError(e as Error);
                if(error?.message)alert(error.message);
            }
        }
    }

    function tournamentDate(){
        if(!tournament)return(<>No information</>);
        const startDate = new Date((tournament.startsAt ?? ""));
        const endDate = new Date((tournament.endsAt ?? ""));
        let message;
        if(tournament.privacy==Tournament.privacy.PUBLIC) message="Tournament is public";
        else message="Tournament is private";
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
                    className=" font-bold">{tournament.title}</span></label>

                <label style={{width: "50%"}} className="label cursor-pointer  rounded-b-btn textarea ">
                    <input
                        className="textarea textarea-primary bg-base-200"
                        onChange={event => setNewTitle(event.target.value)}
                        placeholder="Tournament Title..."/>

                </label>
            </ul>
                <ul className="menu menu-vertical lg:menu-horizontal" style={{width: "100%"}}>
                    <label style={{width: "50%"}} className="label cursor-pointer  rounded-b-btn textarea "><span
                        className=" font-bold">{"Max students:" + tournament.maxParticipants}</span></label>

                    <label style={{width: "50%"}} className="label cursor-pointer  rounded-b-btn textarea ">
                        <input type="number"
                            className="textarea textarea-primary bg-base-200"
                            onChange={event => setnewMaxParticipants(+event.target.value)}
                            placeholder="Max number of students..."/>

                    </label>

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
                        <span className=" font-bold">Public</span>
                        <input
                            type="checkbox"
                            id="topping"
                            className="toggle"
                            value="true"
                            checked={newPublic}
                            onChange={handleOnChange}
                        />

                    </label>

            </ul>
                <label className="btn btn-primary" onClick={() => editTournament()}>Edit</label>
            </>
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
                                    {editDescription()}
                                </div>
                            </div>
                        </div>
                        <ul style={{width: "100%"}} className="menu-lg lg:menu-horizontal bg-base-100 rounded-box">
                            <TournamentLeaderboard/>
                            <TournamentCoordinators/>
                            <TournamentBattles/>
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
                        <dialog id="my_modal_4" className="modal">
                            <div className="modal-box">
                                <h3 className="mb-2 font-bold text-2xl">Hello! You are the creator so you can be edit
                                    this: </h3>
                                <h2 className="mb-1 font-bold text-xl">Old</h2>
                                <h1 className="mb-5 font-bold">{tournament?.description}</h1>
                                <h2 className="mb-2 font-bold text-xl">New</h2>
                                <input style={{width:"100%"}}
                                    className="mb-6 textarea textarea-lg bg-base-200 horizontal"
                                    onChange={event => setNewDescription(event.target.value)}
                                    placeholder="Tournament description..."/>

                                <label className="btn btn-primary" onClick={() => editTournamentDescription()}>Edit</label>

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
                        <dialog id="my_modal_2" className="modal">
                            <span className="loading loading-spinner text-primary"></span>
                        </dialog>
                    </div>
                    <div style={{top: "0%", position: "fixed", width: "100%", height: "10%"}}><NavBar/></div>
                </>


                )

}
