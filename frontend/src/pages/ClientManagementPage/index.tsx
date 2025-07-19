import { useState, useEffect, useMemo } from 'react';
import { useAuth } from '../../hooks/useAuth';
import toast from 'react-hot-toast';
import type { Client } from '../../types/client';
import { Header } from '../../components/Header';
import { ClientCard } from '../../components/ClientCard';
import { ClientModal } from '../../components/ClientModal';


export function ClientManagementPage() {
  const { token } = useAuth();
  const [clients, setClients] = useState<Client[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [clientToEdit, setClientToEdit] = useState<Client | null>(null);
  const [searchTerm, setSearchTerm] = useState('');


  const fetchClients = async () => {
    setIsLoading(true);
    try {
      const response = await fetch('http://localhost:8080/api/v1/clients', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (!response.ok) throw new Error('Falha ao buscar clientes.');
      const data = await response.json();
      setClients(data.content || data);
    } catch (error) {
      console.error(error);
      toast.error('Não foi possível carregar os clientes.');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (token) {
      fetchClients();
    }
  }, [token]);

  const handleOpenCreateModal = () => {
    setClientToEdit(null);
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (client: Client) => {
    setClientToEdit(client);
    setIsModalOpen(true);
  };
  
  const handleSaveSuccess = () => {
    setIsModalOpen(false);
    fetchClients();
  };

  const handleDeleteClient = async (clientId: number) => {
    if (window.confirm('Tem certeza que deseja excluir este cliente?')) {
      try {
        const response = await fetch(`http://localhost:8080/api/v1/clients/${clientId}`, {
          method: 'DELETE',
          headers: { 'Authorization': `Bearer ${token}` },
        });
        if (!response.ok) throw new Error('Falha ao deletar cliente.');
        toast.success('Cliente deletado com sucesso!');
        fetchClients();
      } catch (error: any) {
        toast.error(error.message);
      }
    }
  };

  const filteredClients = useMemo(() => {
    if (!searchTerm) return clients;
    return clients.filter(c => 
      c.name.toLowerCase().includes(searchTerm.toLowerCase())
    );
  }, [clients, searchTerm]);

  return (
    <div className="bg-gray-50 min-h-screen">
      <Header title="Clientes" />

      <main className="pt-20 pb-24 px-4 max-w-4xl mx-auto">
        <div className="mb-6">
          <input 
            type="text" 
            placeholder="Buscar cliente por nome..." 
            className="w-full px-4 py-2 border ..."
            value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)}
          />
        </div>

        <div className="space-y-4">
          {isLoading && <p>Carregando clientes...</p>}
          {!isLoading && filteredClients.map(client => (
            <ClientCard 
              key={client.id}
              client={client}
              onEdit={() => handleOpenEditModal(client)}
              onDelete={() => handleDeleteClient(client.id)}
            />
          ))}
        </div>
      </main>

      <button onClick={handleOpenCreateModal} className="fixed bottom-6 right-6 bg-green-500 hover:bg-green-600 text-white font-bold py-3 px-4 rounded-full shadow-lg flex items-center gap-2 transition-transform transform hover:scale-110 z-50">
        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path></svg>
        <span className="hidden sm:inline">Novo Cliente</span>
      </button>

      <ClientModal
        isOpen={isModalOpen}
        onRequestClose={() => setIsModalOpen(false)}
        onSaveSuccess={handleSaveSuccess}
        clientToEdit={clientToEdit}
      />
    </div>
  );
}