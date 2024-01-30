import {useContext} from "react";
import {AuthContext} from "../context/AuthContext.ts";
import logout1 from "../assets/logout.png";
import {useNavigate} from "react-router-dom";

export const LogoutButton= () => {
    const { user, setUser, setToken } = useContext(AuthContext);
    const navigate=useNavigate();
    function logout() {
        (document.getElementById('my_modal_2') as HTMLDialogElement).showModal();
        setUser(null);
        localStorage.removeItem("user");

        setToken(null);
        localStorage.removeItem("token");
        navigate("/");
    }
    if(user) {
        return (
            <div role="button" className="btn btn-ghost btn-circle">
                <img src={logout1} onClick={() => logout()} style={{width: "40%"}}>
                </img>
                <dialog id="my_modal_2" className="modal">
                    <span className="loading loading-spinner text-primary"></span>
                </dialog>
            </div>
        )
    }
    return (
        <></>
    )
}