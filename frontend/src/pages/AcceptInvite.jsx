import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import Button from '../components/Button';
import Input from '../components/Input';
import { useAuth } from '../context/AuthContext';

const AcceptInvite = () => {
    const [searchParams] = useSearchParams();
    const token = searchParams.get('token');
    const navigate = useNavigate();
    const { login } = useAuth(); // We might auto-login after register, or just redirect

    const [email, setEmail] = useState(''); // Could pre-fill from backend if we had an endpoint to validate token first
    const [password, setPassword] = useState('');
    const [name, setName] = useState(''); // Optional if we added Name field, but we only have email/password in RegisterRequest for now
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [inviteDetails, setInviteDetails] = useState(null);

    useEffect(() => {
        if (!token) {
            setError("Missing invitation token.");
            return;
        }
        // Optional: Verify token and get email
        fetchInviteDetails();
    }, [token]);

    const fetchInviteDetails = async () => {
        try {
            const res = await api.get(`/invitations/${token}`);
            setInviteDetails(res.data);
            setEmail(res.data.email); // Pre-fill email
        } catch (err) {
            setError("Invalid or expired invitation.");
        }
    };

    const handleRegister = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            // Updated RegisterRequest payload structure
            await api.post('/auth/register', {
                email,
                password,
                companyName: "Joined via Invite", // Dummy value, backend ignores it for invites
                invitationToken: token
            });

            // Auto login or redirect to login
            // For simplicity, let's redirect to login with a success message or try to login immediately
            navigate('/?message=Registered successfully! Please login.');
        } catch (err) {
            setError(err.response?.data?.message || "Registration failed");
        } finally {
            setLoading(false);
        }
    };

    if (error && !inviteDetails) {
        return (
            <div style={{ padding: '4rem', textAlign: 'center', color: 'white' }}>
                <h2>{error}</h2>
                <Button onClick={() => navigate('/')} style={{ marginTop: '1rem' }}>Go Home</Button>
            </div>
        );
    }

    return (
        <div style={{
            minHeight: '100vh',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '2rem',
            background: 'radial-gradient(circle at top right, #2a2a40, #1a1a2e)'
        }}>
            <div className="card glass" style={{ width: '100%', maxWidth: '400px', padding: '2rem' }}>
                <h2 className="gradient-text" style={{ textAlign: 'center', marginBottom: '1rem' }}>Accept Invitation</h2>
                <p style={{ textAlign: 'center', color: 'var(--text-muted)', marginBottom: '2rem' }}>
                    You have been invited to join <strong>saas-platform</strong>.
                </p>

                <form onSubmit={handleRegister}>
                    <Input
                        label="Email"
                        type="email"
                        value={email}
                        readOnly // Lock email to the invited one
                        style={{ opacity: 0.7 }}
                    />
                    <Input
                        label="Create Password"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />

                    {error && <p style={{ color: '#ff4444', fontSize: '0.9rem', marginBottom: '1rem' }}>{error}</p>}

                    <Button type="submit" style={{ width: '100%' }} disabled={loading}>
                        {loading ? 'Joining...' : 'Join Team'}
                    </Button>
                </form>
            </div>
        </div>
    );
};

export default AcceptInvite;
