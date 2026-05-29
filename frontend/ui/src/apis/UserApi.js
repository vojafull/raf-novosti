import axiosInstance from '../axiosInstance.js';

export async function getUsers(page = 1, pageSize = 10) {
    const response = await axiosInstance.get('/users', { params: { page, pageSize } });
    return response.data;
}
export async function createUser(data) {
    const response = await axiosInstance.post('/users', data);
    return response.data;
}
export async function updateUser(id, data) {
    const response = await axiosInstance.put(`/users/${id}`, data);
    return response.data;
}
export async function toggleUserStatus(id) {
    const response = await axiosInstance.put(`/users/${id}/toggle-status`);
    return response.data;
}