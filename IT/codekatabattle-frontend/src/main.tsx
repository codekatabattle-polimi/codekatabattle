import React from 'react';
import ReactDOM from 'react-dom/client';
import { OpenAPI } from "./services/openapi";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import { AppWrapper } from "./AppWrapper.tsx";

//import { RootRoute } from './routes/RootRoute.tsx';
import { HomePage } from './routes/HomePage.tsx';

import './main.css';

OpenAPI.BASE = import.meta.env.VITE_API_URL;
document.title = "CodeKataBattle";

const router = createBrowserRouter([
    {
        path: "/",
        element: <HomePage/>,
    },
]);

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <AppWrapper>
            <RouterProvider router={router}/>
        </AppWrapper>
    </React.StrictMode>,
)