import {Link, useParams} from "react-router-dom";
import {useContext, useEffect, useState} from "react";
import {AuthContext} from "../context/AuthContext.ts";
import {Battle, BattleParticipant, BattleParticipantUpdateDTO, BattleService} from "../services/openapi";
import {NavBar} from "./NavBar.tsx";
import nunchaku from "../assets/nunchaku.png";

export const PerformeOME= () => {
    const params = useParams();
    const {user}=useContext(AuthContext);
    const [battle, setBattle] = useState<Battle | null>(null);
    const [error, setError] = useState<Error | null>(null);
    const [OMEs] = useState(
        new Array(battle?.participants?.length).fill(0)
    );
    const handleOnChange = (position: number ,value: number) => {
        OMEs[position]=value;
    }
    async function performOME(index:number, participantId:number){
        if(participantId==-1){
            alert("Participant Id not found");
            return;
        }
        if(!battle?.id){
            alert("Battle Id not found");
            return;
        }
        const battleUpdate:BattleParticipantUpdateDTO={score:OMEs[index]}
        try{
            await BattleService.updateBattleParticipantById(battle.id,participantId,battleUpdate);
            location.reload();
        }catch (e){
            setError(e as Error);
            if(error?.message)alert(error.message);
        }
    }

    useEffect(() => {
        fetchParticipants();
    }, [params,user]);

    async function fetchParticipants(){
        try {
            const battle=await BattleService.findById1(+params.bId!);
            setBattle(battle);
        } catch (e) {
            setError(e as Error);
            if(error?.message)alert(error.message);
        }
    }
    if(!battle)return (
        <>
            <div style={{top: "0%", position: "fixed", width: "100%", height: "10%"}}><NavBar/></div>
            <center>No battle</center>
        </>

    )
        ;
    if (battle.creator != user?.login) return (
        <>
            <center>
                <div className="stat-value text-2xl" style={{top: "8%", position: "fixed", width: "100%", height: "10%"}}>The Performe OME can be executed only by the creator of the Battle
                </div>
            </center>
            <div style={{top: "0%", position: "fixed", width: "100%", height: "10%"}}><NavBar/></div>
            <center>No battle</center>
        </>

    );
    function OMEorNotOME(index:number,participantId:number,recivedOME: boolean){
        if(!recivedOME)return(
            <>
                <td>
                    <input
                        className="textarea textarea-primary bg-base-200"
                        onChange={event => handleOnChange(index, +event.target.value)}
                        placeholder="Score OME..."/>
                </td>
                <td>
                    <label className="btn btn-primary"
                           onClick={() => performOME(index, participantId)}>Edit</label>
                </td>
            </>
        )
        return (
            <>
                <td>Already performed</td>
                <td></td>
            </>
        )
    }

    function participants() {
        if (!battle) return (<>nada</>)
        if (!battle.participants) return (<>nanda</>)
        return (battle.participants.map(((participant: BattleParticipant, index) => (
                <tr>
                    <th>{index + 1}</th>
                    <td>{participant.username}</td>
                    <td>{participant.score}</td>
                    <td><Link to={location + (participant.username ?? "")}>Repository link</Link></td>
                    {OMEorNotOME(index,(participant.id ?? -1),(participant?.receivedOME ?? true))}
                </tr>
            )))
        )
    }

    return (
        <>
            <div style={{top: "8%", width: "100%", position: "absolute"}}>

                <div className="navbar bg-base-200" style={{width: "100%"}}>
                <div className="navbar-start"></div>
                    <div className="navbar-center">
                        <div className="w-10 h-10 rounded-full">
                            <img src={nunchaku}/>
                        </div>
                        <h1 className=" text-3xl font-bold">Performe OME</h1>
                        <div className="w-10 h-10 rounded-full">
                            <img src={nunchaku}/>
                        </div>
                    </div>
                    <div className="navbar-end"></div>
                </div>
                <div className="overflow-x-auto">
                    <table className="table table-xs">
                        <thead>
                        <tr>
                            <th></th>
                            <th>Participant</th>
                            <th>Score</th>
                            <th>Repository Link</th>
                            <th>OME</th>
                            <th>edit</th>
                        </tr>
                        </thead>
                        <tbody>
                        {participants()}
                        </tbody>
                    </table>
                </div>
            </div>
            <div style={{top: "0%", position: "fixed", width: "100%", height: "10%"}}><NavBar/></div>

        </>
    )
}