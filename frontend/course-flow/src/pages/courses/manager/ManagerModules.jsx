import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate, useParams, Link } from "react-router-dom";
import { Button, Modal, Form, OverlayTrigger, Tooltip } from "react-bootstrap";
import { useForm } from "react-hook-form";
import { ToastContainer, toast, Bounce } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const ManagerModules = () => {
    const { courseId } = useParams();
    const [courseName, setCourseName] = useState("");
    const [modules, setModules] = useState([]);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [showModuleModal, setShowModuleModal] = useState(false);
    const [moduleToDelete, setModuleToDelete] = useState(null);
    const [editingModule, setEditingModule] = useState(null);
    const navigate = useNavigate();
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    const { register, handleSubmit, setValue, formState: { errors }, reset } = useForm();

    useEffect(() => {
        if (!token || role !== "MANAGER") return;
        axios.get(`http://localhost:8080/manager/courses/course/${courseId}`, {
            headers: { Authorization: `Bearer ${token}` }
        })
        .then(response => setCourseName(response.data))
        .catch((error) => {
            toast.error(`Failed to fetch course details: ${error.response?.data?.message || "Internal server error"}`, {
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
        });

        axios.get(`http://localhost:8080/manager/modules/${courseId}/all`, { headers: { Authorization: `Bearer ${token}` } })
            .then(response => setModules(response.data))
            .catch((error) => {
                toast.error(`Failed to fetch modules: ${error.response?.data?.message || "Internal server error"}`, {
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
            });
    }, [token, courseId]);

    const handleDelete = async () => {
        if (!moduleToDelete) return;
        try {
            await axios.delete(`http://localhost:8080/manager/modules/delete/${moduleToDelete.id}`, { headers: { Authorization: `Bearer ${token}` } });
            setModules(modules.filter(module => module.id !== moduleToDelete.id));
            setShowDeleteModal(false);
            toast.success('Module has been successfully deleted!', {
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
        } catch {
            toast.error('Failed to delete module.', {
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
    };

    const handleOpenModuleModal = (module = null) => {
        reset();
        setEditingModule(module);
        if (module) {
            setValue("name", module.name);
            setValue("description", module.description);
            setValue("position", module.position);
        }
        setShowModuleModal(true);
    };

    const onSubmit = async (data) => {
        try {
            const url = editingModule 
                ? `http://localhost:8080/manager/modules/${courseId}/update/${editingModule.id}` 
                : `http://localhost:8080/manager/modules/${courseId}/create`;
            
            const method = editingModule ? "PATCH" : "POST";
            
            await axios({
                method,
                url,
                data,
                headers: { Authorization: `Bearer ${token}`, "Content-Type": "application/json" }
            });
            setShowModuleModal(false);
            const successMessage = editingModule 
                ? 'Module has been updated successfully!' 
                : 'Module has been created successfully!';
            toast.success(successMessage, {
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
            const errorMessage = error.response && error.response.data && error.response.data.message
                ? error.response.data.message
                : 'Failed to submit module.';
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

    if (!token) {
        return (
            <div className="d-flex align-items-start justify-content-center min-vh-100" style={{ paddingTop: "3rem" }}>
                <h2>You need to be authenticated to view the manager modules.</h2>;
            </div>)
    }
    if (role !== "MANAGER") {
        return (
            <div className="d-flex align-items-start justify-content-center min-vh-100" style={{ paddingTop: "3rem" }}>
                <h2>You do not have permission to view this page.</h2>;
            </div>)
    }

    return (
        <div className="d-flex align-items-start justify-content-center min-vh-100" style={{ paddingTop: "3rem" }}>

            <div className="container mt-5">
                <div style={{ display: "flex", alignItems: "center" }}>
                    <Link to="/manager/courses" style={{ fontWeight: "bold", color: "#007bff", textDecoration: "none" }}>
                        Courses
                    </Link>
                    <span style={{ margin: "0 8px" }}>/</span>
                    <span style={{ color: "#6c757d" }}>Modules</span>
                </div>
                <h2 className="mb-4">Modules for Course - {courseName || "Loading..."}</h2>
                <ul className="list-group">
                    {modules.map(module => (
                        <li key={module.id} className="list-group-item d-flex justify-content-between align-items-center">
                            <Link to={`/manager/courses/${courseId}/modules/${module.id}/overview`} className="text-decoration-none">
                                {module.name}
                            </Link>
                            <div>
                                <Button variant="secondary" onClick={() => handleOpenModuleModal(module)}>Edit</Button>
                                <Button variant="danger" className="ms-2" onClick={() => { setModuleToDelete(module); setShowDeleteModal(true); }}>Delete</Button>
                            </div>
                        </li>
                    ))}
                </ul>
                <Button className="mt-4" onClick={() => handleOpenModuleModal()}>Add New Module</Button>

                <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)}>
                    <Modal.Header closeButton>
                        <Modal.Title>Confirm Delete</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        Are you sure you want to delete {moduleToDelete?.name}?
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={() => setShowDeleteModal(false)}>Cancel</Button>
                        <Button variant="danger" onClick={handleDelete}>Delete</Button>
                    </Modal.Footer>
                </Modal>

                <Modal show={showModuleModal} onHide={() => setShowModuleModal(false)}>
                    <Modal.Header closeButton>
                        <Modal.Title>{editingModule ? "Edit Module" : "Create Module"}</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <Form onSubmit={handleSubmit(onSubmit)}>
                            <Form.Group className="mb-3">
                                <Form.Label>Module Name</Form.Label>
                                <OverlayTrigger
                                    placement="right"
                                    overlay={<Tooltip id="tooltip-description">Enter up to 255 characters.</Tooltip>}
                                >
                                    <span style={{ borderRadius: "50%", backgroundColor: "#f0f0f0", padding: "4px 8px", cursor: "pointer", marginLeft: "8px" }}>
                                    i
                                    </span>
                                </OverlayTrigger>
                                <Form.Control type="text" {...register("name", { required: true })} />
                                {errors.name && <p className="text-danger">Module name is required</p>}
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>Description</Form.Label>
                                <OverlayTrigger
                                    placement="right"
                                    overlay={<Tooltip id="tooltip-description">Enter up to 255 characters.</Tooltip>}
                                >
                                    <span style={{ borderRadius: "50%", backgroundColor: "#f0f0f0", padding: "4px 8px", cursor: "pointer", marginLeft: "8px" }}>
                                    i
                                    </span>
                                </OverlayTrigger>
                                <Form.Control as="textarea" rows={4} {...register("description", { required: true })} />
                                {errors.description && <p className="text-danger">Description is required</p>}
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>Position</Form.Label>
                                <OverlayTrigger
                                    placement="right"
                                    overlay={<Tooltip id="tooltip-description">Enter positive number for a module position.</Tooltip>}
                                >
                                    <span style={{ borderRadius: "50%", backgroundColor: "#f0f0f0", padding: "4px 8px", cursor: "pointer", marginLeft: "8px" }}>
                                    i
                                    </span>
                                </OverlayTrigger>
                                <Form.Control type="number" {...register("position", { required: true })} />
                                {errors.position && <p className="text-danger">Position is required</p>}
                            </Form.Group>
                            <Button type="submit" className="w-100">{editingModule ? "Update" : "Create"} Module</Button>
                        </Form>
                    </Modal.Body>
                </Modal>
            </div>
            <ToastContainer />
        </div>
    );
};

export default ManagerModules;
