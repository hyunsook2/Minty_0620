import { Form, Button, Row, Col } from "react-bootstrap";

function JobForm() {
    return (
        <Row className="justify-content-center">
            <Col md={6}>
                <Form>
                    <h4>급해요 양식</h4>
                    <Form.Group className="mb-3 d-flex" controlId="exampleForm.ControlInput1">
                        <Form.Label className="me-2">제목</Form.Label>
                        <Form.Control type="text" />
                    </Form.Group>
                    <Form.Group className="mb-3 d-flex" controlId="exampleForm.ControlTextarea1">
                        <Form.Label className="me-2">내용</Form.Label>
                        <Form.Control as="textarea" rows={3} />
                    </Form.Group>
                    <Button as="input" type="submit" value="내셈" />
                </Form>
            </Col>
        </Row>
    )
}

export default JobForm;