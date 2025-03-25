import React, { useEffect, useState } from "react";
import axios from "axios";
import { useParams, Link } from "react-router-dom";
import { Card, Table, Button, Modal } from "react-bootstrap";
import { ToastContainer, toast, Bounce } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const GradesOverview = () => {
  const { courseId } = useParams();
  const [grades, setGrades] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");
  const [showModal, setShowModal] = useState(false);
  const [selectedStudentId, setSelectedStudentId] = useState(null);

  useEffect(() => {
    if (!token && !role) return;
    const fetchGrades = async () => {
      try {
        let response;
        if (role === "MANAGER") {
          response = await axios.get(
            `http://localhost:8080/manager/grade/all-students?courseId=${courseId}`,
            { headers: { Authorization: `Bearer ${token}` } }
          );
        } else {
          response = await axios.get(
            `http://localhost:8080/grades/all?courseId=${courseId}`,
            { headers: { Authorization: `Bearer ${token}` } }
          );
        }
        setGrades(response.data);
      } catch (error) {
        const errorMessage = error.response && error.response.data && error.response.data.message
            ? error.response.data.message
            : 'Failed to fetch grades: Internal Server Error';
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

    fetchGrades();
  }, [courseId, role]);

  const handleShowModal = (studentId) => {
    setSelectedStudentId(studentId);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedStudentId(null);
  };

  const handleDeleteStudent = async () => {
    if (!selectedStudentId) return;

    try {
      await axios.delete(`http://localhost:8080/enroll/cancel/${courseId}?userId=${selectedStudentId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      setGrades(grades.filter(student => student.studentId !== selectedStudentId));
      toast.success('Student was removed successfully from the course!', {
          position: "bottom-right",
          autoClose: 1000,
          hideProgressBar: false,
          closeOnClick: false,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
          theme: "light",
          transition: Bounce,
          });
    } catch (error) {
      const errorMessage = error.response && error.response.data && error.response.data.message
          ? error.response.data.message
          : 'Failed to remove student: Internal Server Error';
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
      handleCloseModal();
    }
  };

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
  if (grades.length === 0) {
    return (
        <div className="d-flex align-items-start justify-content-center min-vh-100" style={{ paddingTop: "3rem" }}>
            <h2>No grades available.</h2>;
        </div>)
  }

  return (
    <div className="d-flex align-items-start justify-content-center min-vh-100" style={{ paddingTop: "3rem" }}>
      <div className="container py-5">
        <div className="mb-4 pb-2" style={{ borderBottom: "1px solid #ddd" }}>
          <Link to={`/courses/${courseId}`} style={{ fontWeight: "bold", color: "#007bff", textDecoration: "none" }}>
            Course detail
          </Link>
          <span style={{ margin: "0 8px" }}>/</span>
          <Link to={`/course/${courseId}/overview`} style={{ fontWeight: "bold", color: "#007bff", textDecoration: "none" }}>
            Course overview
          </Link>
          <span style={{ margin: "0 8px" }}>/</span>
          <span style={{ color: "#6c757d" }}>Grades</span>
        </div>
        {role === "MANAGER" ? (
          <>
            <h1 className="text-center mb-4">Students Overview</h1>
            <Card className="shadow-sm p-4">
              <Table striped bordered hover>
                <thead className="thead-dark">
                  <tr>
                    <th>#</th>
                    <th>Full Name</th>
                    <th>Total Score</th>
                    <th>Max Total Score</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {grades.map((grade, index) => (
                    <tr key={grade.studentId}>
                      <td>{index + 1}</td>
                      <td>{grade.fullName}</td>
                      <td>{grade.totalScore}</td>
                      <td>{grade.maxTotalScore}</td>
                      <td>
                        <Button
                          variant="danger"
                          onClick={() => handleShowModal(grade.studentId)}
                        >
                          Remove
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </Card>
          </>
        ) : (
          <>
            <h1 className="text-center mb-4">Your Grades</h1>
            <Card className="shadow-sm p-4">
              <Table striped bordered hover>
                <thead className="thead-dark">
                  <tr>
                    <th>#</th>
                    <th>Title</th>
                    <th>Description</th>
                    <th>Due Date</th>
                    <th>Max Score</th>
                    <th>Your Score</th>
                  </tr>
                </thead>
                <tbody>
                  {grades.map((grade, index) => (
                    <tr key={grade.id}>
                      <td>{index + 1}</td>
                      <td>
                        <Link
                          to={`/course/${courseId}/assignment/${grade.id}/overview`}
                          className="text-decoration-none text-dark"
                        >
                          {grade.title}
                        </Link>
                      </td>
                      <td>{grade.description}</td>
                      <td>{new Date(grade.dueDate).toLocaleDateString()}</td>
                      <td>{grade.maxScore}</td>
                      <td>{grade.currentScore ?? "Not Graded"}</td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </Card>
          </>
        )}
      </div>

      <Modal show={showModal} onHide={handleCloseModal} centered>
        <Modal.Header closeButton>
          <Modal.Title>Confirm Removal</Modal.Title>
        </Modal.Header>
        <Modal.Body>Are you sure you want to remove this student from the course?</Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleCloseModal}>
            Cancel
          </Button>
          <Button variant="danger" onClick={handleDeleteStudent}>
            Remove
          </Button>
        </Modal.Footer>
      </Modal>

      <ToastContainer />
    </div>
  );
};

export default GradesOverview;
