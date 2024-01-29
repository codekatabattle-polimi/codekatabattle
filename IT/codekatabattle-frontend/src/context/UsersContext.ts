import { createContext } from "react";
import { GHUser } from "../services/openapi";

export type TUsersContext = {
    users: Array<GHUser> | null;
    setUsers: (users: Array<GHUser> | null) => void;
};

export const UsersContext = createContext<TUsersContext>({
    users: null,
    setUsers: () => {},
});
