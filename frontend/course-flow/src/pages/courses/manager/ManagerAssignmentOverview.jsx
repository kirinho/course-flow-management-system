import React, { useEffect, useState } from "react";
import axios from "axios";
import { useParams, useLocation, useNavigate } from "react-router-dom";
import { Card, Button, Modal, Form, Dropdown } from "react-bootstrap";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";

const ManagerAssignmentOverview = () => {
  const { courseId, assignmentId } = useParams();
  const location = useLocation();
  const navigate = useNavigate(); 
  const queryParams = new URLSearchParams(location.search);
  const selectedUserId = queryParams.get("userId");
  const [assignment, setAssignment] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [attachments, setAttachments] = useState([]);
  const [students, setStudents] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [gradeScore, setGradeScore] = useState("");
  const [gradeFeedback, setGradeFeedback] = useState("");
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);

  useEffect(() => {
    const fetchAssignment = async () => {
      try {
        const token = localStorage.getItem("token");
        const url = selectedUserId
          ? `http://localhost:8080/assignment/${assignmentId}/overview?courseId=${courseId}&userId=${selectedUserId}`
          : `http://localhost:8080/assignment/${assignmentId}/overview?courseId=${courseId}`;
        const response = await axios.get(url, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setAssignment(response.data);
        setAttachments(response.data.attachments);
        if (response.data.submission && response.data.submission.grade) {
          setGradeScore(response.data.submission.grade.score);
          setGradeFeedback(response.data.submission.grade.feedback || "");
        }
      } catch (error) {
        console.error("Error fetching assignment overview", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchAssignment();
  }, [assignmentId, selectedUserId]);

  useEffect(() => {
    if (!selectedUserId) {
      const fetchStudents = async () => {
        try {
          const token = localStorage.getItem("token");
          const response = await axios.get(
            `http://localhost:8080/assignment/${assignmentId}/all-students`,
            { headers: { Authorization: `Bearer ${token}` } }
          );
          setStudents(response.data);
        } catch (error) {
          console.error("Error fetching students", error);
        }
      };
      fetchStudents();
    }
  }, [assignmentId, selectedUserId]);

  const handleDownload = async (fileId) => {
    try {
      const token = localStorage.getItem("token");
      const response = await axios.get(`http://localhost:8080/attachment/${fileId}`, {
        headers: { Authorization: `Bearer ${token}` },
        responseType: "json",
      });
      const { fileName, fileData } = response.data;
      const byteCharacters = atob(fileData);
      const byteNumbers = new Array(byteCharacters.length)
        .fill(0)
        .map((_, i) => byteCharacters.charCodeAt(i));
      const byteArray = new Uint8Array(byteNumbers);
      const blob = new Blob([byteArray]);
      const link = document.createElement("a");
      link.href = URL.createObjectURL(blob);
      link.download = fileName;
      link.click();
    } catch (error) {
      console.error("Error downloading the file", error);
    }
  };

  const handleSubmit = async () => {
    try {
      const token = localStorage.getItem("token");
      let url = "";
      let method = "";

      if (assignment.submission && assignment.submission.grade) {
        url = `http://localhost:8080/manager/grade/${assignment.submission.grade.id}/update`;
        method = "patch";
      } else {
        url = `http://localhost:8080/manager/grade/${assignment.submission.id}/create`;
        method = "post";
      }

      const payload = {
        score: parseInt(gradeScore, 10),
        feedback: gradeFeedback,
      };

      await axios({
        method,
        url,
        headers: { Authorization: `Bearer ${token}` },
        data: payload,
      });

      setShowModal(false);
      window.location.reload();
    } catch (error) {
      console.error("Error submitting grade", error);
    }
  };

  const handleDeleteConfirm = async () => {
    try {
      const token = localStorage.getItem("token");
      await axios.delete(
        `http://localhost:8080/manager/grade/${assignment.submission.grade.id}/delete`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setShowDeleteConfirm(false);
      window.location.reload();
    } catch (error) {
      console.error("Error deleting grade", error);
    }
  };

  const getFileIcon = (fileType) => {
    switch (fileType) {
      case "PDF":
        return "file-earmark-pdf";
      case "DOCX":
      case "DOC":
        return "file-earmark-word";
      case "XSLX":
      case "XSL":
        return "file-earmark-excel";
      default:
        return "file-earmark";
    }
  };

  const handleStudentSelect = (studentId) => {
    navigate(`/manager/course/${courseId}/assignment/${assignmentId}/overview?userId=${studentId}`);
  };

  if (isLoading)
    return <div className="text-center text-lg">Loading...</div>;
  if (!assignment)
    return (
      <div className="text-center text-danger">Assignment not found.</div>
    );

  return (
    <div className="d-flex align-items-start justify-content-center min-vh-100" style={{ paddingTop: "3rem" }}>

      <div className="container py-5">
              {!selectedUserId && (
          <div className="mb-4">
            <h3>Choose student for grading:</h3>
            <Dropdown>
              <Dropdown.Toggle variant="primary" id="dropdown-basic">
                Choose student
              </Dropdown.Toggle>
              <Dropdown.Menu>
                {students.map((student) => (
                  <Dropdown.Item
                    key={student.id}
                    onClick={() => handleStudentSelect(student.id)}
                  >
                    {student.fullName} ({student.email})
                  </Dropdown.Item>
                ))}
              </Dropdown.Menu>
            </Dropdown>
          </div>
        )}
        <div className="row">
          <div className="col-12 mb-4">
            <Card className="bg-light border shadow-sm p-4 rounded-lg">
              <div className="text-center mb-5">
                <h1 className="mt-4">{assignment.title}</h1>
                <p className="text-muted">{assignment.description}</p>
              </div>
              <p>
                <strong>Due Date:</strong>{" "}
                {new Date(assignment.dueDate).toLocaleDateString()}
              </p>
              <p>
                <strong>Max Score:</strong> {assignment.maxScore}
              </p>
              <h3 className="text-dark">Attachments</h3>
              <ul className="list-unstyled">
                {attachments.length > 0 ? (
                  attachments.map((file) => (
                    <li
                      key={file.id}
                      className="d-flex align-items-center gap-3 mb-3"
                    >
                      <i className={`bi bi-${getFileIcon(file.fileType)} me-2`} />
                      <Button
                        variant="link"
                        onClick={() => handleDownload(file.id)}
                        className="text-primary"
                      >
                        {file.fileName}
                      </Button>
                    </li>
                  ))
                ) : (
                  <p className="text-muted">No attachments available.</p>
                )}
              </ul>
            </Card>
          </div>
        </div>

        {assignment.submission ? (
          <div className="row">
            <div className="col-12 mb-4">
              {/* Відображення подання (read-only) */}
              <Card className="bg-light border shadow-sm p-4 rounded-lg">
                <div className="d-flex justify-content-between align-items-center">
                  <h3 className="text-dark">Submission</h3>
                </div>
                <p>
                  <strong>Submitted At:</strong>{" "}
                  {new Date(assignment.submission.submittedAt).toLocaleDateString()}
                </p>
                <ReactMarkdown remarkPlugins={[remarkGfm]}>
                  {assignment.submission.textSubmission || "No submission."}
                </ReactMarkdown>
                <h3 className="text-dark mt-3">Submission Attachments</h3>
                <ul className="list-unstyled">
                  {assignment.submission.attachments &&
                  assignment.submission.attachments.length > 0 ? (
                    assignment.submission.attachments.map((file) => (
                      <li
                        key={file.id}
                        className="d-flex align-items-center gap-3 mb-3"
                      >
                        <i className={`bi bi-${getFileIcon(file.fileType)} me-2`} />
                        <Button
                          variant="link"
                          onClick={() => handleDownload(file.id)}
                          className="text-primary"
                        >
                          {file.fileName}
                        </Button>
                      </li>
                    ))
                  ) : (
                    <p className="text-muted">No attachments available.</p>
                  )}
                </ul>
              </Card>

              {assignment.submission.grade && (
                <Card className="bg-light border shadow-sm p-4 rounded-lg mt-3">
                  <div className="d-flex justify-content-between align-items-center">
                    <h3 className="text-dark">Grade</h3>
                    <Dropdown>
                      <Dropdown.Toggle variant="secondary" id="dropdown-basic">
                        ⋮
                      </Dropdown.Toggle>
                      <Dropdown.Menu>
                        <Dropdown.Item onClick={() => setShowModal(true)}>
                          Edit Grade
                        </Dropdown.Item>
                        <Dropdown.Item
                          onClick={() => setShowDeleteConfirm(true)}
                          className="text-danger"
                        >
                          Delete Grade
                        </Dropdown.Item>
                      </Dropdown.Menu>
                    </Dropdown>
                  </div>
                  <p>
                    <strong>Score:</strong>{" "}
                    {assignment.submission.grade.score}
                  </p>
                  <p>
                    <strong>Feedback:</strong>{" "}
                    {assignment.submission.grade.feedback}
                  </p>
                  <p>
                    <strong>Manager:</strong>{" "}
                    {assignment.submission.grade.managerFullName}
                  </p>
                </Card>
              )}

              {!assignment.submission.grade && (
                <Button onClick={() => setShowModal(true)}>Create Grade</Button>
              )}
            </div>
          </div>
        ) : (
          <div className="text-center text-warning">
            Submission not available for grading.
          </div>
        )}

        <Modal show={showModal} onHide={() => setShowModal(false)}>
          <Modal.Header closeButton>
            <Modal.Title>
              {assignment.submission && assignment.submission.grade
                ? "Edit Grade"
                : "Create Grade"}
            </Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label>Score</Form.Label>
              <Form.Control
                type="number"
                value={gradeScore}
                onChange={(e) => setGradeScore(e.target.value)}
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Feedback</Form.Label>
              <Form.Control
                as="textarea"
                value={gradeFeedback}
                onChange={(e) => setGradeFeedback(e.target.value)}
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowModal(false)}>
              Close
            </Button>
            <Button variant="primary" onClick={handleSubmit}>
              {assignment.submission && assignment.submission.grade
                ? "Update"
                : "Submit"}
            </Button>
          </Modal.Footer>
        </Modal>

        <Modal show={showDeleteConfirm} onHide={() => setShowDeleteConfirm(false)}>
          <Modal.Header closeButton>
            <Modal.Title>Confirm Deletion</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            Are you sure you want to delete this grade? This action cannot be undone.
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowDeleteConfirm(false)}>
              Cancel
            </Button>
            <Button variant="danger" onClick={handleDeleteConfirm}>
              Delete
            </Button>
          </Modal.Footer>
        </Modal>
      </div>
    </div>
  );
};

export default ManagerAssignmentOverview;
