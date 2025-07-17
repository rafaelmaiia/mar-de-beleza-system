import Modal from 'react-modal';
import toast from 'react-hot-toast';
import { useAuth } from '../../hooks/useAuth';
import type { Appointment } from '../../types/appointment';
import { statusTranslations, statusStyles } from '../AppointmentCard/styles';

type StatusUpdateModalProps = {
  isOpen: boolean;
  onRequestClose: () => void;
  onUpdateSuccess: () => void;
  appointment: Appointment | null;
};

// Estilos para centralizar e formatar o modal
const customStyles = {
  content: {
    top: '50%', left: '50%', right: 'auto', bottom: 'auto',
    marginRight: '-50%', transform: 'translate(-50%, -50%)',
    background: 'none', // Remove o fundo branco padrão da biblioteca
    border: 'none',     // Remove a borda padrão
    padding: '0',
  },
  overlay: {
    backgroundColor: 'rgba(0, 0, 0, 0.50)' // Fundo um pouco mais escuro
  }
};

const statusOptions = Object.keys(statusTranslations) as (keyof typeof statusTranslations)[];

Modal.setAppElement('#root');

export function StatusUpdateModal({ isOpen, onRequestClose, onUpdateSuccess, appointment }: StatusUpdateModalProps) {
  const { token } = useAuth();

  const handleStatusUpdate = async (newStatusKey: string) => {
    if (!appointment) return;

    // O payload agora é muito mais simples!
    const apiPayload = {
      status: newStatusKey,
    };

    try {
      const response = await fetch(`http://localhost:8080/api/v1/appointments/${appointment.id}/status`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(apiPayload),
      });

      if (!response.ok) { throw new Error("Falha ao atualizar o status.") }
      
      toast.success('Status atualizado com sucesso!');
      onUpdateSuccess();
      onRequestClose();
    } catch (err: any) {
      toast.error(err.message);
    }
  };

  return (
    <Modal isOpen={isOpen} onRequestClose={onRequestClose} style={customStyles}>
      {/* A MUDANÇA ESTÁ AQUI: 
        - Removemos 'w-11/12' para não limitar a largura.
        - 'max-w-md' é uma classe que dá uma largura máxima maior.
        - 'min-w-[300px]' garante uma largura mínima para evitar quebras.
      */}
      <div className="bg-white rounded-lg shadow-xl p-6 min-w-[300px] max-w-md">
        
        {/* Adicionamos 'whitespace-nowrap' para impedir a quebra de linha no título */}
        <h3 className="text-xl font-semibold mb-6 text-gray-800 text-center whitespace-nowrap">
          Atualizar Status do Agendamento
        </h3>

        <div className="flex flex-col space-y-3">
          {Object.entries(statusTranslations).map(([statusKey, translatedValue]) => {
            const style = statusStyles[translatedValue as keyof typeof statusStyles];
            return (
              <button 
                key={statusKey} 
                onClick={() => handleStatusUpdate(statusKey)}
                // O 'whitespace-nowrap' aqui também impede que o texto do botão quebre
                className={`w-full ${style.bg} ${style.text} px-4 py-3 rounded-md hover:opacity-80 font-semibold transition-opacity whitespace-nowrap`}
              >
                {translatedValue}
              </button>
            )
          })}
        </div>
        <button onClick={onRequestClose} className="mt-6 w-full bg-gray-200 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-300">Cancelar</button>
      </div>
    </Modal>
  );
}