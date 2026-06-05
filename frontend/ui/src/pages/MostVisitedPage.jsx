import { useState, useEffect } from 'react';
import PublicNavbar from '../components/PublicNavbar.jsx';
import NewsCard from '../components/NewsCard.jsx';
import MostReactedSidebar from '../components/MostReactedSidebar.jsx';
import { getMostVisited } from '../apis/NewsApi.js';

const MostVisitedPage = () => {
    const [news, setNews] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        getMostVisited()
            .then(data => setNews(data))
            .catch(() => {})
            .finally(() => setLoading(false));
    }, []);

    return (
        <>
            <PublicNavbar />
            <div className="container mt-4">
                <div className="row">
                    <div className="col-lg-9">
                        <h2 className="mb-4 border-bottom pb-2">Najcitanije (poslednjih 30 dana)</h2>
                        {loading ? (
                            <div className="text-center py-5">
                                <div className="spinner-border text-primary" role="status" />
                            </div>
                        ) : news.length === 0 ? (
                            <p className="text-muted">Nema podataka.</p>
                        ) : (
                            <div className="row row-cols-1 row-cols-md-2 g-4">
                                {news.map((item, idx) => (
                                    <div key={item.id} className="col">
                                        <div className="position-relative">
                                            <span className="badge bg-danger position-absolute top-0 end-0 m-2 fs-6 z-1">
                                                #{idx + 1}
                                            </span>
                                            <NewsCard news={item} />
                                        </div>
                                    </div>
                                ))}
                            </div>
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

export default MostVisitedPage;