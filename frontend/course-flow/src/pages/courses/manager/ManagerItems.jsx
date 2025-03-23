import React, { useEffect, useState } from "react";
import axios from "axios";
import { Link, useNavigate, useParams } from "react-router-dom";
import { Button, Modal, Form, Alert } from "react-bootstrap";
import { useForm } from "react-hook-form";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";

const ManagerItems = () => {
    const { courseId, moduleId } = useParams();
    const [moduleName, setModuleName] = useState("");
    const [items, setItems] = useState([]);
    const [error, setError] = useState(null);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [showItemModal, setShowItemModal] = useState(false);
    const [itemToDelete, setItemToDelete] = useState(null);
    const [itemType, setItemType] = useState(null);
    const [editingItem, setEditingItem] = useState(null);
    const navigate = useNavigate();
    const token = localStorage.getItem("token");
    const { register, handleSubmit, setValue, formState: { errors }, reset } = useForm();
    const [attachments, setAttachments] = useState([]);

    useEffect(() => {
        if (!token) return;
        axios.get(`http://localhost:8080/manager/modules/module/${moduleId}`, {
            headers: { Authorization: `Bearer ${token}` }
        })
        .then(response => setModuleName(response.data))
        .catch(() => setError("Error fetching module details."));

        axios.get(`http://localhost:8080/manager/modules/overview/${moduleId}/all`, { headers: { Authorization: `Bearer ${token}` } })
            .then(response => setItems(response.data))
            .catch(() => setError("Error fetching items."));
    }, [token, moduleId]);

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

    const handleDelete = async (item) => {
        if (!item) return;
        let deleteUrl = '';
    
        if (item.type === 'LESSON') {
            deleteUrl = `http://localhost:8080/manager/lessons/delete/${item.id}`;
        } else if (item.type === 'ASSIGNMENT') {
            deleteUrl = `http://localhost:8080/manager/assignments/delete/${item.id}`;
        } else {
            setError("Invalid item type for deletion.");
            return;
        }
    
        try {
            await axios.delete(deleteUrl, {
                headers: { Authorization: `Bearer ${token}` }
            });
    
            setItems(items.filter(existingItem => existingItem.id !== item.id));
            setShowDeleteModal(false);
        } catch {
            setError("Failed to delete item.");
        }
    };        

    const handleOpenItemModal = (item = null, type = 'LESSON') => {
        reset();
        setEditingItem(item);
        setItemType(type);
    
        if (item) {
            setValue("title", item.title);
            setValue("description", item.description);
            setValue("position", item.position);
            setValue("content", item.content);
            setValue("dueDate", item.dueDate ? new Date(item.dueDate).toISOString().split('T')[0] : "");
            setValue("maxScore", item.maxScore);
        }
        setShowItemModal(true);
    };

    const onSubmit = async (data) => {
        try {
            const formData = new FormData();
            const itemBlob = new Blob([JSON.stringify(data)], { type: 'application/json' });
            formData.append("item", itemBlob, "item.json");
            attachments.forEach(file => formData.append("attachments", file));
    
            let url = '';
            let method = '';
    
            if (itemType === 'LESSON') {
                url = editingItem
                    ? `http://localhost:8080/manager/lessons/${moduleId}/lesson/update/${editingItem.id}`
                    : `http://localhost:8080/manager/lessons/${moduleId}/lesson/create`;
                method = editingItem ? "PATCH" : "POST";
            } else if (itemType === 'ASSIGNMENT') {
                url = editingItem
                    ? `http://localhost:8080/manager/assignments/${moduleId}/assignment/update/${editingItem.id}`
                    : `http://localhost:8080/manager/assignments/${moduleId}/assignment/create`;
                method = editingItem ? "PATCH" : "POST";
            }
    
            await axios({
                method,
                url,
                data: formData,
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'multipart/form-data',
                }
            });
    
            setShowItemModal(false);
            navigate(0);
        } catch (error) {
            console.error("Error submitting item:", error);
            setError("Failed to submit item.");
        }
    };

    const handleFileChange = (e) => {
        setAttachments([...e.target.files]);
    };

    if (!token) {
        return <h2>You need to be authenticated to view the items.</h2>;
    }

    return (
        <div className="d-flex align-items-start justify-content-center min-vh-100" style={{ paddingTop: "3rem" }}>

            <div className="container mt-5">
                <div style={{ display: "flex", alignItems: "center" }}>
                    <Link to="/manager/courses" style={{ fontWeight: "bold", color: "#007bff", textDecoration: "none" }}>
                    Courses
                    </Link>
                    <span style={{ margin: "0 8px" }}>/</span>
                    <Link to={`/manager/courses/${courseId}/modules`} style={{ fontWeight: "bold", color: "#007bff", textDecoration: "none" }}>
                    Modules
                    </Link>
                    <span style={{ margin: "0 8px" }}>/</span>
                    <span style={{ color: "#6c757d" }}>Items</span>
                </div>
                <h2 className="mb-4">Items for module - {moduleName || "Loading..."}</h2>
                {error && <Alert variant="danger">{error}</Alert>}
                <ul className="list-group">
                    {items.map(item => (
                        <li key={item.id} className="list-group-item d-flex justify-content-between align-items-start mb-3 shadow-sm">
                            <div className="w-100">
                                <h5 className="mb-2">{item.title}</h5>
                                <p className="text-muted">{item.description}</p>
        
                                {item.type === 'LESSON' ? (
                                    <>
                                        <p><strong>Content:</strong></p>
                                        <ReactMarkdown remarkPlugins={[remarkGfm]}>{item.content}</ReactMarkdown>
                                        <h3 className="text-dark">Attachments</h3>
                                        <ul className="list-unstyled">
                                            {item.attachments.length > 0 ? (
                                                item.attachments.map(attachment => (
                                                    <li key={attachment.id} className="d-flex align-items-center gap-3 mb-3">
                                                        <i className={`bi bi-${getFileIcon(attachment.fileType)} me-2`} style={{ fontSize: '24px' }} />
                                                        <Button
                                                            variant="link"
                                                            onClick={() => handleDownload(attachment.id)} // функція для завантаження
                                                            className="text-primary"
                                                        >
                                                            {attachment.fileName}
                                                        </Button>
                                                    </li>
                                                ))
                                            ) : (
                                                <p className="text-muted">No attachments available.</p>
                                            )}
                                        </ul>
                                    </>
                                ) : item.type === 'ASSIGNMENT' ? (
                                    <>
                                        <p><strong>Due Date:</strong> {new Date(item.dueDate).toLocaleDateString()}</p>
                                        <p><strong>Max Score:</strong> {item.maxScore}</p>
                                        <h3 className="text-dark">Attachments</h3>
                                        <ul className="list-unstyled">
                                            {item.attachments.length > 0 ? (
                                                item.attachments.map(attachment => (
                                                    <li key={attachment.id} className="d-flex align-items-center gap-3 mb-3">
                                                        <i className={`bi bi-${getFileIcon(attachment.fileType)} me-2`} style={{ fontSize: '24px' }} />
                                                        <Button
                                                            variant="link"
                                                            onClick={() => handleDownload(attachment.id)} // функція для завантаження
                                                            className="text-primary"
                                                        >
                                                            {attachment.fileName}
                                                        </Button>
                                                    </li>
                                                ))
                                            ) : (
                                                <p className="text-muted">No attachments available.</p>
                                            )}
                                        </ul>
                                    </>
                                ) : null}

                                <div className="d-flex justify-content-end">
                                    <Button variant="secondary" className="me-2" onClick={() => handleOpenItemModal(item, item.type)}>
                                        Edit
                                    </Button>
                                    <Button variant="danger" onClick={() => { setItemToDelete(item); setShowDeleteModal(true); }}>
                                        Delete
                                    </Button>
                                </div>
                            </div>
                        </li>
                    ))}
                </ul>
        
                <Button className="mt-4" onClick={() => handleOpenItemModal(null, 'LESSON')}>Add Lesson</Button>
                <Button className="mt-4 ms-2" onClick={() => handleOpenItemModal(null, 'ASSIGNMENT')}>Add Assignment</Button>
        
                <Modal show={showItemModal} onHide={() => setShowItemModal(false)}>
                    <Modal.Header closeButton>
                        <Modal.Title>{editingItem ? "Edit Item" : "Create Item"}</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <Form onSubmit={handleSubmit(onSubmit)}>
                            <Form.Group className="mb-3">
                                <Form.Label>Item Title</Form.Label>
                                <Form.Control type="text" {...register("title", { required: true })} />
                                {errors.title && <p className="text-danger">Item title is required</p>}
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>Description</Form.Label>
                                <Form.Control as="textarea" rows={4} {...register("description", { required: true })} />
                                {errors.description && <p className="text-danger">Description is required</p>}
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>Position</Form.Label>
                                <Form.Control type="number" {...register("position", { required: true })} />
                                {errors.position && <p className="text-danger">Position is required</p>}
                            </Form.Group>
        
                            {/* Якщо це урок (LESSON), додаємо поле для content */}
                            {itemType === 'LESSON' && (
                                <Form.Group className="mb-3">
                                    <Form.Label>Content</Form.Label>
                                    <Form.Control as="textarea" rows={4} {...register("content", { required: true })} />
                                    {errors.content && <p className="text-danger">Content is required</p>}
                                </Form.Group>
                            )}
        
                            {/* Якщо це завдання (ASSIGNMENT), додаємо поля для dueDate і maxScore */}
                            {itemType === 'ASSIGNMENT' && (
                                <>
                                    <Form.Group className="mb-3">
                                        <Form.Label>Due Date</Form.Label>
                                        <Form.Control type="date" {...register("dueDate", { required: true })} />
                                        {errors.dueDate && <p className="text-danger">Due date is required</p>}
                                    </Form.Group>
                                    <Form.Group className="mb-3">
                                        <Form.Label>Max Score</Form.Label>
                                        <Form.Control type="number" {...register("maxScore", { required: true })} />
                                        {errors.maxScore && <p className="text-danger">Max score is required</p>}
                                    </Form.Group>
                                </>
                            )}
        
                            <Form.Group className="mb-3">
                                <Form.Label>Attachments</Form.Label>
                                <Form.Control type="file" multiple onChange={handleFileChange} />
                                {attachments.length > 0 && (
                                    <ul>
                                        {Array.from(attachments).map((file, idx) => (
                                            <li key={idx}>{file.name}</li>
                                        ))}
                                    </ul>
                                )}
                            </Form.Group>
                            <Button type="submit" className="w-100">{editingItem ? "Update" : "Create"} Item</Button>
                        </Form>
                    </Modal.Body>
                </Modal>

                <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)}>
                    <Modal.Header closeButton>
                        <Modal.Title>Confirm Deletion</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <p>Are you sure you want to delete <strong>{itemToDelete?.title}</strong>?</p>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={() => setShowDeleteModal(false)}>Cancel</Button>
                        <Button variant="danger" onClick={() => handleDelete(itemToDelete)}>Delete</Button>
                    </Modal.Footer>
                </Modal>
            </div>
        </div>
    );
};

export default ManagerItems;
