import Modal from 'react-modal';
import { useForm, SubmitHandler } from 'react-hook-form';
import { useEffect } from 'react';
import toast from 'react-hot-toast';
import { useAuth } from '../../hooks/useAuth';
import type { Payment, PaymentMethod } from '../../types/payment';
import type { Appointment } from '../../types/appointment';
import { paymentMethodTranslations } from '../../constants/paymentConstants';

const customStyles = {
  content: {
    top: '50%', left: '50%', right: 'auto', bottom: 'auto',
    marginRight: '-50%', transform: 'translate(-50%, -50%)',
    background: 'none',
    border: 'none',
    padding: '0',
  },
  overlay: {
    backgroundColor: 'rgba(0, 0, 0, 0.50)'
  }
};

Modal.setAppElement('#root');

type FormData = {
  totalAmount: number;
  paymentMethod: string;
  observations: string;
};

type PaymentModalProps = {
  isOpen: boolean;
  onRequestClose: () => void;
  onSaveSuccess: () => void;
  // Pode ser um Pagamento (para editar) ou um Agendamento (para criar um pagamento novo)
  paymentToEdit?: Payment | null;
  appointmentForPayment?: Appointment | null; 
};

export function PaymentModal({ isOpen, onRequestClose, onSaveSuccess, paymentToEdit, appointmentForPayment }: PaymentModalProps) {
  const { register, handleSubmit, reset, setValue, formState: { isSubmitting } } = useForm<FormData>();
  const { token } = useAuth();

  const isEditMode = !!paymentToEdit;
  const targetAppointment = paymentToEdit?.appointment || appointmentForPayment;

  useEffect(() => {
    // Roda sempre que o modal abre ou o modo (criar/editar) muda
    if (isOpen) {
      if (paymentToEdit) {
        // MODO EDIÇÃO: Preenche com dados do pagamento que já existe
        console.log("Modo Edição: Preenchendo formulário para o pagamento ID:", paymentToEdit.id);
        setValue('totalAmount', paymentToEdit.totalAmount);
        setValue('paymentMethod', paymentToEdit.paymentMethod);
        setValue('observations', paymentToEdit.observations || '');
      } else if (appointmentForPayment) {
        // MODO CRIAÇÃO: Preenche com dados do agendamento que foi concluído
        console.log("Modo Criação: Pré-preenchendo valor do agendamento:", appointmentForPayment.price);
        
        reset({
            totalAmount: appointmentForPayment.price,
            paymentMethod: 'PIX', // Define um padrão
            observations: ''
        });
      }
    }
  }, [isOpen, paymentToEdit, appointmentForPayment, setValue, reset]);

  const handleClose = () => {
    reset();
    onRequestClose();
  };

  const onSubmit: SubmitHandler<FormData> = async (data) => {
    if (!targetAppointment) return;

    const apiPayload = {
      appointmentId: targetAppointment.id,
      totalAmount: data.totalAmount,
      paymentMethod: data.paymentMethod,
      observations: data.observations,
    };
    
    const method = isEditMode ? 'PUT' : 'POST';
    const url = isEditMode
      ? `http://localhost:8080/api/v1/payments/${paymentToEdit!.id}`
      : 'http://localhost:8080/api/v1/payments';
    
    try {
      const response = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
        body: JSON.stringify(apiPayload)
      });
      if (!response.ok) throw new Error(`Falha ao ${isEditMode ? 'atualizar' : 'registrar'} o pagamento.`);
      
      toast.success(`Pagamento ${isEditMode ? 'atualizado' : 'registrado'} com sucesso!`);
      onSaveSuccess();
      handleClose();
    } catch (error: any) {
      toast.error(error.message);
    }
  };

  return (
    <Modal isOpen={isOpen} onRequestClose={handleClose} style={customStyles}>
      <div className="bg-white rounded-lg shadow-xl p-6 w-11/12 max-w-md">
        <h3 className="text-xl font-semibold mb-4 text-gray-800">
          {isEditMode ? 'Editar Pagamento' : 'Registrar Pagamento'}
        </h3>
        {targetAppointment && (
            <p className="text-sm text-gray-600 mb-4">
              Cliente: <span className="font-medium">{targetAppointment.client.name}</span>
            </p>
        )}
        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="space-y-4">
            <div>
              <label htmlFor="totalAmount" className="block text-sm font-medium text-gray-700">Valor Pago (R$)</label>
              <input 
                type="number" 
                step="0.01" 
                id="totalAmount" 
                {...register("totalAmount", { required: true, valueAsNumber: true })} 
                className="mt-1 block w-full border-gray-300 rounded-md shadow-sm"
              />
            </div>
            <div>
              <label htmlFor="paymentMethod" className="block text-sm font-medium text-gray-700">Forma de Pagamento</label>
              <select id="paymentMethod" {...register("paymentMethod", { required: true })} className="mt-1 block w-full border-gray-300 rounded-md shadow-sm">
                {Object.entries(paymentMethodTranslations).map(([key, value]) => (
                  <option key={key} value={key}>{value}</option>
                ))}
              </select>
            </div>
             <div>
              <label htmlFor="observations" className="block text-sm font-medium text-gray-700">Observações</label>
              <textarea id="observations" {...register("observations")} rows={3} className="mt-1 block w-full border-gray-300 rounded-md shadow-sm" />
            </div>
          </div>
          <div className="mt-6 flex justify-end gap-3">
            <button type="button" onClick={handleClose} className="bg-gray-200 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-300">Cancelar</button>
            <button type="submit" disabled={isSubmitting} className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 disabled:bg-blue-400">
              {isSubmitting ? 'Salvando...' : (isEditMode ? 'Salvar Alterações' : 'Salvar Pagamento')}
            </button>
          </div>
        </form>
      </div>
    </Modal>
  );
}