import React from 'react';
import '../styles/components.css';

const Input = ({ label, type = 'text', value, onChange, placeholder = ' ', ...props }) => {
    return (
        <div className="input-group">
            <input
                className="input-field"
                type={type}
                value={value}
                onChange={onChange}
                placeholder={placeholder}
                {...props}
            />
            <label className="input-label">{label}</label>
        </div>
    );
};

export default Input;
