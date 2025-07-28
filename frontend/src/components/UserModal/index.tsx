import { IMaskInput } from 'react-imask';
import { Controller } from 'react-hook-form';
import Modal from 'react-modal';
import { useForm, SubmitHandler } from 'react-hook-form';
import { useEffect } from 'react';
import toast from 'react-hot-toast';
import { useAuth } from '../../hooks/useAuth';
import type { SystemUser } from '../../types/user';
import { serviceTypeTranslations } from '../../constants/serviceConstants';

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
  email: string;
  password?: string; // Senha é opcional na edição
  role: string;
  phone: string;
  phoneIsWhatsapp: boolean;
  specialties: string[];
};

type UserModalProps = {
  isOpen: boolean;
  onRequestClose: () => void;
  onSaveSuccess: () => void;
  userToEdit: SystemUser | null;
};

const specialtyOptions = Object.keys(serviceTypeTranslations);

export function UserModal({ isOpen, onRequestClose, onSaveSuccess, userToEdit }: UserModalProps) {
  const { register, handleSubmit, reset, setValue, control, formState: { errors } } = useForm<FormData>();
  const { token } = useAuth();

  useEffect(() => {
    // Preenche o formulário se estivermos no modo de edição
    if (userToEdit) {
      setValue('name', userToEdit.name);
      setValue('email', userToEdit.email);
      setValue('role', userToEdit.role);
      setValue('phone', userToEdit.contact?.phone || '');
      setValue('phoneIsWhatsapp', userToEdit.contact?.phoneIsWhatsapp || false);
      setValue('specialties', userToEdit.specialties || []);
    } else {
      // Limpa o formulário para um novo usuário, definindo padrões
      reset({ name: '', email: '', password: '', role: 'STAFF', phone: '', phoneIsWhatsapp: true, specialties: [] });
    }
  }, [userToEdit, isOpen, reset, setValue]);

  const onSubmit: SubmitHandler<FormData> = async (data) => {
    // Monta o payload para a API (UserRequestDTO)
    const apiPayload = {
        name: data.name,
        email: data.email,
        password: data.password,
        role: data.role,
        contact: {
            phone: data.phone,
            phoneIsWhatsapp: data.phoneIsWhatsapp
        },
        specialties: data.specialties
    };

    // Remove a senha do payload se estiver vazia (para não sobrescrever com vazio na edição)
    if (!apiPayload.password) {
        delete (apiPayload as Partial<typeof apiPayload>).password;
    }

    const method = userToEdit ? 'PUT' : 'POST';
    const url = userToEdit
      ? `http://localhost:8080/api/v1/users/${userToEdit.id}`
      : `http://localhost:8080/api/v1/users`;

    try {
      const response = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
        body: JSON.stringify(apiPayload)
      });
      if (!response.ok) throw new Error('Falha ao salvar usuário.');
      
      toast.success(`Usuário ${userToEdit ? 'atualizado' : 'criado'} com sucesso!`);
      onSaveSuccess();
    } catch (error: any) {
      toast.error(error.message);
    }
  };

  return (
    <Modal isOpen={isOpen} onRequestClose={onRequestClose} style={customStyles}>
      <div className="bg-white rounded-lg shadow-xl p-6 w-11/12 max-w-lg">
        <h3 className="text-xl font-semibold mb-4">{userToEdit ? 'Editar Usuário' : 'Novo Usuário'}</h3>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label htmlFor="name" className="block text-sm font-medium text-gray-700">Nome</label>
              <input id="name" {...register("name", { required: true })} className="mt-1 block w-full border-gray-300 rounded-md shadow-sm" />
            </div>
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700">Email</label>
              <input id="email" type="email" {...register("email", { required: true })} className="mt-1 block w-full border-gray-300 rounded-md shadow-sm" />
            </div>
            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-700">Senha</label>
              <input id="password" type="password" {...register("password")} placeholder={userToEdit ? "Deixe em branco para não alterar" : ""} className="mt-1 block w-full border-gray-300 rounded-md shadow-sm" />
            </div>
            <div>
              <label htmlFor="role" className="block text-sm font-medium text-gray-700">Perfil</label>
              <select id="role" {...register("role")} className="mt-1 block w-full border-gray-300 rounded-md shadow-sm">
                  <option value="STAFF">Funcionária</option>
                  <option value="ADMIN">Admin</option>
              </select>
            </div>
          </div>
          
          {/* --- CAMPO DE TELEFONE --- */}
          <div>
            <label htmlFor="phone" className="block text-sm font-medium text-gray-700">Telefone</label>
            <Controller
              name="phone"
              control={control}
              rules={{ 
                required: "O telefone é obrigatório",
                pattern: {
                  value: /^\d{10,11}$/,
                  message: "O telefone deve ter 10 ou 11 dígitos."
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
          <div className="flex items-center">
            <input type="checkbox" {...register("phoneIsWhatsapp")} className="h-4 w-4 rounded" />
            <label className="ml-2 block text-sm text-gray-900">Este número é WhatsApp</label>
          </div>
          {/* --- FIM DO CAMPO DE TELEFONE --- */}
          
          <div>
            <label className="block text-sm font-medium text-gray-700">Especialidades</label>
            <div className="mt-2 grid grid-cols-2 sm:grid-cols-3 gap-2">
                {specialtyOptions.map(specialtyKey => (
                    <div key={specialtyKey} className="flex items-center">
                        <input type="checkbox" id={specialtyKey} value={specialtyKey} {...register("specialties")} className="h-4 w-4 rounded"/>
                        <label htmlFor={specialtyKey} className="ml-2 block text-sm text-gray-900">{serviceTypeTranslations[specialtyKey]}</label>
                    </div>
                ))}
            </div>
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