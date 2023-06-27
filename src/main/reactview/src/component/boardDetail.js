import axios from 'axios';
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Container, Row, Col, Button, Carousel, Stack, Modal } from 'react-bootstrap';
import '../css/boardDetail.css';
import { formatDistanceToNow, parseISO } from 'date-fns';
import { ko } from 'date-fns/locale';

function BoardDetail({ csrfToken }) {
  let [tradeBoard, setTradeBoard] = useState({});
    const [isAuthor, setIsAuthor] = useState(false);

  let [imageList, setImageList] = useState([]);
  let [currentImage, setCurrentImage] = useState(0);
  const [nickName, setNickName] = useState('');
  const [showModal, setShowModal] = useState(false); // Modal 표시 여부 상태
  const { id } = useParams();

const navigate = useNavigate();

   const handleEditClick = () => {
     navigate(`/writeForm/${id}`, { state: { tradeBoard, imageList } });
   };

   const handleDeleteClick = () => {

   }


const fetchData = () => {
  axios
    .get(`/api/boardDetail/${id}`)
    .then((response) => {
      if (response.status === 200) {
        setTradeBoard(response.data.tradeBoard);
        let list = [...response.data.imageList];
        setNickName(response.data.nickName);
        setImageList(list);
        setIsAuthor(response.data.author);
      } else {
        alert("알 수 없는 오류");
        window.history.back(); // 이전 페이지로 이동
      }
    })
    .catch((error) => {
      if (error.response && error.response.data && error.response.data.error) {
        alert(error.response.data.error);
      } else {
        console.log(error);
        alert("error");
      }
      window.history.back(); // 이전 페이지로 이동
    });
};


  useEffect(() => {
    fetchData();
  }, []);

  let timeAgo = '';
  if (tradeBoard.createdDate) {
    timeAgo = formatDistanceToNow(parseISO(tradeBoard.createdDate), { addSuffix: true, locale: ko });
  }

  const handleImageClick = (index) => {
    setCurrentImage(index);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
  };

  const purchasingReq = () => {
      axios
        .post('/api/purchasingReq', tradeBoard.id, {
          headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken,
          },
        })
        .then((response) => {
          window.location.href = response.data;
        })
        .catch((error) => {
             if (error.response && error.response.status === 400) {
                      alert("이미 존재하는 구매 요청입니다.");
                      window.location.href = error.response.data;
             } else {
               // Other errors - Show generic error message in alert
               console.log(error);
               alert('An error occurred.');
             }
           });
    };

  return (
    <Container>
      <Row className="board-top">
        <Col className="overflow-container img-container">
          <Carousel
            variant={'dark'}
            interval={null}
            slide={false}
            activeIndex={currentImage}
            onSelect={(index) => setCurrentImage(index)}
          >
            {imageList.map((img, index) => (
              <Carousel.Item key={img.id}>
                <img
                  src={`https://storage.cloud.google.com/reboot-minty-storage/${img.imgUrl}`}
                  alt="Board Image"
                  className="board-img"
                  onClick={() => handleImageClick(index)} // 이미지 클릭 이벤트 처리
                />
              </Carousel.Item>
            ))}
          </Carousel>
        </Col>
        <Col>
          <Stack gap={3}>
            <h2>{tradeBoard.title}</h2>
            <h2>{Number(tradeBoard.price).toLocaleString()} 원</h2>
            <h2>{nickName}</h2>
          </Stack>
          <Col className="board-stats">
            <span>🤍 {tradeBoard.interesting}</span>
            <span>👁‍ {tradeBoard.visit_count}</span>
            <span>{timeAgo}</span>
          </Col>
          <Col className="button-groups">
            {!isAuthor && <Button variant="primary">찜하기</Button>}
            {!isAuthor &&<Button variant="secondary">채팅</Button>}
            {!isAuthor &&<Button variant="success" onClick={purchasingReq}>
              구매 신청
            </Button>}
          </Col>
        </Col>
      </Row>
      <br />
      <br />
      <Row className="board-content">
        <Col>{tradeBoard.content}</Col>
      </Row>
      <br/><br/>
      <Row>
        <Col md={12}></Col>
       <Col md={3}>
       <div>
       <Row className="justify-content-end">
        <Col>
            {isAuthor && <Button variant="primary" onClick={handleEditClick} style={{gap:"3"}}>수정</Button>}
            {isAuthor && <Button variant="primary" onClick={handleDeleteClick}>삭제</Button>}
        </Col>
       </Row>
       </div>
       </Col>
      </Row>

      <Modal show={showModal} onHide={handleCloseModal} centered>
        <Modal.Body className="modal-body">
          <img
            src={`https://storage.cloud.google.com/reboot-minty-storage/${imageList[currentImage]?.imgUrl}`}
            alt="Board Image"
            className="modal-img"
          />
        </Modal.Body>
      </Modal>
    </Container>
  );
}

export default BoardDetail;