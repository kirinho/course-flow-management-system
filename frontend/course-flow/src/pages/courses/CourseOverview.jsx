import React, { useEffect, useState } from "react";
import axios from "axios";
import { Link, useParams, useNavigate } from "react-router-dom";
import { Card } from "react-bootstrap";
import { FaBook, FaTasks } from "react-icons/fa";
import { ToastContainer, toast, Bounce } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const CourseOverview = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [course, setCourse] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [hasAccess, setHasAccess] = useState(null);
  const token = localStorage.getItem("token");
  const role = localStorage.getItem('role');

  useEffect(() => {
    if (!token) return;
    const checkEnrollment = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/enroll/info/${id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (response.status === 200) {
          setHasAccess(true);
        }
      } catch (error) {
        if (error.response?.status === 403) {
          setHasAccess(false);
          setTimeout(() => navigate(`/courses/${id}`), 3000);
        }
      }
    };

    checkEnrollment();
  }, [id]);

  useEffect(() => {
    if (!token) return;
    if (hasAccess === false) {
      setIsLoading(false);
    } else if (hasAccess === true) {
      const fetchCourseOverview = async () => {
        try {
          const response = await axios.get(`http://localhost:8080/courses/${id}/overview`, {
            headers: { Authorization: `Bearer ${token}` },
          });
          setCourse(response.data);
        } catch (error) {
          const errorMessage = error.response && error.response.data && error.response.data.message
              ? error.response.data.message
              : 'Error fetching course overview: Internal Server Error';
          toast.error(errorMessage, {
              position: "bottom-right",
              autoClose: 5000,
              hideProgressBar: false,
              closeOnClick: false,
              pauseOnHover: true,
              draggable: true,
              progress: undefined,
              theme: "light",
              transition: Bounce,
              });
        } finally {
          setIsLoading(false);
        }
      };

      fetchCourseOverview();
    }
  }, [hasAccess, id]);

  if (!token) {
    return (
        <div className="d-flex align-items-start justify-content-center min-vh-100" style={{ paddingTop: "3rem" }}>
            <h2>You need to be authenticated to view the courses.</h2>;
        </div>)
  }
  if (isLoading) {
    return (
        <div className="d-flex align-items-start justify-content-center min-vh-100" style={{ paddingTop: "3rem" }}>
            <h2>Loading...</h2>;
        </div>)
  }
  if (hasAccess === false) {
    return (
        <div className="d-flex align-items-start justify-content-center min-vh-100" style={{ paddingTop: "3rem" }}>
            <h2>Access denied.</h2>;
        </div>)
  }
  if (!course) {
    return (
        <div className="d-flex align-items-start justify-content-center min-vh-100" style={{ paddingTop: "3rem" }}>
            <h2>Course not found.</h2>;
        </div>)
  }

  return (
    <div className="d-flex align-items-start justify-content-center min-vh-100" style={{ paddingTop: "3rem" }}>
      <div className="container py-5">
        <div className="mb-4 pb-2" style={{ borderBottom: "1px solid #ddd" }}>
          <Link to={`/courses/${id}`} style={{ fontWeight: "bold", color: "#007bff", textDecoration: "none" }}>
            Course detail
          </Link>
          <span style={{ margin: "0 8px" }}>/</span>
          <span style={{ color: "#6c757d" }}>Course overview</span>
          <span style={{ margin: "0 8px" }}>/</span>
          <Link to={`/course/${id}/grades`} style={{ fontWeight: "bold", color: "#007bff", textDecoration: "none" }}>
            Grades
          </Link>
        </div>
        <div className="text-center mb-5">
          {course.image ? (
            <img
              className="rounded img-fluid shadow-lg"
              src={`data:image/jpeg;base64,${course.image}`}
              alt={course.name}
              style={{ maxWidth: "600px" }}
            />
          ) : (
            <div className="bg-secondary text-white py-5 rounded-lg">
              No Image Available
            </div>
          )}
          <h1 className="mt-4">{course.name}</h1>
          <p className="text-muted">{course.description}</p>
        </div>

        <div className="modules-container">
          {course.modules.length > 0 ? (
            course.modules.map((module) => (
              <div key={module.id} className="module-card">
                <Card className="bg-light border shadow-sm p-4 rounded-lg">
                  <h3 className="text-dark">{module.name}</h3>
                  <p className="text-muted">{module.description}</p>

                  <ul className="list-unstyled">
                    {module.lessonAssignments.map((item) => (
                      <li
                        key={item.id}
                        className={`d-flex align-items-center gap-3 p-3 rounded mb-2 ${
                          item.type === "LESSON" ? "bg-primary text-white" : "bg-warning text-dark"
                        }`}
                      >
                        {item.type === "LESSON" ? (
                          <FaBook className="text-white" />
                        ) : (
                          <FaTasks className="text-dark" />
                        )}
                      <span>
                        {item.type === "LESSON" ? (
                          <Link
                            to={`/course/${id}/lesson/${item.id}/overview`}
                            className="text-decoration-none text-white"
                          >
                            {item.title}
                          </Link>
                        ) : role === "STUDENT" ? (
                          <Link
                            to={`/course/${id}/assignment/${item.id}/overview`}
                            className="text-decoration-none text-dark"
                          >
                            {item.title}
                          </Link>
                        ) : <Link
                              to={`/manager/course/${id}/assignment/${item.id}/overview`}
                              className="text-decoration-none text-dark"
                            >
                              {item.title}
                            </Link>
                        }
                      </span>
                      </li>
                    ))}
                  </ul>
                </Card>
              </div>
            ))
          ) : (
            <p className="text-center text-muted">No modules available.</p>
          )}
        </div>
      </div>
      <ToastContainer />
    </div>
  );
};

export default CourseOverview;
