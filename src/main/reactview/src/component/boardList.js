import React, { useState, useEffect } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import Pagination from './pagination';
import { Button, Container, Row, Col, Nav, Form } from 'react-bootstrap';
import '../css/boardList.css';
import { formatDistanceToNow, parseISO } from 'date-fns';
import { ko } from 'date-fns/locale';
import { BiSearch } from 'react-icons/bi';
import InfiniteScroll from 'react-infinite-scroll-component';


function BoardList() {
  const [topCategories, setTopCategories] = useState([]);
  const [subCategories, setSubCategories] = useState([]);
  const [loadedBoardIds, setLoadedBoardIds] = useState([]); // 기존에 로드된 게시물의 ID 저장
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [tradeBoards, setTradeBoards] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [searchQueryInput, setSearchQueryInput] = useState('');
  const [subCategoryId, setSubCategoryId] = useState(null);
  const [minPrice, setMinPrice] = useState(null);
  const [maxPrice, setMaxPrice] = useState(null);
  const [minPriceInput, setMinPriceInput] = useState(null);
  const [maxPriceInput, setMaxPriceInput] = useState(null);
  const [sortBy, setSortBy] = useState('');
  const [activeFilters, setActiveFilters] = useState([]);
  const [page, setPage] = useState(pageParam ? Number(pageParam) : 0);
  const [hasMore, setHasMore] = useState(true);
  const { page: pageParam } = useParams();

  const handleSearch = (e) => {
    e.preventDefault();
    const searchQuery = e.target.elements.searchQuery.value;
    setSearchQuery(searchQuery);
    setPage(0);
    if (searchQuery) {
      const existingFilter = activeFilters.find((filter) => filter.type === '검색어');
      if (existingFilter) {
        setActiveFilters((prevFilters) =>
          prevFilters.map((filter) =>
            filter.type === '검색어' ? { type: '검색어', value: searchQuery } : filter
          )
        );
      } else {
        setActiveFilters((prevFilters) => [...prevFilters, { type: '검색어', value: searchQuery }]);
      }
    }

    fetchData();
  };



  const handleSortByChange = (e) => {
    const selectedSortBy = e.target.value;
    setSortBy(selectedSortBy);
    setPage(0);
    if (selectedSortBy) {
      let filterValue;
      switch (selectedSortBy) {
        case 'itemDesc':
          filterValue = '최신순';
          break;
        case 'priceAsc':
          filterValue = '낮은 가격순';
          break;
        case 'priceDesc':
          filterValue = '높은 가격순';
          break;
        default:
          filterValue = '';
          break;
      }

      const existingFilter = activeFilters.find((filter) => filter.type === '정렬 방식');
      if (existingFilter) {
        setActiveFilters((prevFilters) =>
          prevFilters.map((filter) =>
            filter.type === '정렬 방식' ? { type: '정렬 방식', value: filterValue } : filter
          )
        );
      } else {
        setActiveFilters((prevFilters) => [...prevFilters, { type: '정렬 방식', value: filterValue }]);
      }
    }

    fetchData();
  };

  const searchByPrice = (e) => {
    e.preventDefault();
    const minPrice = e.target.elements.minPrice.value;
    const maxPrice = e.target.elements.maxPrice.value;
    setMinPrice(parseInt(minPrice));
    setMaxPrice(parseInt(maxPrice));
    setPage(0);
    const existingMinPriceFilter = activeFilters.find((filter) => filter.type === '최소 가격');
    const existingMaxPriceFilter = activeFilters.find((filter) => filter.type === '최대 가격');

    if (minPrice && existingMinPriceFilter) {
      setActiveFilters((prevFilters) =>
        prevFilters.map((filter) =>
          filter.type === '최소 가격' ? { type: '최소 가격', value: minPrice } : filter
        )
      );
    } else if (minPrice) {
      setActiveFilters((prevFilters) => [...prevFilters, { type: '최소 가격', value: minPrice }]);
    }

    if (maxPrice && existingMaxPriceFilter) {
      setActiveFilters((prevFilters) =>
        prevFilters.map((filter) =>
          filter.type === '최대 가격' ? { type: '최대 가격', value: maxPrice } : filter
        )
      );
    } else if (maxPrice) {
      setActiveFilters((prevFilters) => [...prevFilters, { type: '최대 가격', value: maxPrice }]);
    }

    fetchData();
  };

  useEffect(() => {
    fetchData();
  }, [subCategoryId, searchQuery, minPrice, maxPrice, sortBy]);



  const fetchData = async () => {
    let endpoint = '/api/boardList';

    if (subCategoryId) {
      endpoint += `/category/${subCategoryId}`;
    }
    if (searchQuery) {
      endpoint += `/searchQuery/${searchQuery}`;
    }
    if (minPrice) {
      endpoint += `/minPrice/${minPrice}`;
    }
    if (maxPrice) {
      endpoint += `/maxPrice/${maxPrice}`;
    }
    if (sortBy) {
      endpoint += `/sortBy/${sortBy}`;
    }
    endpoint += `/page/${page}`;

    console.log(endpoint);
    await axios
      .get(endpoint)
      .then((response) => {
        if (page === 0 && endpoint != `/api/boardList/page/0`) {
          // If it's the first page, reset the tradeBoards state
          setTradeBoards(response.data.tradeBoards);
        } else {
          // If it's not the first page, append the new tradeBoards to the existing state
          setTradeBoards((prevBoards) => [...prevBoards, ...response.data.tradeBoards]);
        }
        let top = [...response.data.top];
        let sub = [...response.data.sub];
        setTopCategories(top);
        setSubCategories(sub);

        const nextPage = page + 1; // Calculate the next page
        setPage(nextPage); // Update the page state to the next page
        setHasMore(response.data.hasNext);
      })
      .catch((error) => {
        console.error('Error fetching data:', error);
      });
  };



const fetchDataWithDelay = () => {
  setTimeout(fetchData, 500);
};


  const removeFilter = (filterType) => {
    setActiveFilters((prevFilters) => prevFilters.filter((filter) => filter.type !== filterType));
    switch (filterType) {
      case '검색어':
        setSearchQuery('');
        setPage(0);
        break;
      case '최소 가격':
        setMinPrice(null);
        setPage(0);
        break;
      case '최대 가격':
        setMaxPrice(null);
        setPage(0);
        break;
      case '정렬 방식':
        setSortBy('');
        setPage(0);
        break;
      case '카테고리':
        setSubCategoryId(null);
        setPage(0);
        break;
      default:
        break;
    }

    fetchData();
  };


  const renderTopCategories = topCategories.map((category) => (
    <Nav.Item key={category.id}>
      <Button
        onClick={() => handleTopCategoryClick(category.id)}
        active={category.id === selectedCategory}
        className="category-link"
        style={{ backgroundColor: 'white' }}
        type="button"
      >
        {category.name}
      </Button>
    </Nav.Item>
  ));

  const renderSubCategories = subCategories
    .filter((subcategory) => subcategory.topCategory.id === selectedCategory)
    .map((subcategory) => (
      <Nav.Item key={subcategory.id}>
        <Button
          onClick={() => handleSubCategoryClick(subcategory.id, subcategory.name)}
          className={`sub-category-link ${subCategoryId === subcategory.id ? 'active' : ''}`}
        >
          {subcategory.name}
        </Button>
      </Nav.Item>
    ));
  const handleTopCategoryClick = (categoryId) => {
    setSelectedCategory(categoryId === selectedCategory ? null : categoryId);
    setSubCategoryId(null);
  };

  const handleSubCategoryClick = (subCategoryId, subCategoryName) => {
    setSubCategoryId(subCategoryId);
    setPage(0);
    const existingSubCategoryFilter = activeFilters.find((filter) => filter.type === '카테고리');
    if (existingSubCategoryFilter) {
      setActiveFilters((prevFilters) =>
        prevFilters.map((filter) =>
          filter.type === '카테고리' ? { type: '카테고리', value: subCategoryName } : filter
        )
      );
    } else {
      setActiveFilters((prevFilters) => [...prevFilters, { type: '카테고리', value: subCategoryName }]);
    }
  };


  return (
    <Container fluid>
      <Row className="justify-content-start filtertext">
        <div>
          <p>필터</p>
          {activeFilters.map((filter, index) => (
            <React.Fragment key={filter.type}>
              <div className={`filter ${filter.value.length > 10 ? 'long-text' : ''}`} key={filter.type}>
                <span>
                  {filter.type} : {filter.value}
                </span>
                <button onClick={() => removeFilter(filter.type)}>x</button>
              </div>
              {(index + 1) % 2 === 0 && <br />} {/* 줄바꿈을 위한 br 요소 */}
            </React.Fragment>
          ))}
        </div>
      </Row>

      <Row className="justify-content-end">

        <Col md={3}>
          <Form.Select className="sortBy" onChange={handleSortByChange}>
            <option value="">정렬 방식</option>
            <option value="itemDesc">최신순</option>
            <option value="priceAsc">낮은 가격순</option>
            <option value="priceDesc">높은 가격순</option>
          </Form.Select>
          <form onSubmit={handleSearch}>
              <Row className="d-flex ">
                <Col sm={2}>
                   <input type="text" className="search-input" name="searchQuery" value={searchQueryInput} onChange={(e) => setSearchQueryInput(e.target.value)} />
                     <button type="submit">
                        <BiSearch />
                     </button>
                </Col>
              </Row>
           </form>
         </Col>
      </Row>

      <Row className="justify-content-end">
       <Col xs="auto">
                       <a href="/writeForm" className="ml-auto">
                           <Button className="writebutton">
                               <p className="writebuttontext">글쓰기</p>
                           </Button>
                       </a>
                     </Col>
        <Col md={3}>
         <Form onSubmit={searchByPrice} className="d-flex align-items-center m-filter">
           <Form.Group className="mr-2">
             <Form.Control
               type="number"
               name="minPrice"
               placeholder="최소 가격"
               value={minPriceInput}
               onChange={(e) => setMinPriceInput(e.target.value)}
             />
           </Form.Group>
           <Form.Group className="mr-2">
             <Form.Control
               type="number"
               name="maxPrice"
               placeholder="최대 가격"
               value={maxPriceInput}
               onChange={(e) => setMaxPriceInput(e.target.value)}
             />
           </Form.Group>
           <Button variant="primary" type="submit" className="mr-2 search" >
             검색
           </Button>
         </Form>
        </Col>
      </Row>
      <Row>
        <Col sm={1}>
          <Nav className="flex-column">
            <div>
              <Button href={`/boardList`} className="category-link">
                전체
              </Button>
            </div>
            {renderTopCategories}
          </Nav>
        </Col>
        <Col sm={1} className={selectedCategory ? 'show-subcategory' : 'hide-subcategory'}>
          {selectedCategory && <Nav className="flex-column">{renderSubCategories}</Nav>}
        </Col>

        <Col sm={9} className={selectedCategory ? 'pushed-content' : ''}>
          {tradeBoards ? (

            <InfiniteScroll
              dataLength={tradeBoards.length}
              next={fetchDataWithDelay}
              hasMore={hasMore}
              loader={
                <div className="loader-container">
                  <div className="loader"></div>
                </div>
              }

              scrollableTarget="scrollable-container"
            >
              <div className="sell-boards-container">
                {tradeBoards.map((board) => {
                  let timeAgo = formatDistanceToNow(parseISO(board.createdDate), { addSuffix: true, locale: ko });

                  return (
                    <div key={board.id} className="sell-board">
                      <Nav.Link href={`/boardDetail/${board.id}`}>
                        <div className="sell-board-image">
                          <img
                            src={`https://storage.cloud.google.com/reboot-minty-storage/${board.thumbnail}`}
                            alt={board.title}
                            className="sell-board-image"
                            effect="opacity"
                          />
                        </div>
                        <div className="sell-board-tt">{board.title}</div>
                        <div className="sell-board-p">{board.price.toLocaleString()} 원</div>
                        <Row>
                          <Col>
                            <div className="sell-board-nn">
                              <span>🤍 {board.interesting}</span>
                            </div>
                          </Col>
                          <Col>
                            <div className="sell-board-cd">
                              <span>{timeAgo}</span>
                            </div>
                          </Col>
                        </Row>
                        <div className="sell-board-ul">
                          <span> {board.userLocation.address}</span>
                        </div>
                      </Nav.Link>
                    </div>
                  );
                })}
              </div>
            </InfiniteScroll>
          ) : (
            <div className="no-results-message">게시물이 없습니다.</div>
          )}
        </Col>
      </Row>
    </Container>
  );
}

export default BoardList;
