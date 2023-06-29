import React, { useEffect, useState } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import Pagination from './pagination';
import '../css/jobList.css';
import { Container, Row, Col, Nav, Form } from 'react-bootstrap';
import { formatDistanceToNow, parseISO } from 'date-fns';
import { ko } from 'date-fns/locale';

function JobList() {
  const [jobs, setJobs] = useState([]);
  const [searchBy, setSearchBy] = useState(null);
  const [searchQuery, setSearchQuery] = useState(null);
  const [searchQueryInput, setSearchQueryInput] = useState('');
  const { page: pageParam } = useParams();
  const [totalPages, setTotalPages] = useState(0);
  const [currentPage, setCurrentPage] = useState(pageParam ? Number(pageParam) : 1);

  const navigate = useNavigate();

  const setCurrentPageAndNavigate = (pageNumber) => {
    setCurrentPage(pageNumber);

    const searchBy = document.getElementById("searchBy").value;
    const url = searchQuery
      ? `/api/jobList/searchQuery/${searchBy}/${searchQuery}/${pageNumber}`
      : `/api/jobList/${pageNumber}`;

    navigate(`/jobList/${pageNumber}`);
    fetchJobList(url);
  };


  useEffect(() => {
    fetchJobList();
  }, [searchQuery, currentPage]);

  const fetchJobList = () => {
    const url = searchQuery ? `/api/jobList/searchQuery/${searchBy}/${searchQuery}/${currentPage}` : `/api/jobList/${currentPage}`;

    axios
      .get(url)
      .then((response) => {
        const data = response.data;
        setJobs(data.jobPage.content);
        setTotalPages(data.totalPages);
      })
      .catch((error) => {
        console.error('Error fetching job list:', error);
      });
  };

  const handleSearch = (e) => {
    e.preventDefault();
    setCurrentPage(1);

    const searchBy = e.target.elements.searchBy.value;
    const searchQuery = e.target.elements.searchQuery.value;

    setSearchBy(searchBy);
    setSearchQuery(searchQuery);
    setSearchQueryInput(searchQuery); // Update the separate state variable

    const url = searchQuery
      ? `/api/jobList/searchQuery/${searchBy}/${searchQuery}/${currentPage}`
      : `/api/jobList/${currentPage}`;

    fetchJobList(url);
  };




  return (
    <Container fluid>
      <Row>
        <form onSubmit={handleSearch}>
          <Row className="justify-content-end">
            <Col md={2}>
              <Form.Select name="searchBy">
                <option name="searchBy" value="title">제목</option>
                <option name="searchBy" value="payTotal">시급</option>
                <option name="searchBy" value="jobLocation">지역</option>
              </Form.Select>
            </Col>
            <Col md={4}>
              <input type="text" name="searchQuery" value={searchQueryInput} onChange={(e) => setSearchQueryInput(e.target.value)} />
              <button type="submit">Search</button>
            </Col>
          </Row>
        </form>
      </Row>

      <Row className="justify-content-center">
        <div className="job-list-container">
          {jobs.map((job) => {
            let timeAgo = formatDistanceToNow(parseISO(job.createdDate), { addSuffix: true, locale: ko });
            return (
              <Col md={10} key={job.id} className="job-list">
                <Nav.Link href={`/jobDetail/${job.id}`}>
                  <div className="job-image">
                    <img
                      src={`https://storage.cloud.google.com/reboot-minty-storage/${job.thumbnail}`}
                      alt={job.title}
                      className="job-image"
                      effect="opacity"
                    />
                  </div>
                  <div className="job-tt">{job.title}</div>
                  <div className="job-content">{job.content}</div>
                  <div className="job-location">{job.jobLocation}</div>
                  <div className="job-pay">{job.payTotal.toLocaleString()} 원</div>
                  <div className="job-createdDate">{timeAgo}</div>
                </Nav.Link>
              </Col>
            );
          })}
        </div>
      </Row>

      <Row className="justify-content-center">
        <div className="pagination-container">
          <Pagination totalPages={totalPages} currentPage={currentPage} setCurrentPage={setCurrentPageAndNavigate} />
        </div>
      </Row>
    </Container>
  );
}

export default JobList;