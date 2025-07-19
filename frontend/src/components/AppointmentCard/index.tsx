import type { Appointment } from '../../types/appointment';
import { statusTranslations, statusStyles } from './styles';

type AppointmentCardProps = {
  appointment: Appointment;
  onEditClick: () => void;
  onStatusClick: () => void;
};

export function AppointmentCard({ appointment, onEditClick, onStatusClick }: AppointmentCardProps) {
  const { client, appointmentDate, status, professional, service, price, observations } = appointment;
  const whatsappLink = `https://api.whatsapp.com/send/?phone=55${client.contact.phone}`;

  const translatedStatus = statusTranslations[status as keyof typeof statusTranslations] || status;
  const style = statusStyles[translatedStatus as keyof typeof statusStyles];
  const isCanceled = status === 'CANCELED';

  const formatTime = (dateString: string) => {
    return new Date(dateString).toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
  };

  // Componente interno para exibir as informações do cliente
  const ClientInfo = () => (
    <div>
      <p className="font-bold text-lg text-gray-800">{client.name}</p>
      
      <div className="flex items-center gap-2 mt-1"> 
        <p className="text-sm text-gray-500">{client.contact.phone}</p>

        {client.contact.phoneIsWhatsapp && (
          <svg className="w-3.5 h-3.5 text-green-600" fill="currentColor" viewBox="0 0 24 24">
              <path d="M.057 24l1.687-6.163c-1.041-1.804-1.588-3.849-1.587-5.946.003-6.556 5.338-11.891 11.893-11.891 3.181.001 6.167 1.24 8.413 3.488 2.245 2.248 3.481 5.236 3.48 8.414-.003 6.557-5.338 11.892-11.894 11.892-1.99-.001-3.951-.5-5.688-1.448l-6.305 1.654zm6.597-3.807c1.676.995 3.276 1.591 5.392 1.592 5.448 0 9.886-4.434 9.889-9.885.002-5.462-4.415-9.89-9.881-9.892-5.452 0-9.887 4.434-9.889 9.886-.001 2.267.651 4.383 1.803 6.123l-1.218 4.439 4.555-1.193z"/>
          </svg>
        )}
      </div>
    </div>
  );

  return (
    <div className={`relative bg-white p-4 rounded-lg shadow-md border-l-4 ${style.border} ${isCanceled ? 'opacity-70' : ''}`}>
      <div className="flex items-center justify-between mb-2">
        <span className={`font-bold text-lg ${isCanceled ? 'text-gray-500 line-through' : 'text-gray-800'}`}>{formatTime(appointmentDate)}</span>
        <button onClick={onStatusClick} className={`text-xs font-semibold px-3 py-1 ${style.bg} ${style.text} rounded-full transition-transform hover:scale-105`}>
          {translatedStatus}
        </button>
      </div>
      
      <div className="mt-2 pr-10">
        
        {client.contact.phoneIsWhatsapp ? (
          <a href={whatsappLink} target="_blank" rel="noopener noreferrer" className="block hover:bg-gray-50 p-2 -m-2 rounded-lg transition-colors">
            <ClientInfo />
          </a>
        ) : (
          <div className="block p-2 -m-2">
            <ClientInfo />
          </div>
        )}

        <div className="pl-2 mt-2">
          <p className={`text-sm ${isCanceled ? 'text-gray-400 line-through' : 'text-gray-500'}`}>
            Profissional: <span className="font-medium text-gray-600">{professional.name}</span>
          </p>
          <p className={`text-sm ${isCanceled ? 'text-gray-400 line-through' : 'text-gray-500'}`}>
            Serviço: <span className="font-medium text-gray-600">{service.name}</span>
          </p>
          <p className={`text-sm font-semibold ${isCanceled ? 'text-gray-400 line-through' : 'text-gray-500'}`}>
            Preço: <span className="font-medium text-green-600">R$ {price.toFixed(2).replace('.', ',')}</span>
          </p>
        </div>

        {observations && (
          <p className="text-sm text-gray-500 mt-2 pt-2 border-t border-gray-100">
            <strong>Obs:</strong> {observations}
          </p>
        )}
      </div>

      {!isCanceled && (
        <div className="absolute bottom-4 right-4 group">
            <button 
              onClick={onEditClick} 
              className="p-2 rounded-full bg-gray-100 hover:bg-blue-100 hover:text-blue-700 transition-colors"
              aria-label="Editar agendamento"
            >
              <span className="text-xl">✏️</span>
            </button>
            <div className="absolute bottom-full right-0 mb-2 hidden group-hover:block bg-gray-800 text-white text-xs rounded py-1 px-2">
                Editar
            </div>
        </div>
      )}
    </div>
  );
}