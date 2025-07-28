import { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import { useDebounce } from 'use-debounce';
import { useAuth } from '../../hooks/useAuth';
import type { Appointment } from '../../types/appointment';

import { Header } from '../../components/Header';
import { FilterPanel } from '../../components/FilterPanel';
import { DetailedAppointmentCard } from '../../components/DetailedAppointmentCard';
import { Pagination } from '../../components/Pagination';
import { AppointmentModal } from '../../components/AppointmentModal';
import { StatusUpdateModal } from '../../components/StatusUpdateModal';

export function AppointmentManagementPage() {
  const { token } = useAuth();
  
  // Estado para os valores atuais dos filtros
  const [filters, setFilters] = useState({
    startDate: '',
    endDate: '',
    professionalId: '',
    clientId: '',
    status: ''
  });
  
  // Estado para paginação
  const [currentPage, setCurrentPage] = useState(0);
  
  // Cria uma versão "atrasada" (debounced) dos filtros para evitar chamadas excessivas à API
  const [debouncedFilters] = useDebounce(filters, 500);

  // Estado para forçar a atualização da lista após uma ação (create, update, delete)
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  // Estados para os dados e modais
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [pageData, setPageData] = useState<any>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isAppointmentModalOpen, setIsAppointmentModalOpen] = useState(false);
  const [appointmentToEdit, setAppointmentToEdit] = useState<Appointment | null>(null);
  const [statusUpdateAppointment, setStatusUpdateAppointment] = useState<Appointment | null>(null);

  // Efeito principal para buscar os dados
  useEffect(() => {
    const fetchAppointments = async () => {
      if (!token) return;
      setIsLoading(true);
      
      const params = new URLSearchParams();
      // Adiciona filtros à URL apenas se eles tiverem um valor
      if (debouncedFilters.startDate) params.append('startDate', debouncedFilters.startDate);
      if (debouncedFilters.endDate) params.append('endDate', debouncedFilters.endDate);
      if (debouncedFilters.professionalId) params.append('professionalId', debouncedFilters.professionalId);
      if (debouncedFilters.clientId) params.append('clientId', debouncedFilters.clientId);
      if (debouncedFilters.status) params.append('status', debouncedFilters.status);
      
      params.append('page', currentPage.toString());
      params.append('size', '5'); // 5 itens por página
      params.append('sort', 'appointmentDate,desc');

      try {
        const response = await fetch(`http://localhost:8080/api/v1/appointments?${params.toString()}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!response.ok) throw new Error('Falha ao buscar agendamentos.');
        const data = await response.json();
        setAppointments(data.content || []);
        setPageData(data);
      } catch (error: any) {
        toast.error(error.message);
      } finally {
        setIsLoading(false);
      }
    };

    fetchAppointments();
  }, [token, debouncedFilters, currentPage, refreshTrigger]);

  // --- Handlers (Funções de Controle) ---

  const handleFilterChange = (filterName: string, value: any) => {
    setFilters(prevFilters => ({ ...prevFilters, [filterName]: value }));
    setCurrentPage(0); // Volta para a primeira página ao mudar um filtro
  };

  const handleClearFilters = () => {
    setFilters({ startDate: '', endDate: '', professionalId: '', clientId: '', status: '' });
    setCurrentPage(0);
  };
  
  const handlePageClick = (event: { selected: number }) => {
    setCurrentPage(event.selected);
  };
  
  const handleRefresh = () => {
    setRefreshTrigger(prev => prev + 1); // "Puxa o gatilho" para o useEffect rodar de novo
  };

  const handleSaveSuccess = () => {
    setIsAppointmentModalOpen(false);
    setStatusUpdateAppointment(null);
    handleRefresh(); 
  };

  const handleOpenCreateModal = () => {
    setAppointmentToEdit(null);
    setIsAppointmentModalOpen(true);
  };

  const handleOpenEditModal = (appointment: Appointment) => {
    setAppointmentToEdit(appointment);
    setIsAppointmentModalOpen(true);
  };

  const handleOpenStatusModal = (appointment: Appointment) => {
    setStatusUpdateAppointment(appointment);
  };

  const handleCancelOrReactivate = async (appointment: Appointment) => {
    const isCanceled = appointment.status === 'CANCELED';
    const newStatus = isCanceled ? 'SCHEDULED' : 'CANCELED';
    const actionText = isCanceled ? 'reativar' : 'cancelar';

    if (window.confirm(`Tem certeza que deseja ${actionText} este agendamento?`)) {
      try {
        const response = await fetch(`http://localhost:8080/api/v1/appointments/${appointment.id}/status`, {
          method: 'PATCH',
          headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
          body: JSON.stringify({ status: newStatus }),
        });
        if (!response.ok) throw new Error(`Falha ao ${actionText} o agendamento.`);
        toast.success(`Agendamento ${actionText} com sucesso!`);
        handleRefresh(); // Dispara a atualização da lista
      } catch (error: any) {
        toast.error(error.message);
      }
    }
  };

  return (
    <div className="bg-gray-50 min-h-screen">
      <Header title="Agendamentos" />

      <main className="pt-20 pb-24 px-4 max-w-4xl mx-auto">
        <FilterPanel 
          filters={filters}
          onFilterChange={handleFilterChange}
          onClearFilters={handleClearFilters}
        />
        
        <div className="space-y-4">
          {isLoading && <p>Carregando agendamentos...</p>}
          {!isLoading && appointments.map(app => (
            <DetailedAppointmentCard
              key={app.id}
              appointment={app}
              onEdit={() => handleOpenEditModal(app)}
              onStatusClick={() => handleOpenStatusModal(app)}
              onCancel={() => handleCancelOrReactivate(app)}
            />
          ))}
        </div>

        {pageData && pageData.totalPages > 1 && (
          <Pagination
            pageCount={pageData.totalPages}
            onPageChange={handlePageClick}
          />
        )}
      </main>

      <button onClick={handleOpenCreateModal} className="fixed bottom-6 right-6 ...">
        <span className="hidden sm:inline">Novo Agendamento</span>
      </button>

      {isAppointmentModalOpen && (
        <AppointmentModal
          isOpen={isAppointmentModalOpen}
          onRequestClose={() => setIsAppointmentModalOpen(false)}
          onSaveSuccess={handleSaveSuccess}
          appointmentToEdit={appointmentToEdit}
          // Precisamos passar a 'selectedDate' para o modal. Por enquanto, pode ser a de hoje.
          selectedDate={new Date()} 
        />
      )}

      {statusUpdateAppointment && (
        <StatusUpdateModal
          isOpen={!!statusUpdateAppointment}
          onRequestClose={() => setStatusUpdateAppointment(null)}
          onUpdateSuccess={handleSaveSuccess}
          appointment={statusUpdateAppointment}
        />
      )}
    </div>
  );
}