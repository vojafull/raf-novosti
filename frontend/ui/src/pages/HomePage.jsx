import { useState, useEffect } from 'react';
import PublicNavbar from '../components/PublicNavbar.jsx';
import NewsCard from '../components/NewsCard.jsx';
import MostReactedSidebar from '../components/MostReactedSidebar.jsx';
import {getAllNews, getLatestNews} from '../apis/NewsApi.js';

const HomePage = () => {
    const [news, setNews] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        setLoading(true);
        getLatestNews()
            .then(data => {
                setNews(data || []);
            })
            .catch(() => setError('Greška pri ucitavanju vesti.'))
            .finally(() => setLoading(false));
    }, []);

    return (
        <>
            <PublicNavbar />
            <div className="container mt-4">
                <div className="row">
                    <div className="col-lg-9">
                        <h2 className="mb-4 border-bottom pb-2">Najnovije vesti</h2>
                        {loading && <div className="text-center py-5"><div className="spinner-border text-primary" role="status" /></div>}
                        {error && <div className="alert alert-danger">{error}</div>}
                        {!loading && !error && news.length === 0 && <p className="text-muted">Nema vesti za prikaz.</p>}
                        <div className="row row-cols-1 row-cols-md-2 g-4">
                            {news.map(item => (
                                <div key={item.id} className="col"><NewsCard news={item} /></div>
                            ))}
                        </div>
                    </div>
                    <div className="col-lg-3"><MostReactedSidebar /></div>
                </div>
            </div>
        </>
    );
};

export default HomePage;