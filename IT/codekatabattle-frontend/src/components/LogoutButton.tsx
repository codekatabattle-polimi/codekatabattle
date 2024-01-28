import {useContext} from "react";
import {AuthContext} from "../context/AuthContext.ts";
import logout1 from "../assets/logout.png";

export const LogoutButton= () => {
    const { user, setUser, setToken } = useContext(AuthContext);
    function logout() {
        setUser(null);
        localStorage.removeItem("user");

        setToken(null);
        localStorage.removeItem("token");
    }
    if(user) {
        return (
            <div role="button" className="btn btn-ghost btn-circle">
                <img src={logout1} onClick={() => logout()} style={{width: "40%"}}>
                </img>
            </div>
        )
    }
    return (
        <></>
    )
}