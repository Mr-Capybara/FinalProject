import React, { useState } from 'react';
import { LayoutList, BarChart2, Timer as TimerIcon, Settings, Calendar, FileText, AlertCircle, Tag, Archive, Menu, Sunset, Sunrise, Moon, Plus, Check, CalendarDays, X } from 'lucide-react';
import { DashboardView } from './views/DashboardView';
import { TimerView } from './views/TimerView';
import { TaskListView } from './views/TaskListView';
import { EditTaskView } from './views/EditTaskView';
import { SettingsView } from './views/SettingsView';
import { CalendarView } from './views/CalendarView';

export type TabState = 'calendar' | 'tasks' | 'stats' | 'timer' | 'settings' | 'edit-task';

export interface Task {
  id: string;
  title: string;
  category: string;
  priority: 'low' | 'medium' | 'high';
  dueDate?: string;
  dueTime?: string;
  completed: boolean;
  notes?: string;
}

const todayStr = new Date().toISOString().split('T')[0];

const INITIAL_TASKS: Task[] = [
  { id: '1', title: 'Mindfulness Meditation', category: 'Wellness', priority: 'medium', completed: true, dueDate: todayStr, dueTime: '08:00' },
  { id: '2', title: 'Draft Q4 Strategy Document', category: 'Deep Work', priority: 'high', completed: false, dueDate: todayStr, dueTime: '11:00' },
  { id: '3', title: 'Design System Review Sync', category: 'Meeting', priority: 'medium', completed: false, dueDate: todayStr, dueTime: '14:00' },
  { id: '4', title: 'Process UI Feedback from Team', category: 'Design', priority: 'low', completed: false, dueDate: todayStr, dueTime: '16:00' },
  { id: '5', title: 'Read "The Design of Everyday Things"', category: 'Study', priority: 'low', completed: false, dueDate: todayStr, dueTime: '20:00' },
];

export default function App() {
  const [currentTab, setCurrentTab] = useState<TabState>('calendar');
  const [tasks, setTasks] = useState<Task[]>(INITIAL_TASKS);
  const [editingTaskId, setEditingTaskId] = useState<string | null>(null);
  const [selectedCategory, setSelectedCategory] = useState<string>('All Tasks');
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  const categories = ['All Tasks', ...Array.from(new Set(tasks.map(t => t.category)))];

  const toggleTaskCompletion = (taskId: string) => {
    setTasks(prev => prev.map(t => t.id === taskId ? { ...t, completed: !t.completed } : t));
  };

  const handleEditTask = (taskId: string | null) => {
    setEditingTaskId(taskId);
    setCurrentTab('edit-task');
  };

  const saveTask = (taskData: Omit<Task, 'id'>, id?: string) => {
    if (id) {
      setTasks(prev => prev.map(t => t.id === id ? { ...t, ...taskData } : t));
    } else {
      setTasks(prev => [...prev, { ...taskData, id: Math.random().toString(36).substring(7) }]);
    }
    // Return to previous tab if possible, defaulting to tasks
    setCurrentTab(prev => prev === 'edit-task' ? 'tasks' : prev);
    setEditingTaskId(null);
  };

  const handleCategorySelect = (cat: string) => {
    setSelectedCategory(cat);
    setCurrentTab('tasks');
    setIsMobileMenuOpen(false);
  };

  return (
    <div className="flex items-center justify-center h-screen bg-surface-container-highest overflow-hidden">
      <div className="w-full max-w-[480px] h-full bg-background relative flex flex-col shadow-2xl overflow-hidden">
        {/* Mobile Drawer Overlay */}
        {isMobileMenuOpen && (
          <div 
            className="absolute inset-0 bg-black/20 z-40" 
            onClick={() => setIsMobileMenuOpen(false)}
          />
        )}

        {/* Sidebar / Drawer */}
        {currentTab !== 'edit-task' && (
          <aside className={`absolute top-0 left-0 bottom-0 flex flex-col py-container-padding bg-surface-container-low w-72 rounded-r-xl shadow-2xl shrink-0 z-50 transition-transform duration-300 ${isMobileMenuOpen ? 'translate-x-0' : '-translate-x-full'}`}>
            <div className="absolute top-4 right-4">
              <button onClick={() => setIsMobileMenuOpen(false)} className="p-2 text-on-surface-variant hover:bg-surface-container rounded-full">
                <X size={20} />
              </button>
            </div>
            <div className="px-container-padding mb-8 mt-4">
              <div className="flex items-center gap-4">
                <img src="https://lh3.googleusercontent.com/aida-public/AB6AXuBaZtm90MN5vvVxpDMilE6QEypgSXEIItSjb3CyI6yo7dDespJOaMR3sB-1zw1KPg_flntLBkIr3YwvaU0wUsI_vd-DQ19H8wKFIFgG4xvEXq7MFb_ZAxqyOLrYFcZ8zU1xebWz2FND2S6Tmh9Mm7OEjdtVDsfsVMG7KGx-e1iTqlGGr59eo1Hoqcl4UjJd9ib3npiBJBoJWLGR4mwNFBG2q2If0TN6ivLGtJ5T17oN2khnQisajoOxeyDnFvx9rUwOygIeumhc5oni" alt="User profile" className="w-12 h-12 rounded-full object-cover shadow-sm" />
                <div>
                  <h2 className="font-headline-md text-headline-md text-primary">Alex Chen</h2>
                  <p className="font-label-sm text-label-sm text-on-surface-variant">Focusing on: Deep Work</p>
                </div>
              </div>
            </div>
            <div className="flex-1 overflow-y-auto px-4 flex flex-col gap-1">
              <div className="font-label-sm text-label-sm text-outline px-3 mb-2 mt-2 uppercase tracking-wider">Categories</div>
              {categories.map(cat => (
                <SidebarItem 
                  key={cat}
                  icon={<Tag size={20} />} 
                  label={cat} 
                  active={currentTab === 'tasks' && selectedCategory === cat} 
                  onClick={() => handleCategorySelect(cat)} 
                />
              ))}
            </div>
          </aside>
        )}

        {/* Main Content */}
        <main className="flex-1 flex flex-col h-full relative overflow-hidden bg-background">
          {currentTab === 'edit-task' ? (
             <EditTaskView 
               task={tasks.find(t => t.id === editingTaskId)}
               onCancel={() => { setCurrentTab('tasks'); setEditingTaskId(null); }}
               onSave={(data) => saveTask(data, editingTaskId ?? undefined)}
             />
          ) : (
            <>
              <header className="bg-background flex justify-between items-center px-container-padding py-stack-gap w-full shrink-0 z-10 min-h-[72px]">
                {currentTab === 'tasks' ? (
                  <button onClick={() => setIsMobileMenuOpen(true)} className="text-primary hover:opacity-80 transition-opacity active:scale-95">
                    <Menu size={28} />
                  </button>
                ) : (
                  <div className="w-7"></div> /* Placeholder to keep title centered if needed, or adjust flex */
                )}
                
                <div className="flex-1 flex justify-center">
                  <h1 className="font-headline-lg text-headline-lg font-bold text-primary tracking-tight">Clarity</h1>
                </div>
                
                <div className="w-7"></div>
              </header>

              <div className="flex-1 overflow-y-auto px-container-padding pb-32 pt-2 custom-scrollbar">
                {currentTab === 'calendar' && <CalendarView tasks={tasks} toggleTaskCompletion={toggleTaskCompletion} handleEditTask={handleEditTask} />}
                {currentTab === 'tasks' && <TaskListView tasks={tasks} selectedCategory={selectedCategory} toggleTaskCompletion={toggleTaskCompletion} handleEditTask={handleEditTask} />}
                {currentTab === 'stats' && <DashboardView />}
                {currentTab === 'timer' && <TimerView />}
                {currentTab === 'settings' && <SettingsView />}
              </div>

              {currentTab === 'tasks' && (
                <button 
                  onClick={() => handleEditTask(null)}
                  className="absolute bottom-24 right-6 w-14 h-14 rounded-xl bg-primary text-on-primary shadow-lg flex items-center justify-center hover:opacity-90 active:scale-95 transition-all z-40"
                >
                  <Plus size={28} />
                </button>
              )}

              {/* Bottom Nav */}
              <nav className="absolute bottom-0 left-0 w-full flex justify-around items-center px-4 py-2 pb-safe bg-surface shadow-[0px_-4px_20px_rgba(0,0,0,0.04)] z-50">
                <BottomNavItem icon={<CalendarDays size={24} />} label="Calendar" active={currentTab === 'calendar'} onClick={() => setCurrentTab('calendar')} />
                <BottomNavItem icon={<LayoutList size={24} />} label="Tasks" active={currentTab === 'tasks'} onClick={() => setCurrentTab('tasks')} />
                <BottomNavItem icon={<BarChart2 size={24} />} label="Stats" active={currentTab === 'stats'} onClick={() => setCurrentTab('stats')} />
                <BottomNavItem icon={<TimerIcon size={24} />} label="Timer" active={currentTab === 'timer'} onClick={() => setCurrentTab('timer')} />
                <BottomNavItem icon={<Settings size={24} />} label="Settings" active={currentTab === 'settings'} onClick={() => setCurrentTab('settings')} />
              </nav>
            </>
          )}
        </main>
      </div>
    </div>
  );
}

function SidebarItem({ icon, label, active = false, onClick }: { icon: React.ReactNode, label: string, active?: boolean, onClick?: () => void }) {
  if (active) {
    return (
      <button onClick={onClick} className="w-full flex items-center gap-4 bg-secondary-container text-on-secondary-container font-bold rounded-lg p-3 active:translate-x-1 transition-transform shadow-sm">
        <div className="text-on-secondary-container">{icon}</div>
        <span className="font-body-md text-body-md truncate">{label}</span>
      </button>
    );
  }
  return (
    <button onClick={onClick} className="w-full flex items-center gap-4 text-on-surface-variant p-3 hover:bg-surface-container-high transition-all rounded-lg active:translate-x-1 group">
      <div className="group-hover:text-on-surface">{icon}</div>
      <span className="font-body-md text-body-md group-hover:text-on-surface truncate">{label}</span>
    </button>
  );
}

function BottomNavItem({ icon, label, active = false, onClick }: { icon: React.ReactNode, label: string, active?: boolean, onClick?: () => void }) {
  if (active) {
    return (
      <button onClick={onClick} className="flex flex-col items-center justify-center bg-primary-container text-on-primary-container rounded-xl px-2 sm:px-4 py-1.5 active:scale-95 transition-transform duration-200">
        <div className="mb-1">{icon}</div>
        <span className="font-label-sm text-label-sm font-bold">{label}</span>
      </button>
    );
  }
  return (
    <button onClick={onClick} className="flex flex-col items-center justify-center text-on-surface-variant px-2 sm:px-4 py-1.5 hover:bg-surface-container-high transition-colors rounded-xl active:scale-95 duration-200 group">
      <div className="mb-1 group-hover:text-on-surface transition-colors">{icon}</div>
      <span className="font-label-sm text-label-sm group-hover:text-on-surface transition-colors">{label}</span>
    </button>
  );
}
