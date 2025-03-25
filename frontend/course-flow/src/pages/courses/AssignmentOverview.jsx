import React, { useEffect, useState } from "react";
import axios from "axios";
import { useParams, useNavigate, Link } from "react-router-dom";
import { Card, Button, Modal, Form, Dropdown, OverlayTrigger, Tooltip } from "react-bootstrap";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import { ToastContainer, toast, Bounce } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

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
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");

  useEffect(() => {
    if (!token || role !== "STUDENT") return;
    const checkAccess = async () => {
      try {
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
    if (!token || role !== "STUDENT") return;
    if (hasAccess === true) {
      const fetchAssignment = async () => {
        try {
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
          const errorMessage = error.response && error.response.data && error.response.data.message
              ? error.response.data.message
              : 'Error fetching assignment overview: Internal Server Error';
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

      fetchAssignment();
    } else if (hasAccess === false) {
      setIsLoading(false);
    }
  }, [hasAccess, assignmentId]);

  const handleDownload = async (fileId) => {
    try {
        const response = await axios.get(`http://localhost:8080/attachment/${fileId}`, {
            headers: { Authorization: `Bearer ${token}` },
            responseType: 'json',
        });

        const { fileName, fileData } = response.data;

        const byteCharacters = atob(fileData);
        const byteNumbers = new Array(byteCharacters.length).fill(0).map((_, i) => byteCharacters.charCodeAt(i));
        const byteArray = new Uint8Array(byteNumbers);
        const blob = new Blob([byteArray]);

        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = fileName;
        link.click();
    } catch (error) {
        const errorMessage = error.response && error.response.data && error.response.data.message
            ? error.response.data.message
            : 'Failed to download file: Internal Server Error';
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
      }
  };

  const handleFileChange = (e) => {
    setFiles([...e.target.files]);
  };

  const handleSubmit = async () => {
    try {
      const formData = new FormData();
      formData.append("textSubmission", textSubmission);
      files.forEach((file) => formData.append("attachments", file));

      const url = assignment.submission
        ? `http://localhost:8080/submission/${assignment.submission.id}/update`
        : `http://localhost:8080/submission/assignment/${assignmentId}/create`;

      const response = await axios({
        method: assignment.submission ? "patch" : "post",
        url,
        headers: { Authorization: `Bearer ${token}` },
        data: formData,
      });

      setShowModal(false);
      if (response.status === 201) {
        toast.success("Submission created successfully!", {
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
      } else if (response.status === 200) {
        toast.success("Submission updated successfully!", {
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
      }
      setTimeout(() => {
          navigate(0);
      }, 1500);
    } catch (error) {
        const errorMessage =
        error.response && error.response.data && error.response.data.message
          ? error.response.data.message
          : "Error submitting the submission: Internal Server Error";

      if (error.response && error.response.status === 400) {
        toast.error("Invalid submission: please provide text or attachments.", {
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
      } else if (error.response && error.response.status === 404) {
        toast.error("Submission not found!", {
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
      } else {
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
    }
  };

  const handleDeleteConfirm = async () => {
    try {
      await axios.delete(`http://localhost:8080/submission/${assignment.submission.id}/delete`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setShowDeleteConfirm(false);
      toast.success('Submission has been successfully deleted', {
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
          setTimeout(() => {
              navigate(0);
        }, 1500);
    } catch (error) {
      console.error("Error deleting submission", error);
      const errorMessage = error.response && error.response.data && error.response.data.message
        ? error.response.data.message
        : 'Error deleting submission: Internal Server Error';
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

  if (!token) {
    return (
        <div className="d-flex align-items-start justify-content-center min-vh-100" style={{ paddingTop: "3rem" }}>
            <h2>You need to be authenticated to view the assignment.</h2>;
        </div>)
  }
  if (role !== "STUDENT") {
      return (
          <div className="d-flex align-items-start justify-content-center min-vh-100" style={{ paddingTop: "3rem" }}>
              <h2>You do not have permission to view this page.</h2>;
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
  if (!assignment) {
    return (
        <div className="d-flex align-items-start justify-content-center min-vh-100" style={{ paddingTop: "3rem" }}>
            <h2>Assignment not found.</h2>;
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
              Course Overview
            </Link>
            <span style={{ margin: "0 8px" }}>/</span>
            <span style={{ color: "#6c757d" }}>Assignment overview</span>
            <span style={{ margin: "0 8px" }}>/</span>
            <Link to={`/course/${courseId}/grades`} style={{ fontWeight: "bold", color: "#007bff", textDecoration: "none" }}>
              Grades
            </Link>
          </div>
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
              <p><strong>Feedback:</strong>  {assignment.submission.grade.feedback || "No feedback."}</p>
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
                <OverlayTrigger
                  placement="right"
                  trigger="click"
                  overlay={<Tooltip id="tooltip-description">Supports **Markdown** formatting:<br />
                      <a href="https://www.markdownguide.org/basic-syntax/" target="_blank" rel="noopener noreferrer">
                          Click the link to familiarize yourself with structure
                      </a>
                  </Tooltip>}
                  >
                      <span style={{ borderRadius: "50%", backgroundColor: "#f0f0f0", padding: "4px 8px", cursor: "pointer", marginLeft: "8px" }}>
                      i
                      </span>
                  </OverlayTrigger>
                <Form.Control as="textarea" value={textSubmission} onChange={(e) => setTextSubmission(e.target.value)} />
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Attachments</Form.Label>
                <OverlayTrigger
                    placement="right"
                    overlay={<Tooltip id="tooltip-description">Upload files: '.doc', 'docx', '.pdf', '.xsl', 'xslx'.<br />
                    Note: during update your files will be appended to your current files!
                    </Tooltip>}
                >
                    <span style={{ borderRadius: "50%", backgroundColor: "#f0f0f0", padding: "4px 8px", cursor: "pointer", marginLeft: "8px" }}>
                    i
                    </span>
                </OverlayTrigger>
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
      <ToastContainer />
    </div>
  );
};

export default AssignmentOverview;
