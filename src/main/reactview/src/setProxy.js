import './App.css';
import WriteForm from './component/writeForm';
import BoardList from './component/BoardList';
import { Routes, Route, Link, useNavigate, Outlet } from 'react-router-dom';
function App() {
  return (
    <div className="App">
      <Routes>
        <Route path="/" Component={WriteForm}>글쓰기</Route>
        <Route path="/boardList" Component={BoardList}></Route>
      </Routes>
    </div>
  );
}

export default App;