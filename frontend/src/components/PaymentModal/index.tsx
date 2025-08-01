import Modal from 'react-modal';
import { useForm, SubmitHandler } from 'react-hook-form';
import { useEffect } from 'react';
import toast from 'react-hot-toast';
import { useAuth } from '../../hooks/useAuth';
import type { Payment, PaymentMethod } from '../../types/payment';
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
  paymentToEdit: Payment | null;
};

export function PaymentModal({ isOpen, onRequestClose, onSaveSuccess, paymentToEdit }: PaymentModalProps) {
  const { register, handleSubmit, reset, setValue, formState: { isSubmitting } } = useForm<FormData>();
  const { token } = useAuth();

  useEffect(() => {
    if (paymentToEdit) {
      setValue('totalAmount', paymentToEdit.totalAmount);
      setValue('paymentMethod', paymentToEdit.paymentMethod);
      setValue('observations', paymentToEdit.observations || '');
    }
  }, [paymentToEdit, isOpen, setValue]);

  const handleClose = () => {
    reset();
    onRequestClose();
  };

  const onSubmit: SubmitHandler<FormData> = async (data) => {
    if (!paymentToEdit) return;

    const apiPayload = {
      appointmentId: paymentToEdit.appointment.id,
      totalAmount: data.totalAmount,
      paymentMethod: data.paymentMethod,
      observations: data.observations,
    };
    
    try {
      const response = await fetch(`http://localhost:8080/api/v1/payments/${paymentToEdit.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
        body: JSON.stringify(apiPayload)
      });
      if (!response.ok) throw new Error('Falha ao atualizar o pagamento.');
      
      toast.success('Pagamento atualizado com sucesso!');
      onSaveSuccess();
      handleClose();
    } catch (error: any) {
      toast.error(error.message);
    }
  };

  return (
    <Modal isOpen={isOpen} onRequestClose={handleClose} style={customStyles}>
      <div className="bg-white rounded-lg shadow-xl p-6 w-11/12 max-w-md">
        <h3 className="text-xl font-semibold mb-4 text-gray-800">Editar Pagamento</h3>
        {paymentToEdit && (
            <p className="text-sm text-gray-600 mb-4">
              Cliente: <span className="font-medium">{paymentToEdit.appointment.client.name}</span>
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
              {isSubmitting ? 'Salvando...' : 'Salvar Alterações'}
            </button>
          </div>
        </form>
      </div>
    </Modal>
  );
}