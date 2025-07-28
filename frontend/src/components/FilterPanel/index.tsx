import { useState, useEffect } from 'react';
import Select from 'react-select';
import { useAuth } from '../../hooks/useAuth';
import type { SystemUser } from '../../types/user';
import type { Client } from '../../types/client';
import { statusTranslations } from '../../constants/statusConstants';

type FilterPanelProps = {
  filters: {
    startDate: string;
    endDate: string;
    professionalId: string;
    clientId: string;
    status: string;
  };
  onFilterChange: (filterName: string, value: any) => void;
  onClearFilters: () => void;
};

export function FilterPanel({ filters, onFilterChange, onClearFilters }: FilterPanelProps) {
  const { token } = useAuth();
  const [isOpen, setIsOpen] = useState(false);

  // --- ESTADOS PARA GUARDAR AS OPÇÕES DOS SELECTS ---
  const [professionalOptions, setProfessionalOptions] = useState<{ value: number; label: string }[]>([]);
  const [clientOptions, setClientOptions] = useState<{ value: number; label: string }[]>([]);
  const [statusOptions, setStatusOptions] = useState<{ value: string; label: string }[]>([]);
  // Busca os dados para os selects quando o componente é montado
  useEffect(() => {
    if (!token) return;

    const options = Object.entries(statusTranslations).map(([key, value]) => ({
      value: key,
      label: value,
    }));
    setStatusOptions(options);

    const headers = { 'Authorization': `Bearer ${token}` };

    fetch('http://localhost:8080/api/v1/users?canBeScheduled=true', { headers })
      .then(res => res.json())
      .then(data => setProfessionalOptions((data || []).map((p: SystemUser) => ({ value: p.id, label: p.name }))))
      .catch(error => console.error("Falha ao buscar profissionais.", error));

    fetch('http://localhost:8080/api/v1/clients?size=1000', { headers })
      .then(res => res.json())
      .then(data => setClientOptions((data.content || []).map((c: Client) => ({ value: c.id, label: c.name }))))
      .catch(error => console.error("Falha ao buscar clientes.", error));
  }, [token]);

  return (
    <div className="bg-white p-4 rounded-lg shadow-md mb-6">
      <button onClick={() => setIsOpen(!isOpen)} className="w-full flex justify-between items-center text-left font-semibold text-gray-700">
        <span>Filtros Avançados</span>
        <svg className={`w-5 h-5 transform transition-transform ${isOpen ? 'rotate-180' : ''}`} fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7"></path></svg>
      </button>

      <div 
        className="transition-all duration-500 ease-in-out overflow-hidden"
        style={{ maxHeight: isOpen ? '500px' : '0px' }}
      >
        <div className="pt-4 border-t mt-4">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 items-start">
            <div>
              <label className="block text-sm font-medium text-gray-700">De</label>
              <input 
                type="date" 
                value={filters.startDate}
                onChange={(e) => onFilterChange('startDate', e.target.value)}
                className="mt-1 block w-full border-gray-300 rounded-md shadow-sm"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Até</label>
              <input 
                type="date" 
                value={filters.endDate}
                onChange={(e) => onFilterChange('endDate', e.target.value)}
                className="mt-1 block w-full border-gray-300 rounded-md shadow-sm"
              />
            </div>
            <div>
                <label className="block text-sm font-medium text-gray-700">Cliente</label>
                <Select
                    options={[{ value: '', label: 'Todos' }, ...clientOptions]}
                    onChange={(option) => onFilterChange('clientId', option?.value || '')}
                    placeholder="Selecione..."
                    className="mt-1"
                    value={clientOptions.find(c => c.value === Number(filters.clientId)) || null}
                />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Profissional</label>
              <Select
                options={[{ value: '', label: 'Todas' }, ...professionalOptions]}
                onChange={(option) => onFilterChange('professionalId', option?.value || '')}
                placeholder="Selecione..."
                className="mt-1"
                value={professionalOptions.find(p => p.value === Number(filters.professionalId)) || null}

                menuPortalTarget={document.body}
                styles={{ 
                  menuPortal: base => ({ ...base, zIndex: 9999 }),
                  menu: base => ({ ...base, zIndex: 9999, maxHeight: 400 })
                }}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Status</label>
              <Select
                options={[{ value: '', label: 'Todos' }, ...statusOptions]}
                onChange={(option) => onFilterChange('status', option?.value || '')}
                placeholder="Selecione..."
                className="mt-1"
                value={statusOptions.find(s => s.value === filters.status) || null}

                menuPortalTarget={document.body}
                styles={{ 
                  menuPortal: base => ({ ...base, zIndex: 9999 }),
                  menu: base => ({ ...base, zIndex: 9999, maxHeight: 400 })
                }}
              />
            </div>
          </div>
          <div className="mt-4 flex justify-end">
            <button onClick={onClearFilters} className="bg-gray-200 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-300 text-sm font-medium">Limpar Filtros</button>
          </div>
        </div>
      </div>
    </div>
  );
}