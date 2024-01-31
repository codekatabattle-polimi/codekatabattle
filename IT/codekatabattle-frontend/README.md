# CodeKataBattle - Frontend

This is the frontend of the MVP of the CodeKataBattle application. It is a Single Page Application (SPA) developed with React + TypeScript with Vite as the bundler.
Please refer to the [ITD](https://github.com/codekatabattle-polimi/OrciuoloVitelloGiallongo/tree/master/DeliveryFolder) for more in-depth specifications regarding the implementation.

## Getting started

### Prerequisites

- [CKB Backend](https://github.com/codekatabattle-polimi/OrciuoloVitelloGiallongo/tree/master/IT/codekatabattle) installed and running on the system.
- If needed, edit the `VITE_API_URL` environment variabile inside the `.env` file to point to the running backend. Default value is http://localhost:8000 and should not be changed unless the backend is running somewhere else.
- [Node.js 20.11.0 LTS](https://nodejs.org) installed on the system (`node` and `npm` commands must work properly).

## Installing and running locally

Position your terminal in the root of the project (which is the same directory where this file is located), then run the following commands:

```shell
npm install
npm run dev
```

The frontend will be available on `http://localhost:5173`, with Hot Module Replacement (HMR, also called "hot reload").

## Building for production

Run the following command:

```shell
npm run build
```

The build output will be located in the `dist/` directory.

## Syncing types with the backend through OpenAPI

Run the following command:

```shell
npm run types:openapi
```

Whenever the backend is updated, in order to generate the `services/openapi` directory thanks to the [OpenAPI](https://www.openapis.org/) standard.
This ensures that all API calls and models are aligned with what's on the current version of the backend.
