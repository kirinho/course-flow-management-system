import React, { useEffect, useState } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import Pagination from "react-bootstrap/Pagination";
import { ToastContainer, toast, Bounce } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const Courses = () => {
  const [courses, setCourses] = useState([]);
  const [orderBy, setOrderBy] = useState("asc");
  const [searchName, setSearchName] = useState("");
  const [enrolled, setEnrolled] = useState(false);
  const [count, setCount] = useState(20);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const token = localStorage.getItem("token");

  useEffect(() => {
    if (!token) {
      return;
    }

    const fetchCourses = async () => {
      try {
        const response = await axios.get("http://localhost:8080/courses/all", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
          params: {
            orderBy: orderBy,
            name: searchName,
            flag: enrolled,
            page: page - 1,
            size: count,
          },
        });
        setCourses(response.data.courses);
        setTotalPages(response.data.totalPages);
      } catch (error) {
        let errorMessage = "Error fetching courses.";

        if (error.response) {
          switch (error.response.status) {
            case 400:
              errorMessage = "Error: Bad request.";
              break;
            case 401:
              errorMessage = "Error: Unauthorized access.";
              break;
            case 403:
              errorMessage = "Error: Forbidden.";
              break;
            case 404:
              errorMessage = "Error: Courses not found.";
              break;
            default:
              errorMessage = "Error: Something went wrong. Please try again later.";
          }
        } else {
          errorMessage = "No response was received from the server.";
        }
        toast.error(errorMessage, {
            position: "bottom-right",
            autoClose: 4000,
            hideProgressBar: false,
            closeOnClick: false,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
            theme: "light",
            transition: Bounce,
            });
      }
    };

    fetchCourses();
  }, [token, orderBy, searchName, enrolled, count, page]);

  const paginationItems = [];
  for (let number = 1; number <= totalPages; number++) {
    paginationItems.push(
      <Pagination.Item key={number} active={number === page} onClick={() => setPage(number)}>
        {number}
      </Pagination.Item>
    );
  }

  if (!token) {
    return (
        <div className="d-flex align-items-start justify-content-center min-vh-100" style={{ paddingTop: "3rem" }}>
            <h2>You need to be authenticated to view the courses.</h2>;
        </div>)
  }

  return (
    <div className="d-flex align-items-start justify-content-center min-vh-100" style={{ paddingTop: "3rem" }}>
      <div className="container py-5">
        <div className="text-center mb-5">
          <h5 className="text-primary text-uppercase mb-3" style={{ letterSpacing: "5px" }}>
            Courses
          </h5>
        </div>

        <div className="input-group mb-4 shadow-sm border rounded">
          <div className="input-group-prepend">
            <span className="input-group-text bg-primary text-white">
              <i className="fa fa-search"></i>
            </span>
          </div>
          <input
            type="text"
            className="form-control"
            placeholder="Search by name"
            value={searchName}
            onChange={(e) => setSearchName(e.target.value)}
          />
          <span style={{ margin: "0 8px" }}></span>
          <div className="input-group-append">
            <span className="input-group-text">
              <input
                type="checkbox"
                checked={enrolled}
                onChange={(e) => setEnrolled(e.target.checked)}
                style={{ marginRight: "5px" }}
              />
              Enrolled
            </span>
          </div>
          <span style={{ margin: "0 8px" }}></span>
          <div className="input-group-append">
            <select
              className="form-control"
              value={count}
              onChange={(e) => setCount(Number(e.target.value))}
            >
              <option value={1}>1</option>
              <option value={5}>5</option>
              <option value={10}>10</option>
              <option value={20}>20</option>
            </select>
          </div>
          <span style={{ margin: "0 8px" }}></span>
          <div className="input-group-append">
            <select
              className="form-control"
              value={orderBy}
              onChange={(e) => setOrderBy(e.target.value)}
            >
              <option value="asc">Ascending</option>
              <option value="desc">Descending</option>
            </select>
          </div>
        </div>
        
        <div className="row">
          {courses.map((course) => (
            <div key={course.id} className="col-lg-3 col-md-6 mb-4">
              <div className="cat-item position-relative overflow-hidden rounded mb-2">
                {course.imageBase64 ? (
                  <img
                    className="img-fluid"
                    src={`data:image/jpeg;base64,${course.imageBase64}`}
                    alt={course.name}
                    style={{
                      width: "100%",
                      height: "200px",
                      objectFit: "cover",
                      borderRadius: "8px",
                    }}
                  />
                ) : (
                  <div
                    style={{
                      width: "100%",
                      height: "200px",
                      backgroundColor: "#ddd",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      borderRadius: "8px",
                      color: "#777",
                    }}
                  >
                    No Image Available
                  </div>
                )}
                <Link className="cat-overlay text-white text-decoration-none" to={`/courses/${course.id}`}>
                  <h4 className="text-white font-weight-medium">{course.name}</h4>
                  <p className="text-white" style={{ fontStyle: "italic" }}>
                    Author: {course.authorName}
                  </p>
                  <p className="text-white" style={{ fontStyle: "italic" }}>
                    Contact email: {course.authorEmail}
                  </p>
                </Link>
              </div>
            </div>
          ))}
        </div>
        <div className="d-flex justify-content-center mt-4">
          <Pagination>{paginationItems}</Pagination>
        </div>
      </div>
    <ToastContainer />
  </div>
  );
};

export default Courses;
