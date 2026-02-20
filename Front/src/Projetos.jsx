import { useEffect, useState, useMemo } from 'react';
import api from './services/api';
import { useNavigate } from 'react-router-dom';

function Projetos() {
    const [projetos, setProjetos] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [novoProjeto, setNovoProjeto] = useState({ nome: '', descricao: '' });
    
    // Estados Relatório
    const [showRelatorio, setShowRelatorio] = useState(false);
    const [dadosBrutos, setDadosBrutos] = useState([]);
    const [projetoSelecionado, setProjetoSelecionado] = useState(null);
    const [filtroTarefa, setFiltroTarefa] = useState('TODAS');

    const navigate = useNavigate();
    const usuario = JSON.parse(localStorage.getItem('usuario'));

    useEffect(() => {
        if (!usuario) navigate('/');
        else carregarProjetos();
    }, []);

    const carregarProjetos = async () => {
        try {
            const res = await api.get('/projetos');
            setProjetos(res.data);
        } catch (error) {
            console.error("Erro ao carregar projetos", error);
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('usuario'); 
        navigate('/'); 
    };

    // --- Relatório Logic ---
    const abrirExtrato = async (projeto) => {
        try {
            const res = await api.get(`/projetos/${projeto.id}/horas/detalhes?idExecutor=${usuario.id}`);
            const dadosOrdenados = res.data.sort((a, b) => new Date(b.data) - new Date(a.data));
            setDadosBrutos(dadosOrdenados);
            setFiltroTarefa('TODAS');
            setProjetoSelecionado(projeto);
            setShowRelatorio(true);
        } catch (error) {
            alert('Erro ao carregar dados.');
        }
    };

    const listaTarefasUnicas = useMemo(() => [...new Set(dadosBrutos.map(d => d.tarefaTitulo))], [dadosBrutos]);
    const dadosFiltrados = useMemo(() => filtroTarefa === 'TODAS' ? dadosBrutos : dadosBrutos.filter(i => i.tarefaTitulo === filtroTarefa), [dadosBrutos, filtroTarefa]);
    const totalHorasExibidas = useMemo(() => dadosFiltrados.reduce((acc, curr) => acc + curr.horas, 0), [dadosFiltrados]);

    const criarProjeto = async (e) => {
        e.preventDefault();
        try {
            const resProj = await api.post('/projetos', {
                nome: novoProjeto.nome,
                descricao: novoProjeto.descricao,
                gerenteId: usuario.id
            });
            let idFluxo = 1; 
            try { const resF = await api.post('/fluxos/padrao'); idFluxo = resF.data.id; } catch(e){}
            await api.post(`/projetos/${resProj.data.id}/fluxo/${idFluxo}?idExecutor=${usuario.id}`);
            
            alert('Projeto criado!');
            setShowModal(false);
            setNovoProjeto({ nome: '', descricao: '' });
            carregarProjetos();
        } catch (error) {
            alert('Erro ao criar projeto.');
        }
    };

    // Função para gerar cor de ícone baseada no nome
    const getIconColor = (nome) => {
        const colors = ['#6366F1', '#10B981', '#F59E0B', '#EC4899', '#8B5CF6'];
        const index = nome.length % colors.length;
        return colors[index];
    };

    return (
        <div style={{ minHeight: '100vh', backgroundColor: 'var(--bg-body)' }}>
            
            {/* NAVBAR */}
            <nav className="navbar navbar-expand-lg sticky-top mb-5">
                <div className="container">
                    <div className="d-flex align-items-center">
                        {/* Logo Minimalista */}
                        <div className="d-flex align-items-center justify-content-center rounded-3 me-3" 
                             style={{width: '40px', height: '40px', background: 'rgba(99, 102, 241, 0.1)', border: '1px solid rgba(99, 102, 241, 0.3)'}}>
                            <i className="bi bi-layers-fill" style={{color: '#818CF8'}}></i>
                        </div>
                        <div>
                            <h5 className="mb-0 fw-bold brand-font text-white">WorkFlow</h5>
                        </div>
                    </div>

                    <div className="d-flex align-items-center gap-3">
                        <div className="text-end d-none d-md-block">
                            <div className="fw-bold text-white small">{usuario?.nome}</div>
                            <div className="text-uppercase small" style={{color: 'var(--text-muted)', fontSize: '0.65rem', letterSpacing: '1px'}}>{usuario?.papel}</div>
                        </div>
                        <button className="btn btn-outline-secondary btn-sm rounded-circle d-flex align-items-center justify-content-center" 
                                onClick={handleLogout} title="Sair" style={{width: '38px', height: '38px'}}>
                            <i className="bi bi-power"></i>
                        </button>
                    </div>
                </div>
            </nav>

            {/* CONTEÚDO */}
            <div className="container pb-5">
                
                <div className="d-flex flex-wrap justify-content-between align-items-end mb-5">
                    <div>
                        <h2 className="fw-bold mb-2 display-6">
                            Meus <span className="text-gradient">Projetos</span>
                        </h2>
                        <p className="mb-0" style={{color: 'var(--text-muted)'}}>Gerencie suas equipes e acompanhe o progresso.</p>
                    </div>
                    <div className="mt-3 mt-md-0 d-flex gap-2">
                        {usuario?.papel === 'ADMIN' && (
                            <button className="btn btn-outline-secondary" onClick={() => navigate('/usuarios')}>
                                <i className="bi bi-people me-2"></i> Equipe
                            </button>
                        )}
                        <button className="btn btn-primary" onClick={() => setShowModal(true)}>
                            <i className="bi bi-plus-lg me-2"></i> Novo Projeto
                        </button>
                    </div>
                </div>

                <div className="row g-4">
                    {projetos.map(projeto => {
                        const meuPapel = usuario?.papel;
                        const idGerente = projeto.gerente?.id;
                        const podeExcluir = (meuPapel === 'ADMIN') || (meuPapel === 'GERENTE' && String(usuario.id) === String(idGerente));
                        const iconColor = getIconColor(projeto.nome);

                        return (
                            <div key={projeto.id} className="col-md-6 col-lg-4">
                                <div className="card h-100 p-4 d-flex flex-column">
                                    
                                    <div className="d-flex justify-content-between align-items-start mb-3">
                                        <div className="d-flex align-items-center gap-3">
                                            <div className="rounded-3 d-flex align-items-center justify-content-center fw-bold" 
                                                 style={{width: '48px', height: '48px', backgroundColor: `${iconColor}20`, color: iconColor, fontSize: '1.2rem'}}>
                                                {projeto.nome.charAt(0).toUpperCase()}
                                            </div>
                                            <div>
                                                <h5 className="card-title fw-bold mb-0 text-white">{projeto.nome}</h5>
                                                <small style={{color: 'var(--text-muted)', fontSize: '0.75rem'}}>
                                                    Gerente: {projeto.gerente?.nome}
                                                </small>
                                            </div>
                                        </div>
                                        
                                        {podeExcluir && (
                                            <button className="btn btn-link text-muted p-0 opacity-50 hover-opacity-100" 
                                                onClick={(e) => {
                                                    e.stopPropagation();
                                                    if(window.confirm(`Excluir "${projeto.nome}"?`)) {
                                                        api.delete(`/projetos/${projeto.id}?idExecutor=${usuario.id}`).then(carregarProjetos);
                                                    }
                                                }}>
                                                <i className="bi bi-trash"></i>
                                            </button>
                                        )}
                                    </div>
                                    
                                    <p className="card-text mb-4 flex-grow-1" style={{color: '#94A3B8', fontSize: '0.9rem', lineHeight: '1.6'}}>
                                        {projeto.descricao || 'Sem descrição definida.'}
                                    </p>
                                    
                                    <div className="d-flex gap-2 pt-3 border-top" style={{borderColor: 'rgba(255,255,255,0.05)'}}>
                                        <button onClick={() => abrirExtrato(projeto)} className="btn btn-outline-secondary flex-grow-1 btn-sm">
                                            Relatório
                                        </button>
                                        <button onClick={() => navigate(`/kanban/${projeto.id}`)} className="btn btn-primary flex-grow-1 btn-sm">
                                            Abrir Quadro
                                        </button>
                                    </div>
                                </div>
                            </div>
                        );
                    })}
                </div>
            </div>

            {/* Modal Novo Projeto (Dark) */}
            {showModal && (
                <div className="modal show d-block" style={{backgroundColor: 'rgba(0,0,0,0.8)', backdropFilter: 'blur(5px)'}}>
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title fw-bold text-white">Novo Projeto</h5>
                                <button className="btn-close btn-close-white" onClick={() => setShowModal(false)}></button>
                            </div>
                            <div className="modal-body p-4">
                                <form onSubmit={criarProjeto}>
                                    <div className="mb-3">
                                        <label className="form-label text-muted small fw-bold text-uppercase">Nome</label>
                                        <input className="form-control" autoFocus required value={novoProjeto.nome} onChange={e => setNovoProjeto({...novoProjeto, nome: e.target.value})} placeholder="Ex: App Mobile" />
                                    </div>
                                    <div className="mb-4">
                                        <label className="form-label text-muted small fw-bold text-uppercase">Descrição</label>
                                        <textarea className="form-control" rows="3" value={novoProjeto.descricao} onChange={e => setNovoProjeto({...novoProjeto, descricao: e.target.value})} />
                                    </div>
                                    <div className="d-grid">
                                        <button className="btn btn-primary">Criar Projeto</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {/* Modal Extrato (Dark) */}
            {showRelatorio && projetoSelecionado && (
                <div className="modal show d-block" style={{backgroundColor: 'rgba(0,0,0,0.8)', backdropFilter: 'blur(5px)', zIndex: 1060}}>
                    <div className="modal-dialog modal-xl modal-dialog-centered">
                        <div className="modal-content overflow-hidden">
                            <div className="modal-header p-4" style={{backgroundColor: '#0F172A'}}>
                                <div>
                                    <h5 className="modal-title mb-1 fw-bold text-white">Extrato de Horas</h5>
                                    <span className="badge" style={{backgroundColor: 'rgba(99, 102, 241, 0.2)', color: '#818CF8'}}>{projetoSelecionado.nome}</span>
                                </div>
                                <button className="btn-close btn-close-white" onClick={() => setShowRelatorio(false)}></button>
                            </div>
                            <div className="modal-body p-0" style={{backgroundColor: '#0F172A'}}>
                                <div className="p-4 border-bottom" style={{borderColor: '#1E293B'}}>
                                    <div className="row g-4 align-items-end">
                                        <div className="col-md-6">
                                            <label className="form-label text-muted small fw-bold text-uppercase">Filtrar Tarefa</label>
                                            <select className="form-select" value={filtroTarefa} onChange={(e) => setFiltroTarefa(e.target.value)}>
                                                <option value="TODAS">Todas as Tarefas</option>
                                                {listaTarefasUnicas.map(t => <option key={t} value={t}>{t}</option>)}
                                            </select>
                                        </div>
                                        <div className="col-md-6">
                                            <div className="p-3 rounded-3 d-flex justify-content-between align-items-center" style={{background: 'linear-gradient(135deg, #1E293B 0%, #0F172A 100%)', border: '1px solid #334155'}}>
                                                <span className="text-muted small fw-bold text-uppercase">Total</span>
                                                <span className="h4 mb-0 fw-bold text-white">{totalHorasExibidas.toFixed(2)}h</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="table-responsive" style={{maxHeight: '400px'}}>
                                    <table className="table table-dark table-hover mb-0" style={{backgroundColor: 'transparent'}}>
                                        <thead style={{backgroundColor: '#1E293B'}}>
                                            <tr>
                                                <th className="ps-4 py-3 text-muted small text-uppercase">Tarefa</th>
                                                <th className="py-3 text-muted small text-uppercase">Quem</th>
                                                <th className="py-3 text-muted small text-uppercase">Data</th>
                                                <th className="text-end pe-4 py-3 text-muted small text-uppercase">Horas</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {dadosFiltrados.map((reg, idx) => (
                                                <tr key={idx}>
                                                    <td className="ps-4 text-white fw-bold">{reg.tarefaTitulo}</td>
                                                    <td className="text-muted">{reg.usuarioNome}</td>
                                                    <td className="text-muted">{new Date(reg.data).toLocaleDateString('pt-BR')}</td>
                                                    <td className="text-end pe-4 text-white fw-bold">{reg.horas}h</td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default Projetos;