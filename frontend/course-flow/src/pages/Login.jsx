import React, { useState } from 'react';
import axios from 'axios';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as Yup from 'yup';
import { loginUser, googleLoginUser, loginOAuth2User } from '../api/auth';
import { useNavigate } from 'react-router-dom';
import { useGoogleLogin } from '@react-oauth/google';
import google from '../assets/img/google.jpg'

const validationSchema = Yup.object().shape({
    email: Yup.string().required('Email is mandatory').email('Invalid email'),
    password: Yup.string().required('Password is mandatory').min(8).max(100),
});

const Login = () => {
    const navigate = useNavigate();
    const [message, setMessage] = useState(null);
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
            setMessage('Success!');
            setTimeout(() => setMessage(null), 3000);
            navigate('/');
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
            setMessage(errorMessage);
            setTimeout(() => setMessage(null), 3000);
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
                setMessage('Google login successful!');
                setTimeout(() => setMessage(null), 3000);
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
                setMessage(errorMessage);
                setTimeout(() => setMessage(null), 3000);
            }
        },
        onError: (error) => {
            console.error("Google login error:", error);
            setMessage('Google login failed.');
            setTimeout(() => setMessage(null), 3000);
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
                            {message && (
                                <div className="mt-3 alert alert-danger">
                                    {message}
                                </div>
                            )}
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
        </div>
    );
};

export default Login;
