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

    const fetchTasks = async () => {
        try {
            const res = await api.get('/tasks');
            setTasks(res.data);
        } catch (err) {
            console.error("Failed to fetch tasks", err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchTasks();
    }, []);

    const handleCreateTask = async (e) => {
        e.preventDefault();
        if (!newTaskTitle.trim()) return;

        try {
            const res = await api.post('/tasks', {
                title: newTaskTitle,
                description: 'Created via Frontend',
                status: 'PENDING'
            });
            setTasks([...tasks, res.data]);
            setNewTaskTitle('');
        } catch (err) {
            console.error("Failed to create task", err);
        }
    };

    const handleToggleStatus = async (task) => {
        const newStatus = task.status === 'DONE' ? 'PENDING' : 'DONE';
        try {
            const res = await api.put(`/tasks/${task.id}`, { status: newStatus });
            setTasks(tasks.map(t => t.id === task.id ? res.data : t));
        } catch (err) {
            console.error("Failed to update task", err);
        }
    };

    const handleDelete = async (id) => {
        try {
            await api.delete(`/tasks/${id}`);
            setTasks(tasks.filter(t => t.id !== id));
        } catch (err) {
            console.error("Failed to delete task", err);
        }
    };

    return (
        <div style={{ padding: '2rem', maxWidth: '1200px', margin: '0 auto' }}>
            <header style={{ marginBottom: '3rem', display: 'flex', justifyContent: 'space-between', alignItems: 'end' }}>
                <div>
                    <h1 className="gradient-text">Dashboard</h1>
                    <p style={{ color: 'var(--text-muted)' }}>Manage your tasks effectively, {user?.email}</p>
                </div>
                <div style={{ fontSize: '3rem', opacity: 0.1 }}>🚀</div>
            </header>

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
                    <h3 style={{ marginBottom: '1.5rem' }}>Your Tasks</h3>

                    {loading ? (
                        <p>Loading tasks...</p>
                    ) : tasks.length === 0 ? (
                        <div className="card" style={{ textAlign: 'center', color: 'var(--text-muted)' }}>
                            <p>No tasks yet. Create one to get started!</p>
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
