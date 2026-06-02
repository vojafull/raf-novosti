import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import PublicNavbar from '../components/PublicNavbar.jsx';
import NewsCard from '../components/NewsCard.jsx';
import MostReactedSidebar from '../components/MostReactedSidebar.jsx';
import Pagination from '../components/Pagination.jsx';
import { getNewsByCategory } from '../apis/NewsApi.js';
import { getCategoryById } from '../apis/CategoryApi.js';

const CategoryPage = () => {
    const { id } = useParams();
    const [news, setNews] = useState([]);
    const [category, setCategory] = useState(null);
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        getCategoryById(id)
            .then(data => setCategory(data.category || data))
            .catch(() => {});
    }, [id]);

    useEffect(() => {
        setLoading(true);
        getNewsByCategory(id, page, 10)
            .then(data => {
                setNews(data.items || []);
                setTotalPages(data.totalPages || 1);
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
                        <h2 className="mb-1 border-bottom pb-2">
                            {category ? category.name : 'Kategorija'}
                        </h2>
                        {category?.description && (
                            <p className="text-muted mb-4">{category.description}</p>
                        )}

                        {loading ? (
                            <div className="text-center py-5">
                                <div className="spinner-border text-primary" role="status" />
                            </div>
                        ) : news.length === 0 ? (
                            <p className="text-muted">Nema vesti u ovoj kategoriji.</p>
                        ) : (
                            <>
                                <div className="row row-cols-1 row-cols-md-2 g-4 mb-4">
                                    {news.map(item => (
                                        <div key={item.id} className="col">
                                            <NewsCard news={item} />
                                        </div>
                                    ))}
                                </div>
                                <Pagination
                                    currentPage={page}
                                    totalPages={totalPages}
                                    onPageChange={setPage}
                                />
                            </>
                        )}
                    </div>
                    <div className="col-lg-3">
                        <MostReactedSidebar />
                    </div>
                </div>
            </div>
        </>
    );
};

export default CategoryPage;