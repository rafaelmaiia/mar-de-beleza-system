import Modal from 'react-modal';
import { useForm, SubmitHandler } from 'react-hook-form';
import { useEffect } from 'react';
import toast from 'react-hot-toast';
import { useAuth } from '../../hooks/useAuth';
import type { SalonService } from '../../types/salonService';

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
  name: string;
  serviceType: string;
  durationInMinutes: number;
  price: number;
};

type ServiceModalProps = {
  isOpen: boolean;
  onRequestClose: () => void;
  onSaveSuccess: () => void;
  serviceToEdit: SalonService | null;
};

export function ServiceModal({ isOpen, onRequestClose, onSaveSuccess, serviceToEdit }: ServiceModalProps) {
  const { register, handleSubmit, reset, setValue } = useForm<FormData>();
  const { token } = useAuth();

  useEffect(() => {
    if (serviceToEdit) {
      setValue('name', serviceToEdit.name);
      setValue('serviceType', serviceToEdit.serviceType);
      setValue('durationInMinutes', serviceToEdit.durationInMinutes);
      setValue('price', serviceToEdit.price);
    } else {
      reset({ name: '', serviceType: 'OTHER', durationInMinutes: undefined, price: undefined });
    }
  }, [serviceToEdit, isOpen, reset, setValue]);

  const onSubmit: SubmitHandler<FormData> = async (data) => {
    const method = serviceToEdit ? 'PUT' : 'POST';
    const url = serviceToEdit
      ? `http://localhost:8080/api/v1/salonServices/${serviceToEdit.id}`
      : `http://localhost:8080/api/v1/salonServices`;

    try {
      const response = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
        body: JSON.stringify(data)
      });
      if (!response.ok) throw new Error('Falha ao salvar serviço.');
      
      toast.success(`Serviço ${serviceToEdit ? 'atualizado' : 'criado'} com sucesso!`);
      onSaveSuccess();
    } catch (error: any) {
      toast.error(error.message);
    }
  };

  return (
    <Modal isOpen={isOpen} onRequestClose={onRequestClose} style={customStyles}>
      <div className="bg-white rounded-lg shadow-xl p-6 w-11/12 max-w-lg">
        <h3 className="text-xl font-semibold mb-4">{serviceToEdit ? 'Editar Serviço' : 'Novo Serviço'}</h3>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">Nome do Serviço</label>
            <input {...register("name", { required: true })} className="mt-1 block w-full border-gray-300 rounded-md shadow-sm" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Tipo</label>
            <select {...register("serviceType")} className="mt-1 block w-full border-gray-300 rounded-md shadow-sm">
                <option value="HAIR">Cabelo</option>
                <option value="LASH">Cílios</option>
                <option value="EYEBROW">Sobrancelha</option>
                <option value="OTHER">Outro</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Duração (minutos)</label>
            <input type="number" {...register("durationInMinutes", { required: true, valueAsNumber: true })} className="mt-1 block w-full border-gray-300 rounded-md shadow-sm" />
          </div>
           <div>
            <label className="block text-sm font-medium text-gray-700">Preço (R$)</label>
            <input type="number" step="0.01" {...register("price", { required: true, valueAsNumber: true })} className="mt-1 block w-full border-gray-300 rounded-md shadow-sm" />
          </div>
          <div className="mt-6 flex justify-end gap-3">
            <button type="button" onClick={onRequestClose} className="bg-gray-200 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-300">Cancelar</button>
            <button type="submit" className="bg-green-500 text-white px-4 py-2 rounded-md hover:bg-green-600">Salvar</button>
          </div>
        </form>
      </div>
    </Modal>
  );
}