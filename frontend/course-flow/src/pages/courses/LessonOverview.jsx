import React, { useEffect, useState } from "react";
import axios from "axios";
import { useParams, useNavigate, Link } from "react-router-dom";
import { Card, Button } from "react-bootstrap";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import { ToastContainer, toast, Bounce } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const LessonOverview = () => {
  const { courseId, lessonId } = useParams();
  const navigate = useNavigate();
  const [lesson, setLesson] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [attachments, setAttachments] = useState([]);
  const [hasAccess, setHasAccess] = useState(null);
  const token = localStorage.getItem("token");

  useEffect(() => {
    if (!token) return;
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
  }, [lessonId, navigate]);

  useEffect(() => {
    if (!token) return;
    if (hasAccess === true) {
      const fetchLesson = async () => {
        try {
          const response = await axios.get(`http://localhost:8080/lesson/course/${courseId}/lesson/${lessonId}/overview`, {
            headers: { Authorization: `Bearer ${token}` },
          });
          setLesson(response.data);
          setAttachments(response.data.attachments);
        } catch (error) {
          const errorMessage = error.response && error.response.data && error.response.data.message
              ? error.response.data.message
              : 'Error fetching lesson overview: Internal Server Error';
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

      fetchLesson();
    } else if (hasAccess === false) {
      setIsLoading(false);
    }
  }, [hasAccess, lessonId]);

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
            <h2>You need to be authenticated to view the lesson.</h2>;
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
  if (!lesson) {
    return (
        <div className="d-flex align-items-start justify-content-center min-vh-100" style={{ paddingTop: "3rem" }}>
            <h2>Lesson not found.</h2>;
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
          <span style={{ color: "#6c757d" }}>Lesson overview</span>
          <span style={{ margin: "0 8px" }}>/</span>
          <Link to={`/course/${courseId}/grades`} style={{ fontWeight: "bold", color: "#007bff", textDecoration: "none" }}>
            Grades
          </Link>
        </div>
        <div className="text-center mb-5">
          <h1 className="mt-4">{lesson.title}</h1>
          <p className="text-muted">{lesson.description}</p>
        </div>

        <div className="row">
          <div className="col-12 mb-4">
            <Card className="bg-light border shadow-sm p-4 rounded-lg">
              <ReactMarkdown remarkPlugins={[remarkGfm]}>{lesson.content}</ReactMarkdown>
            </Card>
          </div>
        </div>

        <div className="row">
          <div className="col-12 mb-4">
            <Card className="bg-light border shadow-sm p-4 rounded-lg">
              <h3 className="text-dark">Attachments</h3>
              <ul className="list-unstyled">
                {attachments.length > 0 ? (
                  attachments.map((file) => (
                    <li key={file.id} className="d-flex align-items-center gap-3 mb-3">
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
      </div>
      <ToastContainer />
    </div>
  );
};

export default LessonOverview;
