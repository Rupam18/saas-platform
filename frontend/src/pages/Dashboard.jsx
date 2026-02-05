import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import Button from '../components/Button';
import Input from '../components/Input';
import MetricsCards from '../components/MetricsCards';
import { FiPlus, FiTrash2, FiCheckCircle, FiCircle } from 'react-icons/fi';
import { PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Legend } from 'recharts';

const Dashboard = () => {
    const { user } = useAuth();
    const [tasks, setTasks] = useState([]);
    const [stats, setStats] = useState({});
    const [newTaskTitle, setNewTaskTitle] = useState('');
    const [newTaskStatus, setNewTaskStatus] = useState('PENDING');
    const [loading, setLoading] = useState(true);

    // Pagination & Filter State
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [titleFilter, setTitleFilter] = useState('');
    const [statusFilter, setStatusFilter] = useState('');

    const fetchTasks = async () => {
        setLoading(true);
        try {
            const params = {
                page,
                size: 5, // Default page size
                sort: 'createdAt,desc'
            };
            if (titleFilter) params.title = titleFilter;
            if (statusFilter) params.status = statusFilter;

            const res = await api.get('/tasks', { params });
            // Handle both Page<Task> (backend Phase 4) and List<Task> (backend Phase 3 fallback)
            if (res.data.content) {
                setTasks(res.data.content);
                setTotalPages(res.data.totalPages);
            } else {
                setTasks(res.data); // Fallback if backend not paginated yet (though it should be)
                setTotalPages(1);
            }
        } catch (err) {
            console.error("Failed to fetch tasks", err);
        } finally {
            setLoading(false);
        }
    };

    const fetchAnalytics = async () => {
        try {
            const res = await api.get('/analytics/tasks');
            setStats(res.data);
        } catch (err) {
            console.error("Failed to fetch analytics", err);
        }
    };

    // Debounce search or fetch on dependency change
    useEffect(() => {
        fetchTasks();
    }, [page, statusFilter]); // Fetch when page or status changes

    // Update stats whenever tasks change (simple approximation) or on mount
    useEffect(() => {
        fetchAnalytics();
    }, [tasks]); // Refresh stats when tasks list updates (e.g. after create/delete)

    const handleSearch = (e) => {
        e.preventDefault();
        setPage(0); // Reset to first page on new search
        fetchTasks();
    };

    const handleCreateTask = async (e) => {
        e.preventDefault();
        if (!newTaskTitle.trim()) return;

        try {
            await api.post('/tasks', {
                title: newTaskTitle,
                description: 'Created via Frontend',
                status: newTaskStatus
            });
            setNewTaskTitle('');
            setNewTaskStatus('PENDING');
            fetchTasks(); // Refresh list to show new task (and potentially re-sort)
        } catch (err) {
            console.error("Failed to create task", err);
        }
    };

    const handleStatusChange = async (task, newStatus) => {
        // Optimistic UI Update
        const previousTasks = [...tasks];
        setTasks(tasks.map(t => t.id === task.id ? { ...t, status: newStatus } : t));

        try {
            await api.put(`/tasks/${task.id}`, {
                title: task.title,
                description: task.description,
                status: newStatus
            });
            fetchTasks(); // Refresh to confirm and update sort order
        } catch (err) {
            console.error("Failed to update task", err);
            setTasks(previousTasks); // Revert on failure
        }
    };

    const handleDelete = async (id) => {
        try {
            await api.delete(`/tasks/${id}`);
            fetchTasks(); // Refresh list
        } catch (err) {
            console.error("Failed to delete task", err);
        }
    };

    const handlePageChange = (newPage) => {
        if (newPage >= 0 && newPage < totalPages) {
            setPage(newPage);
        }
    };

    // Chart Data Preparation
    const pieData = [
        { name: 'Completed', value: stats.completedTasks || 0, color: '#00C851' },
        { name: 'Pending', value: stats.pendingTasks || 0, color: '#ff4444' },
        { name: 'In Progress', value: stats.inProgressTasks || 0, color: '#ffbb33' },
    ].filter(d => d.value > 0);

    const barData = stats.activity || [];

    return (
        <div style={{ padding: '2rem', maxWidth: '1200px', margin: '0 auto' }}>
            <header style={{ marginBottom: '2rem', display: 'flex', justifyContent: 'space-between', alignItems: 'end' }}>
                <div>
                    <h1 className="gradient-text">Dashboard</h1>
                    <p style={{ color: 'var(--text-muted)' }}>Manage your tasks effectively, {user?.email}</p>
                </div>
                <div style={{ fontSize: '3rem', opacity: 0.1 }}>🚀</div>
            </header>

            {/* Analytics Section */}
            <MetricsCards data={stats} />

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(400px, 1fr))', gap: '2rem', marginBottom: '3rem' }}>
                {/* Pie Chart */}
                <div className="card glass">
                    <h3 style={{ marginBottom: '1rem' }}>Task Distribution</h3>
                    <div style={{ height: '300px' }}>
                        <ResponsiveContainer width="100%" height="100%">
                            <PieChart>
                                <Pie
                                    data={pieData}
                                    cx="50%"
                                    cy="50%"
                                    innerRadius={60}
                                    outerRadius={80}
                                    paddingAngle={5}
                                    dataKey="value"
                                >
                                    {pieData.map((entry, index) => (
                                        <Cell key={`cell-${index}`} fill={entry.color} />
                                    ))}
                                </Pie>
                                <Tooltip />
                                <Legend />
                            </PieChart>
                        </ResponsiveContainer>
                    </div>
                </div>

                {/* Bar Chart */}
                <div className="card glass">
                    <h3 style={{ marginBottom: '1rem' }}>Activity (Last 7 Days)</h3>
                    <div style={{ height: '300px' }}>
                        <ResponsiveContainer width="100%" height="100%">
                            <BarChart data={barData}>
                                <XAxis dataKey="date" stroke="var(--text-muted)" tickFormatter={(str) => str ? str.slice(5) : ''} />
                                <YAxis stroke="var(--text-muted)" allowDecimals={false} />
                                <Tooltip cursor={{ fill: 'rgba(255,255,255,0.05)' }} contentStyle={{ backgroundColor: '#1a1a2e', border: 'none' }} />
                                <Bar dataKey="count" fill="hsl(var(--primary))" radius={[4, 4, 0, 0]} />
                            </BarChart>
                        </ResponsiveContainer>
                    </div>
                </div>
            </div>


            {/* Controls: Search & Filter */}
            <div className="card glass" style={{ marginBottom: '2rem', display: 'flex', gap: '1rem', flexWrap: 'wrap', alignItems: 'center' }}>
                <form onSubmit={handleSearch} style={{ display: 'flex', gap: '1rem', flex: 1 }}>
                    <Input
                        placeholder="Search tasks..."
                        value={titleFilter}
                        onChange={(e) => setTitleFilter(e.target.value)}
                        style={{ marginBottom: 0 }}
                    />
                    <Button type="submit">Search</Button>
                </form>

                <select
                    value={statusFilter}
                    onChange={(e) => {
                        setStatusFilter(e.target.value);
                        setPage(0); // Reset page on filter change
                    }}
                    style={{
                        padding: '0.75rem',
                        borderRadius: '8px',
                        border: '1px solid rgba(255,255,255,0.1)',
                        background: 'rgba(255,255,255,0.05)',
                        color: 'white'
                    }}
                >
                    <option value="">All Statuses</option>
                    <option value="PENDING">Pending</option>
                    <option value="IN_PROGRESS">In Progress</option>
                    <option value="DONE">Done</option>
                </select>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '2rem' }}>

                {/* Create Task Card */}
                <div className="card glass" style={{ height: 'fit-content' }}>
                    <h3 style={{ marginBottom: '1.5rem' }}>New Task</h3>
                    <form onSubmit={handleCreateTask}>
                        <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1rem' }}>
                            <Input
                                label="Task Title"
                                value={newTaskTitle}
                                onChange={(e) => setNewTaskTitle(e.target.value)}
                                style={{ flex: 1, marginBottom: 0 }}
                            />
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                                <label style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>Status</label>
                                <select
                                    value={newTaskStatus}
                                    onChange={(e) => setNewTaskStatus(e.target.value)}
                                    style={{
                                        padding: '0.75rem',
                                        borderRadius: '8px',
                                        border: '1px solid rgba(255,255,255,0.1)',
                                        background: 'rgba(255,255,255,0.05)',
                                        color: 'white',
                                        height: '46px'
                                    }}
                                >
                                    <option value="PENDING">Pending</option>
                                    <option value="IN_PROGRESS">In Progress</option>
                                    <option value="DONE">Completed</option>
                                </select>
                            </div>
                        </div>
                        <Button type="submit" style={{ width: '100%' }}>
                            <FiPlus /> Add Task
                        </Button>
                    </form>
                </div>

                {/* Task List */}
                <div style={{ gridColumn: 'span 2' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
                        <h3>Your Tasks</h3>
                        {/* Pagination Controls */}
                        <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                            <Button
                                variant="outline"
                                disabled={page === 0}
                                onClick={() => handlePageChange(page - 1)}
                                style={{ padding: '0.5rem 1rem' }}
                            >
                                Prev
                            </Button>
                            <span style={{ color: 'var(--text-muted)' }}>
                                Page {page + 1} of {totalPages || 1}
                            </span>
                            <Button
                                variant="outline"
                                disabled={page >= totalPages - 1}
                                onClick={() => handlePageChange(page + 1)}
                                style={{ padding: '0.5rem 1rem' }}
                            >
                                Next
                            </Button>
                        </div>
                    </div>

                    {loading ? (
                        <p>Loading tasks...</p>
                    ) : tasks.length === 0 ? (
                        <div className="card" style={{ textAlign: 'center', color: 'var(--text-muted)' }}>
                            <p>No tasks found.</p>
                        </div>
                    ) : (
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                            {tasks.map(task => (
                                <div key={task.id} className="card" style={{
                                    padding: '1.5rem',
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'space-between',
                                    borderLeft: `4px solid ${task.status === 'DONE' ? 'hsl(var(--secondary))' : 'hsl(var(--primary))'}`
                                }}>
                                    <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                                        <select
                                            value={task.status}
                                            onChange={(e) => handleStatusChange(task, e.target.value)}
                                            style={{
                                                padding: '0.4rem 0.8rem',
                                                borderRadius: '20px',
                                                border: 'none',
                                                background: task.status === 'DONE' ? 'rgba(76, 175, 80, 0.2)' : task.status === 'IN_PROGRESS' ? 'rgba(255, 187, 51, 0.2)' : 'rgba(255, 68, 68, 0.2)',
                                                color: task.status === 'DONE' ? '#4caf50' : task.status === 'IN_PROGRESS' ? '#ffbb33' : '#ff4444',
                                                cursor: 'pointer',
                                                fontSize: '0.85rem',
                                                fontWeight: '600',
                                                appearance: 'none', /* Remove default arrow for cleaner look (optional, or custom arrow) */
                                                textAlign: 'center',
                                                minWidth: '100px'
                                            }}
                                            onClick={(e) => e.stopPropagation()}
                                        >
                                            <option value="PENDING">Pending</option>
                                            <option value="IN_PROGRESS">In Progress</option>
                                            <option value="DONE">Completed</option>
                                        </select>
                                        <div>
                                            <h4 style={{ textDecoration: task.status === 'DONE' ? 'line-through' : 'none', color: task.status === 'DONE' ? 'var(--text-muted)' : 'var(--text-main)' }}>
                                                {task.title}
                                            </h4>

                                        </div>
                                    </div>

                                    {user?.roles?.includes('ADMIN') && (
                                        <Button variant="outline" onClick={() => handleDelete(task.id)} style={{ padding: '0.5rem', color: '#ff4d4d', borderColor: '#ff4d4d' }}>
                                            <FiTrash2 />
                                        </Button>
                                    )}
                                </div>
                            ))}
                        </div>
                    )}
                </div>

            </div>
        </div>
    );
};

export default Dashboard;
