import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate, Link } from "react-router-dom";
import { Dropdown, Button, Modal, Form, Alert } from "react-bootstrap";
import { useForm } from "react-hook-form";

const ManagerCourses = () => {
    const [courses, setCourses] = useState([]);
    const [error, setError] = useState(null);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [showCourseModal, setShowCourseModal] = useState(false);
    const [courseToDelete, setCourseToDelete] = useState(null);
    const [editingCourse, setEditingCourse] = useState(null);
    const [previewImage, setPreviewImage] = useState(null);
    const navigate = useNavigate();
    const token = localStorage.getItem("token");
    const { register, handleSubmit, setValue, formState: { errors }, reset } = useForm();

    useEffect(() => {
        if (!token) return;
        axios.get("http://localhost:8080/manager/courses/all", { headers: { Authorization: `Bearer ${token}` } })
            .then(response => setCourses(response.data))
            .catch(() => setError("Error fetching courses."));
    }, [token]);

    const handleDelete = async () => {
        if (!courseToDelete) return;
        try {
            await axios.delete(`http://localhost:8080/manager/courses/course/delete/${courseToDelete.id}`, { headers: { Authorization: `Bearer ${token}` } });
            setCourses(courses.filter(course => course.id !== courseToDelete.id));
            setShowDeleteModal(false);
        } catch {
            setError("Failed to delete course.");
        }
    };

    const handleOpenCourseModal = (course = null) => {
        reset();
        setEditingCourse(course);
        if (course) {
            setValue("name", course.name);
            setValue("description", course.description);
            setPreviewImage(course.imageBase64 ? `data:image/jpeg;base64,${course.imageBase64}` : null);
        } else {
            setPreviewImage(null);
        }
        setShowCourseModal(true);
    };

    const onFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => setPreviewImage(reader.result);
            reader.readAsDataURL(file);
        }
    };

    const onSubmit = async (data) => {
        try {
            const formData = new FormData();
            formData.append("name", data.name);
            formData.append("description", data.description);
            if (data.image && data.image[0]) {
                formData.append("image", data.image[0]);
            }
            
            const url = editingCourse 
                ? `http://localhost:8080/manager/courses/course/update/${editingCourse.id}` 
                : "http://localhost:8080/manager/courses/course/create";
            
            const method = editingCourse ? "PATCH" : "POST";
            
            await axios({
                method,
                url,
                data: formData,
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "multipart/form-data"
                }
            });
            setShowCourseModal(false);
            navigate(0);
        } catch {
            setError("Failed to submit course.");
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
                            <Link to={`/manager/courses/${course.id}/modules`}>
                                <img 
                                    src={course.imageBase64 ? `data:image/jpeg;base64,${course.imageBase64}` : "https://via.placeholder.com/200"} 
                                    alt={course.name} 
                                    className="card-img-top" 
                                    style={{ height: "200px", objectFit: "cover", cursor: "pointer" }}
                                />
                            </Link>
                            <div className="card-body">
                                <h5 className="card-title">{course.name}</h5>
                                <Dropdown>
                                    <Dropdown.Toggle variant="secondary" id="dropdown-basic">⋮</Dropdown.Toggle>
                                    <Dropdown.Menu>
                                        <Dropdown.Item onClick={() => handleOpenCourseModal(course)}>Edit</Dropdown.Item>
                                        <Dropdown.Item onClick={() => { setCourseToDelete(course); setShowDeleteModal(true); }}>Delete</Dropdown.Item>
                                    </Dropdown.Menu>
                                </Dropdown>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
            <Button className="mt-4" onClick={() => handleOpenCourseModal()}>Add New Course</Button>

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

            <Modal show={showCourseModal} onHide={() => setShowCourseModal(false)} size="lg">
                <Modal.Header closeButton>
                    <Modal.Title>{editingCourse ? "Edit Course" : "Create Course"}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form onSubmit={handleSubmit(onSubmit)}>
                        <Form.Group className="mb-3">
                            <Form.Label>Course Name</Form.Label>
                            <Form.Control type="text" placeholder="Enter course name" {...register("name", { required: true })} />
                            {errors.name && <p className="text-danger">Course name is required</p>}
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Description</Form.Label>
                            <Form.Control as="textarea" rows={4} placeholder="Enter course description" {...register("description", { required: true })} />
                            {errors.description && <p className="text-danger">Description is required</p>}
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Course Image</Form.Label>
                            {previewImage && <img src={previewImage} alt="Preview" className="img-fluid mb-2" style={{ maxWidth: "100%" }} />}
                            <Form.Control type="file" {...register("image")} onChange={onFileChange} />
                        </Form.Group>
                        <Button type="submit" className="w-100">{editingCourse ? "Update" : "Create"} Course</Button>
                    </Form>
                </Modal.Body>
            </Modal>
        </div>
    );
};

export default ManagerCourses;
