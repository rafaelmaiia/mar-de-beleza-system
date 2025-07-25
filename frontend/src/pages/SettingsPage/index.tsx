import { useState } from 'react'; // Importe o useState
import { Header } from '../../components/Header';
import { useAuth } from '../../hooks/useAuth';
import { ChangePasswordModal } from '../../components/ChangePasswordModal';

export function SettingsPage() {
  const { user, logout } = useAuth();
  const [isPasswordModalOpen, setIsPasswordModalOpen] = useState(false);

  return (
    <div className="bg-gray-50 min-h-screen">
      <Header title="Configurações" />

      <main className="pt-20 pb-24 px-4 max-w-4xl mx-auto">
        <div className="space-y-8">

          {/* Seção: Perfil */}
          <div>
            <h2 className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-3">Perfil</h2>
            <div className="bg-white rounded-lg shadow-md">
              <ul className="divide-y divide-gray-200">
                <li className="p-4 flex justify-between items-center">
                  <span className="font-medium text-gray-700">Nome</span>
                  <span className="text-gray-500">{user?.name}</span>
                </li>
                <li className="p-4 flex justify-between items-center">
                  <span className="font-medium text-gray-700">Email</span>
                  <span className="text-gray-500">{user?.email}</span>
                </li>
                <li className="p-4">
                  {/* Botão para abrir o modal de alterar senha */}
                  <button onClick={() => setIsPasswordModalOpen(true)} className="font-medium text-indigo-600 hover:text-indigo-500">
                    Alterar Senha
                  </button>
                </li>
              </ul>
            </div>
          </div>

          {/* Seção: Aparência */}
          <div>
            <h2 className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-3">Aparência</h2>
            <div className="bg-white rounded-lg shadow-md">
              <ul className="divide-y divide-gray-200">
                  <li className="p-4 flex justify-between items-center">
                    <span className="font-medium text-gray-700">Modo Escuro</span>
                    {/* Lógica do Toggle Switch (ainda sem funcionalidade) */}
                    <div className="relative inline-block w-10 mr-2 align-middle select-none">
                        <input type="checkbox" name="toggle" id="toggle-dark-mode" className="toggle-checkbox absolute block w-6 h-6 rounded-full bg-white border-4 appearance-none cursor-pointer"/>
                        <label htmlFor="toggle-dark-mode" className="toggle-label block overflow-hidden h-6 rounded-full bg-gray-300 cursor-pointer"></label>
                    </div>
                  </li>
              </ul>
            </div>
          </div>

          {/* --- BOTÃO DE SAIR --- */}
          <div className="mt-10">
            <button 
              onClick={logout}
              className="w-full bg-red-500 hover:bg-red-600 text-white font-bold py-3 px-4 rounded-lg shadow-md transition-colors"
            >
              Sair da Conta
            </button>
          </div>
          {/* --- FIM DO BOTÃO DE SAIR --- */}
        </div>
      </main>

    <ChangePasswordModal 
        isOpen={isPasswordModalOpen}
        onRequestClose={() => setIsPasswordModalOpen(false)}
      />
    </div>
  );
}