import { Link, useNavigate } from 'react-router-dom';
import { getCurrentUser, logout, isAdmin } from '../auth.js';

const CmsNavbar = () => {
    const navigate = useNavigate();
    const user = getCurrentUser();

    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-primary">
            <div className="container-fluid">
                <Link className="navbar-brand fw-bold" to="/cms/categories">RAF CMS</Link>
                <Link className="navbar-brand fw-bold" to="/">Pocetna</Link>
                <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#cmsNav">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse" id="cmsNav">
                    <ul className="navbar-nav me-auto mb-2 mb-lg-0">
                        <li className="nav-item"><Link className="nav-link" to="/cms/categories">Kategorije</Link></li>
                        <li className="nav-item"><Link className="nav-link" to="/cms/news">Vesti</Link></li>
                        {isAdmin() && (
                            <li className="nav-item"><Link className="nav-link" to="/cms/users">Korisnici</Link></li>
                        )}
                    </ul>
                    <div className="d-flex align-items-center gap-3">
                        {user && (
                            <span className="text-white">
                                {user.firstName} {user.lastName}
                                <span className="badge bg-light text-dark ms-2 small">
                                    {user.type === 'ADMIN' ? 'Admin' : 'Urednik'}
                                </span>
                            </span>
                        )}
                        <button className="btn btn-outline-light btn-sm" onClick={() => logout(navigate)}>
                            Odjava
                        </button>
                    </div>
                </div>
            </div>
        </nav>
    );
};

export default CmsNavbar;