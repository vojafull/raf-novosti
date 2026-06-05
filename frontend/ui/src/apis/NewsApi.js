import axiosInstance from '../axiosInstance.js';
import {getSessionId} from "../session.js";

export async function getLatestNews() {
    const response = await axiosInstance.get('/news/latest');
    return response.data;
}
export async function getMostVisited() {
    const response = await axiosInstance.get('/news/most-visited');
    return response.data;
}
export async function getMostReacted() {
    const response = await axiosInstance.get('/news/most-reacted');
    return response.data;
}
export async function searchNews(query, page = 1, pageSize = 10) {
    const response = await axiosInstance.get('/news/search', { params: { q: query, page, pageSize } });
    return response.data;
}
export async function getNewsByTag(tagId, page = 1, pageSize = 10) {
    const response = await axiosInstance.get(`/news/tag/${tagId}`, { params: { page, pageSize } });
    return response.data;
}
export async function getNewsByCategory(categoryId, page = 1, pageSize = 10) {
    const response = await axiosInstance.get(`/news/category/${categoryId}`, { params: { page, pageSize } });
    return response.data;
}
export async function getNewsById(id) {
    const sessionId = getSessionId();
    const response = await axiosInstance.get(`/news/${id}`, {
        params: { sessionId }
    });
    return response.data;
}
export async function getComments(newsId, page = 1, pageSize = 10) {
    const response = await axiosInstance.get(`/news/${newsId}/comments`, { params: { page, pageSize } });
    return response.data;
}
export async function addComment(newsId, data) {
    const response = await axiosInstance.post(`/news/${newsId}/comments`, data);
    return response.data;
}
export async function reactToNews(newsId, reaction) {
    const response = await axiosInstance.post(`/news/${newsId}/reaction`, { reaction });
    return response.data;
}
export async function reactToComment(commentId, reaction) {
    const response = await axiosInstance.post(`/news/comments/${commentId}/reaction`, { reaction });
    return response.data;
}
export async function getAllNews(page = 1, pageSize = 10) {
    const response = await axiosInstance.get('/news', { params: { page, pageSize } });
    return response.data;
}
export async function createNews(data) {
    const response = await axiosInstance.post('/news', data);
    return response.data;
}
export async function updateNews(id, data) {
    const response = await axiosInstance.put(`/news/${id}`, data);
    return response.data;
}
export async function deleteNews(id) {
    const response = await axiosInstance.delete(`/news/${id}`);
    return response.data;
}