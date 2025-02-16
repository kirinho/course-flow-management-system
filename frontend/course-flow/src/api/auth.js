import axios from 'axios';

const API_URL = 'http://localhost:8080/auth';
const API_OAUTH2_URL = 'http://localhost:8082/auth';

export const registerUser = async (data) => {
    return await axios.post(`${API_URL}/signup`, data);
};

export const loginUser = async (data) => {
    return await axios.post(`${API_URL}/login`, data);
};

export const confirmEmail = async (token) => {
    return await axios.post(`${API_URL}/confirm-email?token=${token}`);
};

export const googleLoginUser = async (accessToken) => {
    const googleUser = await axios.get('https://www.googleapis.com/oauth2/v1/userinfo', {
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    });

    const userData = {
        fullName: googleUser.data.name,
        email: googleUser.data.email
    };
    return userData;
};

export const loginOAuth2User = async (data) => {
    return await axios.post(`${API_OAUTH2_URL}/login-oauth2`, data);
};
