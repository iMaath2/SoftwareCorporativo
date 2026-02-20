import { useState } from 'react';
import api from './services/api';
import { useNavigate } from 'react-router-dom';

function Login() {
    const [email, setEmail] = useState('');
    const [senha, setSenha] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await api.post('/usuarios/login', { email, senha });
            // Salva o usuário no navegador para usar nas outras telas
            localStorage.setItem('usuario', JSON.stringify(response.data));
            navigate('/dashboard');
        } catch (error) {
            alert('Login falhou! Verifique suas credenciais.');
        }
    };

    return (
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-4">
                    <div className="card shadow">
                        <div className="card-header bg-primary text-white">Login</div>
                        <div className="card-body">
                            <form onSubmit={handleLogin}>
                                <div className="mb-3">
                                    <label>Email</label>
                                    <input type="email" className="form-control" 
                                           value={email} onChange={e => setEmail(e.target.value)} />
                                </div>
                                <div className="mb-3">
                                    <label>Senha</label>
                                    <input type="password" className="form-control" 
                                           value={senha} onChange={e => setSenha(e.target.value)} />
                                </div>
                                <button type="submit" className="btn btn-primary w-100">Entrar</button>
                                    <div className="mt-3 text-center">
                                    <small>Não tem conta? <a href="/cadastro">Cadastre-se aqui</a></small>
                                    </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Login;