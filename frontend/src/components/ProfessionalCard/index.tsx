import type { Professional } from '../../types/professional';
import { serviceTypeTranslations } from '../../constants/serviceConstants';

type ProfessionalCardProps = {
  professional: Professional;
  onEdit: () => void;
  onDelete: () => void;
};

export function ProfessionalCard({ professional, onEdit, onDelete }: ProfessionalCardProps) {
  return (
    <div className="bg-white p-4 rounded-lg shadow-md transition-shadow hover:shadow-lg">
      <div className="flex items-start justify-between">
        <div>
          <p className="font-bold text-lg text-gray-800">{professional.name}</p>
          <div className="flex items-center gap-2 text-sm text-gray-500">
            <span>{professional.contact.phone}</span>
            {professional.contact.phoneIsWhatsapp ? (
              <span className="px-2 py-0.5 bg-green-100 text-green-800 text-xs font-medium rounded-full">
                WhatsApp
              </span>
            ) : (
              <span className="px-2 py-0.5 bg-red-100 text-red-800 text-xs font-medium rounded-full">
                Não é WhatsApp
              </span>
            )}
          </div>
          
          {/* Tags de Especialidades */}
          <div className="flex flex-wrap gap-2 mt-3">
            {professional.specialties.map(specialty => (
              <span key={specialty} className="text-xs font-medium px-2.5 py-1 bg-indigo-100 text-indigo-800 rounded-full">
                {serviceTypeTranslations[specialty] || specialty}
              </span>
            ))}
          </div>
        </div>

        {/* Botões de Ação */}
        <div className="flex gap-2">
          <button onClick={onEdit} className="p-2 text-blue-600 hover:bg-blue-100 rounded-full transition-colors" title="Editar">
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.5L16.732 3.732z"></path></svg>
          </button>
          <button onClick={onDelete} className="p-2 text-red-600 hover:bg-red-100 rounded-full transition-colors" title="Deletar">
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
          </button>
        </div>
      </div>
    </div>
  );
}