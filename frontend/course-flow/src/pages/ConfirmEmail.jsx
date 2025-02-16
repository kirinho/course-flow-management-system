import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { confirmEmail } from '../api/auth';

const ConfirmEmail = () => {
    const [message, setMessage] = useState(null);
    const [status, setStatus] = useState('loading');
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    useEffect(() => {
        const token = searchParams.get('token');
        if (!token) {
            setStatus('error');
            setMessage('Помилка: Невірний токен.');
            return;
        }

        const handleConfirmEmail = async () => {
            try {
                const response = await confirmEmail(token);

                if (response.status === 200) {
                    localStorage.setItem('token', response.data);
                    setStatus('success');
                    setMessage('Акаунт успішно підтверджено!');
                    setTimeout(() => navigate('/'), 3000);
                }
            } catch (error) {
                setStatus('error');
                let errorMessage = 'Помилка підтвердження акаунту.';

                if (error.response) {
                    switch (error.response.status) {
                        case 400:
                            errorMessage = 'Помилка: Токен не існує.';
                            break;
                        case 409:
                            errorMessage = 'Помилка: Токен вже активовано.';
                            break;
                        case 404:
                            errorMessage = 'Помилка: Термін дії токена вийшов.';
                            break;
                        default:
                            errorMessage = 'Помилка: Щось пішло не так. Спробуйте пізніше.';
                    }
                } else {
                    errorMessage = 'Помилка з\'єднання. Перевірте інтернет.';
                }
                setMessage(errorMessage);
            }
        };

        handleConfirmEmail();
    }, [searchParams, navigate]);

    return (
        <div className="container">
            <div className="row justify-content-center">
                <div className="col-md-6">
                    <div>
                        <h2 className="mb-4">Підтвердження Email</h2>
                        {status === 'loading' && <p>Підтвердження акаунту...</p>}
                        {message && (
                            <div className="alert alert-info">
                                {message}
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ConfirmEmail;
