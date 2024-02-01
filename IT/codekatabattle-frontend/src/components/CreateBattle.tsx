import {SubmitHandler, useForm} from "react-hook-form";
import {Battle, BattleDTO, BattleService, BattleTest} from "../services/openapi";
import {NavBar} from "./NavBar.tsx";
import {useState} from "react";
import {useNavigate, useParams} from "react-router-dom";


export default function CreateBattle() {

    const { register,  handleSubmit } = useForm<BattleDTO>();
    const [battle, setBattle] = useState<Battle | null>(null);
    const [error, setError] = useState<Error | null>(null);
    const navigate= useNavigate();
    const tId = useParams();

    async function fetchCreateBattle(data: BattleDTO) {
        try {
            data.tournamentId = +tId;
            if(data.language.toString()=="PYTHON")
                data.language=BattleDTO.language.PYTHON;
            if(data.language.toString()=="GOLANG")
                data.language=BattleDTO.language.GOLANG;
            (document.getElementById('my_modal_2') as HTMLDialogElement).showModal();
            const battle = await BattleService.create1(data);
            setBattle(battle);
        } catch (error) {
            setError(error as Error);
        }
    }



    const AddTestForm = () => {
        const { register,  handleSubmit } = useForm<BattleTest>();

        return (
            <dialog   id="my_modal_3" className="modal">
                <div className="modal-box h-11/12 max-h-5xl">
                    <p className="font-bold text-xl">Add test</p>
                    {/*Test form*/}
                    <div className="overflow-x-auto">
                        <form onSubmit={handleSubmit(onSubmit2)}>
                            <div style={{padding: "2%"}}>
                                <input style={{width:"100%"}} className="textarea textarea-primary " {...register("name", {required: true})}
                                        placeholder="Test name..." /></div>
                            <input className="textarea textarea-primary" {...register("input", {required: true})}
                                   placeholder="Test input..." style={{width: "100%", paddingBottom:"1%"}}/>
                            <input
                                className="textarea textarea-primary" {...register("expectedOutput", {required: true})}
                                placeholder="Expected output..." style={{width: "100%", paddingBottom:"1%"}}/>
                            <input className="textarea textarea-primary" {...register("givesScore", {required: true})}
                                   placeholder="Score given..." style={{width: "100%", paddingBottom:"1%"}}/>
                            <div className="form-control" style={{width: "18%", paddingLeft: "2%"}}>
                                <label
                                    className="label cursor-pointer bg-base-200 rounded-box textarea textarea-primary">
                                    <span className="label-text font-bold" style={{paddingLeft: "1%"}}>Public:</span>
                                    <input  {...register("public")} type="checkbox" className="toggle" value="true"/>
                                </label>

                            </div>
                            <button className="btn btn-primary">
                                <input type="submit" value="Submit"/>
                            </button>
                        </form>

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
        )
    }

    const onSubmit1: SubmitHandler<BattleDTO> = (data) => fetchCreateBattle(data);
    const onSubmit2: SubmitHandler<BattleTest> = (data ) => console.log(data);

    if (error) {
        return (
            <>{error.message}</>
        );
    }

    if (battle) {
        const path = "/battles/" + battle.id?.toString();
        navigate(path);
    }

    return (
        /* "handleSubmit" will validate your inputs before invoking "onSubmit" */
        <>
            <form onSubmit={handleSubmit(onSubmit1)}
                  style={{alignSelf: "end", top: "8%", position: "fixed", width: "100%"}}>
                <h1 className="text-3xl font-bold" style={{padding: "1.5%"}}>Create Battle</h1>
                <ul className="menu menu-vertical lg:menu-horizontal " style={{width: "100%"}}>
                    <div style={{padding: "1%", width: "33.33%"}}>
                        <input className="textarea textarea-primary" {...register("title", {required: true})}
                               placeholder="Battle Title..." style={{width: "100%"}}/>
                    </div>


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
                        <div className="form-control" style={{width: "fit-content", paddingLeft: "2%"}}>
                            <label
                                className="label cursor-pointer bg-base-200 rounded-box textarea textarea-primary">
                                <span className="label-text font-bold"
                                      style={{paddingLeft: "1%", paddingRight: "2%"}}>SAT:</span>
                                <input  {...register("enableSAT")} type="checkbox" className="toggle" value="true"/>
                            </label>
                        </div>
                    </ul>
                    <label style={{paddingLeft: "0.5%"}} className="form-control w-full max-w-xs">
                        <div className="label">
                            <span className="label-text">Select the language</span>
                        </div>
                        <select {...register("language")} className="select select-bordered">
                            <option disabled selected>no selection</option>
                            <option value="PYTHON">Python</option>
                            <option value="GOLANG">Golang</option>
                        </select>
                        <div className="label">
                        </div>
                    </label>
                </div>

                <div style={{padding: "1%"}}>
                    <p className="badge badge-primary badge-outline"
                       onClick={() => (document.getElementById('my_modal_3') as HTMLDialogElement).showModal()}>+ Add
                        test</p>
                </div>

                <AddTestForm/>

                <div style={{padding: "1.5%"}}>
                    <button className="btn btn-primary">
                        <input type="submit" value="Submit"/>
                    </button>
                    <dialog id="my_modal_2" className="modal">
                        <span className="loading loading-spinner text-primary"></span>
                    </dialog>
                </div>

            </form>
            <div style={{top: "0%", position: "fixed", width: "100%", height: "10%"}}><NavBar/></div>
        </>
    )
}