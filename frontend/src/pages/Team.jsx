import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import Button from '../components/Button';
import Input from '../components/Input';
import { FiUserPlus, FiMail, FiShield, FiTrash2 } from 'react-icons/fi';
import { useAuth } from '../context/AuthContext';

const Team = () => {
    const { user } = useAuth();
    const [users, setUsers] = useState([]);
    const [inviting, setInviting] = useState(false);
    const [inviteEmail, setInviteEmail] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    useEffect(() => {
        fetchTeam();
    }, []);

    const fetchTeam = async () => {
        try {
            const res = await api.get('/users');
            setUsers(res.data);
        } catch (err) {
            console.error("Failed to fetch team", err);
        } finally {
            setLoading(false);
        }
    };

    const handleInvite = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        try {
            await api.post('/invitations', { email: inviteEmail, role: 'USER' });
            setSuccess(`Invitation sent to ${inviteEmail}`);
            setInviteEmail('');
            setInviting(false);
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to send invitation');
        }
    };

    return (
        <div style={{ padding: '2rem', maxWidth: '1200px', margin: '0 auto' }}>
            <header style={{ marginBottom: '2rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                    <h1 className="gradient-text">Team Management</h1>
                    <p style={{ color: 'var(--text-muted)' }}>Collaborate with your organization</p>
                </div>
                {user?.roles?.includes('ADMIN') && (
                    <Button onClick={() => setInviting(!inviting)}>
                        <FiUserPlus /> Invite Member
                    </Button>
                )}
            </header>

            {/* Invite Form */}
            {inviting && (
                <div className="card glass" style={{ marginBottom: '2rem', maxWidth: '600px' }}>
                    <h3 style={{ marginBottom: '1rem' }}>Invite New Member</h3>
                    {error && <p style={{ color: '#ff4444', marginBottom: '1rem' }}>{error}</p>}
                    {success && <p style={{ color: '#00C851', marginBottom: '1rem' }}>{success}</p>}

                    <form onSubmit={handleInvite} style={{ display: 'flex', gap: '1rem' }}>
                        <Input
                            type="email"
                            placeholder="colleague@example.com"
                            value={inviteEmail}
                            onChange={(e) => setInviteEmail(e.target.value)}
                            style={{ marginBottom: 0, flex: 1 }}
                            required
                        />
                        <Button type="submit">Send Invite</Button>
                    </form>
                </div>
            )}

            {/* Team List */}
            <div className="card glass">
                <h3 style={{ marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <FiShield /> Team Members ({users.length})
                </h3>

                {loading ? (
                    <p>Loading...</p>
                ) : (
                    <div style={{ display: 'grid', gap: '1rem' }}>
                        {users.map(user => (
                            <div key={user.id} style={{
                                display: 'flex',
                                justifyContent: 'space-between',
                                alignItems: 'center',
                                padding: '1rem',
                                background: 'rgba(255,255,255,0.03)',
                                borderRadius: '8px'
                            }}>
                                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                                    <div style={{
                                        width: '40px', height: '40px',
                                        borderRadius: '50%',
                                        background: 'linear-gradient(135deg, var(--primary), var(--secondary))',
                                        display: 'flex', alignItems: 'center', justifyContent: 'center',
                                        fontWeight: 'bold', fontSize: '1.2rem'
                                    }}>
                                        {user.email.charAt(0).toUpperCase()}
                                    </div>
                                    <div>
                                        <p style={{ fontWeight: '500' }}>{user.email}</p>
                                        <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                                            {user.roles?.join(', ') || 'USER'}
                                        </p>
                                    </div>
                                </div>
                                <span style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Active</span>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default Team;
