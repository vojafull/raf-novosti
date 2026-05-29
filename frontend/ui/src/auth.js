const JWT_KEY = 'jwt';
export function saveToken(token) {
    localStorage.setItem(JWT_KEY, token);
}

export function removeToken() {
    localStorage.removeItem(JWT_KEY);
}

export function getToken() {
    return localStorage.getItem(JWT_KEY);
}

function decodePayload() {
    const token = getToken();
    if (!token) return null;
    try {
        const parts = token.split('.');
        if (parts.length !== 3) return null;
        const payload = JSON.parse(atob(parts[1].replace(/-/g, '+').replace(/_/g, '/')));
        return payload;
    } catch {
        return null;
    }
}

export function isAuthenticated() {
    const payload = decodePayload();
    if (!payload || !payload.exp) return false;

    return Date.now() < payload.exp * 1000;
}

export function getCurrentUser() {
    const payload = decodePayload();
    if (!payload) return null;
    return {
        id: parseInt(payload.sub), // sub je userId
        email: payload.email,
        type: payload.type, // 'ADMIN' ili 'CONTENT_CREATOR'
        firstName: payload.firstName,
        lastName: payload.lastName,
    };
}

export function isAdmin() {
    const user = getCurrentUser();
    return user && user.type === 'ADMIN';
}

export function logout(navigate) {
    removeToken();
    navigate('/login');
}