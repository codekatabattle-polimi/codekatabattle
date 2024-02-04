import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import { BattleEntry, BattleService, BattleTestResult} from "../services/openapi";
import {NavBar} from "./NavBar.tsx";

export const BattleEntries= () => {
    const params = useParams();
    const [error, setError] = useState<Error | null>(null);
    const [entries, setEntries] = useState<Array<BattleEntry >| undefined>(undefined);

    useEffect(() => {
        fetchBattle();
    }, [params]);

    if (!params) {
        return (
            <>Id not found</>
        )
    }

    function  participantRight(entry : BattleEntry){
        if(!entry.participant)
            return false;
        return ((entry.participant).username == params.username);
    }
    async function fetchBattle(){
        try {
            const tbattle = await BattleService.findById1(+params.bId!);
            const entryList = tbattle.entries;
            setEntries(entryList?.filter((entry) => participantRight(entry)));
        } catch (error1) {
            setError(error as Error);
            alert(error?.message)
        }
    }

    function testScoreList(testScores : Array<BattleTestResult>){
        return(
            testScores.map((score) => (
                <tr>
                    <th className="font-bold" style={{alignItems: "center"}}>
                        {score.name}
                    </th>

                    <th>
                        {score.input}
                    </th>

                    <th>
                        {score.output}
                    </th>

                    <th>
                        {score.score}
                    </th>

                    <th>
                        {score.exitCode.toString()}
                    </th>

                    <th>
                        {score.timeout.toString()}
                    </th>

                    <th>
                        {score.error?.toString()}
                    </th>

                    <th>
                        {score.passed.toString()}
                    </th>

                </tr>
            ))
        )
    }

    function testScoreListModal(testScores: Array<BattleTestResult>) {
        return (

            <dialog id="my_modal_3" className="modal">
                <div  style={{padding:"1%"}} className="bg bg-base-100 rounded-box">
                    <h1 className="text-2xl font-bold" style={{paddingTop: "1.5%", paddingLeft: "0.5%"}}>
                        Test results
                    </h1>
                    <table>
                        <div style={{height: "fit-content"}} className="boxx">
                            <div className="boxx-inner">
                                <thead>
                                <th>Name</th>
                                <th>Input</th>
                                <th>Output</th>
                                <th>Score</th>
                                <th>Exit code</th>
                                <th>Timeout</th>
                                <th>Error</th>
                                <th>Passed</th>
                                </thead>
                                <tbody>
                                {testScoreList(testScores)}
                                </tbody>
                            </div>
                        </div>

                    </table>

                    <div style={{padding: "2%"}} className="modal-action">
                        <form method="dialog">

                            <button className="btn">Close</button>
                        </form>
                    </div>
                </div>


            </dialog>


        )
    }

    function seeModal(lenght: number) {
        if (lenght == 0) {
            alert("No tests")
            return
        }
        (document.getElementById('my_modal_3') as HTMLDialogElement).showModal()
    }

    function entryList() {
        return (
            entries?.map((entry, index) => (
                    <tr  className=" bg bg-base-200">
                        <th className="font-bold" style={{alignItems: "center"}}>
                            {index + 1}
                        </th>

                        <th>
                            <div>{entry.status}</div>
                        </th>

                        <th>
                            {entry.score}
                        </th>


                        <th>
                            {(entry.processResult?.satResult?.satName ?? "-").toString()}
                        </th>

                        <th>
                            {(entry.processResult?.satResult?.score ?? "-").toString()}
                        </th>

                        <th>
                            {(entry.processResult?.satResult?.warnings ?? "-").toString()}
                        </th>
                        <th>
                            <div style={{paddingLeft: "1.5%"}}>
                                <p className="badge badge-info badge-outline"
                                   onClick={() => seeModal((entry.processResult?.testResults.length ?? 0))}>
                                    See test results
                                </p>
                            </div>
                            {testScoreListModal((entry.processResult?.testResults ?? []))}
                        </th>

                    </tr>
                )
            )
        )
    }

    return (
        <>

            <div className="" style={{alignSelf: "end", position: "fixed", top: "8%", width: "100%"}}>
                <h1 className="text-3xl font-bold" style={{paddingTop: "2%", paddingLeft: "4%"}}>
                    {params.username?.toString() + "'s entries"}
                </h1>
                <div style={{paddingLeft: "3%", paddingRight: "3%", paddingTop: "2%"}} className="overflow-x-auto ">
                    <table  className="table bg bg-base-300">
                    {/* head */}
                        <thead>
                        <tr>
                            <th></th>
                            <th>Status</th>
                            <th>Score</th>
                            <th>Executed SAT</th>
                            <th>SAT score</th>
                            <th>SAT warnings</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        {entryList()}
                        </tbody>
                    </table>
                </div>
            </div>

            <div style={{top: "0%", position: "fixed", width: "100%", height: "10%"}}><NavBar/></div>

        </>
    )

}