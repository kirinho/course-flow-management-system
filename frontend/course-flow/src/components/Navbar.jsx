import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';

const Navbar = () => {
    const navigate = useNavigate();
    const [role, setRole] = useState(localStorage.getItem('role'));
    const token = localStorage.getItem('token');
    const isAuthenticated = !!token;

    useEffect(() => {
        if (!role && token) {
            axios.get('http://localhost:8080/users/me/role', {
                headers: { 'Authorization': `Bearer ${token}` }
            })
            .then(response => {
                localStorage.setItem('role', response.data);
                setRole(response.data);
            })
            .catch(error => console.error('Failed to fetch role', error));
        }
    }, [role, token]);

    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        navigate('/');
    };

    return (
        <div className="container-fluid">
            <div className="row border-top px-xl-5">
                <div className="col-lg-3 d-none d-lg-block">
                    <div className="col-lg-3">
                        <Link to="/" className="text-decoration-none">
                            <h1 className="m-0"><span className="text-primary">Course</span>Flow</h1>
                        </Link>
                    </div>
                </div>
                <div className="col-lg-9">
                    <nav className="navbar navbar-expand-lg bg-light navbar-light py-3 py-lg-0 px-0">
                        <Link to="/" className="text-decoration-none d-block d-lg-none">
                            <h1 className="m-0"><span className="text-primary">Course</span>Flow</h1>
                        </Link>
                        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarCollapse">
                            <span className="navbar-toggler-icon"></span>
                        </button>
                        <div className="collapse navbar-collapse justify-content-between" id="navbarCollapse">
                            <div className="navbar-nav py-0">
                                <Link className="nav-item nav-link active" to="/">Home</Link>
                                <Link className="nav-item nav-link" to="/about">About</Link>
                                <Link className="nav-item nav-link" to="/courses">Courses</Link>
                                <Link className="nav-item nav-link" to="/contact">Contact</Link>
                                {role === 'MANAGER' && (
                                    <Link className="nav-item nav-link" to="/manager/courses">Manage Courses</Link>
                                )}
                            </div>
                            {isAuthenticated ? (
                                <button className="btn btn-primary py-2 px-4 ml-auto d-none d-lg-block" onClick={handleLogout}>Logout</button>
                            ) : (
                                <div className="ml-auto d-flex">
                                    <Link className="btn btn-outline-primary py-2 px-4 me-2 d-none d-lg-block" to="/login">Sign in</Link>
                                    <Link className="btn btn-primary py-2 px-4 d-none d-lg-block" to="/register">Sign up</Link>
                                </div>
                            )}
                        </div>
                    </nav>
                </div>
            </div>
        </div>
    );
};

export default Navbar;
