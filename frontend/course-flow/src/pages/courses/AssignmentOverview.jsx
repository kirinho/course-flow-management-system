import React, { useEffect, useState } from "react";
import axios from "axios";
import { useParams, useNavigate } from "react-router-dom";
import { Card, Button, Modal, Form, Dropdown } from "react-bootstrap";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";

const AssignmentOverview = () => {
  const { courseId, assignmentId } = useParams();
  const navigate = useNavigate();
  const [assignment, setAssignment] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [attachments, setAttachments] = useState([]);
  const [submissionAttachments, setSubmissionAttachments] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [textSubmission, setTextSubmission] = useState("");
  const [files, setFiles] = useState([]);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [hasAccess, setHasAccess] = useState(null);

  useEffect(() => {
    const checkAccess = async () => {
      try {
        const token = localStorage.getItem("token");
        const response = await axios.get(`http://localhost:8080/enroll/info/${courseId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (response.status === 200) {
          setHasAccess(true);
        }
      } catch (error) {
        if (error.response?.status === 403) {
          setHasAccess(false);
          setTimeout(() => navigate(`/courses`), 3000);
        }
      }
    };

    checkAccess();
  }, [courseId, navigate]);

  useEffect(() => {
    if (hasAccess === true) {
      const fetchAssignment = async () => {
        try {
          const token = localStorage.getItem("token");
          const response = await axios.get(`http://localhost:8080/assignment/${assignmentId}/overview?courseId=${courseId}`, {
            headers: { Authorization: `Bearer ${token}` },
          });
          setAssignment(response.data);
          setAttachments(response.data.attachments);
          if (response.data.submission) {
            setSubmissionAttachments(response.data.submission.attachments);
            setTextSubmission(response.data.submission.textSubmission || "");
          }
        } catch (error) {
          console.error("Error fetching assignment overview", error);
        } finally {
          setIsLoading(false);
        }
      };

      fetchAssignment();
    } else if (hasAccess === false) {
      setIsLoading(false);
    }
  }, [hasAccess, assignmentId]);

  const handleDownload = async (fileId) => {
    try {
      const token = localStorage.getItem("token");
      const response = await axios.get(`http://localhost:8080/attachment/${fileId}`, {
        headers: { Authorization: `Bearer ${token}` },
        responseType: "json",
      });
      const { fileName, fileData } = response.data;
      const byteCharacters = atob(fileData);
      const byteNumbers = new Array(byteCharacters.length).fill(0).map((_, i) => byteCharacters.charCodeAt(i));
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

  const handleFileChange = (e) => {
    setFiles([...e.target.files]);
  };

  const handleSubmit = async () => {
    try {
      const token = localStorage.getItem("token");
      const formData = new FormData();
      formData.append("textSubmission", textSubmission);
      files.forEach((file) => formData.append("attachments", file));

      const url = assignment.submission
        ? `http://localhost:8080/submission/${assignment.submission.id}/update`
        : `http://localhost:8080/submission/assignment/${assignmentId}/create`;

      await axios({
        method: assignment.submission ? "patch" : "post",
        url,
        headers: { Authorization: `Bearer ${token}` },
        data: formData,
      });

      setShowModal(false);
      window.location.reload();
    } catch (error) {
      console.error("Error submitting the assignment", error);
    }
  };

  const handleDeleteConfirm = async () => {
    try {
      const token = localStorage.getItem("token");
      await axios.delete(`http://localhost:8080/submission/${assignment.submission.id}/delete`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setShowDeleteConfirm(false);
      window.location.reload();
    } catch (error) {
      console.error("Error deleting submission", error);
    }
  };

  const getFileIcon = (fileType) => {
    switch (fileType) {
      case 'PDF':
        return 'file-earmark-pdf';
      case 'DOCX':
        return 'file-earmark-word';
      case 'DOC':
        return 'file-earmark-word';
      case 'XSLX':
        return 'file-earmark-excel';
      case 'XSL':
        return 'file-earmark-excel';
      default:
        return 'file-earmark';
    }
  };

  if (isLoading) return <div className="text-center text-lg">Loading...</div>;
  if (hasAccess === false) return <div className="text-center text-danger">Access denied.</div>;
  if (!assignment)
    return <div className="text-center text-danger">Assignment not found.</div>;

  return (
    <div className="d-flex align-items-start justify-content-center min-vh-100" style={{ paddingTop: "3rem" }}>

      <div className="container py-5">
        <div className="row">
          <div className="col-12 mb-4">
            <Card className="bg-light border shadow-sm p-4 rounded-lg">
              <div className="text-center mb-5">
                <h1 className="mt-4">{assignment.title}</h1>
                <p className="text-muted">{assignment.description}</p>
              </div>
              <p><strong>Due Date:</strong> {new Date(assignment.dueDate).toLocaleDateString()}</p>
              <p><strong>Max Score:</strong> {assignment.maxScore}</p>
              <h3 className="text-dark">Attachments</h3>
              <ul className="list-unstyled">
                {attachments.length > 0 ? (
                  attachments.map((file) => (
                    <li key={file.id} className="d-flex align-items-center gap-3 mb-3">
                      <i className={`bi bi-${getFileIcon(file.fileType)} me-2`} />
                      <Button variant="link" onClick={() => handleDownload(file.id)} className="text-primary">
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
              <Card className="bg-light border shadow-sm p-4 rounded-lg">
                <div className="d-flex justify-content-between align-items-center">
                  <h3 className="text-dark">Submission</h3>
                  <Dropdown>
                    <Dropdown.Toggle variant="secondary" id="dropdown-basic">⋮</Dropdown.Toggle>
                    <Dropdown.Menu>
                      <Dropdown.Item onClick={() => setShowModal(true)}>Edit Submission</Dropdown.Item>
                      <Dropdown.Item onClick={() => setShowDeleteConfirm(true)} className="text-danger">Delete Submission</Dropdown.Item>
                    </Dropdown.Menu>
                  </Dropdown>
                </div>
                <p><strong>Submitted At:</strong> {new Date(assignment.submission.submittedAt).toLocaleDateString()}</p>
                <ReactMarkdown remarkPlugins={[remarkGfm]}>{assignment.submission.textSubmission || "No submission."}</ReactMarkdown>
                <h3 className="text-dark">Attachments</h3>
                <ul className="list-unstyled">
                  {submissionAttachments.length > 0 ? (
                    submissionAttachments.map((file) => (
                      <li key={file.id} className="d-flex align-items-center gap-3 mb-3">
                        <i className={`bi bi-${getFileIcon(file.fileType)} me-2`} />
                        <Button variant="link" onClick={() => handleDownload(file.id)} className="text-primary">
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
            <div className="justify-content-between align-items-center">
              <h3 className="text-dark">Grade</h3>
              <p><strong>Score:</strong> {assignment.submission.grade.score}</p>
              <p><strong>Feedback:</strong>  {assignment.submission.grade.feedback}</p>
              <p><strong>Manager:</strong> {assignment.submission.grade.managerFullName}</p>
            </div>
          </Card>
          )}
              </div>
            </div>
          ) : (
            <Button onClick={() => setShowModal(true)}>Create Submission</Button>
          )}

          <Modal show={showModal} onHide={() => setShowModal(false)}>
            <Modal.Header closeButton>
              <Modal.Title>{assignment.submission ? "Edit Submission" : "Create Submission"}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
              <Form.Group className="mb-3">
                <Form.Label>Text Submission</Form.Label>
                <Form.Control as="textarea" value={textSubmission} onChange={(e) => setTextSubmission(e.target.value)} />
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Attachments</Form.Label>
                <Form.Control type="file" multiple onChange={handleFileChange} />
              </Form.Group>
            </Modal.Body>
            <Modal.Footer>
              <Button variant="secondary" onClick={() => setShowModal(false)}>Close</Button>
              <Button variant="primary" onClick={handleSubmit}>{assignment.submission ? "Update" : "Submit"}</Button>
            </Modal.Footer>
          </Modal>

          <Modal show={showDeleteConfirm} onHide={() => setShowDeleteConfirm(false)}>
            <Modal.Header closeButton>
              <Modal.Title>Confirm Deletion</Modal.Title>
            </Modal.Header>
            <Modal.Body>Are you sure you want to delete this submission? This action cannot be undone.</Modal.Body>
            <Modal.Footer>
              <Button variant="secondary" onClick={() => setShowDeleteConfirm(false)}>Cancel</Button>
              <Button variant="danger" onClick={handleDeleteConfirm}>Delete</Button>
            </Modal.Footer>
          </Modal>
      </div>
    </div>
  );
};

export default AssignmentOverview;
