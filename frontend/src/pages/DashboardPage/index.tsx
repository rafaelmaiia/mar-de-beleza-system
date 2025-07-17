import { useState, useEffect } from 'react';
import { formatInTimeZone, toDate } from 'date-fns-tz';
import { format } from 'date-fns';
import { useAuth } from '../../hooks/useAuth';
import { Header } from '../../components/Header';
import { AppointmentCard } from '../../components/AppointmentCard';
import { DateSelector } from '../../components/DateSelector';
import { AppointmentModal } from '../../components/AppointmentModal';
import type { Appointment } from '../../types/appointment';
import { StatusUpdateModal } from '../../components/StatusUpdateModal';

const decodeToken = (token: string) => {
  try {
    return JSON.parse(atob(token.split('.')[1]));
  } catch (error) {
    return null;
  }
};

export function DashboardPage() {
  const { token } = useAuth();

  const getUserNameFromToken = () => {
    if (token) {
      const decoded = decodeToken(token);
      return decoded?.fullName || decoded?.sub || 'Usuário';
    }
    return 'Usuário';
  };

  const userName = getUserNameFromToken();

  const [currentDate, setCurrentDate] = useState(new Date());
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const isToday = () => {
    const today = new Date();
    return currentDate.toDateString() === today.toDateString();
  };

  const [refreshTrigger, setRefreshTrigger] = useState(0);
  const handleRefresh = () => {
    setRefreshTrigger(prev => prev + 1);
  };

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingAppointment, setEditingAppointment] = useState<Appointment | null>(null);

  const handleOpenCreateModal = () => {
    setEditingAppointment(null); // Garante que estamos no modo "criar"
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (appointment: Appointment) => {
    setEditingAppointment(appointment); // Passa o agendamento para o estado
    setIsModalOpen(true);
  };

  const [statusUpdateAppointment, setStatusUpdateAppointment] = useState<Appointment | null>(null);

  const handleOpenStatusModal = (appointment: Appointment) => {
    setStatusUpdateAppointment(appointment);
  };

  const handleCloseStatusModal = () => {
    setStatusUpdateAppointment(null);
  };

  useEffect(() => {
    if (!token) {
        setIsLoading(false);
        return;
    }

    const fetchAppointments = async () => {
      setIsLoading(true);
      setError(null);

      const today = format(currentDate, 'yyyy-MM-dd');

      try {
        const response = await fetch(`http://localhost:8080/api/v1/appointments?date=${today}&sort=appointmentDate,asc`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });

        if (!response.ok) {
          throw new Error('Falha ao buscar agendamentos do dia.');
        }

        const data = await response.json();
        setAppointments(data.content || []);
      } catch (err: any) {
        setError(err.message);
        console.error(err);
      } finally {
        setIsLoading(false);
      }
    };

    fetchAppointments();
  }, [currentDate, refreshTrigger, token]);

  const findNextAppointment = () => {
    const now = new Date();
    return appointments
      .filter(app => app.status !== 'CANCELED' && app.status !== 'DONE')
      .find(app => new Date(app.appointmentDate) > now);
  };

  const handleDateChange = (newDate: Date) => {
    setCurrentDate(newDate);
  };

  const nextAppointment = findNextAppointment();

  // Funções para formatação
  const formatTime = (dateString: string) => {
    const date = toDate(dateString, { timeZone: 'America/Sao_Paulo' });
    return format(date, 'HH:mm', { timeZone: 'America/Sao_Paulo' });
  };

  const formatDateForDisplay = (date: Date) => {
    const today = new Date();
    const tomorrow = new Date();
    tomorrow.setDate(today.getDate() + 1);

    if (date.toDateString() === today.toDateString()) {
      return 'Hoje';
    }
    if (date.toDateString() === tomorrow.toDateString()) {
        return 'Amanhã';
    }
    return date.toLocaleDateString('pt-BR', { day: '2-digit', month: 'long' });
  };

  return (
    <div className="bg-gray-50 min-h-screen">
      <Header title="Home" />

      <main className="pt-20 pb-8 px-4 max-w-4xl mx-auto">
        <div className="mb-6">
          <p className="text-xl text-gray-700">Bem-vinda, <span className="font-semibold">{userName}</span></p>
        </div>

        {/* Card de Destaque: Próximo Agendamento */}
        {isToday() && nextAppointment && (
          <div className="mb-8">
            <h2 className="text-lg font-semibold text-gray-800 mb-3">Próximo agendamento</h2>
            <div className="bg-gradient-to-r from-purple-500 to-indigo-600 text-white p-5 rounded-xl shadow-lg transform hover:scale-105 transition-transform duration-300">
              
              {/* Container principal com Flexbox */}
              <div className="flex justify-between items-start">
                
                {/* Div da Esquerda (empilhada) */}
                <div>
                  <a href={`https://api.whatsapp.com/send/?phone=55${nextAppointment.client.contact.phone}`} target="_blank" rel="noopener noreferrer" className="block hover:opacity-80 transition-opacity">
                    <p className="text-2xl font-bold">{nextAppointment.client.name}</p>
                    <p className="text-xs opacity-80">{nextAppointment.client.contact.phone}</p>
                  </a>
                  <div className="mt-2">
                    <p className="text-sm opacity-90">{nextAppointment.service.name}</p>
                    <p className="text-sm font-semibold opacity-90 mt-1">R$ {nextAppointment.price.toFixed(2).replace('.', ',')}</p>
                  </div>
                </div>

                {/* Div da Direita (empilhada e alinhada à direita) */}
                <div className="text-right">
                  <p className="text-3xl font-bold">{formatTime(nextAppointment.appointmentDate)}</p>
                  <p className="text-xs uppercase tracking-wider opacity-80">Hoje</p>
                </div>

              </div>
            </div>
          </div>
        )}

        <DateSelector 
          selectedDate={currentDate}
          onDateChange={handleDateChange}
        />

        <div className="space-y-4">
          {isLoading && <p>Carregando agenda...</p>}
          {error && <p className="text-red-500">{error}</p>}
          {!isLoading && !error && appointments.length === 0 && (
            <p>Nenhum agendamento para {formatDateForDisplay(currentDate).toLowerCase()}.</p>
          )}
          
          {!isLoading && !error && appointments.map(app => (
            <AppointmentCard
              key={app.id}
              onEditClick={() => handleOpenEditModal(app)}
              onStatusClick={() => handleOpenStatusModal(app)}
              time={formatTime(app.appointmentDate)}
              clientName={app.client.name}
              clientPhone={app.client.contact.phone}
              professionalName={app.professional.name}
              services={app.service.name}
              price={app.price}
              status={app.status}
              observations={app.observations}
            />
          ))}
        </div>
      </main>

      {/* BOTÃO FLUTUANTE */}
      <button 
        onClick={handleOpenCreateModal}
        className="fixed bottom-6 right-6 bg-green-500 hover:bg-green-600 text-white font-bold py-3 px-5 rounded-full shadow-lg flex items-center gap-2 transition-transform transform hover:scale-110 z-50"
      >
        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path></svg>
        <span className="hidden sm:inline">Novo Agendamento</span>
      </button>

      {/* O MODAL */}
      {/* Só é renderizado se 'isModalOpen' for true */}
      <AppointmentModal 
        isOpen={isModalOpen}
        onRequestClose={() => setIsModalOpen(false)}
        selectedDate={currentDate}
        onSaveSuccess={handleRefresh}
        appointmentToEdit={editingAppointment}
      />

      {/* NOVO MODAL DE STATUS */}
      <StatusUpdateModal
        isOpen={!!statusUpdateAppointment}
        onRequestClose={handleCloseStatusModal}
        onUpdateSuccess={() => {
          handleCloseStatusModal();
          handleRefresh(); // Reutiliza a função de refresh que já temos!
        }}
        appointment={statusUpdateAppointment}
      />
    </div>
  );
}