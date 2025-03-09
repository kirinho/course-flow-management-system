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
            setMessage('Error: Invalid token.');
            return;
        }

        const handleConfirmEmail = async () => {
            try {
                const response = await confirmEmail(token);

                if (response.status === 200) {
                    localStorage.setItem('token', response.data);
                    setStatus('success');
                    setMessage('Your account has been successfully verified!');
                    setTimeout(() => navigate('/'), 3000);
                }
            } catch (error) {
                setStatus('error');
                let errorMessage = 'Account verification error.';

                if (error.response) {
                    switch (error.response.status) {
                        case 400:
                            errorMessage = 'Error: The token does not exist.';
                            break;
                        case 409:
                            errorMessage = 'Error: The token has already been activated.';
                            break;
                        case 404:
                            errorMessage = 'Error: The token has expired.';
                            break;
                        default:
                            errorMessage = 'Error: Something went wrong. Please try again later.';
                    }
                } else {
                    errorMessage = 'No response was received from the server.';
                }
                setMessage(errorMessage);
            }
        };

        handleConfirmEmail();
    }, [searchParams, navigate]);

    return (
        <div className="d-flex align-items-start justify-content-center vh-100">
            <div className="container">
                <div className="row justify-content-center">
                    <div className="col-md-6">
                        <div className="text-center" style={{ transform: 'translateY(-1%)' }}>
                            <h2 className="mb-4">Email confirmation</h2>
                            {status === 'loading' && <p>Account confirmation...</p>}
                            {message && (
                                <div className="alert alert-info">
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

export default ConfirmEmail;
