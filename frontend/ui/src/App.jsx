import { lazy, Suspense } from 'react'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { isAuthenticated } from './auth.js'

const HomePage         = lazy(() => import('./pages/HomePage.jsx'));
const SingleNewPage    = lazy(() => import('./pages/SingleNewsPage.jsx'));
const CategoryPage     = lazy(() => import('./pages/CategoryPage.jsx'));
const MostVisitedPage  = lazy(() => import('./pages/MostVisitedPage.jsx'));
const TagPage          = lazy(() => import('./pages/TagPage.jsx'));
const SearchPage       = lazy(() => import('./pages/SearchPage.jsx'));
const LoginPage        = lazy(() => import('./pages/LoginPage.jsx'));
const CategoriesPage   = lazy(() => import('./pages/cms/CategoriesPage.jsx'));
const CategoryFormPage = lazy(() => import('./pages/cms/CategoryFormPage.jsx'));
const CmsNewsPage      = lazy(() => import('./pages/cms/CmsNewsPage.jsx'));
const NewsFormPage     = lazy(() => import('./pages/cms/NewsFormPage.jsx'));
const UsersPage        = lazy(() => import('./pages/cms/UsersPage.jsx'));
const UserFormPage     = lazy(() => import('./pages/cms/UserFormPage.jsx'));

const PrivateRoute = ({ element }) => {
  return isAuthenticated() ? element : <Navigate to="/login" replace />;
};

const LoadingSpinner = () => (
    <div className="d-flex justify-content-center align-items-center" style={{ height: '100vh' }}>
      <div className="spinner-border text-primary" role="status">
        <span className="visually-hidden">Ucitavanje...</span>
      </div>
    </div>
);

function App() {
  return (
      <BrowserRouter>
        <Suspense fallback={<LoadingSpinner />}>
          <Routes>

            {/* JAVNE  */}
            <Route path="/"              element={<HomePage />} />
            <Route path="/news/:id"      element={<SingleNewPage />} />
            <Route path="/category/:id"  element={<CategoryPage />} />
            <Route path="/most-visited"  element={<MostVisitedPage />} />
            <Route path="/tag/:id"       element={<TagPage />} />
            <Route path="/search"        element={<SearchPage />} />
            <Route path="/login"         element={<LoginPage />} />


            {/* CMS */}

            <Route path="/cms"           element={<PrivateRoute element={<Navigate to="/cms/categories" replace />} />} />
            <Route path="/cms/categories"        element={<PrivateRoute element={<CategoriesPage />} />} />
            <Route path="/cms/categories/new"    element={<PrivateRoute element={<CategoryFormPage />} />} />
            <Route path="/cms/categories/:id/edit" element={<PrivateRoute element={<CategoryFormPage />} />} />
            <Route path="/cms/news"              element={<PrivateRoute element={<CmsNewsPage />} />} />
            <Route path="/cms/news/new"          element={<PrivateRoute element={<NewsFormPage />} />} />
            <Route path="/cms/news/:id/edit"     element={<PrivateRoute element={<NewsFormPage />} />} />
            <Route path="/cms/users"             element={<PrivateRoute element={<UsersPage />} />} />
            <Route path="/cms/users/new"         element={<PrivateRoute element={<UserFormPage />} />} />
            <Route path="/cms/users/:id/edit"    element={<PrivateRoute element={<UserFormPage />} />} />

            {/* 404 */}
            <Route path="*" element={
              <div className="text-center mt-5">
                <h1>404</h1>
                <p>Stranica nije pronadjena.</p>
                <a href="/" className="btn btn-primary">Pocetna</a>
              </div>
            } />

          </Routes>
        </Suspense>
      </BrowserRouter>
  );
}

export default App