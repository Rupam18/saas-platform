import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Input from '../components/Input';
import Button from '../components/Button';
import '../styles/components.css';

const Landing = () => {
    const [isLogin, setIsLogin] = useState(true);
    const [formData, setFormData] = useState({ email: '', password: '', companyName: '', role: 'USER' });
    const [error, setError] = useState('');
    const { login, register } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        try {
            if (isLogin) {
                await login(formData.email, formData.password);
            } else {
                await register(formData);
                // Auto login or ask to login? Let's just switch to login or auto-login
                await login(formData.email, formData.password);
            }
            navigate('/dashboard');
        } catch (err) {
            console.error(err);
            if (err.response && err.response.data) {
                // If validation error (Map), show first error or generic message
                const data = err.response.data;
                if (typeof data === 'object' && !data.message && !data.error) {
                    // Assert it's a map of field errors
                    const firstError = Object.values(data)[0];
                    setError(firstError || 'Validation failed');
                } else {
                    setError(data.message || data.error || 'Authentication failed. Check credentials.');
                }
            } else {
                setError('Authentication failed. Check credentials.');
            }
        }
    };

    return (
        <div style={{
            minHeight: 'calc(100vh - 80px)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '2rem'
        }}>
            <div className="card glass" style={{ maxWidth: '450px', width: '100%' }}>
                <h1 className="gradient-text" style={{ textAlign: 'center', marginBottom: '2rem' }}>
                    {isLogin ? 'Welcome Back' : 'Get Started'}
                </h1>

                <form onSubmit={handleSubmit}>
                    {!isLogin && (
                        <>
                            <Input
                                label="Company Name"
                                name="companyName"
                                value={formData.companyName}
                                onChange={handleChange}
                            />
                            {/* Simplified role selection for demo */}
                            <div style={{ marginBottom: '1rem' }}>
                                <label style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginRight: '1rem' }}>Role:</label>
                                <select
                                    name="role"
                                    value={formData.role}
                                    onChange={handleChange}
                                    style={{ padding: '0.5rem', borderRadius: '4px' }}
                                >
                                    <option value="USER">User</option>
                                    <option value="ADMIN">Admin</option>
                                </select>
                            </div>
                        </>
                    )}

                    <Input
                        label="Email"
                        name="email"
                        type="email"
                        value={formData.email}
                        onChange={handleChange}
                    />
                    <Input
                        label="Password"
                        name="password"
                        type="password"
                        value={formData.password}
                        onChange={handleChange}
                    />

                    {error && <p style={{ color: '#ff4d4d', marginBottom: '1rem', fontSize: '0.9rem' }}>{error}</p>}

                    <Button type="submit" style={{ width: '100%' }}>
                        {isLogin ? 'Login' : 'create Account'}
                    </Button>
                </form>

                <p style={{ textAlign: 'center', marginTop: '1.5rem', color: 'hsl(var(--text-muted))' }}>
                    {isLogin ? "Don't have an account? " : "Already have an account? "}
                    <span
                        style={{ color: 'hsl(var(--primary))', cursor: 'pointer', fontWeight: 'bold' }}
                        onClick={() => setIsLogin(!isLogin)}
                    >
                        {isLogin ? 'Register' : 'Login'}
                    </span>
                </p>
            </div>
        </div>
    );
};

export default Landing;
