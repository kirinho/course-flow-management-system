import React, { useState } from 'react';
import axios from 'axios';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as Yup from 'yup';
import { loginUser, googleLoginUser, loginOAuth2User } from '../api/auth';
import { useNavigate } from 'react-router-dom';
import { useGoogleLogin } from '@react-oauth/google';
import google from '../assets/img/google.jpg'
import { ToastContainer, toast, Bounce } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const validationSchema = Yup.object().shape({
    email: Yup.string().required('Email is mandatory').email('Invalid email'),
    password: Yup.string().required('Password is mandatory').min(8).max(100),
});

const Login = () => {
    const navigate = useNavigate();
    const { register, handleSubmit, formState: { errors } } = useForm({
        resolver: yupResolver(validationSchema)
    });

    const onSubmit = async (data) => {
        try {
            const response = await loginUser(data);
            localStorage.setItem('token', response.data);
            const roleResponse = await axios.get('http://localhost:8080/users/me/role', {
                headers: { 'Authorization': `Bearer ${response.data}` }
            });
            localStorage.setItem('role', roleResponse.data);
            toast.success('Success! Redirecting to the home page', {
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
                navigate('/');
            }, 1500);
        } catch (error) {
            let errorMessage = 'There was an error. Please try again.';
            if (error.response) {
                switch (error.response.status) {
                    case 401:
                        errorMessage = 'Incorrect email address or password';
                        break;
                    case 403:
                        errorMessage = 'You do not have the required permissions.';
                        break;
                    default:
                        errorMessage = `Error: ${error.response.status} - ${error.response.data?.message || 'Unknown error'}`;
                }
            } else if (error.request) {
                errorMessage = 'No response was received from the server.';
            } else {
                errorMessage = 'Request for an installation error.';
            }
            toast.error(errorMessage, {
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

    const googleLogin = useGoogleLogin({
        onSuccess: async (credentialResponse) => {
            try {
                const googleUser = await googleLoginUser(credentialResponse.access_token);
                const response = await loginOAuth2User(googleUser);
                localStorage.setItem('token', response.data);
                const roleResponse = await axios.get('http://localhost:8080/users/me/role', {
                    headers: { 'Authorization': `Bearer ${response.data}` }
                });
                localStorage.setItem('role', roleResponse.data);
                navigate('/');
            } catch (error) {
                let errorMessage = 'An error occurred while signing in to Google.';
                if (error.response) {
                    switch (error.response.status) {
                        case 400:
                            errorMessage = 'Invalid request. Check the input data.';
                            break;
                        default:
                            errorMessage = `Google Login Error: ${error.response.status} - ${error.response.data?.message || 'Unknown error'}`;
                    }
                } else if (error.request) {
                    errorMessage = 'No response was received from the server.';
                } else {
                    errorMessage = 'Error setting up Google login.';
                }
                toast.error(errorMessage, {
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
        },
        onError: (error) => {
            toast.error(error, {
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
        },
    });

    return (
        <div className="d-flex align-items-center justify-content-center vh-100">
            <div className="container">
                <div className="row justify-content-center">
                    <div className="col-md-5">
                        <div className="p-4 bg-white rounded shadow-sm" style={{ paddingTop: "2rem", paddingBottom: "2rem" }}> {/* Зменшив відступи */}
                            <h2 className="mb-4 text-center">Login</h2>
                            <form onSubmit={handleSubmit(onSubmit)}>
                                <div className="mb-3">
                                    <input type="email" className="form-control" placeholder="Email" {...register('email')} />
                                    <p className="text-danger">{errors.email?.message}</p>
                                </div>
                                <div className="mb-3">
                                    <input type="password" className="form-control" placeholder="Password" {...register('password')} />
                                    <p className="text-danger">{errors.password?.message}</p>
                                </div>
                                <button type="submit" className="btn btn-primary w-100 py-2">Login</button>
                            </form>
                            <button onClick={() => googleLogin()} className="btn btn-light w-100 py-2 d-flex align-items-center justify-content-center border mt-3">
                                <img src={google}
                                     alt="Google Logo" 
                                     className="me-2" 
                                     style={{ width: "20px", height: "20px" }} /> 
                                Sign in with Google
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            <ToastContainer />
        </div>
    );
};

export default Login;
