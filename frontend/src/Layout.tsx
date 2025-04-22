import { Outlet, NavLink } from "react-router-dom";

const Layout = () => {
    return (
        <div>
            <nav>
                <ul>
                    <li>
                        <NavLink to="/characters">Charaktere</NavLink>
                    </li>
                    <li>
                        <NavLink to="/villages">Orte</NavLink>
                    </li>
                </ul>
            </nav>

            <Outlet />
        </div>
    )
};

export default Layout;