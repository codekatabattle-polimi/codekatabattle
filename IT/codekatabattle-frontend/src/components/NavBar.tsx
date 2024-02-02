import avatar2 from "../assets/avatar2.png";
import avatar3 from "../assets/avatar3.png";
import {LogoutButton} from "./LogoutButton.tsx";
import {Link} from "react-router-dom";
import dojo from "../assets/dojo.png";

export const NavBar= () => {
        return (

            <div className="navbar bg-base-300">
                <div className="navbar-start">
                    <div className="dropdown">
                        <div className="drawer">
                            <input id="my-drawer" type="checkbox" className="drawer-toggle"/>
                            <div className="drawer-content">
                                <label htmlFor="my-drawer">
                                    <div role="button" className="btn btn-ghost btn-circle">
                                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none"
                                             viewBox="0 0 24 24"
                                             stroke="currentColor">
                                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                                  d="M4 6h16M4 12h16M4 18h7"/>
                                        </svg>
                                    </div>
                                </label>
                            </div>
                            <div className="drawer-side">
                                <label htmlFor="my-drawer" aria-label="close sidebar"
                                       className="drawer-overlay"></label>
                                <ul className="menu p-4 w-80 min-h-full bg-base-200 text-base-content">
                                    <li><Link to={"/tournaments/view/0"}>My Tournaments</Link></li>
                                    <li><Link to="/tournament/create">Create Tournaments</Link></li>
                                    <li><a>My Profile</a></li>

                                </ul>
                            </div>
                        </div>

                    </div>
                </div>
                <div className="navbar-center">
                    <Link to="/"><a className="btn btn-ghost text-xl">
                        <ul className="menu menu-vertical lg:menu-horizontal">
                            <div className="w-8 h-8 rounded-full"
                                 style={{paddingLeft: "0.5%"}}>
                                <img src={dojo}/>
                            </div>
                            <a className="text-xl">CodeKataBattle</a>
                            <div className="w-8 h-8 rounded-full"
                                 style={{paddingLeft: "0.5%"}}>
                                <img src={dojo}/>
                            </div>
                        </ul></a></Link>
                </div>
                <div className="navbar-end">

                    <input type="text" placeholder="Type here" className="input input-bordered w-full max-w-xs"/>

                    <div className="indicator">

                        <div className="drawer drawer-end">
                            <input id="my-drawer-4" type="checkbox" className="drawer-toggle"/>
                            <div className="drawer-content">
                                {/* Page content here */}
                                <label htmlFor="my-drawer-4">
                                    <div role="button" className="btn btn-ghost btn-circle">
                                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none"
                                             viewBox="0 0 24 24"
                                             stroke="currentColor">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                                                  d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/>
                                        </svg>

                                    </div>

                                </label>
                            </div>
                            <div className="drawer-side">
                                <label htmlFor="my-drawer-4" aria-label="close sidebar"
                                       className="drawer-overlay"></label>
                                <ul className="menu p-4 w-fit min-h-full bg-base-300 text-base-content">
                                    {/* Sidebar content here */}
                                    <div className="card card-side bg-base-100 shadow-xl w-fit">
                                        <figure><img src={avatar3} style={{width: "30%"}}
                                                     alt="Movie"/></figure>
                                        <div className="card-body">
                                            <h2 className="card-title">New friend request!</h2>
                                            <p>Click the button to add "User1" in the friends list.</p>
                                            <div className="card-actions justify-end">
                                                <button className="btn btn-primary">Add</button>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="card card-side bg-base-100 shadow-xl w-fit">
                                        <figure><img src={avatar2} style={{width: "30%"}}
                                                     alt="Movie"/></figure>
                                        <div className="card-body">
                                            <h2 className="card-title">New Tournament is created!</h2>
                                            <p>Click the button to in the "CodeKataTournament".</p>
                                            <div className="card-actions justify-end">
                                                <button className="btn btn-primary">Join</button>
                                            </div>
                                        </div>
                                    </div>
                                </ul>
                            </div>
                        </div>
                    </div>
                    <LogoutButton/>
                </div>

            </div>


)

}