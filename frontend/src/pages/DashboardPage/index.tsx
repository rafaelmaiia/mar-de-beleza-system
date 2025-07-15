import { useState, useEffect } from 'react';
import { Header } from '../../components/Header';
import { AppointmentCard } from '../../components/AppointmentCard';
import { DateSelector } from '../../components/DateSelector';
import { AppointmentModal } from '../../components/AppointmentModal';
import type { Appointment } from '../../types/appointment';

// DUMMY_LOGGED_IN_USER é um usuário fictício para exibir no cabeçalho
const DUMMY_LOGGED_IN_USER = { name: 'Ana Paula' };

export function DashboardPage() {

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

  useEffect(() => {
    const fetchAppointments = async () => {
      setIsLoading(true);
      setError(null);
      
      const token = localStorage.getItem('accessToken');
      if (!token) {
        setError("Usuário não autenticado. Por favor, faça o login.");
        setIsLoading(false);
        return;
      }

      const dateString = currentDate.toISOString().split('T')[0];

      try {
        const response = await fetch(`http://localhost:8080/api/v1/appointments?date=${dateString}&sort=appointmentDate,asc`, {
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
  }, [currentDate, refreshTrigger]);

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
    return new Date(dateString).toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
  };

  const getServiceNames = (services: any[]) => {
    if (!services || services.length === 0) return 'N/A';
    return services.map(s => s.service.name).join(', ');
  };

  return (
    <div className="bg-gray-50 min-h-screen">
      <Header title="Home" />

      <main className="pt-20 pb-8 px-4 max-w-4xl mx-auto">
        <div className="mb-6">
          <p className="text-xl text-gray-700">Bem-vinda, <span className="font-semibold">{DUMMY_LOGGED_IN_USER.name}</span></p>
        </div>

        {isToday() && nextAppointment && (
          <div className="mb-8">
            <h2 className="text-lg font-semibold text-gray-800 mb-3">Próximo agendamento</h2>
            <div className="bg-gradient-to-r from-purple-500 to-indigo-600 text-white p-5 rounded-xl shadow-lg">
              <div className="flex justify-between items-start">
                <div>
                  <p className="text-2xl font-bold">{nextAppointment.client.name}</p>
                  <p className="text-sm opacity-90">{getServiceNames(nextAppointment.items)}</p>
                </div>
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
          {!isLoading && !error && appointments.length === 0 && <p>Nenhum agendamento para hoje.</p>}
          
          {!isLoading && !error && appointments.map(app => {
              const professionalName = app.items && app.items.length > 0
                  ? app.items[0].professional.name
                  : 'N/A';
              
              const serviceNames = app.items && app.items.length > 0
                  ? app.items.map(item => item.service.name).join(', ')
                  : 'Serviço não especificado';

              return (
                  <AppointmentCard
                      key={app.id}
                      onEditClick={() => handleOpenEditModal(app)}
                      time={formatTime(app.appointmentDate)}
                      clientName={app.client.name}
                      professionalName={professionalName}
                      services={serviceNames}
                      status={app.status}
                      observations={app.observations}
                  />
              );
          })}
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
    </div>
  );
}