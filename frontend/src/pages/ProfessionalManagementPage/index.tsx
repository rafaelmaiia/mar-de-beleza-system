import { useState, useEffect, useMemo } from 'react';
import { useAuth } from '../../hooks/useAuth';
import type { Professional } from '../../types/professional';
import { Header } from '../../components/Header';
import { ProfessionalCard } from '../../components/ProfessionalCard';
import { ProfessionalModal } from '../../components/ProfessionalModal';
import { serviceTypeTranslations } from '../../constants/serviceConstants'; 
import toast from 'react-hot-toast';

export function ProfessionalManagementPage() {
  const { token } = useAuth();
  const [professionals, setProfessionals] = useState<Professional[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [professionalToEdit, setProfessionalToEdit] = useState<Professional | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [specialtyFilter, setSpecialtyFilter] = useState<string>('ALL');

  const fetchProfessionals = async () => {
    setIsLoading(true);
    try {
      const response = await fetch('http://localhost:8080/api/v1/professionals', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (!response.ok) throw new Error('Falha ao buscar profissionais.');
      const data = await response.json();
      setProfessionals(data.content || data);
    } catch (error) {
      console.error(error);
      toast.error('Não foi possível carregar os profissionais.');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (token) {
      fetchProfessionals();
    }
  }, [token]);

  const handleOpenCreateModal = () => {
    setProfessionalToEdit(null);
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (professional: Professional) => {
    setProfessionalToEdit(professional);
    setIsModalOpen(true);
  };
  
  const handleSaveSuccess = () => {
    setIsModalOpen(false);
    fetchProfessionals();
  };

  const handleDeleteProfessional = async (professionalId: number) => {
    if (window.confirm('Tem certeza que deseja excluir este profissional?')) {
      try {
        const response = await fetch(`http://localhost:8080/api/v1/professionals/${professionalId}`, {
          method: 'DELETE',
          headers: { 'Authorization': `Bearer ${token}` },
        });
        if (!response.ok) throw new Error('Falha ao deletar profissional.');
        toast.success('Profissional deletado com sucesso!');
        fetchProfessionals();
      } catch (error: any) {
        toast.error(error.message);
      }
    }
  };

  const availableSpecialties = useMemo(() => {
    const allSpecialties = new Set<string>();
    professionals.forEach(p => {
      p.specialties.forEach(s => allSpecialties.add(s));
    });
    return ['ALL', ...Array.from(allSpecialties)];
  }, [professionals]);

  const filteredProfessionals = useMemo(() => {
    return professionals
      .filter(p => // Filtro de especialidade
        specialtyFilter === 'ALL' || p.specialties.includes(specialtyFilter)
      )
      .filter(p => // Filtro de busca por nome
        p.name.toLowerCase().includes(searchTerm.toLowerCase())
      );
  }, [professionals, searchTerm, specialtyFilter]); //

  return (
    <div className="bg-gray-50 min-h-screen">
      <Header title="Profissionais" />

      <main className="pt-20 pb-24 px-4 max-w-4xl mx-auto">
        <div className="mb-6">
          <input 
            type="text" 
            placeholder="Buscar profissional por nome..." 
            className="w-full px-4 py-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
            value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)}
          />
        </div>

        {/* --- BOTÃO DE FILTRO POR ESPECIALIDADE --- */}
        <div className="flex flex-wrap gap-2 mb-6">
          {availableSpecialties.map(specialty => (
            <button
              key={specialty}
              onClick={() => setSpecialtyFilter(specialty)}
              className={`px-3 py-1 text-sm font-medium rounded-full transition-colors ${
                specialtyFilter === specialty 
                ? 'bg-indigo-600 text-white' 
                : 'bg-gray-200 text-gray-700'
              }`}
            >
              {specialty === 'ALL' ? 'Todos' : serviceTypeTranslations[specialty] || specialty}
            </button>
          ))}
        </div>

        <div className="space-y-4">
          {isLoading && <p>Carregando profissionais...</p>}
          {!isLoading && filteredProfessionals.map(prof => (
            <ProfessionalCard 
              key={prof.id}
              professional={prof}
              onEdit={() => handleOpenEditModal(prof)}
              onDelete={() => handleDeleteProfessional(prof.id)}
            />
          ))}
        </div>
      </main>

      <button onClick={handleOpenCreateModal} className="fixed bottom-6 right-6 bg-green-500 hover:bg-green-600 text-white font-bold py-3 px-4 rounded-full shadow-lg flex items-center gap-2 transition-transform transform hover:scale-110 z-50">
        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path></svg>
        <span className="hidden sm:inline">Nova Profissional</span>
      </button>

      <ProfessionalModal
        isOpen={isModalOpen}
        onRequestClose={() => setIsModalOpen(false)}
        onSaveSuccess={handleSaveSuccess}
        professionalToEdit={professionalToEdit}
      />
    </div>
  );
}