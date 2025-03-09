import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as Yup from 'yup';
import { registerUser } from '../api/auth';
import { useNavigate } from 'react-router-dom';

const validationSchema = Yup.object().shape({
    fullName: Yup.string().required('Full name is mandatory').min(2).max(100),
    email: Yup.string().required('Email is mandatory').email('Email is invalid'),
    password: Yup.string().required('Password is mandatory').min(8).max(100),
});

const Register = () => {
    const navigate = useNavigate();
    const [message, setMessage] = useState(null);
    const { register, handleSubmit, formState: { errors } } = useForm({
        resolver: yupResolver(validationSchema)
    });

    const onSubmit = async (data) => {
        try {
            await registerUser(data);
            setMessage('Registration is successful, check your email to verify your account');
            setTimeout(() => setMessage(null), 10000);
            navigate('/login');
        } catch (error) {
            let errorMessage = 'Registration error, please try again.';
            if (error.response && error.response.status === 409) {
                errorMessage = 'A user with this email already exists.';
            }
            setMessage(errorMessage);
            setTimeout(() => setMessage(null), 3000);
        }
    };

    return (
        <div className="d-flex align-items-center justify-content-center vh-100">
            <div className="container">
                <div className="row justify-content-center">
                    <div className="col-md-5">
                        <div className="p-4 bg-white rounded shadow-sm" style={{ paddingTop: "2rem", paddingBottom: "2rem" }}>
                            <h2 className="mb-4 text-center">Register</h2>
                            <form onSubmit={handleSubmit(onSubmit)}>
                                <div className="mb-3">
                                    <input type="text" className="form-control" placeholder="Full Name" {...register('fullName')} />
                                    <p className="text-danger">{errors.fullName?.message}</p>
                                </div>
                                <div className="mb-3">
                                    <input type="email" className="form-control" placeholder="Email" {...register('email')} />
                                    <p className="text-danger">{errors.email?.message}</p>
                                </div>
                                <div className="mb-3">
                                    <input type="password" className="form-control" placeholder="Password" {...register('password')} />
                                    <p className="text-danger">{errors.password?.message}</p>
                                </div>
                                <button type="submit" className="btn btn-primary w-100 py-2">Register</button>
                            </form>
                            {message && (
                                <div className="mt-3 alert alert-danger">
                                    {message}
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );    
};

export default Register;
