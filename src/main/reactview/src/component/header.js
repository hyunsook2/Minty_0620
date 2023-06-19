import React, { useState, useEffect } from 'react';
import '../css/header.css';
import axios from 'axios';
import { Link } from 'react-router-dom';

function Header({ csrfToken }) {

    const [isLoggedIn, setIsLoggedIn] = useState(false);
    useEffect(() => {
        axios.get(`/api/isLoggedIn`).then((response) => {
            setIsLoggedIn(response.data);
        }).catch((e) => {
            console.error(e);
        })
    })

    const handleLoginClick = () => {
        setIsLoggedIn(true);
    };

    const handleLogoutClick = () => {
        axios.post('/logout', null, {
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            }
        })
            .then(response => {
                setIsLoggedIn(false);
            })
            .catch(e => {
                window.alert('로그아웃 처리를 할 수 없습니다');
            })
    };

    return (

        <div className="headerArea">
            <nav className="navbar navbar-dark" style={{ background: 'white' }}>
                <div className="container d-flex justify-content-between">
                    <a href="/" className="navbar-brand" >
                        <img src="/image/logo.PNG" alt="로고 이미지" width="110px" />
                    </a>
                    <div className="navbar-nav me-md-auto d-flex flex-row gap-2">
                        <p></p>
                        <a href="/boardList/0" className="nav-link menu-a">
                            팝니다
                        </a>
                        <p></p>
                        <a href="/boardList/1" className="nav-link menu-a">
                            삽니다
                        </a>
                        <p></p>
                        <a href="/" className="nav-link menu-a">
                            급해요
                        </a>
                        <p></p>
                        <a href="event" className="nav-link menu-a">
                            이벤트
                        </a>
                        <p></p>
                        <a href="#community" className="nav-link menu-a">
                            커뮤니티
                        </a>
                        <p></p>
                        <a href="#quote" className="nav-link menu-a">
                            시세조회
                        </a>
                        <p></p>
                    </div>
                    <div className="navbar-nav d-flex flex-row gap-2">
                        <a href="/usershop" className="nav-link menu-a">
                            개인상점
                        </a>
                        <p></p>
                        <a href="/mypage" className="nav-link menu-a">
                            마이페이지
                        </a>
                        <p></p>
                        <a href="/manager" className="nav-link menu-a">
                            관리자페이지
                        </a>
                        <p></p>
                        {isLoggedIn ? (
                            <>
                                <a href="/logout" className="nav-link menu-a" onClick={handleLogoutClick}>
                                    로그아웃
                                </a>
                            </>
                        ) : (
                            <>
                                <a href="/login" className="nav-link menu-a">
                                    로그인
                                </a>
                                <a href="/join" className="nav-link menu-a">
                                    회원가입
                                </a>
                            </>
                        )}

                        <p></p>
                        <div>
                        </div>
                    </div>
                </div>
            </nav>
            <div className="scroll-bar" style={{ top: '110px', position: 'fixed', height: '5px', width: 0, background: 'black', zIndex: 10 }}></div>

        </div>
    );
}

export default Header;