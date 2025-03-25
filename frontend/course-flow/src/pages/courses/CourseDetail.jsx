import React, { useEffect, useState } from "react";
import axios from "axios";
import { useParams, useNavigate } from "react-router-dom";
import { Modal, Button, Form } from "react-bootstrap";
import { ToastContainer, toast, Bounce } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const CourseDetail = () => {
  const { id } = useParams();
  const [course, setCourse] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [enrollmentCode, setEnrollmentCode] = useState("");
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  useEffect(() => {
    if (!token) return;
    const fetchCourse = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/courses/course/${id}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setCourse(response.data);
        setIsLoading(false);
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Internal server error';
        toast.error(`Error fetching course details: ${errorMessage}`, {
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
        setIsLoading(false);
      }
    };

    fetchCourse();
  }, [id]);

  const handleEnroll = async () => {
    try {
      const response = await axios.post(
        "http://localhost:8080/enroll",
        {
          enrollmentCode,
          courseId: Number(id),
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      if (response.status === 204) {
        toast.success('Enrollment successful! Redirecting to learning page...', {
          position: "bottom-right",
          autoClose: 3000,
          hideProgressBar: false,
          closeOnClick: false,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
          theme: "light",
          transition: Bounce,
          });
          setTimeout(() => {
            navigate(`/course/${id}/overview`);
        }, 4000); 
      }
    } catch (error) {
      const errorMessage = error.response?.data?.message || 'Please check your enrollment code.';
      toast.error(`Error enrolling to course: ${errorMessage}`, {
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
    }
  };

  if (!token) {
    return (
        <div className="d-flex align-items-start justify-content-center min-vh-100" style={{ paddingTop: "3rem" }}>
            <h2>You need to be authenticated to view the course.</h2>;
        </div>)
  }
  if (isLoading) {
    return (
        <div className="d-flex align-items-start justify-content-center min-vh-100" style={{ paddingTop: "3rem" }}>
            <h2>Loading...</h2>;
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
      <div className="container-fluid py-5">
        <div className="container py-5">
          <div className="row align-items-center">
            <div className="col-lg-5">
              {course.imageBase64 ? (
                <img
                  className="img-fluid rounded mb-4 mb-lg-0"
                  src={`data:image/jpeg;base64,${course.imageBase64}`}
                  alt={course.name}
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
            </div>
            <div className="col-lg-7">
              <div className="text-left mb-4">
                <h5 className="text-primary text-uppercase mb-3" style={{ letterSpacing: "5px" }}>
                  Course Details
                </h5>
                <h1>{course.name}</h1>
              </div>
              <p>{course.description}</p>
              <p>
                {course.enrolled ? (
                  <button className="btn btn-success" onClick={() => navigate(`/course/${id}/overview`)}>
                    Start Learning!
                  </button>
                ) : (
                  <button className="btn btn-primary" onClick={() => setShowModal(true)}>
                    Enroll Now
                  </button>
                )}
              </p>
            </div>
          </div>
        </div>

        <Modal show={showModal} onHide={() => setShowModal(false)}>
          <Modal.Header closeButton>
            <Modal.Title>Enter Enrollment Code</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Form>
              <Form.Group>
                <Form.Label>Enrollment Code</Form.Label>
                <Form.Control
                  type="text"
                  value={enrollmentCode}
                  onChange={(e) => setEnrollmentCode(e.target.value)}
                  maxLength={10}
                  placeholder="Enter code"
                />
              </Form.Group>
            </Form>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowModal(false)}>
              Cancel
            </Button>
            <Button variant="primary" onClick={handleEnroll}>
              Submit
            </Button>
          </Modal.Footer>
        </Modal>
      </div>
      <ToastContainer />
    </div>
  );
};

export default CourseDetail;
