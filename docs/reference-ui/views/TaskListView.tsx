import React from 'react';
import { Check, Clock } from 'lucide-react';
import { Task } from '../App';

interface TaskListViewProps {
  tasks: Task[];
  selectedCategory: string;
  toggleTaskCompletion: (id: string) => void;
  handleEditTask: (id: string) => void;
}

export function TaskListView({ tasks, selectedCategory, toggleTaskCompletion, handleEditTask }: TaskListViewProps) {
  const filteredTasks = selectedCategory === 'All Tasks' 
    ? tasks 
    : tasks.filter(t => t.category === selectedCategory);
  
  const sortedTasks = filteredTasks.sort((a, b) => {
    // Basic sorting, incomplete first, then by title
    if (a.completed === b.completed) {
      return a.title.localeCompare(b.title);
    }
    return a.completed ? 1 : -1;
  });

  return (
    <div className="max-w-3xl mx-auto w-full">
      <div className="mb-6">
        <h2 className="font-headline-lg text-headline-lg text-on-surface font-semibold">{selectedCategory}</h2>
      </div>

      {sortedTasks.length === 0 ? (
        <div className="text-center p-8 bg-surface-container-lowest rounded-2xl shadow-[0px_4px_20px_rgba(0,0,0,0.04)] border border-surface-container text-on-surface-variant">
          No tasks found for this category.
        </div>
      ) : (
        <div className="space-y-stack-gap">
          {sortedTasks.map(task => (
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
              <div className="flex-1 flex flex-col min-w-0">
                <span className={`font-body-lg text-body-lg truncate ${task.completed ? 'text-on-surface-variant line-through' : 'text-on-surface'}`}>
                  {task.title}
                </span>
                {selectedCategory === 'All Tasks' && (
                  <span className="font-label-sm text-label-sm text-on-surface-variant flex items-center gap-1 mt-1">
                    {task.category}
                  </span>
                )}
              </div>
              <span className={`px-2 py-1 rounded-md font-label-sm text-[10px] uppercase tracking-wider shrink-0 border
                 ${task.priority === 'high' ? 'border-error text-error bg-error-container/30' : 
                   task.priority === 'medium' ? 'border-primary text-primary bg-primary-container/20' : 
                   'border-outline text-outline bg-surface-container'
                 }`}
              >
                {task.priority}
              </span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
