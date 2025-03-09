import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import { Form, Button, Alert } from "react-bootstrap";

const CourseForm = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [message, setMessage] = useState(null);
    const [previewImage, setPreviewImage] = useState(null);
    const { register, handleSubmit, setValue, formState: { errors } } = useForm();
    const token = localStorage.getItem("token");

    useEffect(() => {
        if (id) {
            fetchCourseDetails();
        }
    }, [id]);

    const fetchCourseDetails = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/courses/course/${id}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            const { name, description, imageBase64 } = response.data;
            setValue("name", name);
            setValue("description", description);
            if (imageBase64) {
                setPreviewImage(`data:image/jpeg;base64,${imageBase64}`);
            }
        } catch (error) {
            setMessage("Failed to fetch course details.");
        }
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
            if (data.name) formData.append("name", data.name);
            if (data.description) formData.append("description", data.description);
            if (data.image && data.image[0]) {
                formData.append("image", data.image[0]);
            }
    
            const url = id 
                ? `http://localhost:8080/courses/course/update/${id}` 
                : "http://localhost:8080/courses/course/create";
            
            const method = id ? "PATCH" : "POST";
    
            await axios({
                method,
                url,
                data: formData,
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "multipart/form-data"
                }
            });
    
            setMessage(id ? "Course updated successfully!" : "Course created successfully!");
            navigate("/manager/courses");
        } catch (error) {
            setMessage("Failed to submit course.");
        }
    };    

    return (
        <div className="container mt-5">
            <h2>{id ? "Edit Course" : "Create Course"}</h2>
            {message && <Alert variant="info">{message}</Alert>}
            <Form onSubmit={handleSubmit(onSubmit)} encType="multipart/form-data">
                <Form.Group>
                    <Form.Control type="text" placeholder="Course Name" {...register("name", { required: true })} />
                    {errors.name && <p className="text-danger">Course name is required</p>}
                </Form.Group>
                <Form.Group>
                    <Form.Control as="textarea" placeholder="Description" {...register("description", { required: true })} />
                    {errors.description && <p className="text-danger">Description is required</p>}
                </Form.Group>
                <Form.Group>
                    {previewImage && <img src={previewImage} alt="Preview" className="img-fluid mb-2" style={{ maxWidth: "200px" }} />}
                    <Form.Control type="file" {...register("image")} onChange={onFileChange} />
                </Form.Group>
                <Button type="submit">{id ? "Update" : "Create"} Course</Button>
            </Form>
        </div>
    );
};

export default CourseForm;
