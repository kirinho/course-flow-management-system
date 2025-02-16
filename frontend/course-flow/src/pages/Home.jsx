import React from 'react';

const Home = () => {
    const isAuthenticated = localStorage.getItem('token');

    return (
        <div>
            <h1>Home Page</h1>
            {isAuthenticated ? (
                <p>Welcome back, authenticated user!</p>
            ) : (
                <p>Welcome to the authentication system!</p>
            )}
        </div>
    );
};

export default Home;
