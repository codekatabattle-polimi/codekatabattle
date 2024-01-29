import avatar2 from "../assets/avatar1.png"

export const TournamentLeaderboard = () => {
    return (
        <div style={{padding: "1%"}}>
            <div className="collapse collapse-arrow border border-base-300 bg-base-200">
                <input type="checkbox"/>
                <div className="collapse-title text-xl font-medium">
                    Leaderboard
                </div>
                <div className="collapse-content">
                    <div className="overflow-x-auto">
                        <table className="table">
                            {/* head */}
                            <thead>
                            <tr>
                                <th>Position</th>
                                <th>User</th>
                                <th>Score</th>
                            </tr>
                            </thead>
                            <tbody>
                            {/* row 1 */}
                            <tr className="bg-base-300">
                                <th>1</th>
                                <td>
                                    <div className="flex items-center gap-3">
                                        <div className="avatar">
                                            <div className="mask mask-squircle w-12 h-12">
                                                <img src={avatar2}
                                                     alt="Avatar Tailwind CSS Component"/>
                                            </div>
                                        </div>
                                        <div>
                                            <div className="font-bold">Nicks</div>
                                            <div className="text-sm opacity-50">Nicolò Giallongo</div>
                                        </div>
                                    </div>
                                </td>
                                <td>Quality Control Specialist</td>
                                <td>Blue</td>
                            </tr>

                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

    )
}