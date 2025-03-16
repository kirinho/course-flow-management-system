import React, { useEffect, useState } from "react";
import axios from "axios";
import { useParams } from "react-router-dom";
import { Card, Button, Form } from "react-bootstrap";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import { FaBook, FaTasks } from "react-icons/fa";

const LessonOverview = () => {
  const { lessonId } = useParams();
  const [lesson, setLesson] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [attachments, setAttachments] = useState([]);

  useEffect(() => {
    const fetchLesson = async () => {
      try {
        const token = localStorage.getItem("token");
        const response = await axios.get(`http://localhost:8080/lesson/${lessonId}/overview`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setLesson(response.data);
        setAttachments(response.data.attachments);
      } catch (error) {
        console.error("Error fetching lesson overview", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchLesson();
  }, [lessonId]);

  const handleDownload = async (fileId) => {
    try {
        const token = localStorage.getItem("token");
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
        console.error("Error downloading the file", error);
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
  if (!lesson) return <div className="text-center text-danger">Lesson not found.</div>;

  return (
    <div className="container py-5">
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
  );
};

export default LessonOverview;
