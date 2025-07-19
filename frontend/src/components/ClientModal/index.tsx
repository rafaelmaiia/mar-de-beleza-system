import { IMaskInput } from 'react-imask';
import { Controller } from 'react-hook-form';
import Modal from 'react-modal';
import { useForm, SubmitHandler } from 'react-hook-form';
import { useEffect } from 'react';
import toast from 'react-hot-toast';
import { useAuth } from '../../hooks/useAuth';
import type { Client } from '../../types/client';
import { genderTranslations } from '../../constants/clientConstants';


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
  birthDate: string;
  gender: string;
  phone: string;
  phoneIsWhatsapp: boolean;
}

type ClientModalProps = {
  isOpen: boolean;
  onRequestClose: () => void;
  onSaveSuccess: () => void;
  clientToEdit: Client | null;
};

export function ClientModal({ isOpen, onRequestClose, onSaveSuccess, clientToEdit }: ClientModalProps) {
  const { register, handleSubmit, reset, setValue, control, formState: { errors } } = useForm<FormData>();
  const { token } = useAuth();

  useEffect(() => {
    if (clientToEdit) {
      setValue('name', clientToEdit.name);
      setValue('birthDate', clientToEdit.birthDate);
      setValue('gender', clientToEdit.gender);
      setValue('phone', clientToEdit.contact.phone);
      setValue('phoneIsWhatsapp', clientToEdit.contact.phoneIsWhatsapp);
    } else {
      reset({ name: '', birthDate: '', gender: '', phone: '', phoneIsWhatsapp: true });
    }
  }, [clientToEdit, isOpen, reset, setValue]);

  const onSubmit: SubmitHandler<FormData> = async (data) => {
    // Monta payload para a API
    const apiPayload = {
        name: data.name,
        birthDate: data.birthDate,
        gender: data.gender,
        contact: {
            phone: data.phone,
            phoneIsWhatsapp: data.phoneIsWhatsapp
        },
    };

    const method = clientToEdit ? 'PUT' : 'POST';
    const url = clientToEdit
      ? `http://localhost:8080/api/v1/clients/${clientToEdit.id}`
      : `http://localhost:8080/api/v1/clients`;

    try {
      const response = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
        body: JSON.stringify(apiPayload)
      });
      if (!response.ok) throw new Error('Falha ao salvar cliente.');
      
      toast.success(`Cliente ${clientToEdit ? 'atualizado' : 'criado'} com sucesso!`);
      onSaveSuccess();
    } catch (error: any) {
      toast.error(error.message);
    }
  };

  return (
    <Modal isOpen={isOpen} onRequestClose={onRequestClose} style={customStyles}>
      <div className="bg-white rounded-lg shadow-xl p-6 w-11/12 max-w-lg">
        <h3 className="text-xl font-semibold mb-4">{clientToEdit ? 'Editar Cliente' : 'Novo Cliente'}</h3>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          
          <div>
            <label className="block text-sm font-medium text-gray-700">Nome</label>
            <input {...register("name", { required: true })} className="mt-1 block w-full border-gray-300 rounded-md shadow-sm" />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700">Data de Nascimento</label>
            <input type="date" {...register("birthDate")} className="mt-1 block w-full border-gray-300 rounded-md shadow-sm" />
          </div>

          {/* --- CAMPO DE GÊNERO --- */}
          <div>
              <label htmlFor="gender" className="block text-sm font-medium text-gray-700">Gênero</label>
              <select
                  id="gender"
                  {...register("gender")}
                  className="mt-1 block w-full border-gray-300 rounded-md shadow-sm"
              >
                  {Object.entries(genderTranslations).map(([key, value]) => (
                      <option key={key} value={key}>{value}</option>
                  ))}
              </select>
          </div>


          {/* --- CAMPO DE TELEFONE COM MASCARA 'react-imask' --- */}
          <div>
            <label htmlFor="phone" className="block text-sm font-medium text-gray-700">Telefone</label>
            <Controller
              name="phone"
              control={control}
              rules={{ 
                required: "O telefone é obrigatório",
                pattern: {
                  value: /^\d{10,11}$/, // Valida se o campo tem entre 10 e 11 NÚMEROS
                  message: "O telefone deve ter 10 ou 11 dígitos (DDD + número)."
                }
              }}
              render={({ field }) => (
                <IMaskInput
                  mask="(00) 00000-0000"
                  value={field.value}
                  onAccept={(value, mask) => field.onChange(mask.unmaskedValue)}
                  placeholder="(85) 99999-9999"
                  className="mt-1 block w-full border-gray-300 rounded-md shadow-sm"
                />
              )}
            />
            {errors.phone && <p className="text-red-500 text-xs mt-1">{errors.phone.message}</p>}
          </div>
          {/* --- FIM DO CAMPO DE TELEFONE --- */}

          <div className="flex items-center">
            <input type="checkbox" {...register("phoneIsWhatsapp")} className="h-4 w-4 rounded" />
            <label className="ml-2 block text-sm text-gray-900">Este número é WhatsApp</label>
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