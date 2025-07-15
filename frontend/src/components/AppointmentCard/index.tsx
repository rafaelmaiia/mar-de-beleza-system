import React from 'react';

type AppointmentCardProps = {
  time: string;
  clientName: string;
  professionalName: string;
  services: string;
  status: string;
  observations?: string;
  onEditClick: () => void;
};

// DICIONÁRIO DE TRADUÇÕES
const statusTranslations = {
  SCHEDULED: 'Agendado',
  CONFIRMED: 'Confirmado',
  DONE: 'Concluído',
  CANCELED: 'Cancelado',
  NO_SHOW: 'Não Compareceu',
  RESCHEDULED: 'Reagendado',
};

// DICIONÁRIO DE ESTILOS STATUS
const statusStyles = {
  Agendado:    { border: 'border-yellow-500', bg: 'bg-yellow-100', text: 'text-yellow-800' },
  Confirmado:  { border: 'border-blue-500',   bg: 'bg-blue-100',   text: 'text-blue-800'   },
  Concluído:   { border: 'border-green-500',  bg: 'bg-green-100',  text: 'text-green-800'  },
  Cancelado:   { border: 'border-red-500',    bg: 'bg-red-100',    text: 'text-red-800'    },
  'Não Compareceu': { border: 'border-gray-500',   bg: 'bg-gray-200',   text: 'text-gray-800'   },
  Reagendado:  { border: 'border-purple-500', bg: 'bg-purple-100', text: 'text-purple-800' },
};

export function AppointmentCard({ time, clientName, professionalName, services, status, observations, onEditClick }: AppointmentCardProps) {
  
  const translatedStatus = statusTranslations[status as keyof typeof statusTranslations] || status;
  
  const style = statusStyles[translatedStatus as keyof typeof statusStyles];
  
  const isCanceled = status === 'CANCELED';

  return (
    <div className={`relative bg-white p-4 rounded-lg shadow-md border-l-4 ${style.border} ${isCanceled ? 'opacity-70' : ''}`}>
      
      <div className="flex items-center justify-between mb-2">
        <span className={`font-bold text-lg ${isCanceled ? 'text-gray-500 line-through' : 'text-gray-800'}`}>{time}</span>
        <span className={`text-xs font-semibold px-3 py-1 ${style.bg} ${style.text} rounded-full`}>{translatedStatus}</span>
      </div>

      <div className="mt-2 pr-10">
        <p className={`font-semibold ${isCanceled ? 'text-gray-500 line-through' : 'text-gray-700'}`}>{clientName}</p>
        <p className={`text-sm ${isCanceled ? 'text-gray-400 line-through' : 'text-gray-500'}`}>
          Profissional: <span className="font-medium text-gray-600">{professionalName}</span>
        </p>
        <p className={`text-sm ${isCanceled ? 'text-gray-400 line-through' : 'text-gray-500'}`}>
          Serviços: <span className="font-medium text-gray-600">{services}</span>
        </p>

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