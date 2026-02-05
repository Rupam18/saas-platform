import { createContext, useState, useEffect, useContext } from 'react';
import api from '../api/axios';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    // Check if user is logged in (verify token)
    useEffect(() => {
        const checkAuth = async () => {
            const token = localStorage.getItem('token');
            if (token) {
                try {
                    // Fetch user profile to ensure token is valid
                    // Ideally, backend should expose /users/me or verify endpoint
                    // We assume /users/me exists from our backend plan
                    const res = await api.get('/users/me');
                    setUser(res.data);
                } catch (err) {
                    console.error("Token invalid", err);
                    localStorage.removeItem('token');
                    setUser(null);
                }
            }
            setLoading(false);
        };
        checkAuth();
    }, []);

    const login = async (email, password) => {
        const res = await api.post('/auth/login', { email, password });
        const token = res.data; // The backend returns plain string token
        localStorage.setItem('token', token);

        // Fetch user details immediately
        const userRes = await api.get('/users/me');
        setUser(userRes.data);
        return true;
    };

    const register = async (userData) => {
        await api.post('/auth/register', userData);
        return true;
    };

    const logout = () => {
        localStorage.removeItem('token');
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, login, register, logout, loading }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
