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
        path: "/tournaments/view/:page",
        element: <VisualizeTournaments/>,

    },

]);

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <AppWrapper>
            <RouterProvider router={router}/>
        </AppWrapper>
    </React.StrictMode>,
)
