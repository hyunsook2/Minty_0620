import { Form, Container, Row, Col } from 'react-bootstrap';
import { useEffect, useState, useRef } from "react";
import { useLocation } from 'react-router-dom';
import axios from 'axios';
import CommonForm from './commonForm';
import TradeForm from './tradeForm';
import JobForm from './jobForm';
import '../css/writeForm.css';

function WriteForm(props) {
    const [targetCategory, setTargetCategory] = useState('tradeBoard');
    const [subCategory, setSubCategory] = useState('sell');
    const [tradeTopCate, setTradeTopCate] = useState([]);
    const [tradeSubCate, setTradeSubCate] = useState([]);
    const [selectedTopCateId, setSelectedTopCateId] = useState(null);
    const [selectedSubCateId, setSelectedSubCateId] = useState(null);
    const [boardType, setBoardType] = useState(0);
    const [csrfToken, setCsrfToken] = useState('');
    const [tradeBoard, setTradeBoard] = useState(null);
    const [imageList, setImageList] = useState([]);

       const location = useLocation();
        const state = location.state;
    useEffect(() => {
        if (location.state) {
               setTradeBoard(location.state.tradeBoard);
              setSelectedTopCateId(location.state.tradeBoard.topCategory.id);
                  setSelectedSubCateId(location.state.tradeBoard.subCategory.id);
                  setImageList(location.state.imageList);

        }
    }, [location.state]);



    const fetchData = () => {
        axios.get(`/api/writeForm`).then((response)=>{
            let top = [...response.data.top];
            let sub = [...response.data.sub];
            setCsrfToken(response.data.csrfToken);
            setTradeTopCate(top);
            setTradeSubCate(sub);
        })
        .catch((error) => {
                        console.error('Error fetching data:', error);
        });
    };

    function TradeOption() {
        return (
            <>
                <option value="sell">팝니다</option>
                <option value="buy">삽니다</option>
                <option value="emergencyJob">급해요</option>
            </>
        );
    }

    function CommonOption() {
        return (
            <>
                <option value="common">일반게시판</option>
                <option value="notice">공지</option>
                <option value="advertise">광고  </option>
            </>
        );
    }

    useEffect(() => {
            // Update the boardType value whenever subCategory changes
            if (subCategory === "sell") {
                setBoardType(0);
            } else if (subCategory === "buy") {
                setBoardType(1);
            }
        }, [subCategory]);

    useEffect(() => {
        fetchData();
        if (targetCategory === 'tradeBoard') {
            setSubCategory('sell');
            setSelectedTopCateId(1);
        }
        else { setSubCategory('common'); }
    }, [targetCategory], [subCategory]);

    function TradeCategoryContainer({
      targetCategory,
      subCategory,
      tradeTopCate,
      tradeSubCate,
      selectedTopCateId,
      selectedSubCateId,
      setSelectedTopCateId,
      setSelectedSubCateId
    }) {
      return (

     <Row className={`justify-content-center trade-category-container ${targetCategory !== 'tradeBoard' || (subCategory !== 'sell' && subCategory !== 'buy') ? 'hidden' : ''}`}>
          <br /><br />
          <Col sm={8}>
            <Container>
              <Row>
                <Col sm={6}>
                  {targetCategory === "tradeBoard" && (subCategory === "sell" || subCategory === "buy") &&
                    <ul className="scrollable-list">
                      {tradeTopCate.map((ttc) => (
                        <li
                          key={ttc.id}
                          onClick={() => {
                            setSelectedTopCateId(ttc.id);
                            setSelectedSubCateId(null);
                          }}
                          className={selectedTopCateId === ttc.id ? "active" : ""}
                          style={{ cursor: 'pointer' }}
                        >
                          {ttc.name}
                        </li>
                      ))}
                    </ul>
                  }
                </Col>
                <Col sm={6}>
                  {targetCategory === "tradeBoard" && (subCategory === "sell" || subCategory === "buy") && selectedTopCateId &&
                    <ul className="scrollable-list">
                      {tradeSubCate.filter(tsc => tsc.topCategory.id === selectedTopCateId).map((tsc) => (
                        <li
                          key={tsc.id}
                          style={{ cursor: 'pointer' }}
                          onClick={() => setSelectedSubCateId(tsc.id)}
                          className={selectedSubCateId === tsc.id ? 'active' : ''}
                        >
                          {tsc.name}
                        </li>
                      ))}
                    </ul>
                  }
                </Col>
              </Row>
            </Container>
          </Col>
        </Row>
      );
    }



    return (
        <div className="App">
            <Container>
                <Row className="justify-content-center target-container">
                    <Col sm={4}>
                        <Form.Select
                            className="target-cate"
                            onChange={(e) => {
                                setTargetCategory(e.target.value);
                                setSubCategory(null);
                            }}
                        >
                            <option value="tradeBoard">거래게시판</option>
                            <option value="commonBoard">일반게시판</option>

                        </Form.Select>
                    </Col>
                    <Col sm={4}>
                        <Form.Select
                            style={{ textAlign: 'center' }}
                            onChange={(e) => {
                                setSubCategory(e.target.value);
                            }}
                            value={subCategory} // Set selected value for subCategory
                        >
                            {targetCategory === "tradeBoard" ? <TradeOption /> : null}
                            {targetCategory === "commonBoard" ? <CommonOption /> : null}

                        </Form.Select>
                    </Col>
                    <br /><br />
                </Row>
               <TradeCategoryContainer
                         targetCategory={targetCategory}
                         subCategory={subCategory}
                         tradeTopCate={tradeTopCate}
                         tradeSubCate={tradeSubCate}
                         selectedTopCateId={selectedTopCateId}
                         selectedSubCateId={selectedSubCateId}
                         setSelectedTopCateId={setSelectedTopCateId}
                         setSelectedSubCateId={setSelectedSubCateId}
               />

               {(subCategory === "sell" || subCategory === "buy") && targetCategory === "tradeBoard" && (
                 <TradeForm
                   selectedTopCateId={selectedTopCateId}
                   selectedSubCateId={selectedSubCateId}
                    boardType={boardType}
                    csrfToken={csrfToken}
                    tradeBoard={tradeBoard}
                    imageList={imageList}
                 />
               )}
                {(subCategory === "emergencyJob" && targetCategory === "tradeBoard") && <JobForm />}
                {(subCategory === "common" && targetCategory === "commonBoard") && <CommonForm />}
                <br />
            </Container>
        </div>
    );
}

export default WriteForm;