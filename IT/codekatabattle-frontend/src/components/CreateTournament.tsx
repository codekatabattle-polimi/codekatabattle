import {useForm} from "react-hook-form";
import {SubmitHandler} from "react-hook-form";

enum Policy{
    friendOnly="friendsOnly",
    public="public",
    private="private"
}
interface ICreateTournamentForm{
    tournamentTitle: string
    tournamentDescription: string
    ETD: string
    FTD: string
    maxNumberOfStudents: number
    tournamentPolicy: Policy
}
export default function CreateTournament() {
    const {
        register,
        handleSubmit,
    } = useForm<ICreateTournamentForm>()
    const onSubmit: SubmitHandler<ICreateTournamentForm> = (data) => console.log(data)


    return (
        /* "handleSubmit" will validate your inputs before invoking "onSubmit" */

        <form onSubmit={handleSubmit(onSubmit)}>
            <h1 className="text-3xl font-bold" style={{padding: "1%"}}>Create Tournament</h1>
            {/* register your input into the hook by invoking the "register" function */}
            <div style={{padding: "1%"}}>
                <input className="textarea textarea-primary" {...register("tournamentTitle", {required: true})}
                       placeholder="Tournament Title..." style={{width: "33.33%"}}/>
            </div>
            <div style={{padding: "1%"}}>
                <input className="textarea textarea-primary" {...register("tournamentDescription")}
                       style={{width: "99%"}} placeholder="Description..."/>
            </div>
            <div style={{padding: "1%"}}>
                <input type="date" {...register("ETD", {required: true})} />
                <input type="date" {...register("FTD", {required: true})} />
            </div>
            <div style={{padding: "1%"}}>
                <input  className="textarea textarea-primary"{...register("maxNumberOfStudents", {required: true})} placeholder="Max students allowed..."/>
            </div>
            <div style={{padding: "1%"}}>
                <select {...register("tournamentPolicy")}>
                    <option value="friendsOnly">Friends Only</option>
                    <option value="public">Public</option>
                    <option value="private">Private</option>
                </select>

            </div>
            <div style={{padding: "1%"}}>
                <input type="submit"/>
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