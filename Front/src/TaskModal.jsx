import { useState, useEffect } from 'react';
import api from './services/api';

function TaskModal({ tarefa, onClose, onUpdate, usuarioLogado, listaUsuarios }) {   
    const [activeTab, setActiveTab] = useState('detalhes');
    const [titulo, setTitulo] = useState(tarefa.titulo);
    const [descricao, setDescricao] = useState(tarefa.descricao || '');
    const [responsavelId, setResponsavelId] = useState(tarefa.responsavel?.id || '');
    const [dataLimite, setDataLimite] = useState(tarefa.dataLimite || '');
    
    // Estados para Horas e Comentários
    const [dataHoras, setDataHoras] = useState(new Date().toISOString().split('T')[0]);
    const [horas, setHoras] = useState('');
    const [novoComentario, setNovoComentario] = useState('');
    const [todosComentarios, setTodosComentarios] = useState([]); 
    const [totalHoras, setTotalHoras] = useState(0);
    const [listaRegistros, setListaRegistros] = useState([]); 

    useEffect(() => {
        if (tarefa.id) carregarDetalhes();
    }, [tarefa]);

    const carregarDetalhes = async () => {
        try {
            const res = await api.get(`/tarefas/${tarefa.id}`);
            const listaOrdenada = (res.data.comentarios || []).sort((a, b) => new Date(b.dataCriacao) - new Date(a.dataCriacao));
            setTodosComentarios(listaOrdenada);
            setListaRegistros(res.data.registrosHoras || []);
            const resHoras = await api.get(`/tarefas/${tarefa.id}/horas/total`);
            setTotalHoras(resHoras.data);
        } catch (error) { console.error(error); }
    };

    const handleSalvarEdicao = async () => {
        try {
            await api.put(`/tarefas/${tarefa.id}?idExecutor=${usuarioLogado.id}`, { titulo, descricao, dataLimite });
            if (responsavelId && String(responsavelId) !== String(tarefa.responsavel?.id)) {
                await api.patch(`/tarefas/${tarefa.id}/responsavel/${responsavelId}?idExecutor=${usuarioLogado.id}`);
            }
            alert('Atualizado!'); onUpdate(); onClose();
        } catch (error) { alert('Erro ao atualizar.'); }
    };

    const handleRegistrarHoras = async () => {
        if (!horas || horas <= 0) return alert('Inválido');
        try {
            await api.post(`/tarefas/${tarefa.id}/horas`, { horas: parseFloat(horas), data: dataHoras, usuarioId: usuarioLogado.id });
            setHoras(''); alert('Registrado!'); carregarDetalhes();
        } catch (e) { alert('Erro ao registrar.'); }
    };

    const handleComentar = async () => {
        if (!novoComentario.trim()) return;
        try {
            await api.post(`/tarefas/${tarefa.id}/comentarios`, { texto: novoComentario, autorId: usuarioLogado.id });
            setNovoComentario(''); carregarDetalhes();
        } catch (e) { alert('Erro ao comentar.'); }
    };

    const handleExcluirTarefa = async () => {
        if(confirm("Excluir tarefa?")) {
            try { await api.delete(`/tarefas/${tarefa.id}?idExecutor=${usuarioLogado.id}`); onUpdate(); onClose(); }
            catch(e) { alert("Erro ao excluir"); }
        }
    };

    const formatarData = (d) => d ? new Date(d).toLocaleString('pt-BR') : '-';
    const isLog = (t) => ["Moveu", "Criada", "Alterou", "Registrou", "Definiu"].some(l => t.includes(l));
    const listaLogs = todosComentarios.filter(c => isLog(c.texto));
    const listaChat = todosComentarios.filter(c => !isLog(c.texto));

    return (
        <div className="modal show d-block" style={{backgroundColor: 'rgba(0,0,0,0.8)', backdropFilter: 'blur(5px)', zIndex: 1050}}>
            <div className="modal-dialog modal-lg modal-dialog-centered">
                {/* FORÇANDO FUNDO DARK NO MODAL-CONTENT */}
                <div className="modal-content border-0 shadow-lg overflow-hidden" style={{backgroundColor: '#1E293B', color: '#F8FAFC'}}>
                    
                    {/* Header Dark */}
                    <div className="modal-header border-bottom border-secondary" style={{borderColor: '#334155'}}>
                        <div>
                            <small className="text-muted text-uppercase fw-bold">ID: #{tarefa.id}</small>
                            <h5 className="modal-title fw-bold text-white">{tarefa.projeto?.nome}</h5>
                        </div>
                        <button className="btn-close btn-close-white" onClick={onClose}></button>
                    </div>

                    <div className="modal-body">
                        {/* Abas */}
                        <ul className="nav nav-pills nav-fill mb-4 bg-dark p-1 rounded border border-secondary" style={{borderColor: '#334155'}}>
                            {['detalhes', 'horas', 'comentarios', 'historico'].map(tab => (
                                <li className="nav-item" key={tab}>
                                    <button 
                                        className={`nav-link text-capitalize ${activeTab === tab ? 'active bg-primary text-white' : 'text-muted'}`} 
                                        onClick={() => setActiveTab(tab)}
                                        style={{borderRadius: '8px'}}
                                    >
                                        {tab}
                                    </button>
                                </li>
                            ))}
                        </ul>

                        {activeTab === 'detalhes' && (
                            <div className="row g-3">
                                <div className="col-12">
                                    <label className="text-muted small fw-bold text-uppercase">Título</label>
                                    <input className="form-control" value={titulo} onChange={e => setTitulo(e.target.value)} />
                                </div>
                                <div className="col-md-6">
                                    <label className="text-muted small fw-bold text-uppercase">Responsável</label>
                                    <select className="form-select" value={responsavelId} onChange={e => setResponsavelId(e.target.value)}>
                                        <option value="">-- Não Atribuído --</option>
                                        {listaUsuarios.map(u => <option key={u.id} value={u.id}>{u.nome}</option>)}
                                    </select>
                                </div>
                                <div className="col-md-6">
                                    <label className="text-muted small fw-bold text-uppercase">Prazo (Deadline)</label>
                                    <input type="date" className="form-control" value={dataLimite} onChange={e => setDataLimite(e.target.value)} />
                                </div>
                                <div className="col-12">
                                    <label className="text-muted small fw-bold text-uppercase">Descrição</label>
                                    <textarea className="form-control" rows="5" value={descricao} onChange={e => setDescricao(e.target.value)} />
                                </div>
                            </div>
                        )}

                        {activeTab === 'horas' && (
                            <div className="p-2">
                                <div className="card bg-primary text-white mb-3 text-center p-3 border-0">
                                    <h2 className="m-0">{Number(totalHoras).toFixed(2)}h</h2>
                                    <small>Total Investido</small>
                                </div>
                                <div className="row g-2 mb-3">
                                    <div className="col-4"><input type="date" className="form-control" value={dataHoras} onChange={e => setDataHoras(e.target.value)} /></div>
                                    <div className="col-4"><input type="number" step="0.5" className="form-control" placeholder="Horas" value={horas} onChange={e => setHoras(e.target.value)} /></div>
                                    <div className="col-4"><button className="btn btn-outline-primary w-100" onClick={handleRegistrarHoras}>Lançar</button></div>
                                </div>
                                <div className="table-responsive">
                                    <table className="table table-dark table-hover table-sm">
                                        <thead><tr><th>Quem</th><th>Data</th><th>Horas</th></tr></thead>
                                        <tbody>
                                            {listaRegistros.map(r => (
                                                <tr key={r.id}>
                                                    <td>{r.usuario?.nome}</td>
                                                    <td>{r.dataRegistro.split('-').reverse().join('/')}</td>
                                                    <td>{r.horas}h</td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        )}

                        {activeTab === 'comentarios' && (
                            <div className="d-flex flex-column" style={{height: '300px'}}>
                                <div className="flex-grow-1 overflow-auto mb-2 pe-2 custom-scrollbar">
                                    {listaChat.map(c => (
                                        <div key={c.id} className="mb-2 p-2 rounded" style={{backgroundColor: '#0F172A', border: '1px solid #334155'}}>
                                            <div className="d-flex justify-content-between">
                                                <strong className="text-primary small">{c.autor?.nome}</strong>
                                                <small className="text-muted" style={{fontSize: '0.65rem'}}>{formatarData(c.dataCriacao)}</small>
                                            </div>
                                            <p className="mb-0 small">{c.texto}</p>
                                        </div>
                                    ))}
                                </div>
                                <div className="input-group">
                                    <input className="form-control" placeholder="Escreva..." value={novoComentario} onChange={e => setNovoComentario(e.target.value)} />
                                    <button className="btn btn-primary" onClick={handleComentar}><i className="bi bi-send"></i></button>
                                </div>
                            </div>
                        )}

                        {activeTab === 'historico' && (
                            <div className="overflow-auto custom-scrollbar" style={{height: '300px'}}>
                                {listaLogs.map(c => (
                                    <div key={c.id} className="text-center small mb-2 p-2 rounded w-75 mx-auto" style={{backgroundColor: '#0F172A', color: '#94A3B8'}}>
                                        <strong>{c.autor?.nome}</strong> {c.texto.toLowerCase()}
                                        <div style={{fontSize: '0.6rem'}}>{formatarData(c.dataCriacao)}</div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>

                    <div className="modal-footer border-top border-secondary justify-content-between" style={{borderColor: '#334155'}}>
                        <button className="btn btn-outline-danger btn-sm" onClick={handleExcluirTarefa}>Excluir</button>
                        <button className="btn btn-primary px-4" onClick={handleSalvarEdicao}>Salvar</button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default TaskModal;