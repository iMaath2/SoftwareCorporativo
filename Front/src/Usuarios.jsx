import { useEffect, useState } from 'react';
import api from './services/api';
import { useNavigate } from 'react-router-dom';

function Usuarios() {
    const [usuarios, setUsuarios] = useState([]);
    const navigate = useNavigate();
    const usuarioLogado = JSON.parse(localStorage.getItem('usuario'));

    useEffect(() => {
        // Se não for admin, chuta de volta para projetos
        if (!usuarioLogado || usuarioLogado.papel !== 'ADMIN') {
            alert('Acesso restrito a Administradores.');
            navigate('/projetos');
        } else {
            carregarUsuarios();
        }
    }, []);

    const carregarUsuarios = async () => {
        try {
            const res = await api.get('/usuarios');
            setUsuarios(res.data);
        } catch (error) {
            alert("Erro ao carregar lista de utilizadores.");
        }
    };

    const handleExcluir = async (id) => {
        if (id === usuarioLogado.id) {
            alert("Você não pode excluir a si mesmo.");
            return;
        }
        if (!window.confirm("Tem a certeza que deseja excluir este utilizador?")) return;

        try {
            await api.delete(`/usuarios/${id}`);
            alert("Utilizador removido.");
            carregarUsuarios(); // Atualiza a lista
        } catch (error) {
            console.error(error);
            alert("Erro ao excluir. Verifique se o utilizador possui tarefas ou projetos vinculados.");
        }
    };

    return (
        <div className="container mt-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>Gestão de Utilizadores</h2>
                <button className="btn btn-secondary" onClick={() => navigate('/projetos')}>
                    Voltar aos Projetos
                </button>
            </div>

            <div className="card shadow-sm">
                <div className="card-body p-0">
                    <table className="table table-hover mb-0 align-middle">
                        <thead className="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Nome</th>
                                <th>Email</th>
                                <th>Papel</th>
                                <th className="text-end">Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            {usuarios.map(u => (
                                <tr key={u.id}>
                                    <td>{u.id}</td>
                                    <td>{u.nome}</td>
                                    <td>{u.email}</td>
                                    <td>
                                        <span className={`badge ${u.papel === 'ADMIN' ? 'bg-danger' : 'bg-primary'}`}>
                                            {u.papel}
                                        </span>
                                    </td>
                                    <td className="text-end">
                                        <button 
                                            className="btn btn-sm btn-outline-danger" 
                                            onClick={() => handleExcluir(u.id)}
                                            disabled={u.id === usuarioLogado.id}
                                        >
                                            Excluir
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}

export default Usuarios;