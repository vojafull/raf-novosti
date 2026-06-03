import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import CmsNavbar from '../../components/CmsNavbar.jsx';
import Pagination from '../../components/Pagination.jsx';
import { getCategories, deleteCategory } from '../../apis/CategoryApi.js';

const CategoriesPage = () => {
    const [categories, setCategories] = useState([]);
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [successMsg, setSuccessMsg] = useState('');

    const loadCategories = (p = page) => {
        setLoading(true);
        getCategories(p, 10)
            .then(data => {
                setCategories(data.categories || []);
                setTotalPages(data.totalPages || 1);
            })
            .catch(() => setError('Greška pri ucitavanju kategorija.'))
            .finally(() => setLoading(false));
    };

    useEffect(() => { loadCategories(); }, [page]);

    const handleDelete = async (id, name) => {
        if (!confirm(`Obrisati kategoriju "${name}"?`)) return;
        try {
            await deleteCategory(id);
            setSuccessMsg(`Kategorija "${name}" obrisana.`);
            setTimeout(() => setSuccessMsg(''), 3000);
            loadCategories();
        } catch (err) {
            const msg = err.response?.data?.error || 'Greška pri brisanju.';
            setError(msg);
            setTimeout(() => setError(''), 4000);
        }
    };

    return (
        <>
            <CmsNavbar />
            <div className="container-fluid mt-4">
                <div className="d-flex justify-content-between align-items-center mb-3">
                    <h3>Kategorije</h3>
                    <Link to="/cms/categories/new" className="btn btn-primary">+ Dodaj kategoriju</Link>
                </div>
                {error && <div className="alert alert-danger">{error}</div>}
                {successMsg && <div className="alert alert-success">{successMsg}</div>}
                {loading ? (
                    <div className="text-center py-5"><div className="spinner-border text-primary" role="status" /></div>
                ) : (
                    <>
                        <table className="table table-bordered table-hover">
                            <thead className="table-primary">
                            <tr><th>Naziv</th><th>Opis</th><th style={{ width: '160px' }}>Akcije</th></tr>
                            </thead>
                            <tbody>
                            {categories.length === 0 ? (
                                <tr><td colSpan={3} className="text-center text-muted">Nema kategorija.</td></tr>
                            ) : categories.map(cat => (
                                <tr key={cat.id}>
                                    <td>
                                        <Link to={`/cms/news?categoryId=${cat.id}&categoryName=${encodeURIComponent(cat.name)}`}
                                              className="text-decoration-none fw-semibold">{cat.name}</Link>
                                    </td>
                                    <td className="text-muted">{cat.description}</td>
                                    <td>
                                        <Link to={`/cms/categories/${cat.id}/edit`} className="btn btn-sm btn-warning me-2">Izmeni</Link>
                                        <button className="btn btn-sm btn-danger" onClick={() => handleDelete(cat.id, cat.name)}>Obrisi</button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                        <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
                    </>
                )}
            </div>
        </>
    );
};

export default CategoriesPage;