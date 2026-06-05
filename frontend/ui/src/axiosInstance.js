import axios from 'axios';
import { getToken } from './auth.js';

const axiosInstance = axios.create({
    baseURL: '/api',
    timeout: 10000,
    headers: { 'Content-Type': 'application/json' },
    withCredentials: true,
});

axiosInstance.interceptors.request.use(
    (config) => {
        const token = getToken();
        if (token) config.headers.Authorization = `Bearer ${token}`;
        return config;
    },
    (error) => Promise.reject(error)
);

axiosInstance.interceptors.response.use(
    (response) => response,
    (error) => {
        console.error('API greska:', error.response?.status, error.response?.data);
        return Promise.reject(error);
    }
);

export default axiosInstance;