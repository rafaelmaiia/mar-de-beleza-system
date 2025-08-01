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
import { PaymentModal } from '../../components/PaymentModal';

const decodeToken = (token: string) => {
  try {
    return JSON.parse(atob(token.split('.')[1]));
  } catch (error) {
    return null;
  }
};

export function DashboardPage() {
  const { user, token } = useAuth();

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
    setEditingAppointment(null);
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (appointment: Appointment) => {
    setEditingAppointment(appointment);
    setIsModalOpen(true);
  };

  const [statusUpdateAppointment, setStatusUpdateAppointment] = useState<Appointment | null>(null);

  // ESTADO PARA O MODAL DE PAGAMENTO
  const [paymentAppointment, setPaymentAppointment] = useState<Appointment | null>(null);

  const handleStatusUpdateSuccess = (newStatus: string) => {
    const appointmentThatWasUpdated = statusUpdateAppointment;
    
    // Fecha o modal de status
    setStatusUpdateAppointment(null);
    handleRefresh();

    // --- A LÓGICA DE ORQUESTRAÇÃO ---
    // Se o status clicado foi "Concluído", abre o modal de pagamento
    if (newStatus === 'DONE' && appointmentThatWasUpdated) {
      setPaymentAppointment(appointmentThatWasUpdated);
    }
  };

  const handleOpenStatusModal = (appointment: Appointment) => {
    setStatusUpdateAppointment(appointment);
  };

  const handleCloseStatusModal = () => {
    setStatusUpdateAppointment(null);
  };

  useEffect(() => {
    const fetchAppointments = async () => {
      if (!token || !user) {
        setIsLoading(false);
        return;
      }
      
      setIsLoading(true);
      setError(null);
      
      const dateString = format(currentDate, 'yyyy-MM-dd');

      const params = new URLSearchParams({
          date: dateString,
          sort: 'appointmentDate,asc'
      });

      try {
          const response = await fetch(`http://localhost:8080/api/v1/appointments?${params.toString()}`, {
              headers: { 'Authorization': `Bearer ${token}` }
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
  }, [token, refreshTrigger, currentDate]);

  // --- LÓGICA PARA ENCONTRAR O PRÓXIMO AGENDAMENTO ---
  const findNextAppointment = () => {
    const now = new Date();
    
    // Se não tivermos um usuário logado, não há o que procurar.
    if (!user) return null;

    // Filtra apenas os agendamentos DESTE profissional/usuário
    const userAppointments = appointments.filter(app => app.professional.id === user.id);

    // A partir da lista pessoal, filtra os status ativos e futuros
    const upcomingAppointments = userAppointments.filter(app => 
      (app.status === 'SCHEDULED' || app.status === 'CONFIRMED') &&
      new Date(app.appointmentDate) > now
    );

    // Se não houver nenhum, retorna nada
    if (upcomingAppointments.length === 0) {
      return null;
    }

    // Ordena para garantir que o mais próximo venha primeiro
    upcomingAppointments.sort((a, b) => new Date(a.appointmentDate).getTime() - new Date(b.appointmentDate).getTime());
    
    // Retorna o primeiro da lista, que é o próximo
    return upcomingAppointments[0];
  };

  const nextAppointment = findNextAppointment();

  const handleDateChange = (newDate: Date) => {
    setCurrentDate(newDate);
  };

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
              
              <div className="flex justify-between items-start">
                
                {nextAppointment.client.contact.phoneIsWhatsapp ? (
                  // Se TEM WhatsApp, o container principal é um link <a>
                  <a href={`https://api.whatsapp.com/send/?phone=55${nextAppointment.client.contact.phone}`} target="_blank" rel="noopener noreferrer" className="block hover:opacity-80 transition-opacity">
                    <p className="text-2xl font-bold">{nextAppointment.client.name}</p>
                    <div className="flex items-center gap-2 mt-1">
                        <p className="text-xs opacity-80">{nextAppointment.client.contact.phone}</p>
                        <svg className="w-3.5 h-3.5" fill="currentColor" viewBox="0 0 24 24"><path d="M.057 24l1.687-6.163c-1.041-1.804-1.588-3.849-1.587-5.946.003-6.556 5.338-11.891 11.893-11.891 3.181.001 6.167 1.24 8.413 3.488 2.245 2.248 3.481 5.236 3.48 8.414-.003 6.557-5.338 11.892-11.894 11.892-1.99-.001-3.951-.5-5.688-1.448l-6.305 1.654zm6.597-3.807c1.676.995 3.276 1.591 5.392 1.592 5.448 0 9.886-4.434 9.889-9.885.002-5.462-4.415-9.89-9.881-9.892-5.452 0-9.887 4.434-9.889 9.886-.001 2.267.651 4.383 1.803 6.123l-1.218 4.439 4.555-1.193z"/></svg>
                    </div>
                    <div className="mt-2">
                      <p className="text-sm opacity-90">{nextAppointment.service.name}</p>
                      <p className="text-sm font-semibold opacity-90 mt-1">R$ {nextAppointment.price.toFixed(2).replace('.', ',')}</p>
                    </div>
                  </a>
                ) : (
                  // Se NÃO TEM WhatsApp, o container principal é um div
                  <div>
                    <p className="text-2xl font-bold">{nextAppointment.client.name}</p>
                     <div className="flex items-center gap-2 mt-1">
                        <p className="text-xs opacity-80">{nextAppointment.client.contact.phone}</p>
                    </div>
                    <div className="mt-2">
                      <p className="text-sm opacity-90">{nextAppointment.service.name}</p>
                      <p className="text-sm font-semibold opacity-90 mt-1">R$ {nextAppointment.price.toFixed(2).replace('.', ',')}</p>
                    </div>
                  </div>
                )}

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
              appointment={app}
              onEditClick={() => handleOpenEditModal(app)}
              onStatusClick={() => handleOpenStatusModal(app)}
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

      {/* MODAL STATUS */}
      <StatusUpdateModal
        isOpen={!!statusUpdateAppointment}
        onRequestClose={() => setStatusUpdateAppointment(null)}
        onUpdateSuccess={handleStatusUpdateSuccess}
        appointment={statusUpdateAppointment}
      />

      {/* MODAL DE PAGAMENTO */}
      <PaymentModal 
        isOpen={!!paymentAppointment}
        onRequestClose={() => setPaymentAppointment(null)}
        onSaveSuccess={() => {
          setPaymentAppointment(null);
          handleRefresh(); // Atualiza a lista para mostrar o novo status 'DONE'
        }}
        // Passa o agendamento para o modal de pagamento
        appointmentForPayment={paymentAppointment}
      />
    </div>
  );
}