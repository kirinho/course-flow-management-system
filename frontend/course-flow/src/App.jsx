import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Logout from './pages/Logout';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import ConfirmEmail from './pages/ConfirmEmail';
import Courses from './pages/courses/CoursesAll';
import CourseDetail from './pages/courses/CourseDetail';
import ManagerCourses from './pages/courses/manager/ManagerCourses';
import CourseForm from './pages/courses/manager/CourseForm';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import './assets/css/style.css';
import { useEffect } from 'react';

const App = () => {
    // useEffect(() => {
    //     const script = document.createElement('script');
    //     script.src = '/assets/js/main.js';
    //     script.async = true;
    //     document.body.appendChild(script);

    //     return () => {
    //         document.body.removeChild(script);
    //     };
    // }, []);

    return (
        <Router>
            <Navbar />
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/logout" element={<Logout />} />
                <Route path="/confirm-email" element={<ConfirmEmail />} />
                <Route path="/courses" element={<Courses />} />
                <Route path="/courses/:id" element={<CourseDetail />} />
                <Route path="/manager/courses" element={<ManagerCourses />} />
                <Route path="/manager/courses/add" element={<CourseForm />} />
                <Route path="/manager/courses/edit/:id" element={<CourseForm />} />
            </Routes>
            <Footer />
        </Router>
    );
};

export default App;
