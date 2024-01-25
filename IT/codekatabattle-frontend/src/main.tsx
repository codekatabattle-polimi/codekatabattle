import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import './main.css'
import { AuthContext } from "./context/AuthContext.ts";
import { OpenAPI } from "./services/openapi";

OpenAPI.BASE = import.meta.env.VITE_API_URL;

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <AuthContext.Provider value={{
            isLoggedIn: !!localStorage.getItem("token"),
            token: localStorage.getItem("token"),
            user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user") as string) : null,
        }}>
            <App/>
        </AuthContext.Provider>
    </React.StrictMode>,
)
