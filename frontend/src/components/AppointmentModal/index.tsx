import { useEffect, useState } from 'react';
import Modal from 'react-modal';
import { useForm, Controller, SubmitHandler } from 'react-hook-form';
import Select from 'react-select';
import { format } from 'date-fns';
import type { Client } from '../../types/client';
import type { Professional } from '../../types/professional';
import type { SalonService } from '../../types/salonService';
import type { Appointment } from '../../types/appointment';

type FormData = {
  clientId: { value: number; label: string } | null;
  professionalId: { value: number; label: string } | null;
  serviceId: number | undefined;
  appointmentTime: string;
  price: number | undefined;
  observations: string;
};

type AppointmentModalProps = {
  isOpen: boolean;
  onRequestClose: () => void;
  onSaveSuccess: () => void;
  selectedDate: Date;
  appointmentToEdit?: Appointment | null;
};

const customStyles = {
  content: {
    top: '50%', left: '50%', right: 'auto', bottom: 'auto',
    marginRight: '-50%', transform: 'translate(-50%, -50%)',
    border: 'none', padding: '0', borderRadius: '8px',
  },
  overlay: {
    backgroundColor: 'rgba(0, 0, 0, 0.75)'
  }
};

Modal.setAppElement('#root');

export function AppointmentModal({ isOpen, onRequestClose, selectedDate, onSaveSuccess, appointmentToEdit }: AppointmentModalProps) {
  const [clients, setClients] = useState<{ value: number; label: string }[]>([]);
  const [professionals, setProfessionals] = useState<{ value: number; label: string }[]>([]);
  const [services, setServices] = useState<SalonService[]>([]);
  
  const { register, handleSubmit, control, reset, setValue, formState: { errors } } = useForm<FormData>();
  
  useEffect(() => {
    if (isOpen) {
      const token = localStorage.getItem('accessToken');
      if (!token) {
        console.error("Token não encontrado, não é possível buscar dados para o formulário.");
        return;
      }
      const headers = { 'Authorization': `Bearer ${token}` };

      // Busca clientes
      fetch('http://localhost:8080/api/v1/clients', { headers })
        .then(res => res.json())
        .then(data => {
          const list = data.content || data; 
          const clientOptions = list.map((c: Client) => ({ value: c.id, label: c.name }));
          setClients(clientOptions);
        });

      // Busca profissionais
      fetch('http://localhost:8080/api/v1/professionals', { headers })
        .then(res => res.json())
        .then(data => {
          const list = data.content || data;
          const professionalOptions = list.map((p: Professional) => ({ value: p.id, label: p.name }));
          setProfessionals(professionalOptions);
        });

      // Busca serviços
      fetch('http://localhost:8080/api/v1/salonServices', { headers })
        .then(res => res.json())
        .then(data => {
          const list = data.content || data;
          setServices(list);
        });
      
      if (appointmentToEdit) {
        // MODO EDITAR:
        console.log("Modo Edição: Preenchendo formulário para o agendamento ID:", appointmentToEdit.id);
        
        setValue('clientId', { value: appointmentToEdit.client.id, label: appointmentToEdit.client.name });
        setValue('professionalId', { value: appointmentToEdit.professional.id, label: appointmentToEdit.professional.name });
        setValue('serviceId', appointmentToEdit.service.id);
        setValue('appointmentTime', format(new Date(appointmentToEdit.appointmentDate), 'HH:mm'));
        setValue('price', appointmentToEdit.price);
        setValue('observations', appointmentToEdit.observations || '')
      } else {
        // MODO CRIAR: O modal abriu, mas não há agendamento para editar.
        // Limpa o formulário
        console.log("Modo Criação: Resetando o formulário.");
        reset({
            clientId: null,
            professionalId: null,
            serviceId: undefined,
            appointmentTime: '',
            price: undefined,
            observations: ''
            });
      }
    }
  }, [isOpen, appointmentToEdit, reset, setValue]);

  const onSubmit: SubmitHandler<FormData> = async (formData) => {
    const datePart = format(selectedDate, 'yyyy-MM-dd');
    const fullAppointmentDate = `${datePart}T${formData.appointmentTime}:00`;

    const apiPayload = {
      clientId: formData.clientId?.value,
      appointmentDate: fullAppointmentDate,
      observations: formData.observations,
      status: appointmentToEdit?.status || 'SCHEDULED',
      salonServiceId: formData.serviceId,
      professionalId: formData.professionalId?.value,
      price: formData.price,
    };

    // --- LÓGICA DE ENVIO API ---
    const method = appointmentToEdit ? 'PUT' : 'POST';
    const url = appointmentToEdit 
      ? `http://localhost:8080/api/v1/appointments/${appointmentToEdit.id}`
      : 'http://localhost:8080/api/v1/appointments';

    try {
      const token = localStorage.getItem('accessToken');
      const response = await fetch(url, {
        method: method,
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(apiPayload)
      });

      if (!response.ok) {
        const errorData = await response.json();
        // O backend envia 'message' dentro de 'StandardError'
        throw new Error(errorData.error || 'Falha na operação.');
      }

      // Se deu tudo certo
      alert(`Agendamento ${appointmentToEdit ? 'atualizado' : 'criado'} com sucesso!`);
      onSaveSuccess();
      reset();
      onRequestClose();

    } catch (err: any) {
      alert(`Erro: ${err.message}`);
    }
  };
    // --- FIM DA LÓGICA DE ENVIO ---

    const handleCloseModal = () => {
    reset();
    onRequestClose();
  }

  return (
    <Modal isOpen={isOpen} onRequestClose={handleCloseModal} style={customStyles} contentLabel="Novo Agendamento">
      <div className="bg-white rounded-lg shadow-xl p-6 w-full max-w-md">
        <h3 className="text-xl font-semibold mb-4 text-gray-800">{appointmentToEdit ? 'Editar Agendamento' : 'Novo Agendamento'} para {format(selectedDate, 'dd/MM/yyyy')}</h3>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">

          <div>
            <label className="block text-sm font-medium text-gray-700">Cliente</label>
            <Controller
              name="clientId"
              control={control}
              rules={{ required: true }}
              render={({ field }) => <Select {...field} options={clients} placeholder="Digite para buscar um cliente..." />}
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700">Profissional</label>
            <Controller
              name="professionalId"
              control={control}
              rules={{ required: true }}
              render={({ field }) => <Select {...field} options={professionals} placeholder="Selecione um profissional..." />}
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700">Serviço</label>
            <select {...register("serviceId", { required: true })} className="mt-1 block w-full border-gray-300 rounded-md shadow-sm">
                <option value="">Selecione um serviço...</option>
                {services.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
            </select>
          </div>
          
          <div>
            <label htmlFor="appointmentTime" className="block text-sm font-medium text-gray-700">Horário</label>
            <input type="time" id="appointmentTime" {...register("appointmentTime", { required: "Horário é obrigatório" })} className="mt-1 block w-full border-gray-300 rounded-md shadow-sm" />
            {errors.appointmentTime && <p className="text-red-500 text-xs mt-1">{errors.appointmentTime.message}</p>}
          </div>

          <div>
            <label htmlFor="price" className="block text-sm font-medium text-gray-700">Preço (R$)</label>
            <input type="number" step="0.01" id="price" {...register("price", { required: "Preço é obrigatório", valueAsNumber: true })} className="mt-1 block w-full border-gray-300 rounded-md shadow-sm" />
            {errors.price && <p className="text-red-500 text-xs mt-1">{errors.price.message}</p>}
          </div>

          <div>
            <label htmlFor="observations" className="block text-sm font-medium text-gray-700">Observações</label>
            <textarea id="observations" {...register("observations")} rows={3} className="mt-1 block w-full border-gray-300 rounded-md shadow-sm"></textarea>
          </div>

          <div className="mt-6 flex justify-end gap-3">
            <button type="button" onClick={handleCloseModal} className="bg-gray-200 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-300">Cancelar</button>
            <button type="submit" className="bg-green-500 text-white px-4 py-2 rounded-md hover:bg-green-600">Salvar</button>
          </div>
        </form>
      </div>
    </Modal>
  );
}