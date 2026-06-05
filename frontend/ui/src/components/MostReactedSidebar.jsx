import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getMostReacted } from '../apis/NewsApi.js';

const MostReactedSidebar = () => {
    const [news, setNews] = useState([]);

    useEffect(() => {
        getMostReacted()
            .then(data => setNews(data))
            .catch(err => console.error('Greška sidebar:', err));
    }, [news.likes, news.dislikes]);

    if (news.length === 0) return null;

    return (
        <div className="card shadow-sm">
            <div className="card-header bg-danger text-white">
                <h6 className="mb-0">Najpopularnije</h6>
            </div>
            <ul className="list-group list-group-flush">
                {news.map(item => (
                    <li key={item.id} className="list-group-item">
                        <Link to={`/news/${item.id}`} className="text-decoration-none text-dark fw-semibold">
                            {item.title}
                        </Link>
                        <div className="small text-muted mt-1">
                            👍 {item.likes || 0} &nbsp; 👎 {item.dislikes || 0}
                        </div>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default MostReactedSidebar;