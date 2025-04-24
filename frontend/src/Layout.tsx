import { Outlet, NavLink } from "react-router-dom";
import {Flip, ToastContainer} from "react-toastify";

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
            <ToastContainer
                aria-label="ToastContainer"
                position="top-left"
                autoClose={1200}
                hideProgressBar={false}
                newestOnTop={false}
                closeOnClick={true}
                rtl={false}
                draggable
                pauseOnHover
                theme="light"
                transition={Flip}
            />

        </div>
    )
};

export default Layout;