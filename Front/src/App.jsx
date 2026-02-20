import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Login from './Login';
import Projetos from './Projetos';
import Kanban from './Kanban';
import Cadastro from './Cadastro';
import Usuarios from './Usuarios'; // <--- Importe aqui

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/cadastro" element={<Cadastro />} />
        <Route path="/" element={<Login />} />
        <Route path="/projetos" element={<Projetos />} />
        <Route path="/kanban/:idProjeto" element={<Kanban />} />
        <Route path="/usuarios" element={<Usuarios />} /> {/* <--- Adicione a rota */}
      </Routes>
    </BrowserRouter>
  );
}

export default App;