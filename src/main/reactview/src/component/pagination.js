import React, { useState, useEffect } from 'react';
import { Button } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faAngleDoubleLeft, faAngleLeft, faAngleRight, faAngleDoubleRight } from '@fortawesome/free-solid-svg-icons';

function Pagination({ totalPages, currentPage, setCurrentPage }) {
  const pageNumbers = [];

  const BLOCK_SIZE = 10;
  const startBlock = Math.floor((currentPage - 1) / BLOCK_SIZE) * BLOCK_SIZE;
  const endBlock = Math.min(startBlock + BLOCK_SIZE, totalPages);

  for (let i = startBlock; i < endBlock; i++) {
    pageNumbers.push(i + 1);
  }

  const handleClick = (number) => {
    if (number === currentPage) {
      return; // Skip if the clicked page is the current page
    }

    const totalPagesInBlock = Math.floor((totalPages - 1) / BLOCK_SIZE) * BLOCK_SIZE;
    const startPageInBlock = Math.floor((currentPage - 1) / BLOCK_SIZE) * BLOCK_SIZE;

    if (number === startPageInBlock && currentPage !== startPageInBlock + 1) {
      setCurrentPage(startPageInBlock);
    } else if (number === startPageInBlock + BLOCK_SIZE + 1 && currentPage !== startPageInBlock + BLOCK_SIZE) {
      setCurrentPage(startPageInBlock + BLOCK_SIZE + 1);
    } else {
      setCurrentPage(number);
    }
  };

  return (
    <div>
      {currentPage > 1 && <Button className="pageArrowBtn" onClick={() => setCurrentPage(1)}><FontAwesomeIcon icon={faAngleDoubleLeft}/></Button>}
      {currentPage > 9 && <Button className="pageArrowBtn" onClick={() => setCurrentPage(currentPage - 10)}><FontAwesomeIcon icon={faAngleLeft} /></Button>}

      {pageNumbers.map((number) => (
        <Button
          key={number}
          onClick={() => handleClick(number)}
          className={`pageBtn ${currentPage === number ? 'current' : ''}`}
        >
          {number}
        </Button>
      ))}

      {currentPage < totalPages - 9 && (
        <Button className="pageArrowBtn" onClick={() => setCurrentPage(currentPage + 10)}><FontAwesomeIcon icon={faAngleRight} /></Button>
      )}
      {currentPage < totalPages && (
        <Button className="pageArrowBtn" onClick={() => setCurrentPage(totalPages)}><FontAwesomeIcon icon={faAngleDoubleRight} /></Button>
      )}
    </div>
  );
}

export default Pagination;
