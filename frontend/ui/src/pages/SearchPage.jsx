import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import PublicNavbar from '../components/PublicNavbar.jsx';
import NewsCard from '../components/NewsCard.jsx';
import MostReactedSidebar from '../components/MostReactedSidebar.jsx';
import Pagination from '../components/Pagination.jsx';
import { searchNews } from '../apis/NewsApi.js';


const SearchPage = () => {
    const [searchParams] = useSearchParams();
    const query = searchParams.get('q') || '';

    const [news, setNews] = useState([]);
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const [total, setTotal] = useState(0);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!query) return;
        setLoading(true);
        searchNews(query, page, 10)
            .then(data => {
                setNews(data.items || []);
                setTotalPages(data.totalPages || 1);
                setTotal(data.total || 0);
            })
            .catch(() => {})
            .finally(() => setLoading(false));
    }, [query, page]);

    useEffect(() => { setPage(1); }, [query]);

    return (
        <>
            <PublicNavbar />
            <div className="container mt-4">
                <div className="row">
                    <div className="col-lg-9">
                        <h2 className="mb-1 border-bottom pb-2">
                            🔍 Rezultati pretrage
                        </h2>
                        {query && (
                            <p className="text-muted mb-4">
                                Trazili ste: <strong>"{query}"</strong>
                                {!loading && ` — pronađeno ${total} rezultata`}
                            </p>
                        )}
                        {!query && <p className="text-muted">Unesite pojam za pretragu u navbaru.</p>}

                        {loading ? (
                            <div className="text-center py-5">
                                <div className="spinner-border text-primary" role="status" />
                            </div>
                        ) : news.length === 0 && query ? (
                            <p className="text-muted">Nema rezultata za "{query}".</p>
                        ) : (
                            <>
                                <div className="row row-cols-1 row-cols-md-2 g-4 mb-4">
                                    {news.map(item => (
                                        <div key={item.id} className="col">
                                            <NewsCard news={item} />
                                        </div>
                                    ))}
                                </div>
                                <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
                            </>
                        )}
                    </div>
                    <div className="col-lg-3"><MostReactedSidebar /></div>
                </div>
            </div>
        </>
    );
};

export default SearchPage;