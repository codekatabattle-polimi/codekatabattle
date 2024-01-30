import {useForm} from "react-hook-form";
import {SubmitHandler} from "react-hook-form";
import {Tournament, TournamentDTO, TournamentService} from "../services/openapi";
import {NavBar} from "./NavBar.tsx";
import {useState} from "react";
import {useNavigate} from "react-router-dom";


export default function CreateTournament() { // Manca l'aggiunta di badges e TC
    const {
        register,
        handleSubmit,
    } = useForm<TournamentDTO>()
    const [tournament, setTournament] = useState<Tournament | null>(null);
    const [error, setError] = useState<Error | null>(null);
    const navigate= useNavigate();
    async function fetchCreateTournament(data: TournamentDTO){
        try{
            (document.getElementById('my_modal_2') as HTMLDialogElement).showModal();
            const tournament= await TournamentService.create(data);
            setTournament(tournament);
        }
        catch (error) {
            setError(error as Error);
        }
    }
    const onSubmit: SubmitHandler<TournamentDTO> = (data) => fetchCreateTournament(data);

    if(error) {
        return(
            <>{error.message}</>
        );
    }

    if (tournament) {
        const path="/tournaments/"+tournament.id?.toString();
        navigate(path);
    }

    return (
        /* "handleSubmit" will validate your inputs before invoking "onSubmit" */
        <>
            <form onSubmit={handleSubmit(onSubmit)} >
                <h1 className="text-3xl font-bold" style={{padding: "1%"}}>Create Tournament</h1>
                {/* register your input into the hook by invoking the "register" function */}
                <div style={{padding: "1%"}}>
                    <input className="textarea textarea-primary" {...register("title", {required: true})}
                           placeholder="Tournament Title..." style={{width: "33.33%"}}/>
                </div>
                <div style={{padding: "1%"}}>
                    <input className="textarea textarea-primary" {...register("description")}
                           style={{width: "99%"}} placeholder="Description..."/>
                </div>
                <div style={{paddingLeft: "0.5%"}}>
                    <ul style={{width: "70%"}}
                        className="menu menu-vertical lg:menu-horizontal">
                        <div style={{width: "30%", paddingRight: "2%"}}>
                            <ul style={{width: "100%", paddingTop: "2%", paddingBottom: "2%"}}
                                className="menu menu-vertical lg:menu-horizontal bg-base-200 rounded-box">
                                <div className="font-bold" style={{paddingRight: "1%"}}>Enrollment deadline:</div>
                                <input type="date" {...register("startsAt", {
                                    required: true,
                                    setValueAs: (value) => value + "T09:40:46.268Z"
                                })} />
                            </ul>
                        </div>

                        <div style={{width: "23%"}}>
                            <ul style={{width: "100%", paddingTop: "2%", paddingBottom: "2%"}}
                                className="menu menu-vertical lg:menu-horizontal bg-base-200 rounded-box">
                                <div className="font-bold" style={{paddingRight: "1%"}}>Final deadline:</div>
                                <input type="date" {...register("endsAt", {
                                    required: true,
                                    setValueAs: (value) => value + "T09:40:46.268Z"
                                })}/>
                            </ul>
                        </div>


                    </ul>
                </div>
                <div style={{padding: "1%"}}>
                    <button className="btn btn-primary">
                        <input type="submit" value="Submit"/>
                    </button>
                    <dialog id="my_modal_2" className="modal">
                            <span className="loading loading-spinner text-primary"></span>
                    </dialog>
                </div>
            </form>
            <div style={{top: "0%", position: "fixed", width: "100%", height: "10%"}}><NavBar/></div>
            {/* Open the modal using document.getElementById('ID').showModal() method */}
            <button className="btn"
                    onClick={() => (document.getElementById('my_modal_2') as HTMLDialogElement).showModal()}>open modal
            </button>
            <dialog id="my_modal_2" className="modal">
                <span className="loading loading-spinner text-primary"></span>
            </dialog>
        </>
    )
}