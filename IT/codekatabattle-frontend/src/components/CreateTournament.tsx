import {useForm} from "react-hook-form";
import {SubmitHandler} from "react-hook-form";
import {TournamentDTO, TournamentService} from "../services/openapi";


export default function CreateTournament() { // Manca l'aggiunta di badges e TC
    const {
        register,
        handleSubmit,
    } = useForm<TournamentDTO>()
    const onSubmit: SubmitHandler<TournamentDTO> = (data) => TournamentService.create(data)


    return (
        /* "handleSubmit" will validate your inputs before invoking "onSubmit" */

        <form onSubmit={handleSubmit(onSubmit)}>
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
                    <div style={{width: "27%", paddingRight: "2%"}}>
                        <ul style={{width: "100%", paddingTop: "2%", paddingBottom: "2%"}}
                            className="menu menu-vertical lg:menu-horizontal bg-base-200 rounded-box">
                            <div className="font-bold" style={{paddingRight: "1%"}}>Enrollment deadline:</div>
                            <input type="date" {...register("startsAt", {required: true})} />
                        </ul>
                    </div>

                    <div style={{width: "21%"}}>
                        <ul style={{width: "100%", paddingTop: "2%", paddingBottom: "2%"}}
                            className="menu menu-vertical lg:menu-horizontal bg-base-200 rounded-box">
                            <div className="font-bold" style={{paddingRight: "1%"}}>Final deadline:</div>
                            <input type="date" {...register("endsAt", {required: true})}/>
                        </ul>
                    </div>


                </ul>
            </div>
            {/*<div style={{padding: "1%"}}>
                <input className="textarea textarea-primary"{...register("maxNumberOfStudents", {required: true})}
                       placeholder="Max students allowed..."/>
            </div>
            <div style={{padding: "1%"}}>
                <select {...register("tournamentPolicy")}>
                    <option value="friendsOnly">Friends Only</option>
                    <option value="public">Public</option>
                    <option value="private">Private</option>
                </select>

            </div>*/}
            <div style={{padding: "1%"}}>
                <button className="btn btn-primary">
                    <input type="submit" value="Submit"/>
                </button>

            </div>
        </form>
    )

    /*return (
            <>
                <h1 className="text-3xl font-bold" style={{padding:"1%"}}>Create Tournament</h1>

                <form style={{padding: "1%"}}>
                    <input type="textarea" style={{width: "33.33%"}} className="textarea textarea-primary"
                           name="TournamentTitle" id="TournamentTitle"
                           placeholder="Tournament Title..." required/>
                    <div style={{paddingTop: "0.5%"}}>
                        <textarea style={{width: "99%"}} className="textarea textarea-primary textarea-lg"
                                  name="Description" id="Description"
                                  placeholder="Description..." required/>
                    </div>
                    <ul className="menu menu-vertical lg:menu-horizontal">

                        <li><a> <input  type="date" name="EnrollDeadline" id="EnrollDeadline" placeholder="EnrollDeadline" required/></a></li>
                        <li><a><input  type="date" name="FinalDeadline" id="FinalDeadline" placeholder="FinalDeadline" required /></a></li>
                    </ul>
                </form>
            </>

        )*/
}