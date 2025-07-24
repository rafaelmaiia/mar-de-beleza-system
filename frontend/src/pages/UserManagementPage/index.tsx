import { useState, useEffect, useMemo } from 'react';
import { useAuth } from '../../hooks/useAuth';
import type { SystemUser } from '../../types/user.ts';
import { Header } from '../../components/Header';
import { UserCard } from '../../components/UserCard';
import { UserModal } from '../../components/UserModal';
import { serviceTypeTranslations } from '../../constants/serviceConstants'; 
import toast from 'react-hot-toast';

export function UserManagementPage() {
  const { token } = useAuth();

  const [users, setUsers] = useState<SystemUser[]>([]); 
  const [isLoading, setIsLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [userToEdit, setUserToEdit] = useState<SystemUser | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [specialtyFilter, setSpecialtyFilter] = useState<string>('ALL');

  const fetchUsers = async () => {
    setIsLoading(true);
    try {
      const response = await fetch('http://localhost:8080/api/v1/users', { 
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (!response.ok) throw new Error('Falha ao buscar usuários.');
      const data = await response.json();
      setUsers(data || []);
    } catch (error) {
      console.error(error);
      toast.error('Não foi possível carregar os usuários.');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (token) {
      fetchUsers();
    }
  }, [token]);

  const handleOpenCreateModal = () => {
    setUserToEdit(null);
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (user: SystemUser) => {
    setUserToEdit(user);
    setIsModalOpen(true);
  };
  
  const handleSaveSuccess = () => {
    setIsModalOpen(false);
    fetchUsers();
  };

  const handleDeleteUser = async (userId: number) => {
    if (window.confirm('Tem certeza que deseja excluir este usuário?')) {
      try {
        const response = await fetch(`http://localhost:8080/api/v1/users/${userId}`, {
          method: 'DELETE',
          headers: { 'Authorization': `Bearer ${token}` },
        });
        if (!response.ok) throw new Error('Falha ao deletar usuário.');
        toast.success('Usuário deletado com sucesso!');
        fetchUsers();
      } catch (error: any) {
        toast.error(error.message);
      }
    }
  };

  const availableSpecialties = useMemo(() => {
    const allSpecialties = new Set<string>();
    users.forEach(p => {
      p.specialties.forEach(s => allSpecialties.add(s));
    });
    return ['ALL', ...Array.from(allSpecialties)];
  }, [users]);

  const filteredUsers = useMemo(() => {
    return users
      .filter(u => specialtyFilter === 'ALL' || u.specialties.includes(specialtyFilter))
      .filter(u => u.name.toLowerCase().includes(searchTerm.toLowerCase()));
  }, [users, searchTerm, specialtyFilter]);

  return (
    <div className="bg-gray-50 min-h-screen">
      <Header title="Usuários" />

      <main className="pt-20 pb-24 px-4 max-w-4xl mx-auto">
        <div className="mb-6">
          <input 
            type="text" 
            placeholder="Buscar usuário por nome..." 
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
          {isLoading && <p>Carregando usuários...</p>}
           {!isLoading && filteredUsers.map(user => (
            <UserCard 
              key={user.id}
              user={user}
              onEdit={() => handleOpenEditModal(user)}
              onDelete={() => handleDeleteUser(user.id)}
            />
          ))}
        </div>
      </main>

      <button onClick={handleOpenCreateModal} className="fixed bottom-6 right-6 bg-green-500 hover:bg-green-600 text-white font-bold py-3 px-4 rounded-full shadow-lg flex items-center gap-2 transition-transform transform hover:scale-110 z-50">
        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path></svg>
        <span className="hidden sm:inline">Novo Usuário</span>
      </button>

      <UserModal
        isOpen={isModalOpen}
        onRequestClose={() => setIsModalOpen(false)}
        onSaveSuccess={handleSaveSuccess}
        userToEdit={userToEdit}
      />
    </div>
  );
}