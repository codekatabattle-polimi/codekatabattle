export const CreateTournament= () => {

    return(
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

    )
}