import { Button, Form, Row, Col } from 'react-bootstrap';
import { useRef, useState, useEffect } from "react";
import axios from 'axios';
import { DndContext, closestCenter, PointerSensor, useSensor, useSensors } from '@dnd-kit/core';
import { arrayMove, SortableContext, sortableKeyboardCoordinates, rectSortingStrategy } from '@dnd-kit/sortable';
import { Draggable, SortablePhoto } from './sortablePhoto';
import '../css/tradeForm.css';

function TradeForm({ selectedTopCateId, selectedSubCateId, boardType, csrfToken, tradeBoard, imageList }) {
    const selectFile = useRef(null);
    const [previewImages, setPreviewImages] = useState([]);
    const [error, setError] = useState(null);
    const [activeId, setActiveId] = useState(null);

    useEffect(() => {
        console.log(tradeBoard);
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

        formData.append("title", title);
        formData.append("price", price);
        formData.append("content", content);
        formData.append('topCategory', selectedTopCateId);
        formData.append('subCategory', selectedSubCateId);
        formData.append('boardType', boardType);

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

          const url = tradeBoard ? "/tradeUpdate/"+tradeBoard.id : "/tradeWrite";

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

    return (
        <Row className="justify-content-center trade-container">
            <div className="text-center">
                {error && error.subCategory && <p className="text-danger">{error.subCategory}</p>}
            </div>
            <Col md={1}>
                <span>상품 이미지</span>
                {error && error.fileUpload && <p className="text-danger">{error.fileUpload}</p>}
            </Col>
            <Col md={10}>
                <br />
                <Form onSubmit={handleSubmit} encType="multipart/form-data">
                    <Form.Control type="hidden" name="topCategory" value={selectedTopCateId} />
                    <Form.Control type="hidden" name="subCategory" value={selectedSubCateId} />
                    <Form.Control type="hidden" name="boardType" value={boardType} />

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
                     <Form.Label md={2} className="me-2">
                       제목
                     </Form.Label>
                     <Form.Control
                       type="text"
                       name="title"
                       defaultValue={tradeBoard ? tradeBoard.title : ""}
                       isInvalid={error && error.title}
                     />
                   </Form.Group>
                   {error && error.title && <p className="text-danger">{error.title}</p>}

                   <Form.Group className="mb-3 d-flex" controlId="exampleForm.ControlInput1">
                     <Form.Label className="me-2">가격</Form.Label>
                     <Form.Control
                       name="price"
                       type="number"
                       defaultValue={tradeBoard ? tradeBoard.price : ""}
                       isInvalid={error && error.price}
                     />
                   </Form.Group>
                   {error && error.price && <p className="text-danger">{error.price}</p>}

                   <Form.Group className="mb-3 d-flex" controlId="exampleForm.ControlTextarea1">
                     <Form.Label className="me-2">내용</Form.Label>
                     <Form.Control
                       name="content"
                       as="textarea"
                       rows={3}
                       defaultValue={tradeBoard ? tradeBoard.content : ""}
                       isInvalid={error && error.content}
                     />
                   </Form.Group>
                   {error && error.content && <p className="text-danger">{error.content}</p>}
                    <Button as="input" type="submit" value="내셈" />
                </Form>
            </Col>
        </Row>
    );
}

export default TradeForm;