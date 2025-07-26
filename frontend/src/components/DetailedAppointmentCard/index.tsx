import type { Appointment } from '../../types/appointment';
import { statusTranslations, statusStyles } from '../../constants/statusConstants';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';

type DetailedAppointmentCardProps = {
  appointment: Appointment;
  onEdit: () => void;
  onCancel: () => void;
  onStatusClick: () => void;
};

export function DetailedAppointmentCard({ appointment, onEdit, onCancel, onStatusClick }: DetailedAppointmentCardProps) {
  const { client, appointmentDate, status, professional, service, price } = appointment;

  const translatedStatus = statusTranslations[status as keyof typeof statusTranslations] || status;
  const style = statusStyles[translatedStatus as keyof typeof statusStyles];
  const isCanceled = status === 'CANCELED';

  // Formata a data e a hora
  const formattedDateTime = format(new Date(appointmentDate), "dd/MM/yyyy - HH:mm", { locale: ptBR });

  return (
    <div className={`bg-white p-4 rounded-lg shadow-md ${isCanceled ? 'opacity-70' : ''}`}>
      <div className="flex justify-between items-start">
        {/* Lado Esquerdo: Detalhes */}
        <div>
          <p className="text-sm text-gray-500">{formattedDateTime}</p>
          <p className={`font-bold text-lg text-gray-800 ${isCanceled ? 'line-through' : ''}`}>{client.name}</p>
          <p className={`text-sm text-gray-600 ${isCanceled ? 'line-through' : ''}`}>
            Serviço: <span className="font-medium">{service.name}</span>
          </p>
          <p className={`text-sm text-gray-600 ${isCanceled ? 'line-through' : ''}`}>
            Profissional: <span className="font-medium">{professional.name}</span>
          </p>
        </div>

        {/* Lado Direito: Status e Preço */}
        <div className="text-right">
          <button onClick={onStatusClick} className={`text-xs font-semibold px-3 py-1 ${style.bg} ${style.text} rounded-full transition-transform hover:scale-105 whitespace-nowrap`}>
            {translatedStatus}
          </button>
          <p className={`font-semibold text-lg mt-2 ${isCanceled ? 'line-through' : ''}`}>
            R$ {price.toFixed(2).replace('.', ',')}
          </p>
        </div>
      </div>

      {/* Ações na parte de baixo */}
      <div className="border-t mt-4 pt-3 flex justify-end gap-2">
        <button 
          onClick={onEdit} 
          disabled={isCanceled}
          className="text-sm font-medium text-blue-600 hover:bg-blue-50 px-3 py-1 rounded-md disabled:text-gray-400 disabled:cursor-not-allowed"
        >
          Editar
        </button>
        {/* O botão de "Cancelar" pode se tornar "Reativar" se o agendamento estiver cancelado */}
        <button 
          onClick={onCancel} 
          className={`text-sm font-medium px-3 py-1 rounded-md ${
            isCanceled 
            ? 'text-green-600 hover:bg-green-50' 
            : 'text-red-600 hover:bg-red-50'
          }`}
        >
          {isCanceled ? 'Reativar' : 'Cancelar'}
        </button>
      </div>
    </div>
  );
}