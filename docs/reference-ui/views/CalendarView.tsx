import React, { useState, useRef, useEffect } from 'react';
import { ChevronLeft, ChevronRight, Check } from 'lucide-react';
import { Task } from '../App';

interface CalendarViewProps {
  tasks: Task[];
  toggleTaskCompletion: (id: string) => void;
  handleEditTask: (id: string) => void;
}

export function CalendarView({ tasks, toggleTaskCompletion, handleEditTask }: CalendarViewProps) {
  const [currentDate, setCurrentDate] = useState(new Date());
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);
  const [isExpanded, setIsExpanded] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);

  const year = currentDate.getFullYear();
  const month = currentDate.getMonth();

  const daysInMonth = new Date(year, month + 1, 0).getDate();
  const firstDayOfMonth = new Date(year, month, 1).getDay();

  const handlePrev = () => {
    if (isExpanded) {
      setCurrentDate(new Date(year, month - 1, 1));
    } else {
      const newDate = new Date(currentDate);
      newDate.setDate(currentDate.getDate() - 7);
      setCurrentDate(newDate);
    }
  };

  const handleNext = () => {
    if (isExpanded) {
      setCurrentDate(new Date(year, month + 1, 1));
    } else {
      const newDate = new Date(currentDate);
      newDate.setDate(currentDate.getDate() + 7);
      setCurrentDate(newDate);
    }
  };

  // Touch handling for swipe up/down
  const touchStartY = useRef(0);
  const handleTouchStart = (e: React.TouchEvent) => {
    touchStartY.current = e.touches[0].clientY;
  };
  const handleTouchEnd = (e: React.TouchEvent) => {
    const touchEndY = e.changedTouches[0].clientY;
    const diff = touchEndY - touchStartY.current;
    if (diff > 50 && !isExpanded) {
      setIsExpanded(true); // Swipe down
    } else if (diff < -50 && isExpanded) {
      setIsExpanded(false); // Swipe up
    }
  };

  const days = [];
  for (let i = 0; i < firstDayOfMonth; i++) {
    days.push(null);
  }
  for (let i = 1; i <= daysInMonth; i++) {
    days.push(i);
  }

  // Calculate the week row to show when collapsed
  const selectedDateObj = new Date(selectedDate);
  const selectedD = selectedDateObj.getDate();
  const selectedM = selectedDateObj.getMonth();
  const selectedY = selectedDateObj.getFullYear();
  
  // Find which row the currently viewed date falls in
  const viewY = currentDate.getFullYear();
  const viewM = currentDate.getMonth();
  const viewD = currentDate.getDate();
  
  // We'll show the week containing the current view date
  const viewDayOfWeek = currentDate.getDay();
  const weekStart = new Date(currentDate);
  weekStart.setDate(currentDate.getDate() - viewDayOfWeek);
  
  const weekDays = [];
  for (let i = 0; i < 7; i++) {
    const d = new Date(weekStart);
    d.setDate(weekStart.getDate() + i);
    weekDays.push(d);
  }

  const selectedDateTasks = tasks.filter(t => t.dueDate === selectedDate).sort((a, b) => (a.dueTime || '').localeCompare(b.dueTime || ''));
  const monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];

  return (
    <div className="max-w-3xl mx-auto w-full flex flex-col gap-8 pb-8">
      <div 
        ref={containerRef}
        onTouchStart={handleTouchStart}
        onTouchEnd={handleTouchEnd}
        className="bg-surface-container-lowest rounded-2xl p-4 shadow-[0px_4px_20px_rgba(0,0,0,0.04)] select-none transition-all duration-300 overflow-hidden"
      >
        <div className="flex justify-between items-center mb-4">
          <h3 className="font-headline-md text-headline-md font-medium text-on-surface">
            {monthNames[currentDate.getMonth()]} {currentDate.getFullYear()}
          </h3>
          <div className="flex gap-2">
            <button onClick={handlePrev} className="p-2 rounded-full hover:bg-surface-container transition-colors text-on-surface-variant">
              <ChevronLeft size={20} />
            </button>
            <button onClick={handleNext} className="p-2 rounded-full hover:bg-surface-container transition-colors text-on-surface-variant">
              <ChevronRight size={20} />
            </button>
          </div>
        </div>

        <div className="grid grid-cols-7 gap-1 text-center mb-2">
          {['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'].map(day => (
            <div key={day} className="font-label-sm text-label-sm text-outline py-1">{day}</div>
          ))}
        </div>

        {isExpanded ? (
          <div className="grid grid-cols-7 gap-1 text-center animate-in fade-in slide-in-from-top-4 duration-300">
            {days.map((day, index) => {
              if (day === null) return <div key={`empty-${index}`} className="p-2 border border-transparent"></div>;
              
              const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
              const isSelected = dateStr === selectedDate;
              const hasTasks = tasks.some(t => t.dueDate === dateStr);
              const isToday = dateStr === new Date().toISOString().split('T')[0];

              return (
                <button 
                  key={day}
                  onClick={() => {
                    setSelectedDate(dateStr);
                    setCurrentDate(new Date(year, month, day));
                    setIsExpanded(false);
                  }}
                  className={`p-2 rounded-full w-10 h-10 mx-auto flex items-center justify-center relative font-body-md text-body-md transition-colors 
                    ${isSelected ? 'bg-primary text-on-primary' : isToday ? 'bg-secondary-container/50 text-on-surface hover:bg-surface-container-high' : 'text-on-surface hover:bg-surface-container-high'}
                  `}
                >
                  {day}
                  {hasTasks && !isSelected && (
                    <div className="absolute bottom-1 left-1/2 -translate-x-1/2 w-1 h-1 rounded-full bg-primary" />
                  )}
                </button>
              );
            })}
          </div>
        ) : (
          <div className="grid grid-cols-7 gap-1 text-center animate-in fade-in duration-300 relative">
            <div className="absolute inset-x-0 bottom-0 top-0 overflow-x-auto flex snap-x snap-mandatory scrollbar-hide -mx-4 px-4" style={{scrollbarWidth: 'none'}}>
               {/* Just showing one week for horizontal swiping effect conceptually, 
                   but mapped purely by flex container here for simplicity. 
                   Real horizontal infinite scroll requires more complex handling. */}
            </div>
            {weekDays.map((d, index) => {
              const day = d.getDate();
              const dateStr = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
              const isSelected = dateStr === selectedDate;
              const hasTasks = tasks.some(t => t.dueDate === dateStr);
              const isToday = dateStr === new Date().toISOString().split('T')[0];

              return (
                <button 
                  key={index}
                  onClick={() => {
                    setSelectedDate(dateStr);
                    setCurrentDate(d);
                  }}
                  className={`p-2 rounded-full w-10 h-10 mx-auto flex items-center justify-center relative font-body-md text-body-md transition-colors snap-center
                    ${isSelected ? 'bg-primary text-on-primary' : isToday ? 'bg-secondary-container/50 text-on-surface hover:bg-surface-container-high' : 'text-on-surface hover:bg-surface-container-high'}
                  `}
                >
                  {day}
                  {hasTasks && !isSelected && (
                    <div className="absolute bottom-1 left-1/2 -translate-x-1/2 w-1 h-1 rounded-full bg-primary" />
                  )}
                </button>
              );
            })}
          </div>
        )}

        <div className="mt-4 flex justify-center pb-2 cursor-pointer group" onClick={() => setIsExpanded(!isExpanded)}>
            <div className="w-12 h-1.5 bg-surface-container-highest group-hover:bg-outline-variant transition-colors rounded-full" />
        </div>
      </div>

      <div>
        <h3 className="font-headline-md text-headline-md text-on-surface font-medium mb-4">
          Schedule
        </h3>
        {selectedDateTasks.length === 0 ? (
          <div className="text-center p-8 bg-surface-container-lowest rounded-2xl shadow-[0px_4px_20px_rgba(0,0,0,0.04)] border border-surface-container text-on-surface-variant">
            No tasks scheduled for this day.
          </div>
        ) : (
          <div className="flex flex-col gap-stack-gap">
            {selectedDateTasks.map(task => (
              <div 
                key={task.id} 
                onClick={() => handleEditTask(task.id)}
                className={`bg-surface-container-lowest rounded-xl p-stack-gap flex items-center gap-4 soft-shadow transition-colors cursor-pointer group ${task.completed ? 'opacity-60' : 'hover:bg-surface-bright'}`}
              >
                <button 
                  onClick={(e) => { e.stopPropagation(); toggleTaskCompletion(task.id); }}
                  className={`w-6 h-6 rounded flex items-center justify-center border-[1.5px] border-primary shrink-0 transition-all ${task.completed ? 'bg-primary text-on-primary' : 'text-transparent group-hover:bg-primary-container/20'}`}
                >
                  <Check size={16} strokeWidth={3} className={task.completed ? '' : 'opacity-0'} />
                </button>
                <span className={`font-body-lg text-body-lg flex-1 truncate ${task.completed ? 'text-on-surface-variant line-through' : 'text-on-surface'}`}>
                  {task.title}
                </span>
                <span className={`px-3 py-1 rounded-full font-label-sm text-label-sm shrink-0
                   ${task.category === 'Deep Work' ? 'bg-secondary-container text-on-secondary-container' : 
                     task.category === 'Meeting' ? 'bg-tertiary-container text-on-tertiary-container' : 
                     'bg-surface-container-high text-on-surface-variant'
                   }`}
                >
                  {task.category}
                </span>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
