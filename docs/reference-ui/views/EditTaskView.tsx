import { ArrowLeft, Clock, History, AlertCircle, Minus, TrendingDown, Plus, Check, Calendar as CalendarIcon } from 'lucide-react';
import React, { useState } from 'react';
import { Task } from '../App';

interface EditTaskViewProps {
  task?: Task;
  onCancel: () => void;
  onSave: (task: Omit<Task, 'id'>) => void;
}

export function EditTaskView({ task, onCancel, onSave }: EditTaskViewProps) {
  const todayStr = new Date().toISOString().split('T')[0];
  const [title, setTitle] = useState(task?.title || '');
  const [category, setCategory] = useState(task?.category || 'Work');
  const [priority, setPriority] = useState<Task['priority']>(task?.priority || 'medium');
  const [startDate, setStartDate] = useState(task?.dueDate || todayStr);
  const [startTime, setStartTime] = useState(task?.dueTime || '12:00');
  const [notes, setNotes] = useState(task?.notes || '');

  const handleSave = () => {
    if (!title.trim()) return;
    onSave({
      title,
      category,
      priority,
      dueDate: startDate,
      dueTime: startTime,
      notes,
      completed: task?.completed ?? false,
    });
  };

  return (
    <div className="flex-1 flex flex-col h-full bg-background relative overflow-hidden">
      {/* TopAppBar */}
      <header className="w-full flex justify-between items-center px-container-padding py-stack-gap shrink-0 z-10 bg-background">
        <button onClick={onCancel} aria-label="Back" className="text-on-surface-variant hover:opacity-80 transition-opacity active:scale-95">
          <ArrowLeft size={24} />
        </button>
        <div className="font-headline-md text-headline-md text-on-surface text-center flex-1 font-medium">
          {task ? 'Edit Task' : 'New Task'}
        </div>
        <div className="w-[24px]"></div>
      </header>

      {/* Form Content */}
      <main className="flex-1 overflow-y-auto px-container-padding py-stack-gap flex flex-col gap-stack-gap pb-[100px] custom-scrollbar">
        {/* Title Section */}
        <section className="bg-surface-container-lowest rounded-2xl p-container-padding shadow-[0px_4px_20px_rgba(0,0,0,0.04)]">
          <label className="font-label-sm text-label-sm text-on-surface-variant mb-unit block" htmlFor="task-title">Task Title</label>
          <input 
            id="task-title" 
            type="text" 
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="What needs to be done?" 
            className="w-full font-headline-md text-headline-md text-on-surface py-2 border-b border-surface-variant focus:border-primary-container transition-colors placeholder:text-outline-variant bg-transparent outline-none focus:ring-0" 
          />
        </section>

        {/* Category Section */}
        <section className="bg-surface-container-lowest rounded-2xl p-container-padding shadow-[0px_4px_20px_rgba(0,0,0,0.04)]">
          <h3 className="font-label-sm text-label-sm text-on-surface-variant mb-item-padding">Category</h3>
          <div className="flex flex-wrap gap-2">
            <RadioChip name="category" value="Work" checked={category === 'Work'} onChange={() => setCategory('Work')} />
            <RadioChip name="category" value="Personal" checked={category === 'Personal'} onChange={() => setCategory('Personal')} />
            <RadioChip name="category" value="Health" checked={category === 'Health'} onChange={() => setCategory('Health')} />
            <button className="inline-flex items-center justify-center px-4 py-2 rounded-full border border-dashed border-outline font-body-md text-body-md text-outline cursor-pointer transition-colors hover:bg-surface-container">
              <Plus size={16} className="mr-1" /> New
            </button>
          </div>
        </section>

        {/* Priority Section */}
        <section className="bg-surface-container-lowest rounded-2xl p-container-padding shadow-[0px_4px_20px_rgba(0,0,0,0.04)]">
          <h3 className="font-label-sm text-label-sm text-on-surface-variant mb-item-padding">Priority</h3>
          <div className="grid grid-cols-3 gap-3">
            <PriorityChip level="low" checked={priority === 'low'} onChange={() => setPriority('low')} icon={<TrendingDown size={20} className="text-outline mb-1" />} />
            <PriorityChip level="medium" checked={priority === 'medium'} onChange={() => setPriority('medium')} icon={<Minus size={20} className="text-outline mb-1" />} />
            <PriorityChip level="high" checked={priority === 'high'} onChange={() => setPriority('high')} icon={<AlertCircle size={20} className="text-error mb-1" />} />
          </div>
        </section>

        {/* Time Section */}
        <section className="bg-surface-container-lowest rounded-2xl p-container-padding shadow-[0px_4px_20px_rgba(0,0,0,0.04)] flex flex-col md:flex-row gap-4">
          <div className="flex-1">
            <label className="font-label-sm text-label-sm text-on-surface-variant mb-unit block">Deadline</label>
            <div className="flex flex-col gap-2">
              <div className="flex items-center border-b border-surface-variant py-2">
                <CalendarIcon size={20} className="text-outline mr-2 shrink-0" />
                <input 
                  type="date" 
                  value={startDate}
                  onChange={(e) => setStartDate(e.target.value)}
                  className="w-full font-body-lg text-body-lg text-on-surface bg-transparent outline-none border-none" 
                />
              </div>
              <div className="flex items-center border-b border-surface-variant py-2">
                <Clock size={20} className="text-outline mr-2 shrink-0" />
                <input 
                  type="time" 
                  value={startTime}
                  onChange={(e) => setStartTime(e.target.value)}
                  className="w-full font-body-lg text-body-lg text-on-surface bg-transparent outline-none border-none" 
                />
              </div>
            </div>
          </div>
        </section>

        {/* Notes Section */}
        <section className="bg-surface-container-lowest rounded-2xl p-container-padding shadow-[0px_4px_20px_rgba(0,0,0,0.04)]">
          <label className="font-label-sm text-label-sm text-on-surface-variant mb-unit block" htmlFor="task-notes">Notes</label>
          <textarea 
            id="task-notes" 
            rows={3} 
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
            placeholder="Add any additional details or sub-tasks..." 
            className="w-full font-body-md text-body-md text-on-surface py-2 border-b border-surface-variant focus:border-primary-container transition-colors placeholder:text-outline-variant resize-none bg-transparent outline-none border-none"
          ></textarea>
        </section>
      </main>

      {/* Fixed Bottom Action Area */}
      <div className="absolute bottom-0 left-0 w-full p-container-padding bg-background/90 backdrop-blur-sm z-40 pb-safe">
        <button 
          onClick={handleSave}
          disabled={!title.trim()}
          className="w-full bg-primary-container text-on-primary-container font-headline-md text-headline-md py-4 rounded-2xl shadow-[0px_4px_20px_rgba(0,0,0,0.04)] hover:opacity-90 active:scale-95 transition-all flex items-center justify-center disabled:opacity-50"
        >
          <Check size={24} className="mr-2" strokeWidth={3} />
          Save Task
        </button>
      </div>
    </div>
  );
}

function RadioChip({ name, value, checked, onChange }: { name: string, value: string, checked: boolean, onChange: () => void }) {
  return (
    <label className={`inline-flex items-center justify-center px-4 py-2 rounded-full border cursor-pointer transition-colors font-body-md text-body-md
      ${checked ? 'bg-primary-container text-on-primary-container border-primary-container font-medium' : 'border-outline-variant text-on-surface hover:bg-surface-container'}
    `}>
      <input type="radio" name={name} className="sr-only" checked={checked} onChange={onChange} />
      {value}
    </label>
  );
}

function PriorityChip({ level, checked, onChange, icon }: { level: string, checked: boolean, onChange: () => void, icon: React.ReactNode }) {
  return (
    <label className={`flex flex-col items-center justify-center py-3 rounded-xl border cursor-pointer transition-colors font-body-md text-body-md capitalize
      ${checked ? 'bg-primary-container text-on-primary-container border-primary-container font-medium' : 'border-outline-variant text-on-surface hover:bg-surface-container'}
    `}>
      <input type="radio" name="priority" className="sr-only" checked={checked} onChange={onChange} />
      {icon}
      {level}
    </label>
  );
}
