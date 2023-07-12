import './App.css';
import React, { useState, useEffect } from "react";
import WriteForm from './component/writeForm';
import BoardList from './component/boardList';
import Header from './component/header';
import BoardDetail from './component/boardDetail';
import JobList from './component/jobList';
import axios from 'axios';
import './css/global.css';
import { Routes, Route, Link, useNavigate, Outlet } from 'react-router-dom';
function App() {
    const [csrfToken, setCsrfToken] = useState('');

    useEffect(() => {
        async function fetchCsrfToken() {
            try {
                const response = await axios.get('/csrf');
                setCsrfToken(response.data);
                axios.defaults.headers.common['X-CSRF-TOKEN'] = response.data;
            } catch (error) {
                console.error('CSRF 토큰을 가져오는 데 실패했습니다:', error);
            }
        }
        fetchCsrfToken();
    }, []);


    return (
        <React.Fragment>
            <Header csrfToken={csrfToken}/>
            <div className="content">
                <Routes>
                    <Route path="/writeForm" element={<WriteForm csrfToken={csrfToken} />} />
                    <Route path="/writeForm/:boardId" element={<WriteForm csrfToken={csrfToken} />} />
                    <Route path="/boardList" element={<BoardList />} />
                    <Route path="/boardList/page/:page" element={<BoardList />} />
                    <Route path="/boardList/category/:id" element={<BoardList />} />
                    <Route path="/boardList/category/:id/page/:page" element={<BoardList />} />
                    <Route path="/boardList/searchQuery/:searchQuery"element={<BoardList />} />
                    <Route path="/boardList/searchQuery/:searchQuery/page/:page"element={<BoardList />} />
                    <Route path="/boardList/minPrice/:minPrice/page/:page"element={<BoardList />} />
                    <Route path="/boardList/maxPrice/:maxPrice/page/:page"element={<BoardList />} />
                    <Route path="/boardList/minPrice/:minPrice/maxPrice/:maxPrice/page/:page"element={<BoardList />} />
                    <Route path="/boardList/category/:subCategoryId/searchQuery/:searchQuery/page/:page" element={<BoardList />} />
                    <Route path="/boardDetail/:id" element={<BoardDetail csrfToken={csrfToken} />} />
                    <Route path="/jobList" element={<JobList />} />
                    <Route path="/jobList/:page" element={<JobList />} />
                    <Route path="/jobList/searchQuery/:searchBy/:searchQuery" element={<JobList />} />
                    <Route path="/jobList/searchQuery/:searchBy/:searchQuery/:page" element={<JobList />} />
                </Routes>
            </div>
        </React.Fragment>
    );
}


export default App;