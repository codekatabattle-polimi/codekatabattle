import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import {Battle, BattleEntry, BattleService, BattleTestResult} from "../services/openapi";
import {NavBar} from "./NavBar.tsx";

export const BattleEntries= () => {
    const params = useParams();
    const [battle, setBattle] = useState<Battle | null>(null);
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
            setBattle(tbattle);
            const entryList = battle?.entries;
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
                        {score.exitCode}
                    </th>

                    <th>
                        {score.timeout}
                    </th>

                    <th>
                        {score.error}
                    </th>

                    <th>
                        {score.passed}
                    </th>

                </tr>
            ))
        )
    }

    function testScoreListModal(testScores: Array<BattleTestResult>) {
        return (
            <dialog id="my_modal_3" className="modal">
                {testScoreList(testScores)}
            </dialog>
        )
    }

    function entryList() {
        return (
            entries?.map((entry, index) => (
                    <tr>
                        <th className="font-bold" style={{alignItems: "center"}}>
                            {index}
                        </th>

                        <th>
                            <div>{entry.status}</div>
                        </th>

                        <th>
                            {entry.score}
                        </th>

                        <th>
                            <div style={{paddingLeft: "1.5%"}}>
                                <p className="badge badge-primary badge-outline"
                                   onClick={() => (document.getElementById('my_modal_3') as HTMLDialogElement).showModal()}>
                                    See test results
                                </p>
                            </div>
                            {testScoreListModal((entry.processResult?.testResults ?? []))}
                        </th>
                        <th>
                            {(entry.processResult?.satResult ?? "").toString()}
                        </th>
                    </tr>
                )
            )
        )
    }

    return (
        <>
            <div style={{alignSelf: "end", top: "8%", width: "100%"}}>
                <div className="overflow-x-auto">
                    <table className="table">
                        {/* head */}
                        <thead>
                        <tr>
                            <th></th>
                            <th>Status</th>
                            <th>Score</th>
                            <th>r</th>
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