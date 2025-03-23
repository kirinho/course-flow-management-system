import React, { useEffect, useState } from "react";
import axios from "axios";
import { useParams, useNavigate } from "react-router-dom";
import { Modal, Button, Form } from "react-bootstrap";

const CourseDetail = () => {
  const { id } = useParams();
  const [course, setCourse] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [enrollmentCode, setEnrollmentCode] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const fetchCourse = async () => {
      try {
        const token = localStorage.getItem("token");
        const response = await axios.get(`http://localhost:8080/courses/course/${id}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setCourse(response.data);
        setIsLoading(false);
      } catch (error) {
        console.error("Error fetching course details", error);
        setIsLoading(false);
      }
    };

    fetchCourse();
  }, [id]);

  const handleEnroll = async () => {
    const token = localStorage.getItem("token");
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
        alert("Enrollment successful! Redirecting to learning page...");
        navigate(`/course/${id}/learning`);
      }
    } catch (error) {
      console.error("Error enrolling in course", error);
      alert("Failed to enroll. Please check your enrollment code.");
    }
  };

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (!course) {
    return <div>Course not found.</div>;
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
    </div>
  );
};

export default CourseDetail;
