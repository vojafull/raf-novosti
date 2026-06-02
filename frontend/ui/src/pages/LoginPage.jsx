import { useState } from 'react';
import {useNavigate, Navigate, Link} from 'react-router-dom';
import { login } from '../apis/AuthApi.js';
import { saveToken, isAuthenticated } from '../auth.js';

const LoginPage = () => {
    const navigate = useNavigate();
    const [form, setForm] = useState({ email: '', password: '' });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    if (isAuthenticated()) {
        return <Navigate to="/cms/categories" replace />;
    }

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        if (!form.email.trim() || !form.password.trim()) {
            setError('Email i lozinka su obavezni.');
            return;
        }
        setLoading(true);
        try {
            const data = await login(form.email, form.password);
            saveToken(data.jwt);
            navigate('/cms/categories');
        } catch (err) {
            const msg = err.response?.data?.error || 'Pogresni kredencijali. Pokusajte ponovo.';
            setError(msg);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-vh-100 d-flex align-items-center justify-content-center bg-light">
            <div className="card shadow" style={{ width: '400px' }}>
                <div className="card-header bg-primary text-white text-center">
                    <h4 className="mb-0">RAF CMS — Prijava</h4>
                </div>
                <div className="card-body p-4">
                    {error && <div className="alert alert-danger">{error}</div>}
                    <form onSubmit={handleSubmit}>
                        <div className="mb-3">
                            <label className="form-label">Email adresa</label>
                            <input type="email" className="form-control" placeholder="vas@email.com"
                                   value={form.email} onChange={e => setForm(p => ({...p, email: e.target.value}))}
                                   required/>
                        </div>
                        <div className="mb-3">
                            <label className="form-label">Lozinka</label>
                            <input type="password" className="form-control" placeholder="Unesite lozinku"
                                   value={form.password}
                                   onChange={e => setForm(p => ({...p, password: e.target.value}))} required/>
                        </div>
                        <button type="submit" className="btn btn-primary w-100" disabled={loading}>
                            {loading ? 'Prijavljivanje...' : 'Prijavi se'}
                        </button>

                        <div className="text-center mt-2">
                            <Link to="/" className="btn btn-outline-secondary btn-sm"> {"<-- Nazad na početnu"} </Link>
                        </div>
                    </form>
                    <div className="text-center mt-3">
                        <small className="text-muted">Admin: admin@rafnovosti.rs / admin123</small>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default LoginPage;