import { useState } from 'react';
import api from './services/api';
import { useNavigate, Link } from 'react-router-dom';

function Cadastro() {
    const [formData, setFormData] = useState({
        nome: '',
        email: '',
        senha: '',
        papelId: 2 // Padrão: Gerente (pode ser alterado pelo select)
    });
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleCadastro = async (e) => {
        e.preventDefault();
        try {
            // O backend espera um Long/Integer para papelId, o select retorna String.
            // O Javascript/JSON costuma lidar bem, mas garantir a conversão é boa prática.
            const dadosParaEnviar = {
                ...formData,
                papelId: parseInt(formData.papelId)
            };

            await api.post('/usuarios/cadastrar', dadosParaEnviar);
            alert('Usuário cadastrado! Faça login.');
            navigate('/');
        } catch (error) {
            console.error(error);
            alert('Erro ao cadastrar. Tente outro email.');
        }
    };

    return (
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-5">
                    <div className="card shadow">
                        <div className="card-header bg-success text-white">Criar Conta</div>
                        <div className="card-body">
                            <form onSubmit={handleCadastro}>
                                <div className="mb-3">
                                    <label>Nome</label>
                                    <input 
                                        name="nome" 
                                        className="form-control" 
                                        onChange={handleChange} 
                                        required 
                                    />
                                </div>
                                <div className="mb-3">
                                    <label>Email</label>
                                    <input 
                                        name="email" 
                                        type="email" 
                                        className="form-control" 
                                        onChange={handleChange} 
                                        required 
                                    />
                                </div>
                                <div className="mb-3">
                                    <label>Senha</label>
                                    <input 
                                        name="senha" 
                                        type="password" 
                                        className="form-control" 
                                        onChange={handleChange} 
                                        required 
                                    />
                                </div>

                                {/* Seletor de Tipo de Conta Adicionado */}
                                <div className="mb-3">
                                    <label>Tipo de Conta</label>
                                    <select 
                                        name="papelId" 
                                        className="form-select" 
                                        onChange={handleChange} 
                                        value={formData.papelId}
                                    >
                                        <option value="1">Administrador</option>
                                        <option value="2">Gerente</option>
                                        <option value="3">Desenvolvedor</option>
                                    </select>
                                </div>

                                <button type="submit" className="btn btn-success w-100">
                                    Cadastrar
                                </button>
                            </form>
                            <div className="mt-3 text-center">
                                <Link to="/">Voltar para Login</Link>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Cadastro;