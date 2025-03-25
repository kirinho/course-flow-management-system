import React, { useEffect, useState } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import { ToastContainer, toast, Bounce } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const Courses = () => {
  const [courses, setCourses] = useState([]);
  const [sortBy, setSortBy] = useState("id");
  const [orderBy, setOrderBy] = useState("asc");
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
            sortBy: sortBy, orderBy: orderBy
          },
        });
        setCourses(response.data);
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
  }, [token, sortBy, orderBy]);

  if (!token) {
    return (
        <div className="d-flex align-items-start justify-content-center min-vh-100" style={{ paddingTop: "3rem" }}>
            <h2>You need to be authenticated to view the courses.</h2>;
        </div>)
  }

  return (
    <div className="d-flex align-items-start justify-content-center min-vh-100" style={{ paddingTop: "3rem" }}>
      <div className="container-fluid py-5">
        <div className="container pt-5 pb-3">
          <div className="text-center mb-5">
            <h5 className="text-primary text-uppercase mb-3" style={{ letterSpacing: "5px" }}>
              Subjects
            </h5>
            <h1>Explore Top Subjects</h1>
          </div>
          
          <div className="d-flex justify-content-center mb-4">
            <div className="mr-3">
              <label>Sort by:</label>
              <select
                className="form-control"
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value)}
              >
                <option value="id">ID</option>
                <option value="name">Name</option>
              </select>
            </div>

            <div>
              <label>Order by:</label>
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
        </div>
      </div>
      <ToastContainer />
    </div>
  );
};

export default Courses;
