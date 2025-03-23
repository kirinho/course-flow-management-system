import React, { useEffect, useState } from "react";
import axios from "axios";
import { useParams, Link } from "react-router-dom";
import { Card, Table } from "react-bootstrap";

const GradesOverview = () => {
  const { courseId } = useParams();
  const [grades, setGrades] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const role = localStorage.getItem("role");

  useEffect(() => {
    const fetchGrades = async () => {
      try {
        const token = localStorage.getItem("token");
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
      } catch (err) {
        setError("Failed to fetch grades");
      } finally {
        setIsLoading(false);
      }
    };

    fetchGrades();
  }, [courseId, role]);

  if (isLoading) return <div className="text-center text-lg">Loading...</div>;
  if (error) return <div className="text-center text-danger">{error}</div>;
  if (grades.length === 0) return <div className="text-center text-muted">No grades available.</div>;

  return (
    <div className="d-flex align-items-start justify-content-center min-vh-100" style={{ paddingTop: "3rem" }}>
      <div className="container py-5">
        {role === "MANAGER" ? (
          <>
            <h1 className="text-center mb-4">Students Grades</h1>
            <Card className="shadow-sm p-4">
              <Table striped bordered hover>
                <thead className="thead-dark">
                  <tr>
                    <th>#</th>
                    <th>Full Name</th>
                    <th>Total Score</th>
                    <th>Max Total Score</th>
                  </tr>
                </thead>
                <tbody>
                  {grades.map((grade, index) => (
                    <tr key={grade.studentId}>
                      <td>{index + 1}</td>
                      <td>{grade.fullName}</td>
                      <td>{grade.totalScore}</td>
                      <td>{grade.maxTotalScore}</td>
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
                          to={`/assignment/${grade.id}/overview`}
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
    </div>
  );
};

export default GradesOverview;
