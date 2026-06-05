import { useState, useEffect } from 'react';
import { Link, Navigate } from 'react-router-dom';
import CmsNavbar from '../../components/CmsNavbar.jsx';
import Pagination from '../../components/Pagination.jsx';
import { getUsers, toggleUserStatus } from '../../apis/UserApi.js';
import { isAdmin, getCurrentUser } from '../../auth.js';

const UsersPage = () => {
    if (!isAdmin()) {
        return <Navigate to="/cms/categories" replace />;
    }

    const [users, setUsers] = useState([]);
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [successMsg, setSuccessMsg] = useState('');

    const currentUser = getCurrentUser();

    const loadUsers = (p = page) => {
        setLoading(true);
        getUsers(p, 10)
            .then(data => {
                setUsers(data.users || data.items || []);
                setTotalPages(data.totalPages || 1);
            })
            .catch(() => setError('Greška pri ucitavanju korisnika.'))
            .finally(() => setLoading(false));
    };

    useEffect(() => { loadUsers(); }, [page]);

    const handleToggleStatus = async (userId, currentStatus, name) => {
        const action = currentStatus === 'ACTIVE' ? 'deaktivirati' : 'aktivirati';
        if (!confirm(`Da li zelite da ${action} korisnika "${name}"?`)) return;

        try {
            await toggleUserStatus(userId);
            setSuccessMsg(`Status korisnika je promenjen.`);
            setTimeout(() => setSuccessMsg(''), 3000);
            loadUsers();
        } catch (err) {
            const msg = err.response?.data?.error || 'Greška pri promeni statusa.';
            setError(msg);
            setTimeout(() => setError(''), 4000);
        }
    };

    return (
        <>
            <CmsNavbar />
            <div className="container-fluid mt-4">
                <div className="d-flex justify-content-between align-items-center mb-3">
                    <h3>Korisnici</h3>
                    <Link to="/cms/users/new" className="btn btn-primary">
                        + Dodaj korisnika
                    </Link>
                </div>

                {error && <div className="alert alert-danger">{error}</div>}
                {successMsg && <div className="alert alert-success">{successMsg}</div>}

                {loading ? (
                    <div className="text-center py-5">
                        <div className="spinner-border text-primary" role="status" />
                    </div>
                ) : (
                    <>
                        <table className="table table-bordered table-hover">
                            <thead className="table-primary">
                            <tr>
                                <th>Ime i prezime</th>
                                <th>Email</th>
                                <th>Tip</th>
                                <th>Status</th>
                                <th style={{ width: '200px' }}>Akcije</th>
                            </tr>
                            </thead>
                            <tbody>
                            {users.length === 0 ? (
                                <tr>
                                    <td colSpan={5} className="text-center text-muted">
                                        Nema korisnika.
                                    </td>
                                </tr>
                            ) : (
                                users.map(user => (
                                    <tr key={user.id}>
                                        <td>
                                            {user.firstName} {user.lastName}
                                            {user.id === currentUser?.id && (
                                                <span className="badge bg-info ms-2 small">Vi</span>
                                            )}
                                        </td>
                                        <td>{user.email}</td>
                                        <td>
                                                <span className={`badge ${user.type === 'ADMIN' ? 'bg-danger' : 'bg-secondary'}`}>
                                                    {user.type === 'ADMIN' ? 'Admin' : 'Urednik'}
                                                </span>
                                        </td>
                                        <td>
                                                <span className={`badge ${user.status === 'ACTIVE' ? 'bg-success' : 'bg-warning text-dark'}`}>
                                                    {user.status === 'ACTIVE' ? 'Aktivan' : 'Neaktivan'}
                                                </span>
                                        </td>
                                        <td>
                                            <Link
                                                to={`/cms/users/${user.id}/edit`}
                                                className="btn btn-sm btn-warning me-2"
                                            >
                                                Izmeni
                                            </Link>


                                            {user.type === 'CONTENT_CREATOR' && (
                                                <button
                                                    className={`btn btn-sm ${user.status === 'ACTIVE' ? 'btn-outline-danger' : 'btn-outline-success'}`}
                                                    onClick={() => handleToggleStatus(
                                                        user.id, user.status,
                                                        `${user.firstName} ${user.lastName}`
                                                    )}
                                                >
                                                    {user.status === 'ACTIVE' ? 'Deaktiviraj' : 'Aktiviraj'}
                                                </button>
                                            )}
                                        </td>
                                    </tr>
                                ))
                            )}
                            </tbody>
                        </table>

                        <Pagination
                            currentPage={page}
                            totalPages={totalPages}
                            onPageChange={setPage}
                        />
                    </>
                )}
            </div>
        </>
    );
};

export default UsersPage;