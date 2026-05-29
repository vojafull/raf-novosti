import { Link } from 'react-router-dom';


const NewsCard = ({ news }) => {
    const preview = news.content?.length > 200
        ? news.content.substring(0, 200) + '...'
        : news.content;

    const date = news.createdAt
        ? new Date(news.createdAt).toLocaleDateString('sr-RS')
        : '';

    return (
        <div className="card mb-3 shadow-sm h-100">
            <div className="card-body">

                {news.category && (
                    <span className="badge bg-primary mb-2">
                        {news.category.name}
                    </span>
                )}

                <h5 className="card-title">
                    <Link
                        to={`/news/${news.id}`}
                        className="text-decoration-none text-dark"
                    >
                        {news.title}
                    </Link>
                </h5>

                <p className="card-text text-muted">{preview}</p>

                {news.tags && news.tags.length > 0 && (
                    <div className="mb-2">
                        {news.tags.map(tag => (
                            <Link
                                key={tag.id}
                                to={`/tag/${tag.id}`}
                                className="badge bg-secondary me-1 text-decoration-none"
                            >
                                #{tag.name}
                            </Link>
                        ))}
                    </div>
                )}

            </div>
            <div className="card-footer text-muted small d-flex justify-content-between">
                <span>{date}</span>
                {news.author && (
                    <span>{news.author.firstName} {news.author.lastName}</span>
                )}
            </div>
        </div>
    );
};

export default NewsCard;