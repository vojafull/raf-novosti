import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getCategories } from '../apis/CategoryApi.js';

const PublicNavbar = () => {
    const [categories, setCategories] = useState([]);
    const [searchQuery, setSearchQuery] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        getCategories(1, 100)
            .then(data => setCategories(data.categories || []))
            .catch(err => console.error('Greška pri ucitavanju kategorija:', err));
    }, []);

    const handleSearch = (e) => {
        e.preventDefault();
        if (searchQuery.trim()) {
            navigate(`/search?q=${encodeURIComponent(searchQuery.trim())}`);
            setSearchQuery('');
        }
    };

    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
            <div className="container">
                <Link className="navbar-brand fw-bold" to="/">RAF Novosti</Link>
                <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#publicNav">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse" id="publicNav">
                    <ul className="navbar-nav me-auto mb-2 mb-lg-0">
                        <li className="nav-item"><Link className="nav-link" to="/">Pocetna</Link></li>
                        <li className="nav-item"><Link className="nav-link" to="/most-visited">Najcitanije</Link></li>
                        {categories.length > 0 && (
                            <li className="nav-item dropdown">
                                <a className="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
                                    Kategorije
                                </a>
                                <ul className="dropdown-menu">
                                    {categories.map(cat => (
                                        <li key={cat.id}>
                                            <Link className="dropdown-item" to={`/category/${cat.id}`}>{cat.name}</Link>
                                        </li>
                                    ))}
                                </ul>
                            </li>
                        )}
                    </ul>
                    <form className="d-flex" onSubmit={handleSearch}>
                        <input className="form-control me-2" type="search" placeholder="Pretrazite vesti..."
                               value={searchQuery} onChange={e => setSearchQuery(e.target.value)} />
                        <button className="btn btn-outline-light" type="submit">🔍︎</button>
                    </form>
                    <Link className="btn btn-sm btn-outline-warning ms-3" to="/login">CMS</Link>
                </div>
            </div>
        </nav>
    );
};

export default PublicNavbar;