import { GithubLoginButton } from "../components/GithubLoginButton.tsx";
import imageToAdd from "../assets/ckb-logo.jpg";
export function RootRoute() {


    return (
        <>

            <div id="box2" style={{
                alignItems: 'center',
                justifyContent: 'center'
            }}>
                <center><img src={imageToAdd} className="mask mask-squircle" alt="helo" style={{width: "30%"}}></img></center>
                <center>
                    <div><h1 className="text-3xl font-bold" style={{color: "#000000"}}>Welcome to CodeKataBattle!</h1></div>
                    <h2 className="text-2xl"style={{color: "#696969"}}>Improve your programming skills online</h2></center>
                <center><GithubLoginButton/></center>

            </div>
        </>
    )
}
