import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { DragDropContext, Droppable, Draggable } from '@hello-pangea/dnd';
import api from './services/api';
import TaskModal from './TaskModal';

function Kanban() {
    const { idProjeto } = useParams();
    const [projeto, setProjeto] = useState(null);
    const [colunas, setColunas] = useState({});
    const [ordemColunas, setOrdemColunas] = useState([]);
    
    const [loading, setLoading] = useState(true);
    const [tarefaSelecionada, setTarefaSelecionada] = useState(null);
    const [usuarios, setUsuarios] = useState([]);
    
    const navigate = useNavigate();
    const usuarioLogado = JSON.parse(localStorage.getItem('usuario'));

    useEffect(() => {
        carregarDados();
    }, [idProjeto]);

    const carregarDados = async () => {
        try {
            const [resProj, resUsers] = await Promise.all([
                api.get(`/projetos/${idProjeto}`),
                api.get('/usuarios')
            ]);

            const proj = resProj.data;
            const fluxo = proj.fluxoTrabalho;
            const todasTarefas = proj.tarefas || [];

            setProjeto(proj);
            setUsuarios(resUsers.data);

            const colunasObj = {};
            const ordem = [];

            if (fluxo && fluxo.etapas) {
                fluxo.etapas.forEach(etapa => {
                    colunasObj[etapa.id] = {
                        id: etapa.id,
                        nome: etapa.nome,
                        items: todasTarefas.filter(t => t.etapaAtual?.id === etapa.id)
                    };
                    ordem.push(etapa.id);
                });
            }

            setColunas(colunasObj);
            setOrdemColunas(ordem);
            setLoading(false);
        } catch (error) {
            console.error("Erro", error);
            setLoading(false);
        }
    };

    const onDragEnd = async (result) => {
        const { source, destination, draggableId } = result;
        if (!destination) return;
        if (source.droppableId === destination.droppableId && source.index === destination.index) return;

        const colunaOrigem = colunas[source.droppableId];
        const colunaDestino = colunas[destination.droppableId];
        const sourceItems = [...colunaOrigem.items];
        const destItems = source.droppableId === destination.droppableId ? sourceItems : [...colunaDestino.items];

        const [removido] = sourceItems.splice(source.index, 1);
        destItems.splice(destination.index, 0, removido);

        setColunas({
            ...colunas,
            [source.droppableId]: { ...colunaOrigem, items: sourceItems },
            [destination.droppableId]: { ...colunaDestino, items: destItems }
        });

        if (source.droppableId !== destination.droppableId) {
            try {
                removido.etapaAtual = { id: destination.droppableId };
                await api.patch(`/tarefas/${draggableId}/mover/${destination.droppableId}?idExecutor=${usuarioLogado.id}`);
            } catch (error) {
                carregarDados();
            }
        }
    };

    const getPrazoInfo = (dataLimite, nomeEtapa) => {
        if (!dataLimite) return null;
        if (nomeEtapa && (nomeEtapa.toLowerCase().includes('concluído') || nomeEtapa.toLowerCase().includes('done'))) {
            return { color: '#10B981', bg: 'rgba(16, 185, 129, 0.1)', text: 'Entregue', icon: 'bi-check-circle' };
        }
        
        const hoje = new Date();
        hoje.setHours(0,0,0,0);
        const limite = new Date(dataLimite + 'T00:00:00');

        if (limite < hoje) return { color: '#EF4444', bg: 'rgba(239, 68, 68, 0.1)', text: 'Atrasado', icon: 'bi-exclamation-circle' };
        if (limite.getTime() === hoje.getTime()) return { color: '#F59E0B', bg: 'rgba(245, 158, 11, 0.1)', text: 'Vence Hoje', icon: 'bi-clock' };
        
        const dia = limite.getDate().toString().padStart(2, '0');
        const mes = (limite.getMonth() + 1).toString().padStart(2, '0');
        return { color: '#94A3B8', bg: 'rgba(148, 163, 184, 0.1)', text: `${dia}/${mes}`, icon: 'bi-calendar' };
    };

    if (loading) return <div className="text-center mt-5"><div className="spinner-border text-primary"></div></div>;

    return (
        <div className="d-flex flex-column" style={{ height: '100vh', backgroundColor: '#0B1120' }}> {/* Fundo Dark */}
            
            {/* Header Dark */}
            <div className="px-4 py-3 border-bottom d-flex justify-content-between align-items-center shadow-sm" 
                 style={{backgroundColor: '#0F172A', borderColor: '#1E293B', zIndex: 10}}>
                <div className="d-flex align-items-center gap-3">
                    <button onClick={() => navigate('/projetos')} className="btn btn-outline-secondary btn-sm rounded-circle" style={{width: 40, height: 40}}>
                        <i className="bi bi-arrow-left"></i>
                    </button>
                    <div>
                        <h5 className="mb-0 fw-bold text-white">{projeto?.nome}</h5>
                        <small className="text-muted">Fluxo: {projeto?.fluxoTrabalho?.nome}</small>
                    </div>
                </div>
                <div className="d-flex gap-2">
                    {usuarios.slice(0, 3).map(u => (
                        <div key={u.id} className="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center border border-dark" 
                             style={{width: 32, height: 32, fontSize: '0.8rem'}} title={u.nome}>
                            {u.nome.charAt(0)}
                        </div>
                    ))}
                    <button className="btn btn-primary btn-sm px-3" onClick={() => {/* Add logica */}}>
                        <i className="bi bi-plus-lg me-1"></i> Nova Tarefa
                    </button>
                </div>
            </div>

            {/* Área Drag and Drop */}
            <DragDropContext onDragEnd={onDragEnd}>
                <div className="d-flex p-4 gap-4 overflow-x-auto flex-grow-1 align-items-start" style={{backgroundColor: '#0B1120'}}>
                    
                    {ordemColunas.map(idColuna => {
                        const coluna = colunas[idColuna];
                        return (
                            <Droppable key={idColuna} droppableId={String(idColuna)}>
                                {(provided, snapshot) => (
                                    <div
                                        {...provided.droppableProps}
                                        ref={provided.innerRef}
                                        className="d-flex flex-column rounded-3 flex-shrink-0"
                                        style={{
                                            width: 320,
                                            // Colunas em Slate 800 (igual aos cards de projeto)
                                            backgroundColor: snapshot.isDraggingOver ? '#334155' : '#1E293B',
                                            border: '1px solid #334155',
                                            maxHeight: 'calc(100vh - 100px)'
                                        }}
                                    >
                                        <div className="p-3 d-flex justify-content-between align-items-center border-bottom" style={{borderColor: '#334155'}}>
                                            <span className="fw-bold text-white text-uppercase small" style={{letterSpacing: '0.5px'}}>{coluna.nome}</span>
                                            <span className="badge bg-dark text-muted border border-secondary rounded-pill">{coluna.items.length}</span>
                                        </div>

                                        <div className="p-2 d-flex flex-column gap-2 overflow-y-auto custom-scrollbar">
                                            {coluna.items.map((tarefa, index) => {
                                                const prazo = getPrazoInfo(tarefa.dataLimite, coluna.nome);
                                                
                                                return (
                                                    <Draggable key={tarefa.id} draggableId={String(tarefa.id)} index={index}>
                                                        {(provided, snapshot) => (
                                                            <div
                                                                ref={provided.innerRef}
                                                                {...provided.draggableProps}
                                                                {...provided.dragHandleProps}
                                                                onClick={() => setTarefaSelecionada(tarefa)}
                                                                className="card p-3 shadow-sm"
                                                                style={{
                                                                    ...provided.draggableProps.style,
                                                                    // Card um pouco mais claro que a coluna (Slate 700)
                                                                    backgroundColor: '#334155', 
                                                                    border: '1px solid rgba(255,255,255,0.05)',
                                                                    color: 'white',
                                                                    transform: snapshot.isDragging ? provided.draggableProps.style.transform : 'none' 
                                                                }}
                                                            >
                                                                <div className="d-flex justify-content-between mb-2">
                                                                    {tarefa.responsavel ? (
                                                                         <div className="d-flex align-items-center gap-1 px-2 py-1 rounded" style={{fontSize: '0.7rem', backgroundColor: '#0F172A'}}>
                                                                            <i className="bi bi-person-fill text-primary"></i>
                                                                            <span className="fw-bold text-white text-truncate" style={{maxWidth: 100}}>
                                                                                {tarefa.responsavel.nome} {tarefa.responsavel.sobrenome || ''}
                                                                            </span>
                                                                         </div>
                                                                    ) : (
                                                                        <span className="badge bg-dark text-muted border border-secondary fw-normal" style={{fontSize: '0.65rem'}}>Não atribuído</span>
                                                                    )}
                                                                    <small className="text-muted opacity-50" style={{fontSize: '0.65rem'}}>#{tarefa.id}</small>
                                                                </div>

                                                                <h6 className="text-white mb-3 fw-semibold" style={{fontSize: '0.9rem', lineHeight: 1.4}}>{tarefa.titulo}</h6>

                                                                <div className="d-flex justify-content-between align-items-center pt-2 border-top" style={{borderColor: 'rgba(255,255,255,0.1)'}}>
                                                                    {prazo ? (
                                                                        <div className="d-flex align-items-center gap-1 rounded px-2 py-1" style={{backgroundColor: prazo.bg}}>
                                                                            <i className={`bi ${prazo.icon}`} style={{color: prazo.color, fontSize: '0.7rem'}}></i>
                                                                            <span style={{color: prazo.color, fontSize: '0.7rem', fontWeight: 600}}>{prazo.text}</span>
                                                                        </div>
                                                                    ) : <span></span>}
                                                                    
                                                                    {tarefa.responsavel && (
                                                                        <div className="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center" style={{width: 24, height: 24, fontSize: '0.7rem'}}>
                                                                            {tarefa.responsavel.nome.charAt(0)}
                                                                        </div>
                                                                    )}
                                                                </div>
                                                            </div>
                                                        )}
                                                    </Draggable>
                                                );
                                            })}
                                            {provided.placeholder}
                                        </div>
                                    </div>
                                )}
                            </Droppable>
                        );
                    })}
                </div>
            </DragDropContext>

            {tarefaSelecionada && (
                <TaskModal 
                    tarefa={tarefaSelecionada} 
                    onClose={() => setTarefaSelecionada(null)} 
                    onUpdate={carregarDados}
                    usuarioLogado={usuarioLogado}
                    listaUsuarios={usuarios}
                />
            )}
        </div>
    );
}

export default Kanban;