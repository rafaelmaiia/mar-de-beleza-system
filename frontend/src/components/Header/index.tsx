import { useState, useEffect, useRef } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

type HeaderProps = {
  title: string;
};

export function Header({ title }: HeaderProps) {
  const { logout } = useAuth();
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const menuRef = useRef<HTMLDivElement>(null);

  const location = useLocation();
  const navigate = useNavigate();

  const isHomePage = location.pathname === '/dashboard';

  const handleGoBack = () => {
    navigate(-1);
  };

  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setIsMenuOpen(false);
      }
    }

    document.addEventListener("mousedown", handleClickOutside);

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [menuRef]);

  return (
    <header className="bg-white/80 backdrop-blur-sm shadow-sm fixed top-0 left-0 right-0 z-40">
      <div className="max-w-4xl mx-auto px-4">
        <div className="relative flex items-center justify-center h-16">
          
          {/* --- LÓGICA BOTÃO DA ESQUERDA/VOLTAR --- */}
          <div className="absolute left-0">
            {isHomePage ? (
              // Se estiver na Home, mostra o botão de Sair
              <button 
                onClick={logout} 
                className="text-gray-600 hover:text-red-600 focus:outline-none p-2 rounded-full hover:bg-gray-100 transition-colors"
                title="Sair"
              >
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path></svg>
              </button>
            ) : (
              // Se estiver em outra página, mostra o botão de Voltar
              <button 
                onClick={handleGoBack}
                className="text-gray-600 hover:text-gray-900 focus:outline-none p-2 rounded-full hover:bg-gray-100 transition-colors"
                title="Voltar"
              >
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 19l-7-7 7-7"></path></svg>
              </button>
            )}
          </div>

          {/* --- TÍTULO LINK PARA A HOME --- */}
          <div className="flex-1 text-center">
            <Link to="/dashboard" className="text-3xl font-bold text-gray-800 font-playfair hover:opacity-75 transition-opacity">
              {title}
            </Link>
          </div>

          {/* Botão do Menu Dropdown */}
          <div className="absolute right-0" ref={menuRef}>
            <button 
              onClick={() => setIsMenuOpen(!isMenuOpen)} 
              className="text-gray-600 hover:text-gray-900 focus:outline-none p-2 rounded-full hover:bg-gray-100 transition-colors"
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 6h16M4 12h16M4 18h16"></path></svg>
            </button>
            
            {/* Menu Dropdown */}
            <div 
              className={`absolute right-0 top-full mt-2 w-56 origin-top-right rounded-md bg-white shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none 
                         ${isMenuOpen ? 'block' : 'hidden'}`}
              role="menu"
              >
              <div className="py-1" role="none">
                <Link to="/dashboard" className="text-gray-700 block px-4 py-2 text-sm hover:bg-gray-100" role="menuitem">Gerenciar Agendamentos</Link>
                <Link to="/clients" className="text-gray-700 block px-4 py-2 text-sm hover:bg-gray-100" role="menuitem">Gerenciar Clientes</Link>
                <Link to="/users" className="text-gray-700 block px-4 py-2 text-sm hover:bg-gray-100" role="menuitem">Gerenciar Usuárias</Link>
                <Link to="/services" className="text-gray-700 block px-4 py-2 text-sm hover:bg-gray-100" role="menuitem">Gerenciar Serviços</Link>
                <Link to="/settings" className="text-gray-700 block px-4 py-2 text-sm hover:bg-gray-100 border-t border-gray-100 mt-1 pt-2" role="menuitem">Configurações</Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </header>
  );
}