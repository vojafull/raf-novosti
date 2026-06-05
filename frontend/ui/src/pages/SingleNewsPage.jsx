import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import PublicNavbar from '../components/PublicNavbar.jsx';
import MostReactedSidebar from '../components/MostReactedSidebar.jsx';
import Pagination from '../components/Pagination.jsx';
import {
    getNewsById, addComment, reactToNews, reactToComment, getComments
} from '../apis/NewsApi.js';
import {getSessionId} from "../session.js";

getSessionId();

const NewsDetailPage = () => {
    const { id } = useParams();
    const [news, setNews] = useState(null);
    const [related, setRelated] = useState([]);
    const [userReaction, setUserReaction] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const [comments, setComments] = useState([]);
    const [commentsPage, setCommentsPage] = useState(1);
    const [commentsTotalPages, setCommentsTotalPages] = useState(1);

    const [commentForm, setCommentForm] = useState({ authorName: '', content: '' });
    const [commentError, setCommentError] = useState('');
    const [commentSuccess, setCommentSuccess] = useState('');
    const [commentLoading, setCommentLoading] = useState(false);

    useEffect(() => {
        setLoading(true);
        Promise.all([
            getNewsById(id),
            getComments(id, 1, 10)
        ]).then(([newsData, commentsData]) => {
            setNews(newsData.news);
            setRelated(newsData.related || []);
            setUserReaction(newsData.userReaction || null);
            setComments(commentsData.items || []);
            setCommentsTotalPages(commentsData.totalPages || 1);

            const reactions = {};
            (commentsData.items || []).forEach(c => {
                if (c.userReaction) reactions[c.id] = c.userReaction;
            });
            setCommentReactions(reactions);
        })
            .catch(() => setError('Vest nije pronadjena.'))
            .finally(() => setLoading(false));
    }, [id]);

    useEffect(() => {
        if (commentsPage === 1) return;
        getComments(id, commentsPage, 10)
            .then(data => {
                setComments(data.items || []);
                setCommentsTotalPages(data.totalPages || 1);
            })
            .catch(() => {});
    }, [commentsPage, id]);
    const handleNewsReaction = async (reaction) => {
        try {
            const data = await reactToNews(id, reaction);
            setNews(prev => ({ ...prev, likes: data.likes, dislikes: data.dislikes }));
            setUserReaction(data.userReaction);
        } catch (err) {
            console.error('Greška pri reakciji:', err);
        }
    };

    const handleCommentSubmit = async (e) => {
        e.preventDefault();
        setCommentError('');
        setCommentSuccess('');

        if (!commentForm.authorName.trim() || !commentForm.content.trim()) {
            setCommentError('Ime i tekst komentara su obavezni.');
            return;
        }

        setCommentLoading(true);
        try {
            const data = await addComment(id, commentForm);
            setComments(prev => [data.comment, ...prev]);
            setCommentForm({ authorName: '', content: '' });
            setCommentSuccess('Komentar je uspesno dodat!');
        } catch {
            setCommentError('Greška pri dodavanju komentara.');
        } finally {
            setCommentLoading(false);
        }
    };

    const [commentReactions, setCommentReactions] = useState(() => {
        try {
            const saved = localStorage.getItem(`commentReactions_${id}`);
            return saved ? JSON.parse(saved) : {};
        } catch { return {}; }
    });

    useEffect(() => {
        localStorage.setItem(`commentReactions_${id}`, JSON.stringify(commentReactions));
    }, [commentReactions, id]);

    const handleCommentReaction = async (commentId, reaction) => {
        try {
            const data = await reactToComment(commentId, reaction);
            setComments(prev => prev.map(c =>
                c.id === commentId
                    ? { ...c, likes: data.likes, dislikes: data.dislikes }
                    : c
            ));
            setCommentReactions(prev => ({ ...prev, [commentId]: data.userReaction }));
        } catch (err) {
            console.error('Greška pri reakciji:', err);
        }
    };
    const formatDate = (dateStr) => {
        if (!dateStr) return '';
        return new Date(dateStr).toLocaleString('sr-RS');
    };

    if (loading) return (
        <>
            <PublicNavbar />
            <div className="text-center py-5">
                <div className="spinner-border text-primary" role="status" />
            </div>
        </>
    );

    if (error) return (
        <>
            <PublicNavbar />
            <div className="container mt-4">
                <div className="alert alert-danger">{error}</div>
            </div>
        </>
    );

    return (
        <>
            <PublicNavbar />

            <div className="container mt-4">
                <div className="row">

                    <div className="col-lg-9">

                        {news.category && (
                            <Link
                                to={`/category/${news.category.id}`}
                                className="badge bg-primary text-decoration-none mb-3 d-inline-block"
                            >
                                {news.category.name}
                            </Link>
                        )}

                        <h1 className="mb-2">{news.title}</h1>

                        <div className="text-muted small mb-3 d-flex gap-3 flex-wrap">
                            <span>{formatDate(news.createdAt)}</span>
                            {news.author && (
                                <span>{news.author.firstName} {news.author.lastName}</span>
                            )}
                            <span>{news.visitCount} poseta</span>
                        </div>


                        {news.tags?.length > 0 && (
                            <div className="mb-3">
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

                        <div className="mb-4" style={{ lineHeight: '1.8', whiteSpace: 'pre-wrap' }}>
                            {news.content}
                        </div>

                        <div className="d-flex gap-3 mb-4 p-3 bg-light rounded">
                            <button
                                className={`btn ${userReaction === 'LIKE' ? 'btn-success' : 'btn-outline-success'}`}
                                onClick={() => handleNewsReaction('LIKE')}
                            >
                                👍 {news.likes || 0}
                            </button>
                            <button
                                className={`btn ${userReaction === 'DISLIKE' ? 'btn-danger' : 'btn-outline-danger'}`}
                                onClick={() => handleNewsReaction('DISLIKE')}
                            >
                                👎 {news.dislikes || 0}
                            </button>
                        </div>

                        <hr />

                        <h4 className="mb-3">Ostavi komentar</h4>
                        {commentError && <div className="alert alert-danger">{commentError}</div>}
                        {commentSuccess && <div className="alert alert-success">{commentSuccess}</div>}
                        <form onSubmit={handleCommentSubmit} className="mb-4">
                            <div className="mb-2">
                                <input
                                    type="text"
                                    className="form-control"
                                    placeholder="Vase ime *"
                                    value={commentForm.authorName}
                                    onChange={e => setCommentForm(p => ({ ...p, authorName: e.target.value }))}
                                    required
                                />
                            </div>
                            <div className="mb-2">
                                <textarea
                                    className="form-control"
                                    rows={3}
                                    placeholder="Tekst komentara *"
                                    value={commentForm.content}
                                    onChange={e => setCommentForm(p => ({ ...p, content: e.target.value }))}
                                    required
                                />
                            </div>
                            <button
                                type="submit"
                                className="btn btn-primary"
                                disabled={commentLoading}
                            >
                                {commentLoading ? 'Slanje...' : 'Posalji komentar'}
                            </button>
                        </form>

                        <h4 className="mb-3">Komentari ({comments.length})</h4>
                        {comments.length === 0 && (
                            <p className="text-muted">Nema komentara. Budite prvi!</p>
                        )}
                        {comments.map(comment => (
                            <div key={comment.id} className="card mb-3">
                                <div className="card-body">
                                    <div className="d-flex justify-content-between mb-1">
                                        <strong>{comment.authorName}</strong>
                                        <small className="text-muted">{formatDate(comment.createdAt)}</small>
                                    </div>
                                    <p className="mb-2">{comment.content}</p>
                                    <div className="d-flex gap-2">
                                        <button
                                            className={`btn btn-sm ${commentReactions[comment.id] === 'LIKE' ? 'btn-success' : 'btn-outline-success'}`}
                                            onClick={() => handleCommentReaction(comment.id, 'LIKE')}
                                        >
                                            👍 {comment.likes || 0}
                                        </button>
                                        <button
                                            className={`btn btn-sm ${commentReactions[comment.id] === 'DISLIKE' ? 'btn-danger' : 'btn-outline-danger'}`}
                                            onClick={() => handleCommentReaction(comment.id, 'DISLIKE')}
                                        >
                                            👎 {comment.dislikes || 0}
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))}

                        <Pagination
                            currentPage={commentsPage}
                            totalPages={commentsTotalPages}
                            onPageChange={setCommentsPage}
                        />

                        {related.length > 0 && (
                            <div className="mt-5">
                                <h4 className="mb-3 border-bottom pb-2"> Procitaj jos...</h4>
                                <div className="row row-cols-1 row-cols-md-3 g-3">
                                    {related.map(item => (
                                        <div key={item.id} className="col">
                                            <div className="card h-100">
                                                <div className="card-body">
                                                    <Link
                                                        to={`/news/${item.id}`}
                                                        className="text-decoration-none text-dark fw-semibold"
                                                    >
                                                        {item.title}
                                                    </Link>
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>
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

export default NewsDetailPage;