import React from 'react';
import { statusTranslations, statusStyles } from './styles';

type AppointmentCardProps = {
  time: string;
  clientName: string;
  clientPhone: string;
  professionalName: string;
  services: string;
  price: number;
  status: string;
  observations?: string;
  onEditClick: () => void;
  onStatusClick: () => void;
};

export function AppointmentCard({ time, clientName, clientPhone, professionalName, services, price, status, observations, onEditClick, onStatusClick }: AppointmentCardProps) {
  
  const translatedStatus = statusTranslations[status as keyof typeof statusTranslations] || status;
  
  const style = statusStyles[translatedStatus as keyof typeof statusStyles];
  
  const isCanceled = status === 'CANCELED';

  const whatsappLink = `https://api.whatsapp.com/send/?phone=55${clientPhone}`;

  return (
    <div className={`relative bg-white p-4 rounded-lg shadow-md border-l-4 ${style.border} ${isCanceled ? 'opacity-70' : ''}`}>
      
      <div className="flex items-center justify-between mb-2">
        <span className={`font-bold text-lg ${isCanceled ? 'text-gray-500 line-through' : 'text-gray-800'}`}>{time}</span>
        <button onClick={onStatusClick} className={`text-xs font-semibold px-3 py-1 ${style.bg} ${style.text} rounded-full transition-transform hover:scale-105`}>
          {translatedStatus}
        </button>
      </div>

      <div className="mt-2 pr-10">
          <a href={whatsappLink} target="_blank" rel="noopener noreferrer" className="block hover:bg-gray-50 p-2 -m-2 rounded-lg transition-colors">
              <p className={`font-semibold ${isCanceled ? 'text-gray-500 line-through' : 'text-gray-700'}`}>{clientName}</p>
              <p className={`text-sm ${isCanceled ? 'text-gray-400' : 'text-gray-500'}`}>{clientPhone}</p>
          </a>

          <div className="pl-2 mt-2">
              <p className={`text-sm ${isCanceled ? 'text-gray-400 line-through' : 'text-gray-500'}`}>
                  Profissional: <span className="font-medium text-gray-600">{professionalName}</span>
              </p>
              <p className={`text-sm ${isCanceled ? 'text-gray-400 line-through' : 'text-gray-500'}`}>
                  Serviço: <span className="font-medium text-gray-600">{services}</span>
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