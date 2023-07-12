import { Form, Container, Row, Col } from 'react-bootstrap';
import { useEffect, useState, useRef } from "react";
import { useLocation } from 'react-router-dom';
import axios from 'axios';
import CommonForm from './commonForm';
import TradeForm from './tradeForm';
import JobForm from './jobForm';
import '../css/writeForm.css';
import AdForm from './adForm';
import NoticeForm from './noticeForm';

function WriteForm(props) {
    const [targetCategory, setTargetCategory] = useState('tradeBoard');
    const [subCategory, setSubCategory] = useState('trade');
    const [tradeTopCate, setTradeTopCate] = useState([]);
    const [tradeSubCate, setTradeSubCate] = useState([]);
    const [selectedTopCateId, setSelectedTopCateId] = useState(null);
    const [selectedSubCateId, setSelectedSubCateId] = useState(null);
    const [csrfToken, setCsrfToken] = useState('');
    const [tradeBoard, setTradeBoard] = useState(null);
    const [imageList, setImageList] = useState([]);
    const [state, setState] = useState({});
    const [addressCode, setAddressCode] = useState([]);
    const [userLocationList, setUserLocationList] = useState([]);
    const [userRole, setUserRole] = useState('');

    const location = useLocation();
    useEffect(() => {
     if (location.state) {
          const temp = location.state.tradeBoard;
          setTargetCategory("tradeBoard");
          setTradeBoard(temp);
          const topTemp = JSON.stringify(temp.topCategory);
          const subTemp = JSON.stringify(temp.subCategory);
          console.log(JSON.parse(topTemp).id);
          setSelectedTopCateId(JSON.parse(topTemp).id);
          setSelectedSubCateId(JSON.parse(subTemp).id);
          setImageList(location.state.imageList);
        }
    }, [location.state]);

    const fetchData = () => {
        axios.get(`/api/writeForm`).then((response) => {
            let top = [...response.data.top];
            let sub = [...response.data.sub];
            let codes = [...response.data.addressCode];
            let locList = [...response.data.userLocationList];
            setCsrfToken(response.data.csrfToken);
            setUserLocationList(locList);
            setTradeTopCate(top);
            setTradeSubCate(sub);
            setAddressCode(codes);
        })
            .catch((error) => {
                console.error('Error fetching data:', error);
            });
    };
    useEffect(() => {
              axios.get(`/api/isLoggedIn`).then((response) => {
                console.log("useEffect?");
                setUserRole(response.data.userRole);
              }).catch((e) => {
                console.error(e);
              });
    }, []);
    function TradeOption() {
        return (
            <>
                <option value="trade">거래 게시판</option>
                <option value="emergencyJob">알바 구인</option>
            </>
        );
    }
    function CommonOption() {
            return (
                <>
                    <option value="common">일반게시판</option>
                    {userRole === 'ADMIN' ? (<option value="notice">공지</option>) : null}
                    <option value="advertise">광고  </option>
                </>
            );
    }
    useEffect(() => {
        fetchData();
        if (targetCategory === 'tradeBoard') {
            setSubCategory('trade');
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
            <Row className={`justify-content-center trade-category-container ${targetCategory !== 'tradeBoard' || (subCategory !== 'trade') ? 'hidden' : ''}`}>
                <br /><br />
                <Col md={12}>
                    <Container>
                        <Row>
                            {targetCategory === 'tradeBoard' && (subCategory === 'trade') && (
                                <Col md={1}>
                                    <span className="category-span">카테고리</span>
                                </Col>
                            )}
                            <Col sm={5}>
                                {targetCategory === "tradeBoard" && (subCategory === "trade") &&
                                    <ul className="scrollable-list">
                                        {tradeTopCate.map((ttc) => (
                                            <li
                                                key={ttc.id}
                                                onClick={() => {
                                                    setSelectedTopCateId(ttc.id);
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
                          <Col sm={5}>
                            {targetCategory === "tradeBoard" && (subCategory === "trade" || subCategory === "buy") &&
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

                {(subCategory === "trade" || subCategory === "buy") && targetCategory === "tradeBoard" && (
                    <TradeForm
                        selectedTopCateId={selectedTopCateId}
                        selectedSubCateId={selectedSubCateId}
                        csrfToken={csrfToken}
                        tradeBoard={tradeBoard}
                        imageList={imageList}
                        addressCode={addressCode}
                        userLocationList={userLocationList}
                    />
                )}
                {(subCategory === "emergencyJob" && targetCategory === "tradeBoard") && <JobForm csrfToken={csrfToken}/>}
                {(subCategory === "common" && targetCategory === "commonBoard") && <CommonForm status="GENERAL" />}
                {(subCategory === "notice" && targetCategory === "commonBoard") && <NoticeForm status="NOTICE" />}
                {(subCategory === "advertise" && targetCategory === "commonBoard") && <AdForm status="AD" />}
                <br />
            </Container>
        </div>
    );
}

export default WriteForm;