import { Timer as TimerIcon, TrendingUp, Flame } from 'lucide-react';

export function DashboardView() {
  return (
    <div className="max-w-3xl mx-auto flex flex-col gap-stack-gap pb-8">
      {/* Focus Time Hero Card */}
      <div className="bg-surface-container-lowest rounded-xl shadow-[0px_4px_20px_rgba(0,0,0,0.04)] p-6 relative overflow-hidden flex flex-col gap-1 border border-surface-container-low/50">
        <div className="absolute -right-4 -top-4 text-primary/5 select-none pointer-events-none">
          <TimerIcon size={120} strokeWidth={1} />
        </div>
        <h2 className="font-label-sm text-label-sm text-outline uppercase tracking-widest">Focus Time</h2>
        <div className="flex items-end gap-2 mt-1">
          <span className="font-headline-lg text-headline-lg text-primary text-[48px] leading-[48px] font-bold tracking-tight">42.5</span>
          <span className="font-body-md text-body-md text-on-surface-variant mb-1">hours</span>
        </div>
        <div className="mt-4 inline-flex items-center gap-1.5 bg-secondary-container/30 text-on-secondary-container px-3 py-1 rounded-full font-label-sm text-label-sm w-fit">
          <TrendingUp size={14} />
          <span>+8% this week</span>
        </div>
      </div>

      {/* Bento Grid: Completion & Stats */}
      <div className="grid grid-cols-2 gap-stack-gap">
        {/* Completion Rate */}
        <div className="bg-surface-container-lowest rounded-xl shadow-[0px_4px_20px_rgba(0,0,0,0.04)] p-5 flex flex-col items-center justify-between gap-4 border border-surface-container-low/50 relative">
          <h2 className="font-label-sm text-label-sm text-outline uppercase tracking-widest w-full text-left">Completion</h2>
          <div className="relative w-28 h-28 rounded-full flex items-center justify-center" style={{ background: 'conic-gradient(#8fb9a8 85%, #f3f4f1 0)' }}>
            <div className="absolute inset-0 m-[6px] bg-surface-container-lowest rounded-full flex items-center justify-center shadow-inner">
              <div className="flex flex-col items-center">
                <span className="font-headline-md text-headline-md text-primary font-bold">85%</span>
                <span className="text-[10px] text-outline">Rate</span>
              </div>
            </div>
          </div>
        </div>

        {/* Secondary Stats Column */}
        <div className="flex flex-col gap-stack-gap">
          {/* Tasks Done */}
          <div className="bg-surface-container-lowest rounded-xl shadow-[0px_4px_20px_rgba(0,0,0,0.04)] p-4 flex-1 flex flex-col justify-center border border-surface-container-low/50">
            <h2 className="font-label-sm text-label-sm text-outline uppercase tracking-widest mb-1">Tasks Done</h2>
            <span className="font-headline-lg text-headline-lg text-on-surface font-bold">128</span>
          </div>
          {/* Streaks */}
          <div className="bg-surface-container-lowest rounded-xl shadow-[0px_4px_20px_rgba(0,0,0,0.04)] p-4 flex-1 flex flex-col justify-center border border-surface-container-low/50">
            <h2 className="font-label-sm text-label-sm text-outline uppercase tracking-widest mb-1">Current Streak</h2>
            <div className="flex items-center gap-1.5">
              <span className="font-headline-lg text-headline-lg text-on-surface font-bold">12</span>
              <span className="font-body-md text-body-md text-outline">days</span>
              <Flame size={18} className="text-tertiary ml-auto" />
            </div>
          </div>
        </div>
      </div>

      {/* Categories Distribution */}
      <div className="bg-surface-container-lowest rounded-xl shadow-[0px_4px_20px_rgba(0,0,0,0.04)] p-6 flex flex-col gap-5 border border-surface-container-low/50">
        <h2 className="font-label-sm text-label-sm text-outline uppercase tracking-widest">Categories Distribution</h2>
        <div className="flex flex-col gap-4">
          <CategoryBar name="Deep Work" percentage={55} colorClass="bg-primary" />
          <CategoryBar name="Admin & Comms" percentage={30} colorClass="bg-primary-container" />
          <CategoryBar name="Learning" percentage={15} colorClass="bg-secondary-container" />
        </div>
      </div>
    </div>
  );
}

function CategoryBar({ name, percentage, colorClass }: { name: string, percentage: number, colorClass: string }) {
  return (
    <div className="flex flex-col gap-2">
      <div className="flex justify-between items-center font-body-md text-body-md">
        <div className="flex items-center gap-2">
          <span className={`w-2 h-2 rounded-full ${colorClass}`}></span>
          <span className="text-on-surface">{name}</span>
        </div>
        <span className="text-on-surface-variant font-medium">{percentage}%</span>
      </div>
      <div className="h-2 w-full bg-surface-container-low rounded-full overflow-hidden">
        <div className={`h-full rounded-full ${colorClass}`} style={{ width: `${percentage}%` }}></div>
      </div>
    </div>
  );
}
