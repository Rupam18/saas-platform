import React from 'react';
import { FiCheckCircle, FiClock, FiActivity, FiLayers } from 'react-icons/fi';

const MetricsCards = ({ data }) => {
    // data format: { totalTasks, completedTasks, pendingTasks, inProgressTasks }

    const metrics = [
        {
            label: 'Total Tasks',
            value: data.totalTasks || 0,
            icon: <FiLayers />,
            color: 'hsl(210, 100%, 50%)',
            bg: 'hsla(210, 100%, 50%, 0.1)'
        },
        {
            label: 'Completed',
            value: data.completedTasks || 0,
            icon: <FiCheckCircle />,
            color: 'hsl(140, 100%, 50%)',
            bg: 'hsla(140, 100%, 50%, 0.1)'
        },
        {
            label: 'In Progress',
            value: data.inProgressTasks || 0,
            icon: <FiActivity />,
            color: 'hsl(40, 100%, 50%)',
            bg: 'hsla(40, 100%, 50%, 0.1)'
        },
        {
            label: 'Pending',
            value: data.pendingTasks || 0,
            icon: <FiClock />,
            color: 'hsl(340, 100%, 50%)',
            bg: 'hsla(340, 100%, 50%, 0.1)'
        }
    ];

    return (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1.5rem', marginBottom: '2rem' }}>
            {metrics.map((m, i) => (
                <div key={i} className="card glass" style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                    <div style={{
                        padding: '1rem',
                        borderRadius: '12px',
                        background: m.bg,
                        color: m.color,
                        fontSize: '1.5rem',
                        display: 'flex'
                    }}>
                        {m.icon}
                    </div>
                    <div>
                        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginBottom: '0.2rem' }}>{m.label}</p>
                        <h2 style={{ fontSize: '1.8rem', fontWeight: 'bold' }}>{m.value}</h2>
                    </div>
                </div>
            ))}
        </div>
    );
};

export default MetricsCards;
