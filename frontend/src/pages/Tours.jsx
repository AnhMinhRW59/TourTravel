import React, { useState, useEffect } from "react";
import CommonSection from "../shared/CommonSection";
import "../styles/tour.css";
import TourCard from "../shared/TourCard";
import SearchBar from "../shared/SearchBar";
import Newsletter from "../shared/Newsletter";
import { Col, Container, Row } from "reactstrap";
import { BASE_URL } from "../utils/config";
import useFetch from './../components/hooks/useFetch'


const Tours = () => {
  const [pageCount, setPageCount] = useState(0);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(9);
  
  const {data:tours, loading,error} = useFetch(`${BASE_URL}/tours/page?page=${page}&pageSize=${pageSize}`);
  const {data:tourCount} = useFetch(`${BASE_URL}/tours/getTourCount`);

  useEffect(() => {
    const pages = Math.ceil(tourCount / pageSize);
    setPageCount(pages);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }, [page,tourCount,tours]);

  return (
    <>
      <CommonSection title={"Tất Cả Các Tour Đang Có "} />
      <section>
        <Container>
          <Row>
            <SearchBar />
          </Row>
        </Container>
      </section>

      <section className="pt-0">
        <Container>

          {loading&&<h4 className="text-center pt-5"> loading.............</h4>}
          {error&&<h4 className="text-center pt-5"> {error}</h4>}
        {tours && Array.isArray(tours) && !loading && !error &&    
        <Row>
          {tours.map((tour) => (
             tour.maxGroupSize > 0 && (
            <Col lg="3" className="mb-4" key={tour.id}>
              <TourCard tour={tour} />
            </Col>
           )
            ))}

          <Col lg="12">
            <div
              className="pagination d-flex align-items-center 
                justify-content-center mt-4 gap-3"
            >
              {[...Array(pageCount).keys()].map((number) => (
                <span
                  key={number}
                  onClick={() => setPage(number)}
                  className={page === number ? "active__page" : ""}
                >
                  {number + 1}
                </span>
              ))}
            </div>
          </Col>
        </Row>
        }
        </Container>
      </section>

      <Newsletter />
    </>
  );
};

export default Tours;
