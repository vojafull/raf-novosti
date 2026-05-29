import axiosInstance from '../axiosInstance.js';

export async function getCategories(page = 1, pageSize = 10) {
    const response = await axiosInstance.get('/categories', { params: { page, pageSize } });
    return response.data; //  categories, total, page, pageSize, totalPages
}

export async function getCategoryById(id) {
    const response = await axiosInstance.get(`/categories/${id}`);
    return response.data;
}

export async function createCategory(data) {
    const response = await axiosInstance.post('/categories', data);
    return response.data;
}

export async function updateCategory(id, data) {
    const response = await axiosInstance.put(`/categories/${id}`, data);
    return response.data;
}

export async function deleteCategory(id) {
    const response = await axiosInstance.delete(`/categories/${id}`);
    return response.data;
}