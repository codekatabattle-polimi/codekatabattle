import {useForm} from "react-hook-form";
import {SubmitHandler} from "react-hook-form";
import {Tournament, TournamentDTO, TournamentService} from "../services/openapi";
import {NavBar} from "./NavBar.tsx";
import {useState} from "react";
import {useNavigate} from "react-router-dom";
import avatar3 from "../assets/avatar3.png"

export default function CreateTournament() { // Manca l'aggiunta di badges e TC
    const usernames=["giuseppe","nick","giovanni"];
    const { register, formState: { errors }, handleSubmit } = useForm<TournamentDTO>();
    const [tournament, setTournament] = useState<Tournament | null>(null);
    const [error, setError] = useState<Error | null>(null);
    const navigate= useNavigate();
    function selectCoordinator(){
        if (usernames==null) return(<></>);
        return(
            usernames.map((username: string)=>(
                <>
                    <tr>
                        <th>
                            <label>
                                <input {...register("coordinators")} type="checkbox" className="checkbox" value={username}/>
                            </label>
                        </th>
                        <td>
                            <div className="flex items-center gap-3">
                                <div className="avatar">
                                    <div className="mask mask-squircle w-12 h-12">
                                        <img src={avatar3}
                                             alt="Avatar Tailwind CSS Component"/>
                                    </div>
                                </div>
                                <div>
                                    <div className="font-bold">{username}</div>
                                    <div className="text-sm opacity-50">Brazil</div>
                                </div>
                            </div>
                        </td>
                    </tr>
                </>))
        )
    }

    async function fetchCreateTournament(data: TournamentDTO) {
        try {
            (document.getElementById('my_modal_2') as HTMLDialogElement).showModal();
            if (data.privacy != "PUBLIC") data.privacy = TournamentDTO.privacy.PRIVATE;
            else data.privacy = TournamentDTO.privacy.PUBLIC;
            const tournament = await TournamentService.create(data);
            setTournament(tournament);
        } catch (error) {
            setError(error as Error);
        }
    }

    const onSubmit: SubmitHandler<TournamentDTO> = (data) => fetchCreateTournament(data);

    if (error) {
        return (
            <>{error.message}</>
        );
    }

    if (tournament) {
        const path = "/tournaments/" + tournament.id?.toString();
        navigate(path);
    }

    return (
        /* "handleSubmit" will validate your inputs before invoking "onSubmit" */
        <>
            <form onSubmit={handleSubmit(onSubmit)}
                  style={{alignSelf: "end", top: "8%", position: "fixed", width: "100%"}}>
                <h1 className="text-3xl font-bold" style={{padding: "1.5%"}}>Create Tournament</h1>
                <ul className="menu menu-vertical lg:menu-horizontal " style={{width: "100%"}}>
                    <div style={{padding: "1%", width: "33.33%"}}>
                        <input className="textarea textarea-primary" {...register("title", {required: true})}
                               placeholder="Tournament Title..." style={{width: "100%"}}/>
                    </div>

                    <div style={{padding: "1%", width: "33.33%"}}>
                        <input className="textarea textarea-primary" placeholder="Max number of students..."
                               style={{width: "100%"}}
                               {...register("maxParticipants", {required: true, pattern: /^[0-9]+$/i})}
                               aria-invalid={errors.maxParticipants ? "true" : "false"}
                        />
                    </div>
                    {errors.maxParticipants?.type === 'required' &&
                        <p style={{color: "#DC143C", paddingTop: "1.7%"}} className="font-bold" role="alert">Insert a
                            number!!!</p>}

                </ul>

                <div style={{padding: "1.5%"}}>
                    <input className="textarea textarea-primary" {...register("description")}
                           style={{width: "99%"}} placeholder="Description..."/>
                </div>
                <div style={{paddingLeft: "1%"}}>
                    <ul style={{width: "70%"}}
                        className="menu menu-vertical lg:menu-horizontal">
                        <div style={{width: "33%", paddingRight: "2%"}}>
                            <ul style={{width: "100%", paddingTop: "2%", paddingBottom: "2%", height: "100%"}}
                                className="menu menu-vertical lg:menu-horizontal bg-base-200 rounded-box textarea textarea-primary">
                                <div className="font-bold" style={{paddingRight: "1%", paddingTop: "1%"}}>Enrollment
                                    deadline:
                                </div>
                                <input type="date" {...register("startsAt", {
                                    required: true,
                                    setValueAs: (value) => value + "T09:40:46.268Z"
                                })} />
                            </ul>
                        </div>

                        <div style={{width: "33%"}}>
                            <ul style={{width: "100%", paddingTop: "2%", paddingBottom: "2%", height: "100%"}}
                                className="menu menu-vertical lg:menu-horizontal bg-base-200 rounded-box textarea textarea-primary">
                                <div className="font-bold" style={{paddingRight: "1%", paddingTop: "1%"}}>Final
                                    deadline:
                                </div>
                                <input type="date" {...register("endsAt", {
                                    required: true,
                                    setValueAs: (value) => value + "T09:40:46.268Z"
                                })}/>
                            </ul>
                        </div>

                        <div className="form-control" style={{width: "18%", paddingLeft: "2%"}}>
                            <label className="label cursor-pointer bg-base-200 rounded-box textarea textarea-primary">
                                <span className="label-text font-bold" style={{paddingLeft: "1%"}}>Public:</span>
                                <input  {...register("privacy")} type="checkbox" className="toggle" value="PUBLIC"/>
                            </label>
                        </div>
                    </ul>
                    <div style={{ padding: "1%"}}>
                        <p  className="badge badge-primary badge-outline" onClick={()=>(document.getElementById('my_modal_3') as HTMLDialogElement).showModal()}>+ Add coordinator</p>
                    </div>
                </div>
                <div style={{padding: "1.5%"}}>
                    <button className="btn btn-primary">
                        <input type="submit" value="Submit"/>
                    </button>
                    <dialog id="my_modal_2" className="modal">
                        <span className="loading loading-spinner text-primary"></span>
                    </dialog>
                </div>
                <dialog id="my_modal_3" className="modal">
                    <div className="modal-box">
                        <p className="font-bold text-xl">Add coordinator</p>
                        {/*menu coordinator*/}
                        <div className="overflow-x-auto">
                            <table className="table">
                                {/* head */}
                                <thead>
                                <tr>
                                    <th>
                                        <label>
                                            Selected
                                        </label>
                                    </th>
                                    <th>Name</th>
                                    <th></th>
                                </tr>
                                </thead>
                                <tbody>
                                {/* row 1 */}
                                {selectCoordinator()}
                                </tbody>


                            </table>
                        </div>
                        {/*botton close*/}
                        <div className="modal-action">
                            <form method="dialog">
                                {/* if there is a button in form, it will close the modal */}
                                <button className="btn">Close</button>
                            </form>
                        </div>
                    </div>
                    <form method="dialog" className="modal-backdrop">
                        <button>close</button>
                    </form>
                </dialog>
            </form>
            <div style={{top: "0%", position: "fixed", width: "100%", height: "10%"}}><NavBar/></div>
        </>
    )
}