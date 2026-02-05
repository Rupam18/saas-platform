import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Button from './Button';
import { FiLogOut, FiUser } from 'react-icons/fi'; // Requires react-icons

const Navbar = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    return (
        <nav style={{
            padding: '1rem 2rem',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            background: 'hsla(var(--bg-secondary), 0.8)',
            backdropFilter: 'blur(10px)',
            borderBottom: '1px solid var(--border)',
            position: 'sticky',
            top: 0,
            zIndex: 100
        }}>
            <Link to="/" style={{ textDecoration: 'none' }}>
                <h2 className="gradient-text" style={{ fontSize: '1.5rem', fontWeight: '800' }}>SaaS Platform</h2>
            </Link>

            <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
                {user ? (
                    <>
                        <Link to="/dashboard" style={{ color: 'var(--text-main)', textDecoration: 'none' }}>Dashboard</Link>
                        <Link to="/team" style={{ color: 'var(--text-main)', textDecoration: 'none' }}>Team</Link>
                        <Link to="/profile">
                            <Button variant="outline" style={{ padding: '0.5rem' }}>
                                <FiUser />
                            </Button>
                        </Link>
                        <Button variant="outline" onClick={handleLogout}>
                            <FiLogOut /> Logout
                        </Button>
                    </>
                ) : (
                    <Link to="/">
                        {/* Login/Register handled in Hero usually, or separate page. 
                 But for now "Home" is the Landing/Login page. */}
                    </Link>
                )}
            </div>
        </nav>
    );
};

export default Navbar;
