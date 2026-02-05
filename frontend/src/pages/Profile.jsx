import React from 'react';
import { useAuth } from '../context/AuthContext';

const Profile = () => {
    const { user, loading } = useAuth();

    if (loading) return <div>Loading...</div>;

    return (
        <div style={{ padding: '2rem', maxWidth: '600px', margin: '0 auto' }}>
            <h1 className="gradient-text" style={{ marginBottom: '2rem' }}>User Profile</h1>

            <div className="card glass">
                <div style={{ marginBottom: '1.5rem' }}>
                    <label style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Email</label>
                    <h3 style={{ marginTop: '0.2rem' }}>{user?.email}</h3>
                </div>

                <div style={{ marginBottom: '1.5rem' }}>
                    <label style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Company</label>
                    <h3 style={{ marginTop: '0.2rem' }}>{user?.companyName || 'N/A'}</h3>
                </div>

                <div>
                    <label style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Roles</label>
                    <div style={{ display: 'flex', gap: '0.5rem', marginTop: '0.5rem' }}>
                        {user?.roles?.map(role => (
                            <span key={role} style={{
                                background: 'hsla(var(--primary), 0.2)',
                                color: 'hsl(var(--primary))',
                                padding: '0.25rem 0.75rem',
                                borderRadius: '20px',
                                fontSize: '0.9rem'
                            }}>
                                {role}
                            </span>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Profile;
