import {useEffect, useState} from "react";
import {AuthService, GHUser} from "../services/openapi";


export const ImageCreator= ({username}: {username :string}) => {
    const [user,setUser]=useState<GHUser>();

    useEffect(() => {
        fetchUser()
    }, []);
    async function fetchUser(){
            const ghuser=await AuthService.getUserInfo(username);
            setUser(ghuser);
    }

    if(user){
        return(
            <><img src={user.avatar_url}/></>

                )
            }
                return(
                    <>
                        <span className="loading loading-ring loading-lg"></span>
                    </>)
}