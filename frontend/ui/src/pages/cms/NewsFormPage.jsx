import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import CmsNavbar from '../../components/CmsNavbar.jsx';
import { createNews, updateNews, getNewsById } from '../../apis/NewsApi.js';
import { getCategories } from '../../apis/CategoryApi.js';

const NewsFormPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const isEdit = Boolean(id);

    const [form, setForm] = useState({
        title: '',
        content: '',
        categoryId: '',
        tagsInput: '',
    });
    const [categories, setCategories] = useState([]);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        getCategories(1, 100)
            .then(data => setCategories(data.categories || []))
            .catch(() => {});
    }, []);

    useEffect(() => {
        if (!isEdit) return;
        getNewsById(id)
            .then(data => {
                const n = data.news;
                setForm({
                    title: n.title,
                    content: n.content,
                    categoryId: n.category?.id?.toString() || '',
                    tagsInput: n.tags?.map(t => t.name).join(', ') || '',
                });
            })
            .catch(() => setError('Vest nije pronadjena.'));
    }, [id, isEdit]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (!form.title.trim() || !form.content.trim() || !form.categoryId) {
            setError('Naslov, sadrzaj i kategorija su obavezni.');
            return;
        }

        const tagNames = form.tagsInput
            .split(',')
            .map(t => t.trim())
            .filter(t => t.length > 0);

        const payload = {
            title: form.title,
            content: form.content,
            categoryId: parseInt(form.categoryId),
            tagNames,
        };

        setLoading(true);
        try {
            if (isEdit) {
                await updateNews(id, payload);
            } else {
                await createNews(payload);
            }
            navigate('/cms/news');
        } catch (err) {
            const msg = err.response?.data?.error || 'Greška pri cuvanju.';
            setError(msg);
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            <CmsNavbar />
            <div className="container mt-4" style={{ maxWidth: '800px' }}>
                <h3 className="mb-4">
                    {isEdit ? 'Izmeni vest' : 'Dodaj vest'}
                </h3>

                {error && <div className="alert alert-danger">{error}</div>}

                <form onSubmit={handleSubmit}>
                    <div className="mb-3">
                        <label className="form-label">Naslov *</label>
                        <input
                            type="text"
                            className="form-control"
                            value={form.title}
                            onChange={e => setForm(p => ({ ...p, title: e.target.value }))}
                            required
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Kategorija *</label>
                        <select
                            className="form-select"
                            value={form.categoryId}
                            onChange={e => setForm(p => ({ ...p, categoryId: e.target.value }))}
                            required
                        >
                            <option value="">— Odaberi kategoriju —</option>
                            {categories.map(cat => (
                                <option key={cat.id} value={cat.id}>{cat.name}</option>
                            ))}
                        </select>
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Sadrzaj *</label>
                        <textarea
                            className="form-control"
                            rows={10}
                            value={form.content}
                            onChange={e => setForm(p => ({ ...p, content: e.target.value }))}
                            required
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label">
                            Tagovi
                            <small className="text-muted ms-2">
                                (razdvojeni zarezima, npr: sport, fudbal, srbija)
                            </small>
                        </label>
                        <input
                            type="text"
                            className="form-control"
                            placeholder="sport, fudbal, liga"
                            value={form.tagsInput}
                            onChange={e => setForm(p => ({ ...p, tagsInput: e.target.value }))}
                        />
                    </div>

                    <div className="d-flex gap-2">
                        <button type="submit" className="btn btn-primary" disabled={loading}>
                            {loading ? 'Cuvanje...' : (isEdit ? 'Sacuvaj izmene' : 'Objavi vest')}
                        </button>
                        <button
                            type="button"
                            className="btn btn-secondary"
                            onClick={() => navigate('/cms/news')}
                        >
                            Otkazivanje
                        </button>
                    </div>
                </form>
            </div>
        </>
    );
};

export default NewsFormPage;