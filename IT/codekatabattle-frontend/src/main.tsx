import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import './main.css'
import { OpenAPI } from "./services/openapi";

OpenAPI.BASE = import.meta.env.VITE_API_URL;
document.title = "CodeKataBattle";

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <App/>
    </React.StrictMode>,
)
