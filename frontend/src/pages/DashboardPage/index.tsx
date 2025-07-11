import { useState, useEffect } from 'react';
import { Professional } from '../../types/professional';

export function DashboardPage() {
  const [professionals, setProfessionals] = useState<any[]>([]);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');

    if (!token) {
      console.error("Nenhum token de acesso encontrado.");
      return;
    }

    fetch('http://localhost:8080/api/v1/professionals', {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
    .then(response => response.json())
    .then(data => setProfessionals(data.content || data))
    .catch(error => console.error('Erro ao buscar profissionais:', error));
  
  }, []);

  return (
    <div>
        <h1>Mar de Beleza - Agendamentos</h1>
        <h2>Dashboard</h2>
        <h3>Profissionais</h3>
        <ul>
            {professionals.map(p => <li key={p.id}>{p.name}</li>)}
        </ul>
    </div>
  );
}