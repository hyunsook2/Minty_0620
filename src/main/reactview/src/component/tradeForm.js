import { Button, Form, Row, Col, Modal } from 'react-bootstrap';
import DaumPostcode from 'react-daum-postcode';
import { useRef, useState, useEffect } from "react";
import axios from 'axios';
import { BiSearch } from 'react-icons/bi';
import { DndContext, closestCenter, PointerSensor, useSensor, useSensors } from '@dnd-kit/core';
import { arrayMove, SortableContext, sortableKeyboardCoordinates, rectSortingStrategy } from '@dnd-kit/sortable';
import { Draggable, SortablePhoto } from './sortablePhoto';
import '../css/tradeForm.css';

function TradeForm({ selectedTopCateId, selectedSubCateId,  csrfToken, tradeBoard, imageList, addressCode, userLocationList }) {
    const selectFile = useRef(null);
    const [previewImages, setPreviewImages] = useState([]);
    const [error, setError] = useState(null);
    const [activeId, setActiveId] = useState(null);
    const [content, setContent] = useState();
    const [contentLength, setContentLength] = useState(0);
    const [showPostCodeModal, setShowPostCodeModal] = useState(false);
    const [sellArea, setSellArea] = useState('');
    const [searchAddressInput, setSearchAddressInput] = useState('');
    const [searchAddress , setSearchAddress] = useState('');
    const [addressResults, setAddressResults] = useState([]);
    const [showUserLocationModal,setShowUserLocationModal] = useState(false);

    const handleContentChange = (event) => {
      let value = event.target.value;
      if (value.length > 5000) {
        alert("글자수는 5000자까지만 입력할 수 있습니다.");
        value = value.slice(0, 5000);
        setContent(value);
        setContentLength(value.length);
        return;
      }
      setContent(value); // 수정된 부분
      setContentLength(value.length);
    };


    useEffect(() => {
        if (tradeBoard && tradeBoard.content && tradeBoard.sellArea) {
            setSellArea(tradeBoard.sellArea);
            setContent(tradeBoard.content);
            setContentLength(tradeBoard.content.length);
        }
    }, [tradeBoard]);

    useEffect(() => {
        if (imageList && imageList.length > 0) {
            console.log(imageList);
            const newImages = imageList.map((image) => ({
                preview: `https://storage.cloud.google.com/reboot-minty-storage/${image.imgUrl}`,
                file: null
            }));
            setPreviewImages((prevImages) => [...prevImages, ...newImages]);
        }
    }, [imageList]);


    const sensors = useSensors(
        useSensor(PointerSensor)
    );

    function handleDragStart(event) {
        setActiveId(event.active.id);
    }

    function handleDragEnd(event) {
        const { active, over, delta } = event;

        if (active && over && active.id !== over.id && Math.abs(delta.x) > 10) {
            setPreviewImages((prevImages) => {
                const oldIndex = parseInt(active.id);
                const newIndex = parseInt(over.id);
                return arrayMove(prevImages, oldIndex, newIndex);
            });
        }
    }

    const removePreviewImage = (id) => {
        const numericId = parseInt(id, 10);
        setPreviewImages((prevImages) => prevImages.filter((image, index) => index !== numericId));
    };

    const handleFileButtonClick = () => {
        if (selectFile.current) {
            selectFile.current.click();
        }
    };

    const handleFileChange = (event) => {
        const files = event.target.files;
        const selectedImagesCount = files.length;
        const remainingSlots = 10 - previewImages.length;

        if (selectedImagesCount > remainingSlots) {
          alert(`파일은 10장 까지만 첨부 가능합니다.`);
          return;
        }
        if (files && files.length > 0) {
            const imageFiles = Array.from(files).filter(file => file.type.startsWith('image/'));
            const readerPromises = imageFiles.map(file => {
                return new Promise((resolve, reject) => {
                    const reader = new FileReader();
                    reader.onloadend = () => {
                        resolve({ preview: reader.result, file });
                    };
                    reader.onerror = reject;
                    reader.readAsDataURL(file);
                });
            });

            Promise.all(readerPromises)
                .then(results => {
                    setPreviewImages(prevImages => [...prevImages, ...results]);
                })
                .catch(error => {
                    console.error('Error reading files:', error);
                });
        }
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        const form = event.target;
        const formData = new FormData();

        const title = form.elements.title.value;
        const price = form.elements.price.value;
        const content = form.elements.content.value;
        const area = form.elements.sellArea.value;
        formData.append("title", title);
        formData.append("price", price);
        formData.append("content", content);
        formData.append('topCategory', selectedTopCateId);
        formData.append('subCategory', selectedSubCateId);
        formData.append('sellArea', sellArea);


        previewImages.forEach((image, index) => {
            formData.append("fileUpload", image.file);
        });
        const imageUrls = previewImages
            .filter((image) => image.file === null)
            .map((image) => {
                const urlParts = image.preview.split('/');
                const uuid = urlParts[urlParts.length - 1];
                return uuid;
            });
        formData.append('imageUrls', JSON.stringify(imageUrls));

        const url = tradeBoard ? "/tradeUpdate/" + tradeBoard.id : "/tradeWrite";

        axios.post(url, formData, {
            headers: {
                "Content-Type": "multipart/form-data",
                "X-CSRF-TOKEN": csrfToken
            }
        })
            .then(response => {
                const boardId = response.data;
                window.location.href = "/boardDetail/" + boardId;
            })
            .catch(error => {
                if (error.response && error.response.data) {
                    setError(error.response.data);
                } else {
                    setError("오류가 발생했습니다.");
                }
            });
    };

    const setShowUserLocationList = () => {
        setShowUserLocationModal(true);
    }

    const handleUserLocationCloseModal = () => {
        setShowUserLocationModal(false);
    }

    const handlePostCodeCloseModal = () => {
        setShowPostCodeModal(false);
    };

    const setShowPostCode = (e) => {
        e.preventDefault();
        setShowPostCodeModal(true);
    };
    const handleAddressChange = (data) => {
        const sido = data.sido; // 시/도
        const sigungu = data.sigungu; // 시/군/구
        const dong = data.dong; // 동
        const fullAddress = `${sido} ${sigungu} ${dong}`;

        setShowPostCodeModal(false);
      };


    const handleAddressSearch = (e) => {
      e.preventDefault();
      const keyword = e.target.elements.searchAddress.value;
      setSearchAddress(keyword);

      let filteredData;
      if (/\d/.test(keyword)) {
        const regex = new RegExp(`${keyword}`, 'i');
        filteredData = addressCode.filter((item) => {
          return regex.test(`${item.sido} ${item.sigungu} ${item.dong}`);
        });
      } else {

        const pureKeyword = keyword.replace(/\d+/g, '');
        const regex = new RegExp(`${pureKeyword}`, 'i');
        filteredData = addressCode.filter((item) => {
          const pureDong = item.dong.replace(/\d+/g, '');
          return regex.test(`${item.sido} ${item.sigungu} ${pureDong}`);
        });
      }

      const formattedResults = filteredData.map((result) => {
        return `${result.sido} ${result.sigungu} ${result.dong}`;
      });

      setAddressResults(formattedResults);
    };


     const handleCurrentLocationClick = async (e) => {
        e.preventDefault();
       if (navigator.geolocation) {
         navigator.geolocation.getCurrentPosition(
           async (position) => {
             const latitude = position.coords.latitude;
             const longitude = position.coords.longitude;

             const response = await fetch('/api/kakao/location', {
               method: 'POST',
               body: JSON.stringify({ latitude, longitude }),
               headers: {
                 'Content-Type': 'application/json',
                 "X-CSRF-TOKEN": csrfToken,
               },
             });

             if (response.ok) {
               const data = await response.json();
               const administrativeDistrict = data.documents[1].address_name;
               setSellArea(administrativeDistrict);
             } else {
               console.log('서버와의 통신 중 오류가 있습니다.', response.status);
             }
           },
           (error) => {
             console.log('현재 위치를 받는데 실패했습니다.', error);
           }
         );
       } else {
         console.log('현재 위치 인증 서비스는 이 브라우저에서 호환하지 않습니다.');
       }
     };


    return (
        <Row className="justify-content-center trade-container">
            <div className="text-center">
                {error && error.subCategory && <p className="text-danger">{error.subCategory}</p>}
            </div>
            <Col md={12}>
                <span style={{ paddingLeft: '10px', paddingBottom: '10px' }}>상품 이미지</span>
                {error && error.fileUpload && <p className="text-danger">{error.fileUpload}</p>}
            </Col>
            <Col md={12}>
                <br />
                <Form onSubmit={handleSubmit} encType="multipart/form-data">
                    <Form.Control type="hidden" name="topCategory" value={selectedTopCateId} />
                    <Form.Control type="hidden" name="subCategory" value={selectedSubCateId} />

                    <div className="photo-div">
                        <Form.Group>
                            <Row>
                                <Col md={4}>
                                    <Button type="button" onClick={handleFileButtonClick} className="btn btn-secondary photo-btn">
                                        <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" width="180" height="180" className="bi bi-camera" viewBox="-3 0 22 22">
                                            <path d="M15 12a1 1 0 0 1-1 1H2a1 1 0 0 1-1-1V6a1 1 0 0 1 1-1h1.172a3 3 0 0 0 2.12-.879l.83-.828A1 1 0 0 1 6.827 3h2.344a1 1 0 0 1 .707.293l.828.828A3 3 0 0 0 12.828 5H14a1 1 0 0 1 1 1v6zM2 4a2 2 0 0 0-2 2v6a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2h-1.172a2 2 0 0 1-1.414-.586l-.828-.828A2 2 0 0 0 9.172 2H6.828a2 2 0 0 0-1.414.586l-.828.828A2 2 0 0 1 3.172 4H2z"></path>
                                            <path d="M8 11a2.5 2.5 0 1 1 0-5 2.5 2.5 0 0 1 0 5zm0 1a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7zM3 6.5a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0z"></path>
                                            <text x="37%" y="90%" textAnchor="middle" alignmentBaseline="middle" fontSize="5px">{previewImages.length} / 10</text>
                                        </svg>
                                        <input ref={selectFile} type="file" name="fileUpload" accept="image/jpeg, image/jpg , image/png, image/bmp" onChange={handleFileChange} multiple style={{ display: "none" }} />

                                    </Button>
                                </Col>
                                <Col>
                                    <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={handleDragEnd} onDragStart={handleDragStart}>
                                        <SortableContext items={previewImages.map((_, index) => `${index}`)} strategy={rectSortingStrategy}>
                                            <div className="d-flex flex-wrap mt-3">
                                                {previewImages.map((image, index) => (
                                                    image && <Draggable key={index}>
                                                        <SortablePhoto id={index} preview={image.preview} removePreviewImage={removePreviewImage} />
                                                    </Draggable>
                                                ))}
                                            </div>
                                        </SortableContext>
                                    </DndContext>
                                </Col>
                            </Row>
                        </Form.Group>
                        <br />
                    </div>
                    <br /><br />
                    <Form.Group className="mb-3 d-flex" controlId="exampleForm.ControlInput1">
                        <Col md={2}>
                            <Form.Label className="me-2">
                                제목
                            </Form.Label>
                        </Col>
                        <Col md={10}>
                            <Form.Control
                                type="text"
                                name="title"
                                defaultValue={tradeBoard ? tradeBoard.title : ""}
                                isInvalid={error && error.title}
                            />
                        </Col>
                    </Form.Group>
                    {error && error.title && <p className="text-danger">{error.title}</p>}

                    <Form.Group className="mb-3 d-flex" controlId="exampleForm.ControlInput1">
                        <Col md={2}>
                            <Form.Label className="me-2">가격</Form.Label>
                        </Col>
                        <Col md={10}>
                          <Form.Control
                            name="price"
                            type="text"
                            defaultValue={tradeBoard ? parseInt(tradeBoard.price) : ""}
                            isInvalid={error && error.price}
                            onKeyPress={(e) => {
                              const inputValue = e.target.value;
                              if (e.key === "." || (e.key === "e" && inputValue.includes("e"))) {
                                e.preventDefault();
                              }
                            }}
                            onChange={(e) => {
                              const inputValue = e.target.value;
                              if (!/^\d*$/.test(inputValue)) {
                                alert("숫자만 입력할 수 있습니다");
                                e.target.value = inputValue.replace(/\D/g, "");
                              }
                            }}
                          />
                        </Col>
                    </Form.Group>
                    {error && error.price && <p className="text-danger">{error.price}</p>}
                   <Form.Group className="mb-3" controlId="exampleForm.ControlSellArea">
                     <Row>
                       <Col md={2}>
                         <Form.Label className="me-2">
                           거래 희망 지역
                         </Form.Label>
                       </Col>
                       <Col md={10}>
                         <Row>
                           <Col md={6} className="d-flex justify-content-between">
                             <button className="areaButton" onClick={handleCurrentLocationClick}>
                                           현재 위치
                            </button>
                             <button className="areaButton" onClick={setShowUserLocationList}>나의 인증 위치 목록</button>
                             <button className="areaButton" onClick={setShowPostCode}>주소 검색</button>
                           </Col>
                         </Row>
                         <br/>
                         <Row>
                           <Col md={12}>
                             <Form.Control
                               type="text"
                               name="sellArea"
                               value={sellArea}
                               isInvalid={error && error.title}
                               readOnly
                               onChange={(e) => setSellArea(e.target.value)}
                             />
                           </Col>
                         </Row>
                       </Col>
                     </Row>
                   </Form.Group>

                    <Form.Group className="mb-3 d-flex" controlId="exampleForm.ControlTextarea1">
                        <Col md={2}>
                            <Form.Label className="me-2">내용</Form.Label>
                        </Col>
                        <Col md={10}>
                           <Form.Control
                             name="content"
                             as="textarea"
                             rows={10}
                             onChange={handleContentChange}
                             value={content}
                             isInvalid={error && error.content}
                           />
                           <p style={{ textAlign: "right" }}>{contentLength}/5000</p>
                        </Col>
                    </Form.Group>
                    {error && error.content && <p className="text-danger">{error.content}</p>}
                    <Button as="input" type="submit" value="등록" />

                </Form >
                <Modal show={showPostCodeModal} onHide={handlePostCodeCloseModal} backdrop="static" keyboard={false}>
                    <Modal.Header closeButton>
                        <Modal.Title>주소 검색</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <form onSubmit={handleAddressSearch}>
                        <Row className="d-flex">
                            <Col sm={12}>
                               <input type="text" placeholder="시,도 / 시군구 / 동 중 하나를 검색해주세요" className="search-input" name="searchAddress" value={searchAddressInput} onChange={(e) => setSearchAddressInput(e.target.value)} />
                                 <button type="button" onClick={handleAddressSearch}>
                                    <BiSearch />
                                 </button>
                            </Col>
                        </Row>
                        <Row className="addressResults">
                          <ul className="list-group addressResults-list">
                            {addressResults.map((result, index) => (
                              <li key={index} className="list-group-item">
                                <button type="button" className="btn btn-link address-link" onClick={() => { setSellArea(result); handlePostCodeCloseModal(); }}>
                                  {result}
                                </button>
                              </li>
                            ))}
                          </ul>
                        </Row>

                        </form>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={handlePostCodeCloseModal}>
                            닫기
                        </Button>
                    </Modal.Footer>
                </Modal>
                <Modal show={showUserLocationModal} onHide={handleUserLocationCloseModal} backdrop="static" keyboard={false}>
                    <Modal.Header closeButton>
                        <Modal.Title>고객 위치 인증 리스트</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                      <Row className="userLocationList">
                        <ul className="list-group userLocation-list">
                          {userLocationList.map((result, index) => (
                            <li key={index} className="list-group-item">
                              <button
                                type="button"
                                className="btn btn-link address-link"
                                onClick={() => {
                                  setSellArea(result.address);
                                  handleUserLocationCloseModal();
                                }}
                              >
                                {result.address}
                              </button>
                            </li>
                          ))}
                        </ul>
                      </Row>
                    </Modal.Body>

                    <Modal.Footer>
                        <Button variant="secondary" onClick={handleUserLocationCloseModal}>
                            닫기
                        </Button>
                    </Modal.Footer>
                </Modal>

            </Col >
        </Row >
    );
}

export default TradeForm;