import { useState, useEffect } from 'react';
import { useNavigate, useParams, Navigate } from 'react-router-dom';
import CmsNavbar from '../../components/CmsNavbar.jsx';
import { createUser, updateUser } from '../../apis/UserApi.js';
import { getUsers } from '../../apis/UserApi.js';
import { isAdmin } from '../../auth.js';
import axiosInstance from '../../axiosInstance.js';

const UserFormPage = () => {
    if (!isAdmin()) return <Navigate to="/cms/categories" replace />;

    const { id } = useParams();
    const navigate = useNavigate();
    const isEdit = Boolean(id);

    const [form, setForm] = useState({
        firstName: '',
        lastName: '',
        email: '',
        type: 'CONTENT_CREATOR',
        password: '',
        confirmPassword: '',
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!isEdit) return;
        axiosInstance.get(`/users?page=1&pageSize=100`)
            .then(res => {
                const allUsers = res.data.users || res.data.items || [];
                const user = allUsers.find(u => u.id === parseInt(id));
                if (user) {
                    setForm(p => ({
                        ...p,
                        firstName: user.firstName,
                        lastName: user.lastName,
                        email: user.email,
                        type: user.type,
                    }));
                }
            })
            .catch(() => setError('Korisnik nije pronadjen.'));
    }, [id, isEdit]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (!isEdit && form.password !== form.confirmPassword) {
            setError('Lozinke se ne poklapaju.');
            return;
        }

        if (!form.firstName.trim() || !form.lastName.trim() || !form.email.trim()) {
            setError('Ime, prezime i email su obavezni.');
            return;
        }

        setLoading(true);
        try {
            if (isEdit) {
                await updateUser(id, {
                    firstName: form.firstName,
                    lastName: form.lastName,
                    email: form.email,
                    type: form.type,
                });
            } else {
                await createUser(form);
            }
            navigate('/cms/users');
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
            <div className="container mt-4" style={{ maxWidth: '550px' }}>
                <h3 className="mb-4">
                    {isEdit ? 'Izmeni korisnika' : 'Dodaj korisnika'}
                </h3>

                {error && <div className="alert alert-danger">{error}</div>}

                <form onSubmit={handleSubmit}>
                    <div className="row mb-3">
                        <div className="col">
                            <label className="form-label">Ime *</label>
                            <input
                                type="text" className="form-control"
                                value={form.firstName}
                                onChange={e => setForm(p => ({ ...p, firstName: e.target.value }))}
                                required
                            />
                        </div>
                        <div className="col">
                            <label className="form-label">Prezime *</label>
                            <input
                                type="text" className="form-control"
                                value={form.lastName}
                                onChange={e => setForm(p => ({ ...p, lastName: e.target.value }))}
                                required
                            />
                        </div>
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Email *</label>
                        <input
                            type="email" className="form-control"
                            value={form.email}
                            onChange={e => setForm(p => ({ ...p, email: e.target.value }))}
                            required
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Tip korisnika *</label>
                        <select
                            className="form-select"
                            value={form.type}
                            onChange={e => setForm(p => ({ ...p, type: e.target.value }))}
                        >
                            <option value="CONTENT_CREATOR">Urednik sadrzaja</option>
                            <option value="ADMIN">Administrator</option>
                        </select>
                    </div>

                    {!isEdit && (
                        <>
                            <div className="mb-3">
                                <label className="form-label">Lozinka *</label>
                                <input
                                    type="password" className="form-control"
                                    value={form.password}
                                    onChange={e => setForm(p => ({ ...p, password: e.target.value }))}
                                    required
                                />
                            </div>
                            <div className="mb-3">
                                <label className="form-label">Potvrdi lozinku *</label>
                                <input
                                    type="password" className="form-control"
                                    value={form.confirmPassword}
                                    onChange={e => setForm(p => ({ ...p, confirmPassword: e.target.value }))}
                                    required
                                />
                            </div>
                        </>
                    )}

                    <div className="d-flex gap-2">
                        <button type="submit" className="btn btn-primary" disabled={loading}>
                            {loading ? 'Cuvanje...' : (isEdit ? 'Sacuvaj' : 'Dodaj korisnika')}
                        </button>
                        <button type="button" className="btn btn-secondary" onClick={() => navigate('/cms/users')}>
                            Otkazivanje
                        </button>
                    </div>
                </form>
            </div>
        </>
    );
};

export default UserFormPage;