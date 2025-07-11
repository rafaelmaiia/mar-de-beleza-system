import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

export function LoginPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const navigate = useNavigate();

    async function handleLogin(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault();

        const data = {
            email,
            password,
        };

        try {
            const response = await fetch('http://localhost:8080/api/v1/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                throw new Error('Falha no login. Verifique suas credenciais.');
            }

            const responseData = await response.json();

            localStorage.setItem('accessToken', responseData.token);

            navigate('/dashboard');

        } catch (err: any) {
            setError(err.message || 'Ocorreu um erro. Tente novamente.');
        }
    }

    return (
        <div>
            <h1>Acessar sua Conta</h1>
            <form onSubmit={handleLogin}>
                <input
                    placeholder="Email"
                    value={email}
                    onChange={e => setEmail(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="Senha"
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                />

                <button type='submit'>Login</button>
                {error && <p style={{ color: 'red' }}>{error}</p>}
            </form>
        </div>
    );
}