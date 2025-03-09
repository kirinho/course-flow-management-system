import React, { useEffect, useState } from "react";
import axios from "axios";
import { Link, useNavigate } from "react-router-dom";
import { Dropdown, Button, Modal } from "react-bootstrap";

const ManagerCourses = () => {
    const [courses, setCourses] = useState([]);
    const [error, setError] = useState(null);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [courseToDelete, setCourseToDelete] = useState(null);
    const navigate = useNavigate();
    const token = localStorage.getItem("token");

    useEffect(() => {
        if (!token) {
            return;
        }

        const fetchCourses = async () => {
            try {
                const response = await axios.get("http://localhost:8080/courses/all-manager", {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setCourses(response.data);
            } catch (error) {
                setError("Error fetching courses.");
            }
        };

        fetchCourses();
    }, [token]);

    const handleDelete = async () => {
        if (!courseToDelete) return;
        try {
            await axios.delete(`http://localhost:8080/courses/course/delete/${courseToDelete.id}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setCourses(courses.filter(course => course.id !== courseToDelete.id));
            setShowDeleteModal(false);
        } catch (error) {
            setError("Failed to delete course.");
        }
    };

    if (!token) {
        return <h2>You need to be authenticated to view the courses.</h2>;
    }

    return (
        <div className="container mt-5">
            <h2 className="mb-4">My Courses</h2>
            {error && <div className="alert alert-danger">{error}</div>}
            <div className="row">
                {courses.map(course => (
                    <div key={course.id} className="col-md-4">
                        <div className="card">
                            <img 
                                src={course.imageBase64 ? `data:image/jpeg;base64,${course.imageBase64}` : "https://via.placeholder.com/200"} 
                                alt={course.name} 
                                className="card-img-top" 
                                style={{ height: "200px", objectFit: "cover" }}
                            />
                            <div className="card-body">
                                <h5 className="card-title">{course.name}</h5>
                                <Dropdown>
                                    <Dropdown.Toggle variant="secondary" id="dropdown-basic">
                                        ⋮
                                    </Dropdown.Toggle>
                                    <Dropdown.Menu>
                                        <Dropdown.Item onClick={() => navigate(`/manager/courses/edit/${course.id}`)}>Edit</Dropdown.Item>
                                        <Dropdown.Item onClick={() => { setCourseToDelete(course); setShowDeleteModal(true); }}>Delete</Dropdown.Item>
                                    </Dropdown.Menu>
                                </Dropdown>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
            <Button className="mt-4" onClick={() => navigate("/manager/courses/add")}>Add New Course</Button>
            
            <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Confirm Delete</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    Are you sure you want to delete {courseToDelete?.name}?
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowDeleteModal(false)}>Cancel</Button>
                    <Button variant="danger" onClick={handleDelete}>Delete</Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
};

export default ManagerCourses;
