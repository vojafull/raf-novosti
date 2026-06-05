import { useState, useEffect } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import CmsNavbar from '../../components/CmsNavbar.jsx';
import Pagination from '../../components/Pagination.jsx';
import {getAllNews, deleteNews, searchNews, getNewsByCategory} from '../../apis/NewsApi.js';
import { getCurrentUser } from '../../auth.js';

const CmsNewsPage = () => {
    const [searchParams] = useSearchParams();
    const categoryId = searchParams.get('categoryId');
    const categoryName = searchParams.get('categoryName');

    const [news, setNews] = useState([]);
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [successMsg, setSuccessMsg] = useState('');

    const [searchQuery, setSearchQuery] = useState('');
    const [isSearching, setIsSearching] = useState(false);

    const currentUser = getCurrentUser();

    const loadNews = (p = page) => {
        setLoading(true);
        const request = isSearching && searchQuery
            ? searchNews(searchQuery, p, 10)
            : categoryId
                ? getNewsByCategory(categoryId, p, 10)
                : getAllNews(p, 10);

        request
            .then(data => {
                setNews(data.items || []);
                setTotalPages(data.totalPages || 1);
            })
            .catch(() => setError('Greska pri ucitavanju vesti.'))
            .finally(() => setLoading(false));
    };

    useEffect(() => { loadNews(); }, [page, isSearching]);

    const handleSearch = (e) => {
        e.preventDefault();
        setPage(1);
        setIsSearching(true);
        loadNews(1);
    };

    const handleClearSearch = () => {
        setSearchQuery('');
        setIsSearching(false);
        setPage(1);
    };

    const handleDelete = async (id, title) => {
        if (!confirm(`Obrisati vest "${title}"?`)) return;
        try {
            await deleteNews(id);
            setSuccessMsg('Vest je obrisana.');
            setTimeout(() => setSuccessMsg(''), 3000);
            loadNews();
        } catch (err) {
            const msg = err.response?.data?.error || 'Greška pri brisanju.';
            setError(msg);
            setTimeout(() => setError(''), 4000);
        }
    };

    const canEdit = (newsItem) => {
        if (!currentUser) return false;
        if (currentUser.type === 'ADMIN') return true;
        return newsItem.author?.id === currentUser.id;
    };

    const formatDate = (dateStr) => {
        if (!dateStr) return '';
        return new Date(dateStr).toLocaleDateString('sr-RS');
    };

    return (
        <>
            <CmsNavbar />
            <div className="container-fluid mt-4">
                <div className="d-flex justify-content-between align-items-center mb-3">
                    <h3>
                        Vesti
                        {categoryName && (
                            <span className="badge bg-primary ms-2">{categoryName}</span>
                        )}
                    </h3>
                    <Link to="/cms/news/new" className="btn btn-primary">
                        + Dodaj vest
                    </Link>
                </div>

                <form onSubmit={handleSearch} className="d-flex gap-2 mb-3" style={{ maxWidth: '500px' }}>
                    <input
                        type="text"
                        className="form-control"
                        placeholder="Pretraga po naslovu ili sadrzaju..."
                        value={searchQuery}
                        onChange={e => setSearchQuery(e.target.value)}
                    />
                    <button type="submit" className="btn btn-outline-primary">Trazi</button>
                    {isSearching && (
                        <button type="button" className="btn btn-outline-secondary" onClick={handleClearSearch}>
                            Ponisti
                        </button>
                    )}
                </form>

                {error && <div className="alert alert-danger">{error}</div>}
                {successMsg && <div className="alert alert-success">{successMsg}</div>}

                {loading ? (
                    <div className="text-center py-5">
                        <div className="spinner-border text-primary" role="status" />
                    </div>
                ) : (
                    <>
                        <table className="table table-bordered table-hover">
                            <thead className="table-primary">
                            <tr>
                                <th>Naslov</th>
                                <th>Kategorija</th>
                                <th>Autor</th>
                                <th>Datum</th>
                                <th style={{ width: '160px' }}>Akcije</th>
                            </tr>
                            </thead>
                            <tbody>
                            {news.length === 0 ? (
                                <tr>
                                    <td colSpan={5} className="text-center text-muted">
                                        Nema vesti za prikaz.
                                    </td>
                                </tr>
                            ) : (
                                news.map(item => (
                                    <tr key={item.id}>
                                        <td>
                                            <a
                                                href={`/news/${item.id}`}
                                                target="_blank"
                                                rel="noreferrer"
                                                className="text-decoration-none"
                                            >
                                                {item.title}
                                            </a>
                                        </td>
                                        <td>
                                                <span className="badge bg-secondary">
                                                    {item.category?.name}
                                                </span>
                                        </td>
                                        <td>
                                            {item.author?.firstName} {item.author?.lastName}
                                        </td>
                                        <td>{formatDate(item.createdAt)}</td>
                                        <td>
                                            {canEdit(item) ? (
                                                <>
                                                    <Link
                                                        to={`/cms/news/${item.id}/edit`}
                                                        className="btn btn-sm btn-warning me-2"
                                                    >
                                                        Izmeni
                                                    </Link>
                                                    <button
                                                        className="btn btn-sm btn-danger"
                                                        onClick={() => handleDelete(item.id, item.title)}
                                                    >
                                                        Obrisi
                                                    </button>
                                                </>
                                            ) : (
                                                <span className="text-muted small">—</span>
                                            )}
                                        </td>
                                    </tr>
                                ))
                            )}
                            </tbody>
                        </table>

                        <Pagination
                            currentPage={page}
                            totalPages={totalPages}
                            onPageChange={setPage}
                        />
                    </>
                )}
            </div>
        </>
    );
};

export default CmsNewsPage;