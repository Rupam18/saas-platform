import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import Button from '../components/Button';
import Input from '../components/Input';
import { FiPlus, FiTrash2, FiCheckCircle, FiCircle } from 'react-icons/fi';

const Dashboard = () => {
    const { user } = useAuth();
    const [tasks, setTasks] = useState([]);
    const [newTaskTitle, setNewTaskTitle] = useState('');
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

    // Debounce search or fetch on dependency change
    useEffect(() => {
        fetchTasks();
    }, [page, statusFilter]); // Fetch when page or status changes

    // Separate useEffect for search title debounce could be added, but simple button or enter key is easier for MVP
    // For now, let's just add a search button or fetch on enter in the input

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
                status: 'PENDING'
            });
            setNewTaskTitle('');
            fetchTasks(); // Refresh list to show new task (and potentially re-sort)
        } catch (err) {
            console.error("Failed to create task", err);
        }
    };

    const handleToggleStatus = async (task) => {
        const newStatus = task.status === 'DONE' ? 'PENDING' : 'DONE';
        try {
            await api.put(`/tasks/${task.id}`, { status: newStatus });
            fetchTasks(); // Refresh to update list state correctly
        } catch (err) {
            console.error("Failed to update task", err);
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

    return (
        <div style={{ padding: '2rem', maxWidth: '1200px', margin: '0 auto' }}>
            <header style={{ marginBottom: '2rem', display: 'flex', justifyContent: 'space-between', alignItems: 'end' }}>
                <div>
                    <h1 className="gradient-text">Dashboard</h1>
                    <p style={{ color: 'var(--text-muted)' }}>Manage your tasks effectively, {user?.email}</p>
                </div>
                <div style={{ fontSize: '3rem', opacity: 0.1 }}>🚀</div>
            </header>

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
                        <Input
                            label="Task Title"
                            value={newTaskTitle}
                            onChange={(e) => setNewTaskTitle(e.target.value)}
                        />
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
                                        <div
                                            onClick={() => handleToggleStatus(task)}
                                            style={{ cursor: 'pointer', color: task.status === 'DONE' ? 'hsl(var(--secondary))' : 'hsl(var(--text-muted))', fontSize: '1.5rem', display: 'flex' }}
                                        >
                                            {task.status === 'DONE' ? <FiCheckCircle /> : <FiCircle />}
                                        </div>
                                        <div>
                                            <h4 style={{ textDecoration: task.status === 'DONE' ? 'line-through' : 'none', color: task.status === 'DONE' ? 'var(--text-muted)' : 'var(--text-main)' }}>
                                                {task.title}
                                            </h4>
                                            <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)', background: 'hsla(var(--bg-primary), 0.5)', padding: '0.2rem 0.5rem', borderRadius: '4px' }}>
                                                {task.status}
                                            </span>
                                        </div>
                                    </div>

                                    <Button variant="outline" onClick={() => handleDelete(task.id)} style={{ padding: '0.5rem', color: '#ff4d4d', borderColor: '#ff4d4d' }}>
                                        <FiTrash2 />
                                    </Button>
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
