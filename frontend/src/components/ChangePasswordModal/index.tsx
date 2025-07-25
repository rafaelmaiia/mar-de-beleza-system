import Modal from 'react-modal';
import { useForm, SubmitHandler } from 'react-hook-form';
import toast from 'react-hot-toast';
import { useAuth } from '../../hooks/useAuth';

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

type PasswordFormData = {
  currentPassword: string;
  newPassword: string;
  confirmationPassword: string;
};

type ChangePasswordModalProps = {
  isOpen: boolean;
  onRequestClose: () => void;
};

export function ChangePasswordModal({ isOpen, onRequestClose }: ChangePasswordModalProps) {
  const { register, handleSubmit, reset, formState: { errors } } = useForm<PasswordFormData>();
  const { user, token } = useAuth();

  const handleClose = () => {
    reset();
    onRequestClose();
  };

  const onSubmit: SubmitHandler<PasswordFormData> = async (data) => {
    if (!user) {
      toast.error("Usuário não encontrado. Faça o login novamente.");
      return;
    }
    
    try {
      const response = await fetch(`http://localhost:8080/api/v1/users/${user.id}/change-password`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
        body: JSON.stringify(data),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Falha ao alterar a senha.');
      }
      
      toast.success('Senha alterada com sucesso!');
      handleClose();

    } catch (error: any) {
      toast.error(error.message);
    }
  };

  return (
    <Modal isOpen={isOpen} onRequestClose={handleClose} style={customStyles}>
      <div className="bg-white rounded-lg shadow-xl p-6 w-11/12 max-w-md">
        <h3 className="text-xl font-semibold mb-4 text-gray-800">Alterar Senha</h3>
        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="space-y-4">
            <div>
              <label htmlFor="current-password"  className="block text-sm font-medium text-gray-700">Senha Atual</label>
              <input 
                type="password" 
                id="current-password" 
                {...register("currentPassword", { required: "Senha atual é obrigatória" })} 
                className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" 
              />
              {errors.currentPassword && <p className="text-red-500 text-xs mt-1">{errors.currentPassword.message}</p>}
            </div>
            <div>
              <label htmlFor="new-password"  className="block text-sm font-medium text-gray-700">Nova Senha</label>
              <input 
                type="password" 
                id="new-password" 
                {...register("newPassword", { required: "Nova senha é obrigatória", minLength: { value: 6, message: "A senha deve ter no mínimo 6 caracteres"} })} 
                className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" 
              />
              {errors.newPassword && <p className="text-red-500 text-xs mt-1">{errors.newPassword.message}</p>}
            </div>
            <div>
              <label htmlFor="confirm-password"  className="block text-sm font-medium text-gray-700">Confirme a Nova Senha</label>
              <input 
                type="password" 
                id="confirm-password" 
                {...register("confirmationPassword", { required: "Confirmação é obrigatória" })} 
                className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" 
              />
              {errors.confirmationPassword && <p className="text-red-500 text-xs mt-1">{errors.confirmationPassword.message}</p>}
            </div>
          </div>
          <div className="mt-6 flex justify-end gap-3">
            <button type="button" onClick={handleClose} className="bg-gray-200 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-300">Cancelar</button>
            <button type="submit" className="bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700">Salvar Alterações</button>
          </div>
        </form>
      </div>
    </Modal>
  );
}