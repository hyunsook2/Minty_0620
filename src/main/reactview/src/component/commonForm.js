import { Form, Container, Row, Col, Button } from 'react-bootstrap';
import { useEffect, useState } from "react";
import axios from 'axios';
import { useLocation } from 'react-router-dom';

function CommonForm() {
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const location = useLocation();

    const handleSubmit = async (e) => {
        e.preventDefault();

        const data = {
            title,
            content,
            status: 'GENERAL'
        };

        try {
            const response = await axios.post("/api/posts", data);
            console.log(response.data);
            if (response.status === 200) {
                console.log('Saved Successfully.');
                const postId = response.data.id;
                window.location.href = `/communityDetail/${postId}`;
            } else {
                console.log('Failed to save post.');
            }
        } catch (error) {
            console.error('An error occurred while saving the post:', error);
        }
    };

    return (
        <Row className="justify-content-center">
            <Col md={6}>
                <Form onSubmit={handleSubmit}>
                    <h4>일반게시판 양식</h4>
                    <Form.Group className="mb-3 d-flex" controlId="exampleForm.ControlInput1">
                        <Form.Label className="me-2">제목</Form.Label>
                        <Form.Control
                            type="text"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                        />
                    </Form.Group>
                    <Form.Group className="mb-3 d-flex" controlId="exampleForm.ControlTextarea1">
                        <Form.Label className="me-2">내용</Form.Label>
                        <Form.Control
                            as="textarea"
                            rows={3}
                            value={content}
                            onChange={(e) => setContent(e.target.value)}
                        />
                    </Form.Group>
                    <Button as="input" type="submit" value="등록" />
                </Form>
            </Col>
        </Row>
    )
}

export default CommonForm;