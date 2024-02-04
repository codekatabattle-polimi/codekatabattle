import React from 'react';
import ReactDOM from 'react-dom/client';
import { OpenAPI } from "./services/openapi";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import { AppWrapper } from "./AppWrapper.tsx";

//import { RootRoute } from './routes/RootRoute.tsx';
import { HomePage } from './routes/HomePage.tsx';

import './main.css';
import CreateTournament from "./components/CreateTournament.tsx";
import {VisualizeTournament} from "./components/VisualizeTournament.tsx";
import {VisualizeTournaments} from "./components/VisualizeTournaments.tsx";
import CreateBattle from "./components/CreateBattle.tsx";
import {VisualizeBattle} from "./components/VisualizeBattle.tsx";
import {VisualizeCreatedTournaments} from "./components/VisualizeCreatedTournaments.tsx";
import {VisualizeJoinedTournaments} from "./components/VisualizeJoinedTournaments.tsx";
import {VisualizeCoordinatedTournaments} from "./components/VisualizeCoordinatedTournaments.tsx";
import {ProfilePage} from "./components/ProfilePage.tsx";
import {BattleEntries} from "./components/BattleEntry.tsx";
import {PerformeOME} from "./components/PerformeOME.tsx";

OpenAPI.BASE = import.meta.env.VITE_API_URL;

const router = createBrowserRouter([
    {
        path: "/",
        element: <HomePage/>,
    },
    {
        path: "/tournament/create",
        element: <CreateTournament/>,
    },
    {
        path: "/tournaments/:id",
        element: <VisualizeTournament/>,

    },

    {
        path: "/all/tournaments/view/:page",
        element: <VisualizeTournaments/>,

    },
    {
        path: "/created/tournaments/view/:username/:page",
        element: <VisualizeCreatedTournaments/>,

    },
    {
        path: "/joined/tournaments/view/:username/:page",
        element: <VisualizeJoinedTournaments/>,

    },
    {
        path: "/coordinated/tournaments/view/:username/:page",
        element: <VisualizeCoordinatedTournaments/>,

    },


    {
        path: "/tournaments/:tId/battle/create",
        element: <CreateBattle/>,

    },

    {
        path: "/tournaments/:tId/battles/:bId",
        element: <VisualizeBattle/>,

    },

    {
        path: "/tournaments/:tId/battles/:bId/performe/OME",
        element: <PerformeOME/>,

    },

    {
        path: "/profile/:username",
        element: <ProfilePage/>,

    },

    {
        path: "/tournaments/:tId/battles/:bId/:username",
        element: <BattleEntries/>,

    },


]);

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <AppWrapper>
            <RouterProvider router={router}/>
        </AppWrapper>
    </React.StrictMode>,
)
