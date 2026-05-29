import axiosInstance from '../axiosInstance.js';

export async function login(email, password) {
    const response = await axiosInstance.post('/auth/login', { email, password });
    return response.data;
}