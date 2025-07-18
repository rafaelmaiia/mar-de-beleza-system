import { useState, useEffect, useMemo } from 'react';
import toast from 'react-hot-toast';
import { useAuth } from '../../hooks/useAuth';
import type { SalonService } from '../../types/salonService';
import { Header } from '../../components/Header';
import { serviceTypeTranslations } from '../../constants/serviceConstants';
import { ServiceCard } from '../../components/ServiceCard';
import { ServiceModal } from '../../components/ServiceModal';

export function ServiceManagementPage() {
  const { token } = useAuth();
  const [services, setServices] = useState<SalonService[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [serviceToEdit, setServiceToEdit] = useState<SalonService | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [categoryFilter, setCategoryFilter] = useState<string>('ALL');

  const fetchServices = async () => {
    setIsLoading(true);
    try {
      const response = await fetch('http://localhost:8080/api/v1/salonServices', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (!response.ok) throw new Error('Falha ao buscar serviços.');
      const data = await response.json();
      setServices(data.content || data);
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  // --- FUNÇÃO DELETAR ---
  const handleDeleteService = async (serviceId: number) => {
    if (window.confirm('Tem certeza que deseja excluir este serviço? Esta ação não pode ser desfeita.')) {
      try {
        const response = await fetch(`http://localhost:8080/api/v1/salonServices/${serviceId}`, {
          method: 'DELETE',
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          throw new Error('Falha ao deletar o serviço.');
        }

        toast.success('Serviço deletado com sucesso!');
        fetchServices();
      } catch (error: any) {
        toast.error(error.message);
      }
    }
  };

  useEffect(() => {
    if (token) {
      fetchServices();
    }
  }, [token]);

  const handleOpenCreateModal = () => {
    setServiceToEdit(null);
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (service: SalonService) => {
    setServiceToEdit(service);
    setIsModalOpen(true);
  };
  
  const handleSaveSuccess = () => {
    setIsModalOpen(false);
    fetchServices(); // Recarrega a lista após salvar
  }

  // Filtro de busca no frontend
  const filteredServices = useMemo(() => {
    return services
      .filter(s => // <-- FILTRO DE CATEGORIA
        categoryFilter === 'ALL' || s.serviceType === categoryFilter
      )
      .filter(s => // <-- FILTRO DE BUSCA (já existia)
        s.name.toLowerCase().includes(searchTerm.toLowerCase())
      );
  }, [services, searchTerm, categoryFilter]); // <-- Adiciona a nova dependência

  // Pega as categorias únicas dos serviços carregados para criar os botões
  const categories = ['ALL', ...new Set(services.map(s => s.serviceType))];

  return (
    <div className="bg-gray-50 min-h-screen">
      <Header title="Serviços" />

      <main className="pt-20 pb-24 px-4 max-w-4xl mx-auto">
        <div className="mb-6">
          <input 
            type="text" 
            placeholder="Buscar serviço por nome..." 
            className="w-full px-4 py-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
            value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)}
          />
        </div>

        {/* --- NOVOS BOTÕES DE FILTRO --- */}
        <div className="flex flex-wrap gap-2 mb-6">
          {categories.map(category => (
            <button
              key={category}
              onClick={() => setCategoryFilter(category)}
              className={`px-3 py-1 text-sm font-medium rounded-full ${
                categoryFilter === category 
                ? 'bg-indigo-600 text-white' 
                : 'bg-gray-200 text-gray-700'
              }`}
            >
              {/* Traduz o nome da categoria para o botão */}
              {category === 'ALL' ? 'Todos' : serviceTypeTranslations[category] || category}
            </button>
          ))}
        </div>

        <div className="space-y-4">
          {isLoading && <p>Carregando serviços...</p>}
          {!isLoading && filteredServices.map(service => (
            <ServiceCard 
              key={service.id}
              service={service}
              onEdit={() => handleOpenEditModal(service)}
              onDelete={() => handleDeleteService(service.id)} // <-- LIGANDO A NOVA FUNÇÃO
            />
          ))}
        </div>
      </main>

      <button onClick={handleOpenCreateModal} className="fixed bottom-6 right-6 bg-green-500 hover:bg-green-600 text-white font-bold py-3 px-4 rounded-full shadow-lg flex items-center gap-2 transition-transform transform hover:scale-110 z-50">
        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path></svg>
        <span className="hidden sm:inline">Novo Serviço</span>
      </button>

      <ServiceModal
        isOpen={isModalOpen}
        onRequestClose={() => setIsModalOpen(false)}
        onSaveSuccess={handleSaveSuccess}
        serviceToEdit={serviceToEdit}
      />
    </div>
  );
}