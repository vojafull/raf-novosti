const SESSION_KEY = 'raf_session_id';

function generateUUID() {
    return crypto.randomUUID();
}

export function getSessionId() {
    let sessionId = localStorage.getItem(SESSION_KEY);
    if (!sessionId) {
        sessionId = generateUUID();
        localStorage.setItem(SESSION_KEY, sessionId);
    }
    document.cookie = `sessionId=${sessionId}; path=/; SameSite=Lax`;
    return sessionId;
}

export function initSession() {
    getSessionId();
}