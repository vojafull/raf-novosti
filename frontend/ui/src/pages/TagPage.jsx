import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import PublicNavbar from '../components/PublicNavbar.jsx';
import NewsCard from '../components/NewsCard.jsx';
import MostReactedSidebar from '../components/MostReactedSidebar.jsx';
import Pagination from '../components/Pagination.jsx';
import { getNewsByTag } from '../apis/NewsApi.js';


const TagPage = () => {
    const { id } = useParams();
    const [news, setNews] = useState([]);
    const [tagName, setTagName] = useState('');
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        setLoading(true);
        getNewsByTag(id, page, 10)
            .then(data => {
                const items = data.items || [];
                setNews(items);
                setTotalPages(data.totalPages || 1);
                if (items.length > 0 && items[0].tags) {
                    const tag = items[0].tags.find(t => t.id === parseInt(id));
                    if (tag) setTagName(tag.name);
                }
            })
            .catch(() => {})
            .finally(() => setLoading(false));
    }, [id, page]);

    return (
        <>
            <PublicNavbar />
            <div className="container mt-4">
                <div className="row">
                    <div className="col-lg-9">
                        <h2 className="mb-4 border-bottom pb-2">
                            Vesti sa tagom: <span className="text-primary">#{tagName || id}</span>
                        </h2>
                        {loading ? (
                            <div className="text-center py-5">
                                <div className="spinner-border text-primary" role="status" />
                            </div>
                        ) : news.length === 0 ? (
                            <p className="text-muted">Nema vesti sa ovim tagom.</p>
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

export default TagPage;