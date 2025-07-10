import {  useState, useEffect } from 'react';
import { Professional } from './types/professional';

function App() {
  const [professionals, setProfessionals] = useState<Professional[]>([]);

  useEffect(() => {
    fetch('http://localhost:8080/api/v1/professionals')
      .then(response => response.json())
      .then(data => setProfessionals(data))
      .catch(error => console.error('Erro ao buscar profissionais:', error));
  }, []);

  return (
    <div>
      <h1>Mar de Beleza - Agendamentos</h1>
      
      <h2>Profissionais</h2>
      <ul>
        {professionals.map(professional => (
          <li key={professional.id}>
            {professional.name}
          </li>
        ))}
      </ul>
    </div>
  )
}

export default App
