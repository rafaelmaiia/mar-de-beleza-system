import { useEffect, useState, useMemo } from 'react';
import Modal from 'react-modal';
import { useForm, Controller, SubmitHandler } from 'react-hook-form';
import Select from 'react-select';
import { format } from 'date-fns';
import toast from 'react-hot-toast';
import { useAuth } from '../../hooks/useAuth';

// tipos
import type { Client } from '../../types/client';
import type { SystemUser } from '../../types/user';
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

export function AppointmentModal({ isOpen, onRequestClose, onSaveSuccess, selectedDate, appointmentToEdit }: AppointmentModalProps) {
  const { token } = useAuth();

  // Estados para as opções dos selects
  const [clientOptions, setClientOptions] = useState<{ value: number; label: string }[]>([]);
  const [professionalOptions, setProfessionalOptions] = useState<{ value: number; label: string }[]>([]);
  
  // Estados para as listas completas de dados
  const [allProfessionals, setAllProfessionals] = useState<SystemUser[]>([]);
  const [allServices, setAllServices] = useState<SalonService[]>([]);
  
  // Estado para o profissional atualmente selecionado (para o filtro em cascata)
  const [selectedProfessional, setSelectedProfessional] = useState<SystemUser | null>(null);
  const [areOptionsLoading, setAreOptionsLoading] = useState(true);

  const { register, handleSubmit, control, reset, setValue, formState: { errors } } = useForm<FormData>();
  
  // EFEITO 1: Responsável APENAS por buscar os dados das listas
  useEffect(() => {
    if (isOpen) {
      setAreOptionsLoading(true);
      const token = localStorage.getItem('accessToken');
      if (!token) {
        toast.error("Usuário não autenticado.");
        setAreOptionsLoading(false);
        return;
      }
      const headers = { 'Authorization': `Bearer ${token}` };

      // Preparamos as "promessas" de busca
      const fetchClientsPromise = fetch('http://localhost:8080/api/v1/clients', { headers }).then(res => res.json());
      const fetchProfessionalsPromise = fetch('http://localhost:8080/api/v1/users?canBeScheduled=true', { headers }).then(res => res.json());
      const fetchServicesPromise = fetch('http://localhost:8080/api/v1/salonServices', { headers }).then(res => res.json());

      //Promise.all para esperar que TODAS as buscas terminem
      Promise.all([fetchClientsPromise, fetchProfessionalsPromise, fetchServicesPromise])
        .then(([clientData, professionalData, serviceData]) => {
          const clients = clientData.content || clientData;
          const professionals = professionalData || [];
          const services = serviceData.content || serviceData;

          setClientOptions(clients.map((c: Client) => ({ value: c.id, label: c.name })));
          setProfessionalOptions(professionals.map((p: SystemUser) => ({ value: p.id, label: p.name })));
          
          // Guarda as listas completas para lógica de filtro
          setAllProfessionals(professionals);
          setAllServices(services);
        })
        .catch(error => {
          console.error("Erro ao buscar dados para o modal:", error);
          toast.error("Não foi possível carregar os dados do formulário.");
        })
        .finally(() => {
          setAreOptionsLoading(false);
        });
    }
  }, [isOpen]);

  // EFEITO 2: Responsável por preencher o formulário (EXCETO o serviço) e definir o profissional -> corrige timing de serviço não sendo definido corretamente no modo de edição
  useEffect(() => {
    if (isOpen && !areOptionsLoading) {
      if (appointmentToEdit) {
        // MODO EDITAR
        // Encontra e define o profissional selecionado. Isso vai disparar o useMemo.
        const professionalToSet = allProfessionals.find(p => p.id === appointmentToEdit.professional.id);
        setSelectedProfessional(professionalToSet || null);

        // Preenche todos os outros campos
        setValue('clientId', { value: appointmentToEdit.client.id, label: appointmentToEdit.client.name });
        setValue('professionalId', professionalToSet ? { value: professionalToSet.id, label: professionalToSet.name } : null);
        setValue('appointmentTime', format(new Date(appointmentToEdit.appointmentDate), 'HH:mm'));
        setValue('price', appointmentToEdit.price);
        setValue('observations', appointmentToEdit.observations || '');
      } else {
        // MODO CRIAR
        console.log("Modo Criação: Resetando o formulário.");
        setSelectedProfessional(null);
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
  }, [isOpen, areOptionsLoading, appointmentToEdit, allProfessionals, reset, setValue]);

  // LÓGICA DE FILTRO EM CASCATA
  const availableServices = useMemo(() => {
    if (!selectedProfessional) return [];
    return allServices.filter(service => 
      selectedProfessional.specialties.includes(service.serviceType)
    );
  }, [selectedProfessional, allServices]);

  // EFEITO 3: Responsável APENAS por definir o serviço
  useEffect(() => {
    // Ele só roda DEPOIS que 'availableServices' foi recalculada
    // E só no modo de edição.
    if (appointmentToEdit && availableServices.length > 0) {
      // Verifica se o serviço do agendamento realmente existe na lista de serviços disponíveis
      const serviceExistsInList = availableServices.some(s => s.id === appointmentToEdit.service.id);
      if (serviceExistsInList) {
        setValue('serviceId', appointmentToEdit.service.id);
      }
    }
  }, [availableServices, appointmentToEdit, setValue]);

  const onSubmit: SubmitHandler<FormData> = async (formData) => {
    const datePart = format(selectedDate, 'yyyy-MM-dd');
    const fullAppointmentDate = `${datePart}T${formData.appointmentTime}:00`;

    const apiPayload = {
      clientId: formData.clientId?.value,
      appointmentDate: fullAppointmentDate,
      observations: formData.observations,
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
      toast.success(`Agendamento ${appointmentToEdit ? 'atualizado' : 'criado'} com sucesso!`);
      onSaveSuccess();
      reset();
      onRequestClose();

    } catch (err: any) {
      toast.error(err.message || 'Ocorreu um erro desconhecido.');
    }
  };
    // --- FIM DA LÓGICA DE ENVIO ---

    const handleCloseModal = () => {
    reset();
    onRequestClose();
  }

  return (
    <Modal isOpen={isOpen} onRequestClose={onRequestClose} style={customStyles}>
      <div className="bg-white rounded-lg shadow-xl p-6 w-full max-w-md">
        <h3 className="text-xl font-semibold mb-4 text-gray-800">
          {appointmentToEdit ? 'Editar Agendamento' : 'Novo Agendamento'} para {format(selectedDate, 'dd/MM/yyyy')}
        </h3>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">

          {/* Cliente */}
          <div>
            <label>Cliente</label>
            <Controller
                name="clientId"
                control={control}
                rules={{ required: true }}
                render={({ field }) => <Select {...field} options={clientOptions} placeholder="Busque um cliente..." />}
            />
          </div>

          {/* Profissional */}
          <div>
            <label>Profissional</label>
            <Controller
              name="professionalId"
              control={control}
              rules={{ required: true }}
              render={({ field }) => (
                <Select 
                  {...field} 
                  options={professionalOptions}
                  isLoading={areOptionsLoading}
                  placeholder="Selecione um profissional..." 
                  onChange={option => {
                    field.onChange(option);
                    const prof = allProfessionals.find(p => p.id === option?.value);
                    setSelectedProfessional(prof || null);
                    setValue('serviceId', undefined);
                  }}
                />
              )}
            />
          </div>

          {/* Serviço */}
          <div>
            <label>Serviço</label>
            <select 
              {...register("serviceId", { 
                required: true,
                onChange: (e) => {
                  const serviceId = parseInt(e.target.value, 10);
                  const selectedService = allServices.find(s => s.id === serviceId);
                  if (selectedService) {
                    setValue('price', selectedService.price, { shouldValidate: true });
                  }
                },
                setValueAs: v => v ? parseInt(v) : undefined
              })} 
              disabled={!selectedProfessional || areOptionsLoading} 
              className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
              >
              <option value="">{selectedProfessional ? "Selecione um serviço..." : "Selecione um profissional"}</option>
              {availableServices.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
            </select>
          </div>
        
          {/* Horário */}
          <div>
            <label htmlFor="appointmentTime" className="block text-sm font-medium text-gray-700">Horário</label>
            <input type="time" id="appointmentTime" {...register("appointmentTime", { required: "Horário é obrigatório" })} className="mt-1 block w-full border-gray-300 rounded-md shadow-sm" />
            {errors.appointmentTime && <p className="text-red-500 text-xs mt-1">{errors.appointmentTime.message}</p>}
          </div>

          {/* Preço */}
          <div>
            <label htmlFor="price" className="block text-sm font-medium text-gray-700">Preço (R$)</label>
            <input type="number" step="0.01" id="price" {...register("price", { required: "Preço é obrigatório", valueAsNumber: true })} className="mt-1 block w-full border-gray-300 rounded-md shadow-sm" />
            {errors.price && <p className="text-red-500 text-xs mt-1">{errors.price.message}</p>}
          </div>
          
          {/* Observações */}
          <div>
            <label htmlFor="observations" className="block text-sm font-medium text-gray-700">Observações</label>
            <textarea id="observations" {...register("observations")} rows={3} className="mt-1 block w-full border-gray-300 rounded-md shadow-sm"></textarea>
          </div>
          
          {/* Botões */}
          <div className="mt-6 flex justify-end gap-3">
            <button type="button" onClick={handleCloseModal} className="bg-gray-200 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-300">Cancelar</button>
            <button type="submit" className="bg-green-500 text-white px-4 py-2 rounded-md hover:bg-green-600">Salvar</button>
          </div>
        </form>
      </div>
    </Modal>
  );
}