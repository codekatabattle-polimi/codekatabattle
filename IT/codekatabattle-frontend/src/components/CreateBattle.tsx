import {SubmitHandler, useForm} from "react-hook-form";
import {BattleDTO, BattleService, BattleTest} from "../services/openapi";
import {NavBar} from "./NavBar.tsx";
import {useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import createbattle from "../assets/createbattle.png";
import privacy = BattleTest.privacy;


export default function CreateBattle() {

    const { register, formState: { errors }, handleSubmit } = useForm<BattleDTO>();
    const [error, setError] = useState<Error | null>(null);
    const navigate= useNavigate();
    const params = useParams();
    const [testList, setTestList]=useState<BattleTest[] | null>(null);

    async function fetchCreateBattle(data: BattleDTO) {
        try {
            data.tests = (testList ?? []).filter((battleTest ) => (battleTest.name!="_HIDE_" || battleTest.input!="_HIDE_" || battleTest.expectedOutput!="_HIDE_" || battleTest.givesScore!=0 || battleTest.privacy!=privacy.PRIVATE));
            data.tournamentId = +params.tId!;
            if(data.language.toString()=="PYTHON")
                data.language=BattleDTO.language.PYTHON;
            if(data.language.toString()=="GOLANG")
                data.language=BattleDTO.language.GOLANG;
            (document.getElementById('my_modal_2') as HTMLDialogElement).showModal();
            const battle = await BattleService.create1(data);
            navigate("/tournaments/" + params.tId + "/battles/" + battle.id?.toString())
        } catch (error) {
            setError(error as Error);
        }
    }

    function isDuplicate(list : Array<BattleTest>|null){
        if (list == null)
            return false;
        else
        return list.filter((battleTest ) => (battleTest.name!="_HIDE_" || battleTest.input!="_HIDE_" || battleTest.expectedOutput!="_HIDE_" || battleTest.givesScore!=0 || battleTest.privacy!=privacy.PRIVATE)).map((battleTest) =>(test.name==battleTest.name)).reduce((testA, testB) => (testA || testB));
    }

    const test: BattleTest = {name: "", input: "", expectedOutput: "", givesScore: 0, privacy: privacy.PRIVATE}
    function loadData(name: string, input: string, expectedOutput: string, givenScore: string, isPublic : string){

        if (name=="" || input=="" || expectedOutput=="" || givenScore=="")
            alert("Fill all the fields")
        else if( isDuplicate(testList))
            alert("A test with this name exist already.\nChange name please")
        else{
            test.name=name;
            test.input=input;
            test.expectedOutput=expectedOutput;
            test.givesScore= +givenScore!;
            if(isPublic == "PUBLIC")
                test.privacy= privacy.PUBLIC;
            else if(isPublic == "PRIVATE")
                test.privacy= privacy.PRIVATE;
            const tests : BattleTest[] = (testList ?? []).concat(test);
            setTestList(tests);


        }


    }






    const AddTestForm = () => {

        const[name, setName] = useState('');
        const[input, setInput] = useState('');
        const[expectedOutput, setExpectedOutput] = useState('');
        const[givenScore, setGivenScore] = useState('');
        const[isPublic, setPublic] = useState("PRIVATE");


        return (
            <dialog id="my_modal_3" className="modal">
                <ul style={{width: "80%"}} className="menu-lg lg:menu-horizontal bg-base-100 rounded-box">
                    <div style={{width: "50%"}}>
                        <div className="modal-box">
                            <p className="font-bold text-xl">Add test</p>
                            {/*Test form*/}
                            <div className="overflow-x-auto">
                                <div style={{padding: "2%"}}>
                                    <input style={{width: "100%"}}
                                           className="textarea textarea-primary bg-base-300"
                                           onChange={event => setName(event.target.value)}
                                           placeholder="Test name..."/>
                                </div>
                                <div style={{padding: "2%"}}>
                                    <input
                                        className="textarea textarea-primary bg-base-300"
                                        onChange={event => setInput(event.target.value)}
                                        placeholder="Test input..." style={{width: "100%"}}/>
                                </div>
                                <div style={{padding: "2%"}}>
                                    <input
                                        className="textarea textarea-primary bg-base-300"
                                        onChange={event => setExpectedOutput(event.target.value)}
                                        placeholder="Expected output..." style={{width: "100%"}}/>
                                </div>
                                <div style={{padding: "2%"}}>
                                    <input
                                        className="textarea textarea-primary bg-base-300"
                                        onChange={event => setGivenScore(event.target.value)}
                                        placeholder="Score given..." style={{width: "100%", paddingBottom: "1%"}}/>
                                </div>
                                <div className="form-control" style={{width: "30%", padding: "2%"}}>
                                    <label style={{paddingLeft: "0.5%"}} className="form-control w-full max-w-xs ">
                                        <div className="label">
                                            <span className="label-text"></span>
                                        </div>
                                        <select onChange={event => {
                                            setPublic(event.target.value)
                                        }}
                                                className="select select-bordered bg-base-200 rounded-b-btn textarea textarea-primary">
                                            <option value={"PUBLIC"}>PUBLIC</option>
                                            <option disabled selected value={"PRIVATE"}>PRIVATE</option>
                                        </select>
                                        <div className="label">
                                        </div>
                                    </label>

                                </div>

                                <label onClick={() => loadData(name, input, expectedOutput, givenScore, isPublic)}
                                       className="btn btn-primary">Add test</label>


                            </div>
                            {/*botton close*/
                            }
                            <div className="modal-action">
                                <form method="dialog">

                                    <button className="btn">Close</button>
                                </form>
                            </div>
                        </div>

                    </div>

                    <table style={{width: "50%"}} className="table ">
                        <tbody>
                        <div style={{height: "fit-content"}} className="boxx">
                            <div className="boxx-inner">
                                <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Input</th>
                                    <th>Expected output</th>
                                    <th>Score given</th>
                                    <th>Privacy</th>
                                </tr>
                                </thead>
                                {testAdded()}
                            </div>

                        </div>

                        </tbody>
                    </table>

                </ul>
                {/*
                <form method="dialog" className="modal-backdrop">
                    <button>close</button>
                </form>
                */}


            </dialog>
        )
    }


    function remove(index: number) {
        const list = testList;
        if (!testList || !list)
            return;
        if (testList?.length - 1 == index) {
            list.pop();
            setTestList(list);
            loadData("_HIDE_", "_HIDE_", "_HIDE_", "0", "PRIVATE");

        } else {
            const t: BattleTest = list[testList?.length - 1];
            list[testList?.length - 1] = list[index];
            list[index] = t;
            list.pop();
            setTestList(list);
            loadData("_HIDE_", "_HIDE_", "_HIDE_", "0", "PRIVATE");

        }
    }


    const removeButton = (index: number) => {

        return (<label className="badge badge-error badge-outline"
                       onClick={() => remove(index)}>- remove</label>)
    }

    function visualizeAdd(test: BattleTest, index: number) {
        if (test.name == "_HIDE_" && test.input == "_HIDE_" && test.expectedOutput == "_HIDE_" && test.givesScore == 0 && test.privacy == privacy.PRIVATE)
            return (<></>)
        else return (
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


                <th>
                    {test.privacy?.toString()}
                </th>

                <th>
                    {removeButton(index)}
                </th>
            </tr>
        )
    }

    function testAdded() {
        if (testList == null)
            return (<></>)

        return (
            testList.map((test, index) => visualizeAdd(test, index))
        );
    }

    const onSubmit1: SubmitHandler<BattleDTO> = (data) => fetchCreateBattle(data);


    if (error) {
        return (
            <>{error.message.toString()}</>
        );
    }


    return (
        /* "handleSubmit" will validate your inputs before invoking "onSubmit" */
        <>
            <form onSubmit={handleSubmit(onSubmit1)}
                  style={{alignSelf: "end", top: "8%", position: "fixed", width: "100%"}}>
                <ul className="menu menu-vertical lg:menu-horizontal " style={{width: "100%"}}>
                    <div className="w-12 h-12 rounded-full"
                         style={{paddingLeft: "0.5%", paddingTop: "1%",}}>
                        <img src={createbattle}/>
                    </div>
                    <h1 className="text-3xl font-bold" style={{paddingTop: "1.5%", paddingLeft: "0.5%"}}>Create
                        Battle</h1>
                </ul>
                <ul className="menu menu-vertical lg:menu-horizontal " style={{width: "100%"}}>
                    <div style={{padding: "1%", width: "33.33%"}}>
                        <input
                            className="textarea textarea-primary bg-base-200" {...register("title", {required: true})}
                            placeholder="Battle Title..." style={{width: "100%"}}/>
                    </div>

                    <div style={{padding: "1%", width: "33.33%"}}>
                        <input className="textarea textarea-primary bg-base-200" placeholder="Time liness base score..."
                           style={{width: "100%"}}
                           {...register("timelinessBaseScore", {required: true, pattern: /^[0-9]+$/i})}
                           aria-invalid={errors.timelinessBaseScore ? "true" : "false"}
                    />
                </div>
                {errors.timelinessBaseScore?.type === 'required' &&
                    <p style={{color: "#DC143C", paddingTop: "1.7%"}} className="font-bold" role="alert">Insert a
                        number!!!</p>}

            </ul>

            <div style={{paddingBottom: "1.5%", paddingLeft: "1.5%"}}>
                <input className="textarea textarea-primary bg-base-200" {...register("description")}
                       style={{width: "99%"}} placeholder="Description..."/>
            </div>
            <div style={{paddingLeft: "1%"}}>
                <ul style={{width: "70%"}}
                    className="menu menu-vertical lg:menu-horizontal">
                    <div style={{width: "33%", paddingRight: "2%"}}>
                        <ul style={{width: "100%", paddingTop: "2%", paddingBottom: "2%", height: "100%"}}
                            className="menu menu-vertical lg:menu-horizontal bg-base-200 rounded-b-btn textarea textarea-primary">
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
                            className="menu menu-vertical lg:menu-horizontal bg-base-200  rounded-b-btn textarea textarea-primary">
                            <div className="font-bold" style={{paddingRight: "1%", paddingTop: "1%"}}>Final
                                deadline:
                            </div>
                            <input type="date" {...register("endsAt", {
                                required: true,
                                setValueAs: (value) => value + "T09:40:46.268Z"
                            })}/>
                        </ul>
                    </div>

                </ul>
                <div >
                    <ul style={{width: "70%"}} className="menu menu-vertical lg:menu-horizontal">
                <div className="form-control" style={{width: "15%"}}>
                    <label className="label cursor-pointer bg-base-200 rounded-b-btn textarea textarea-primary">
                                <span className="label-text font-bold"
                                      style={{}}>SAT: </span>
                        <input  {...register("enableSAT")} type="checkbox" className="toggle"
                                value="true"/>
                    </label>
                </div>
                <div className="form-control" style={{width: "30%", paddingLeft: "2%"}}>
                    <label className="label cursor-pointer bg-base-200 rounded-b-btn textarea textarea-primary">
                                <span className="label-text font-bold"
                                      style={{paddingLeft: "1%"}}>Manual evaluation: </span>
                        <input  {...register("enableManualEvaluation")} type="checkbox" className="toggle"
                                value="true"/>
                    </label>
                </div>
            </ul>
                </div>

            <label style={{paddingLeft: "0.5%"}} className="form-control w-full max-w-xs ">
                <div className="label">
                    <span className="label-text">Select the language</span>
                </div>
                <select {...register("language")}
                        className="select select-bordered bg-base-200 rounded-b-btn textarea textarea-primary">
                    <option disabled selected>no selection</option>
                    <option value="PYTHON">Python</option>
                    <option value="GOLANG">Golang</option>
                </select>
                <div className="label">
                </div>
            </label>

        </div>

        <div style={{paddingLeft: "1.5%"}}>
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