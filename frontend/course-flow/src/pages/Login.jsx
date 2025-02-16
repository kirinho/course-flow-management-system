import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as Yup from 'yup';
import { loginUser, googleLoginUser, loginOAuth2User } from '../api/auth';
import { useNavigate } from 'react-router-dom';
import { useGoogleLogin } from '@react-oauth/google';

const validationSchema = Yup.object().shape({
    email: Yup.string().required('Email повинен бути заповнений').email('Invalid email'),
    password: Yup.string().required('Пароль повинен бути заповнений').min(8).max(100),
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
            setMessage('Success!');
            setTimeout(() => setMessage(null), 3000);
            navigate('/');
        } catch (error) {
            let errorMessage = 'Виникла помилка. Будь ласка, спробуйте ще раз.';
            if (error.response) {
                switch (error.response.status) {
                    case 401:
                        errorMessage = 'Неправильна адреса електронної пошти або пароль';
                        break;
                    case 403:
                        errorMessage = 'Заборонено. У вас немає необхідних дозволів.';
                        break;
                    default:
                        errorMessage = `Помилка: ${error.response.status} - ${error.response.data?.message || 'Unknown error'}`;
                }
            } else if (error.request) {
                errorMessage = 'Відповідь від сервера не отримано.';
            } else {
                errorMessage = 'Запит на помилку встановлення.';
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
                setMessage('Google login successful!');
                setTimeout(() => setMessage(null), 3000);
                navigate('/');
            } catch (error) {
                let errorMessage = 'Виникла помилка під час входу в Google.';
                if (error.response) {
                    switch (error.response.status) {
                        case 400:
                            errorMessage = 'Неправильний запит. Перевірте вхідні дані.';
                            break;
                        default:
                            errorMessage = `Google Login Error: ${error.response.status} - ${error.response.data?.message || 'Unknown error'}`;
                    }
                } else if (error.request) {
                    errorMessage = 'Відповідь від сервера Google не отримано.';
                } else {
                    errorMessage = 'Помилка налаштування для входу в Google.';
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
        <div className="container">
            <div className="row justify-content-center">
                <div className="col-md-6">
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <h2 className="mb-4">Login</h2>
                        <div className="mb-3">
                            <input type="email" className="form-control" placeholder="Email" {...register('email')} />
                            <p className="text-danger">{errors.email?.message}</p>
                        </div>
                        <div className="mb-3">
                            <input type="password" className="form-control" placeholder="Password" {...register('password')} />
                            <p className="text-danger">{errors.password?.message}</p>
                        </div>
                        <button type="submit" className="btn btn-primary">Login</button>
                        {message && (
                            <div className="mt-3 alert alert-danger">
                                {message}
                            </div>
                        )}
                    </form>
                    <button onClick={() => googleLogin()} className="btn btn-danger mt-3">
                            Sign in with Google
                    </button>
                </div>
            </div>
        </div>
    );
};

export default Login;
