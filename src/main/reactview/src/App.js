import './App.css';
import React, { useState, useEffect } from "react";
import WriteForm from './component/writeForm';
import BoardList from './component/boardList';
import Header from './component/header';
import BoardDetail from './component/boardDetail';
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
                    <Route path="/boardList/:boardType" element={<BoardList />} />
                    <Route path="/boardList/:boardType/:page" element={<BoardList />} />
                    <Route path="/boardList/:boardType/category/:id" element={<BoardList />} />
                    <Route path="/boardList/:boardType/category/:id/:page" element={<BoardList />} />
                    <Route path="/boardDetail/:id" element={<BoardDetail csrfToken={csrfToken} />} />
                </Routes>
            </div>
        </React.Fragment>
    );
}


export default App;