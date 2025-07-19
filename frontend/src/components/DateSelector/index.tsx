import { useState, useEffect, useRef } from 'react';
import { DayPicker } from 'react-day-picker';
import { format, addDays } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import 'react-day-picker/dist/style.css';
import './styles.css';

type DateSelectorProps = {
  selectedDate: Date;
  onDateChange: (date: Date) => void;
};

export function DateSelector({ selectedDate, onDateChange }: DateSelectorProps) {
  const [isCalendarOpen, setIsCalendarOpen] = useState(false);
  const calendarRef = useRef<HTMLDivElement>(null);

  // Hook para detectar cliques fora do calendário
  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      // Se a nossa "âncora" existe E o clique NÃO foi dentro dela
      if (calendarRef.current && !calendarRef.current.contains(event.target as Node)) {
        setIsCalendarOpen(false); // Fecha o calendário
      }
    }
    // Adiciona um "espião" para detectar cliques fora do calendário
    document.addEventListener("mousedown", handleClickOutside);
    // Função de limpeza: remove o "espião" quando o componente é desmontado
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [calendarRef]);

  const handleDayClick = (day: Date | undefined) => {
    if (day) {
      onDateChange(day);
      setIsCalendarOpen(false);
    }
  };

  const handlePrevDay = () => {
    onDateChange(addDays(selectedDate, -1));
  };

  const handleNextDay = () => {
    onDateChange(addDays(selectedDate, 1));
  };
  
  const formattedDate = format(selectedDate, "d 'de' MMMM", { locale: ptBR });

  return (
    <div className="relative flex items-center justify-between mb-6">
      <h2 className="text-lg font-semibold text-gray-800">Agenda:</h2>
      
      <div className="flex items-center gap-2">
        <button onClick={handlePrevDay} className="p-2 rounded-full hover:bg-gray-200 transition-colors">
          <svg className="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 19l-7-7 7-7"></path></svg>
        </button>
        
        <span className="text-base font-medium text-gray-700 w-36 text-center">{formattedDate}</span>
        
        <button onClick={handleNextDay} className="p-2 rounded-full hover:bg-gray-200 transition-colors">
          <svg className="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5l7 7-7 7"></path></svg>
        </button>
        
        <button onClick={() => setIsCalendarOpen(true)} className="p-2 rounded-full hover:bg-gray-200 transition-colors">
          <svg className="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path></svg>
        </button>
      </div>

      {isCalendarOpen && (
        <div ref={calendarRef} className="absolute top-12 right-0 bg-white rounded-lg shadow-xl p-4 z-10">
          <DayPicker
            mode="single"
            selected={selectedDate}
            onSelect={handleDayClick}
            locale={ptBR}
            showOutsideDays
            fixedWeeks
          />
        </div>
      )}
    </div>
  );
}