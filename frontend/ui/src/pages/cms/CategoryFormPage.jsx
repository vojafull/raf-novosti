import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import CmsNavbar from '../../components/CmsNavbar.jsx';
import { createCategory, updateCategory, getCategoryById } from '../../apis/CategoryApi.js';


const CategoryFormPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const isEdit = Boolean(id);

    const [form, setForm] = useState({ name: '', description: '' });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!isEdit) return;
        getCategoryById(id)
            .then(data => {
                const cat = data.category || data;
                setForm({ name: cat.name, description: cat.description });
            })
            .catch(() => setError('Kategorija nije pronadjena.'));
    }, [id, isEdit]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (!form.name.trim() || !form.description.trim()) {
            setError('Naziv i opis su obavezni.');
            return;
        }

        setLoading(true);
        try {
            if (isEdit) {
                await updateCategory(id, form);
            } else {
                await createCategory(form);
            }
            navigate('/cms/categories');
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
            <div className="container mt-4" style={{ maxWidth: '600px' }}>
                <h3 className="mb-4">
                    {isEdit ? 'Izmeni kategoriju' : 'Dodaj kategoriju'}
                </h3>

                {error && <div className="alert alert-danger">{error}</div>}

                <form onSubmit={handleSubmit}>
                    <div className="mb-3">
                        <label className="form-label">Naziv kategorije *</label>
                        <input
                            type="text"
                            className="form-control"
                            value={form.name}
                            onChange={e => setForm(p => ({ ...p, name: e.target.value }))}
                            required
                        />
                    </div>
                    <div className="mb-3">
                        <label className="form-label">Opis *</label>
                        <textarea
                            className="form-control"
                            rows={3}
                            value={form.description}
                            onChange={e => setForm(p => ({ ...p, description: e.target.value }))}
                            required
                        />
                    </div>
                    <div className="d-flex gap-2">
                        <button type="submit" className="btn btn-primary" disabled={loading}>
                            {loading ? 'Cuvanje...' : (isEdit ? 'Sacuvaj izmene' : 'Dodaj kategoriju')}
                        </button>
                        <button
                            type="button"
                            className="btn btn-secondary"
                            onClick={() => navigate('/cms/categories')}
                        >
                            Otkazivanje
                        </button>
                    </div>
                </form>
            </div>
        </>
    );
};

export default CategoryFormPage;