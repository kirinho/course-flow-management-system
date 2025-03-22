import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import ConfirmEmail from './pages/ConfirmEmail';
import Courses from './pages/courses/CoursesAll';
import CourseDetail from './pages/courses/CourseDetail';
import CourseOverview from './pages/courses/CourseOverview';
import LessonOverview from './pages/courses/LessonOverview';
import AssignmentOverview from './pages/courses/AssignmentOverview';
import ManagerAssignmentOverview from './pages/courses/manager/ManagerAssignmentOverview';
import ManagerCourses from './pages/courses/manager/ManagerCourses';
import ManagerModules from './pages/courses/manager/ManagerModules';
import ManagerItems from './pages/courses/manager/ManagerItems';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import './assets/css/style.css';

const App = () => {
    return (
        <Router>
            <Navbar />
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/confirm-email" element={<ConfirmEmail />} />
                <Route path="/courses" element={<Courses />} />
                <Route path="/courses/:id" element={<CourseDetail />} />
                <Route path="/course/:id/overview" element={<CourseOverview />} />
                <Route path="/course/:courseId/lesson/:lessonId/overview" element={<LessonOverview />} />
                <Route path="/assignment/:assignmentId/overview" element={<AssignmentOverview />} />
                <Route path="/manager/assignment/:assignmentId/overview" element={<ManagerAssignmentOverview />} />
                <Route path="/manager/courses" element={<ManagerCourses />} />
                <Route path="/manager/courses/:courseId/modules" element={<ManagerModules />} />
                <Route path="/manager/courses/:courseId/modules/:moduleId/overview" element={<ManagerItems />} /> 
            </Routes>
            <Footer />
        </Router>
    );
};

export default App;
