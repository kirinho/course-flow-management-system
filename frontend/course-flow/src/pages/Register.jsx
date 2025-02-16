import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as Yup from 'yup';
import { registerUser } from '../api/auth';
import { useNavigate } from 'react-router-dom';

const validationSchema = Yup.object().shape({
    fullName: Yup.string().required('Повне ім\'я повинно бути заповнене').min(2).max(100),
    email: Yup.string().required('Email повинен бути заповнений').email('Email is invalid'),
    password: Yup.string().required('Пароль повинен бути заповнений').min(8).max(100),
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
            setMessage('Реєстрація успішна, перевірте свою пошту для верифікації акаунту');
            setTimeout(() => setMessage(null), 10000);
            navigate('/login');
        } catch (error) {
            let errorMessage = 'Помилка реєстрації, спробуйте ще раз.';
            if (error.response && error.response.status === 409) {
                errorMessage = 'Користувач з таким імейлом вже існує.';
            }

            setMessage(errorMessage);
            setTimeout(() => setMessage(null), 3000);
        }
    };

    return (
        <div className="container">
            <div className="row justify-content-center">
                <div className="col-md-6">
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <h2 className="mb-4">Register</h2>
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
                        <button type="submit" className="btn btn-primary">Register</button>
                        {message && (
                            <div className="mt-3 alert alert-danger">
                                {message}
                            </div>
                        )}
                    </form>
                </div>
            </div>
        </div>
    );
};

export default Register;
